/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core;

import de.sky.newcrm.apims.spring.exceptions.ApimsRuntimeException;
import de.sky.newcrm.apims.spring.kafka.core.processing.ApimsProcessingStrategy;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnStateTransitionEvent;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static io.github.resilience4j.circuitbreaker.CircuitBreaker.StateTransition.CLOSED_TO_OPEN;

@Slf4j
public abstract class ApimsResilientKafkaMessageReceiver<I> extends ApimsKafkaMessageReceiver<I> {

    @Value("${spring.kafka.listener.ack-mode}")
    private String ackMode;

    private static final String DLT_TOPIC_SUFFIX = "-dlt";
    private static final String RETRY_TOPIC_SUFFIX = "-retry-";
    private static final String NOT_SUPPORTED = "This method is not supported by resilient message receivers.";

    private final AtomicReference<String> cbUniqueOpenStateId = new AtomicReference<>();
    private final boolean onlyRetryTopicsPauseResumeFlag;
    private final Collection<MessageListenerContainer> listenerContainers = new ArrayList<>();
    private final Collection<MessageListenerContainer> listenerContainersExceptLastRetry = new ArrayList<>();

    private Consumer<ConsumerRecord<String, I>> resilientConsumer;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    protected ApimsResilientKafkaMessageReceiver() {
        this(false);
    }

    protected ApimsResilientKafkaMessageReceiver(boolean onlyRetryTopicsPauseResumeFlag) {
        // Check onEvent method annotation
        boolean found = false;
        for (var method : this.getClass().getDeclaredMethods()) {
            if (method.getName().equals("onEvent") && method.isAnnotationPresent(ApimsRetryAndDeadletterTopic.class)) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new IllegalStateException("The onEvent method in "
                    + this.getClass().getName() + " must be annotated with @ApimsRetryAndDeadletterTopic");
        }
        this.onlyRetryTopicsPauseResumeFlag = onlyRetryTopicsPauseResumeFlag;
    }

    @PostConstruct
    private void init() {
        List<String> listenerIds = Stream.of(getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(KafkaListener.class))
                .map(method -> method.getDeclaredAnnotation(KafkaListener.class).id())
                .filter(StringUtils::hasLength)
                .toList();

        validateListenerConfig(listenerIds);

        final String listenerId = listenerIds.get(0);
        circuitBreakerRegistry
                .find(listenerId)
                .ifPresentOrElse(
                        circuitBreaker -> {
                            circuitBreaker.getEventPublisher().onStateTransition(this::handleOnStateTransitionEvent);
                            resilientConsumer = circuitBreaker.decorateConsumer(super::process);
                        },
                        () -> {
                            log.error("The Configuration for Circuit Breaker '{}' is not present", listenerId);
                            throw new ApimsRuntimeException(
                                    "The Configuration for Circuit Breaker '" + listenerId + "' is not present.");
                        });
    }

    @Override
    public final void onEvent(ConsumerRecord<String, I> consumerRecord) {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    protected final void process(ConsumerRecord<String, I> consumerRecord) {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    protected final void process(ConsumerRecord<String, I> consumerRecord, ApimsProcessingStrategy processingStrategy) {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    protected void process(ConsumerRecord<String, I> consumerRecord, Acknowledgment ack) {
        boolean nack = false;
        try {
            if (consumerRecord.topic().contains(DLT_TOPIC_SUFFIX)
                    || (onlyRetryTopicsPauseResumeFlag
                            && !consumerRecord.topic().contains(RETRY_TOPIC_SUFFIX))) {
                super.process(consumerRecord);
            } else {
                resilientConsumer.accept(consumerRecord);
            }
        } catch (CallNotPermittedException e) {
            // nack
            log.trace("Circuit breaker is open/half-open/disabled, message will be nacked");
            ack.nack(Duration.ofSeconds(1L));
            nack = true;
        } finally {
            // ack
            if (!nack) {
                ack.acknowledge();
            }
        }
    }

    public abstract void onEvent(ConsumerRecord<String, I> consumerRecord, Acknowledgment ack);

    private void handleOnStateTransitionEvent(CircuitBreakerOnStateTransitionEvent event) {

        if (event.getStateTransition() == CLOSED_TO_OPEN) {
            cbUniqueOpenStateId.set(UUID.randomUUID().toString());
        }
        log.warn(
                "Circuit Breaker Id : {} ==> State Transition: {} -> {} - {}",
                event.getCircuitBreakerName(),
                event.getStateTransition().getFromState(),
                event.getStateTransition().getToState(),
                cbUniqueOpenStateId.get());

        cacheListenerContainers(event.getCircuitBreakerName());

        switch (event.getStateTransition()) {
            case CLOSED_TO_OPEN -> listenerContainers.forEach(this::pauseListener);
            case OPEN_TO_HALF_OPEN -> listenerContainersExceptLastRetry.forEach(this::resumeListener);
            case HALF_OPEN_TO_OPEN -> listenerContainersExceptLastRetry.forEach(this::pauseListener);
            case HALF_OPEN_TO_CLOSED -> listenerContainers.forEach(this::resumeListener);
            default -> log.debug("ignoring default case");
        }
    }

    private void pauseListener(MessageListenerContainer messageListenerContainer) {
        log.info("trying to pause listener: {}", messageListenerContainer.getListenerId());
        if (messageListenerContainer.isRunning()) {
            messageListenerContainer.pause();
            log.info("paused listener: {}", messageListenerContainer.getListenerId());
        }
    }

    private void resumeListener(MessageListenerContainer messageListenerContainer) {
        log.info("trying to resume listener: {}", messageListenerContainer.getListenerId());
        if (messageListenerContainer.isContainerPaused()) {
            messageListenerContainer.resume();
            log.info("resumed listener: {}", messageListenerContainer.getListenerId());
        }
    }

    private int getMaxRetryAttempts(Collection<MessageListenerContainer> listenerContainers) {
        return listenerContainers.stream()
                .map(MessageListenerContainer::getListenerId)
                .filter(id -> id.matches(".*" + RETRY_TOPIC_SUFFIX + "\\d+$"))
                .mapToInt(id -> Integer.parseInt(
                        id.substring(id.lastIndexOf(RETRY_TOPIC_SUFFIX) + RETRY_TOPIC_SUFFIX.length())))
                .max()
                .orElse(0);
    }

    private void cacheListenerContainers(String circuitBreakerName) {
        if (listenerContainers.isEmpty()) {
            // Cache all listener containers matching the criteria
            listenerContainers.addAll(kafkaListenerEndpointRegistry.getListenerContainersMatching(
                    container -> container.startsWith(circuitBreakerName)
                            && (!onlyRetryTopicsPauseResumeFlag || container.contains(RETRY_TOPIC_SUFFIX))
                            && !container.contains(DLT_TOPIC_SUFFIX)));

            int maxRetryAttempts = getMaxRetryAttempts(listenerContainers);

            // Cache listener containers excluding the last retry topic
            listenerContainersExceptLastRetry.addAll(listenerContainers.stream()
                    .filter(container -> !container.getListenerId().contains(RETRY_TOPIC_SUFFIX + maxRetryAttempts))
                    .toList());
        }
    }

    private void validateListenerConfig(List<String> listenerIds) {
        if (listenerIds.isEmpty()) {
            throw new ApimsRuntimeException("There is no listener with ID property.");
        }
        if (listenerIds.size() > 1) {
            throw new ApimsRuntimeException("There are multiple listeners with ID property.");
        }
        if (!"MANUAL".equalsIgnoreCase(ackMode)) {
            throw new ApimsRuntimeException(
                    "The ack-mode must be defined with MANUAL for Resilient Kafka Message Receivers.");
        }
    }
}
