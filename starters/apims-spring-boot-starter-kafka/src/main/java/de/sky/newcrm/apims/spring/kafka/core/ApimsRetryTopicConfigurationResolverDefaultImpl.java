/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core;


import com.google.api.client.http.ExponentialBackOffPolicy;
import de.sky.newcrm.apims.spring.environment.core.ApimsValueResolver;
import de.sky.newcrm.apims.spring.utils.AssertUtils;
import de.sky.newcrm.apims.spring.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.retrytopic.*;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.EndpointHandlerMethod;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@SuppressWarnings({"java:S6212"})
public class ApimsRetryTopicConfigurationResolverDefaultImpl
        implements ApimsRetryTopicConfigurationResolver, InitializingBean {

    private EndpointHandlerMethod deadLetterHandlerDelegate;

    private final ApimsRetryTopicConfigurationPropertiesMap properties;
    private final ApimsValueResolver apimsValueResolver;
    private final ApimsDeadLetterHandler apimsDeadLetterHandler;

    @Override
    public void afterPropertiesSet() throws Exception {
        Method dltMethod = ObjectUtils.findMethod(
                ApimsDeadLetterHandler.class, "handleDltMessage", Object.class, Acknowledgment.class);
        AssertUtils.notNullCheck("ApimsDeadLetterHandler.handleDltMessage", dltMethod);
        deadLetterHandlerDelegate = RetryTopicConfigurer.createHandlerMethodWith(apimsDeadLetterHandler, dltMethod);
    }

    @Override
    public RetryTopicConfiguration createConfiguration(
            ApimsRetryAndDeadletterTopic annotation,
            KafkaListener kafkaListener,
            KafkaOperations<?, ?> kafkaTemplate,
            Method method,
            Object bean,
            String beanName,
            String topic) {
        for (Map.Entry<String, ApimsRetryTopicConfigurationProperties> entry :
                properties.getPropertiesMap().entrySet()) {
            if (entry.getKey().equals(topic)) {
                ApimsRetryTopicConfigurationProperties property = entry.getValue();

                if (!property.isEnabled() || topic.isEmpty()) {
                    return null;
                }

                boolean groupBasedRetryAndDltTopics = Boolean.parseBoolean(apimsValueResolver.resolveExpression(
                        annotation.groupBasedRetryAndDltTopics(),
                        String.valueOf(property.isGroupBasedRetryAndDltTopics())));
                int retryAttemptsValue = Integer.parseInt(apimsValueResolver.resolveExpression(
                        annotation.retryAttempts(), String.valueOf(property.getRetryAttempts())));
                if (retryAttemptsValue < 0) {
                    retryAttemptsValue = 0;
                }
                int maxAttempts = retryAttemptsValue + 1;
                long delayValue = Long.parseLong(
                        apimsValueResolver.resolveExpression(annotation.delay(), String.valueOf(property.getDelay())));
                if (delayValue < 1000) {
                    delayValue = 1000;
                }
                double multiplierValue = Double.parseDouble(apimsValueResolver.resolveExpression(
                        annotation.multiplier(), String.valueOf(property.getMultiplier())));
                String dltTopicSuffix =
                        apimsValueResolver.resolveExpression(annotation.dltTopicSuffix(), property.getDltTopicSuffix());
                String retryTopicSuffix = property.getRetryTopicSuffix();
                if (groupBasedRetryAndDltTopics) {
                    String listenerGroupId =
                            apimsValueResolver.resolveExpression(kafkaListener.groupId(), property.getDefaultGroupId());
                    String resourcePrefix = property.getResourcePrefix();
                    String resourcePrefix2 = resourcePrefix.replace('_', '-');
                    if (listenerGroupId.startsWith(resourcePrefix)) {
                        listenerGroupId = listenerGroupId.substring(resourcePrefix.length());
                    } else if (listenerGroupId.startsWith(resourcePrefix2)) {
                        listenerGroupId = listenerGroupId.substring(resourcePrefix2.length());
                    }
                    if (listenerGroupId.startsWith("-") || listenerGroupId.startsWith("_")) {
                        listenerGroupId = listenerGroupId.substring(1);
                    }
                    dltTopicSuffix = "-" + listenerGroupId + dltTopicSuffix;
                    retryTopicSuffix = "-" + listenerGroupId + retryTopicSuffix;
                }

                SleepingBackOffPolicy<?> policy;
                if (SameIntervalTopicReuseStrategy.SINGLE_TOPIC.equals(annotation.sameIntervalTopicReuseStrategy())) {
                    FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
                    fixedBackOffPolicy.setBackOffPeriod(delayValue);
                    policy = fixedBackOffPolicy;
                } else {
                    long current = delayValue;
                    long maxElapsedTime = retryAttemptsValue < 2 ? current : 0;
                    for (int i = 1; i < retryAttemptsValue; i++) {
                        current = (long) (current * multiplierValue);
                        maxElapsedTime += current;
                    }
                    ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
                    exponentialBackOffPolicy.setInitialInterval(delayValue);
                    exponentialBackOffPolicy.setMultiplier(multiplierValue);
                    exponentialBackOffPolicy.setMaxInterval(maxElapsedTime);
                    policy = exponentialBackOffPolicy;
                }

                return RetryTopicConfigurationBuilder.newInstance()
                        .maxAttempts(maxAttempts)
                        .customBackoff(policy)
                        .retryTopicSuffix(retryTopicSuffix)
                        .dltSuffix(dltTopicSuffix)
                        .dltHandlerMethod(getDltProcessor(method, bean))
                        .includeTopic(topic)
                        .listenerFactory(property.getListenerContainerFactory())
                        .autoCreateTopics(
                                property.isAutoCreateTopics(),
                                property.getAutoCreateNumPartitions(),
                                property.getAutoCreateReplicationFactor())
                        .traversingCauses(false)
                        .sameIntervalTopicReuseStrategy(annotation.sameIntervalTopicReuseStrategy())
                        .dltProcessingFailureStrategy(annotation.dltStrategy())
                        .autoStartDltHandler(null)
                        .setTopicSuffixingStrategy(TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE)
                        .timeoutAfter(RetryTopicConstants.NOT_SET)
                        .create(kafkaTemplate);
            }
        }
        return null;
    }

    protected EndpointHandlerMethod getDltProcessor(Method listenerMethod, Object bean) {
        Class<?> declaringClass = listenerMethod.getDeclaringClass();
        return Arrays.stream(ObjectUtils.getDeclaredMethods(declaringClass))
                .filter(method -> AnnotationUtils.findAnnotation(method, DltHandler.class) != null)
                .map(method -> RetryTopicConfigurer.createHandlerMethodWith(bean, method))
                .findFirst()
                .orElse(deadLetterHandlerDelegate);
    }
}
