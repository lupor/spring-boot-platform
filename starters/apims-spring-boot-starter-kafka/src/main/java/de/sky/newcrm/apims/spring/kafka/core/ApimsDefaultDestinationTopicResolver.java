/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core;

import de.sky.newcrm.apims.spring.exceptions.NoRetryableException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.kafka.retrytopic.DefaultDestinationTopicResolver;
import org.springframework.kafka.retrytopic.DestinationTopic;
import org.springframework.kafka.support.serializer.DeserializationException;

import java.time.Clock;
import java.util.*;

@Slf4j
public class ApimsDefaultDestinationTopicResolver extends DefaultDestinationTopicResolver {

    private static final Class<?>[] poisonThrowableClasses =
            new Class[] {DeserializationException.class, SerializationException.class};
    private final List<DestinationTopic> shadowDestinationsTopicList = new ArrayList<>();
    private final Map<String, List<DestinationTopic>> shadowDestinationsTopicMap = new HashMap<>();
    private final ApimsRetryTopicConfigurationPropertiesMap apimsRetryTopicConfigurationPropertiesMap;

    public ApimsDefaultDestinationTopicResolver(
            Clock clock, ApimsRetryTopicConfigurationPropertiesMap apimsRetryTopicConfigurationPropertiesMap) {
        super(clock);
        this.apimsRetryTopicConfigurationPropertiesMap = apimsRetryTopicConfigurationPropertiesMap;
    }

    @Override
    public void addDestinationTopics(String mainListenerId, List<DestinationTopic> destinationsToAdd) {
        for (DestinationTopic destinationTopic : destinationsToAdd) {
            if (!shadowDestinationsTopicList.contains(destinationTopic)) {
                shadowDestinationsTopicList.add(destinationTopic);
            }
        }
        Optional<DestinationTopic> mainDestinationTopic =
                destinationsToAdd.stream().filter(DestinationTopic::isMainTopic).findFirst();
        if (mainDestinationTopic.isPresent()) {
            List<DestinationTopic> list = new ArrayList<>(destinationsToAdd);
            list.sort(Comparator.comparing(DestinationTopic::getDestinationName));
            shadowDestinationsTopicMap.put(mainDestinationTopic.get().getDestinationName(), list);
        }
        super.addDestinationTopics(mainListenerId, destinationsToAdd);
    }

    @Override
    @SuppressWarnings({"java:S3776", "java:S4449"})
    public DestinationTopic resolveDestinationTopic(
            String mainListenerId, String topic, Integer attempt, Exception e, long originalTimestamp) {

        List<DestinationTopic> destinationTopics = getDestinationTopics(topic);
        DestinationTopic destinationTopic = null;
        String trimTopic = topic.split("-").length > 1 ? topic.substring(0, topic.indexOf("-")) : topic;
        ApimsRetryTopicConfigurationProperties apimsRetryTopicConfigurationProperties =
                apimsRetryTopicConfigurationPropertiesMap.getPropertiesMap().get(trimTopic);
        if (isNoRetryableException(e)) {
            if (apimsRetryTopicConfigurationProperties.isUseDltTopicOnNoRetryableException()) {
                final Optional<DestinationTopic> dltTopic = destinationTopics.stream()
                        .filter(DestinationTopic::isDltTopic)
                        .findAny();
                if (dltTopic.isPresent()) {
                    destinationTopic = dltTopic.get();
                }
            }
            if (destinationTopic == null) {
                destinationTopic = new ApimsDestinationDltPoisonNoOpsTopic(null, null);
            }
        }
        if (destinationTopic == null && isPoisonException(e)) {
            log.error("POISON EXCEPTION FOUND: ", e);
            Optional<DestinationTopic> optionalPoisonTopic = destinationTopics.stream()
                    .filter(ApimsDestinationDltPoisonNoOpsTopic.class::isInstance)
                    .findAny();
            destinationTopic = optionalPoisonTopic.orElse(new ApimsDestinationDltPoisonNoOpsTopic(null, null));
        }
        if (destinationTopic == null
                && attempt != null
                && isLastRetryTopic(topic, apimsRetryTopicConfigurationProperties)) {
            int lastRetryAttempt = attempt
                    - apimsRetryTopicConfigurationProperties.getRetryAttempts()
                    - apimsRetryTopicConfigurationProperties.getLastRetryAttempts();
            if (lastRetryAttempt < 0) {
                final Optional<DestinationTopic> lastRetryTopic = shadowDestinationsTopicList.stream()
                        .filter(dt -> dt.getDestinationName().equalsIgnoreCase(topic))
                        .findFirst();
                if (lastRetryTopic.isPresent()) {
                    destinationTopic = lastRetryTopic.get();
                }
            }
        }
        if (destinationTopic == null) {
            destinationTopic = super.resolveDestinationTopic(mainListenerId, topic, attempt, e, originalTimestamp);
        }
        log.debug(
                "|------ [_____KAFKA] : Resolved topic: '{}', delay: {}",
                destinationTopic.isNoOpsTopic() ? "NoOps:none" : destinationTopic.getDestinationName(),
                destinationTopic.isNoOpsTopic() ? "NoOps:unknown" : destinationTopic.getDestinationDelay());
        return destinationTopic;
    }

    public List<DestinationTopic> getDestinationTopics(String topic) {
        List<DestinationTopic> list = shadowDestinationsTopicMap.get(topic);
        if (list != null) {
            return list;
        }
        for (List<DestinationTopic> childList : shadowDestinationsTopicMap.values()) {
            final Optional<DestinationTopic> childTopic = childList.stream()
                    .filter(dt -> dt.getDestinationName().equalsIgnoreCase(topic))
                    .findFirst();
            if (childTopic.isPresent()) {
                return childList;
            }
        }
        return new ArrayList<>();
    }

    protected boolean isPoisonException(Throwable e) {
        if (e == null) {
            return false;
        }
        while (e != null) {
            final Class<? extends Throwable> clazz = e.getClass();
            for (Class<?> poisonThrowableClass : poisonThrowableClasses) {
                if (poisonThrowableClass.isAssignableFrom(clazz)) {
                    return true;
                }
            }
            e = e.getCause();
        }
        return false;
    }

    protected boolean isNoRetryableException(Throwable e) {
        while (e != null) {
            final Class<? extends Throwable> clazz = e.getClass();
            if (NoRetryableException.class.isAssignableFrom(clazz)) {
                return true;
            }
            e = e.getCause();
        }
        return false;
    }

    protected boolean isLastRetryTopic(String topic, ApimsRetryTopicConfigurationProperties retryProperty) {
        return topic.endsWith(calculateLastRetryTopicSuffix(retryProperty));
    }

    protected String calculateLastRetryTopicSuffix(ApimsRetryTopicConfigurationProperties retryProperty) {
        return retryProperty.getRetryTopicSuffix() + "-" + (retryProperty.getRetryAttempts() - 1);
    }
}
