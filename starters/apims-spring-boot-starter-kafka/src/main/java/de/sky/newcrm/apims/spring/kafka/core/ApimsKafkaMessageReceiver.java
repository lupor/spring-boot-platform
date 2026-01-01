/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core;

import de.sky.newcrm.apims.spring.core.processing.ApimsProcessingGroup;
import de.sky.newcrm.apims.spring.core.processing.ApimsProcessingStrategy;
import de.sky.newcrm.apims.spring.core.processing.ApimsProcessorExecutor;
import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

import java.util.Arrays;
import java.util.stream.Stream;

public abstract class ApimsKafkaMessageReceiver<I> extends ApimsProcessorExecutor<I> {

    @Value("${spring.kafka.listener.ack-mode}")
    private String ackMode;

    @PostConstruct
    private void init() {
        boolean hasAckParam = Stream.of(getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(KafkaListener.class))
                .anyMatch(method ->
                        Arrays.stream(method.getParameterTypes()).anyMatch(Acknowledgment.class::isAssignableFrom));
        if ("MANUAL".equalsIgnoreCase(ackMode) && !hasAckParam) {
            throw new IllegalStateException(
                    "Invalid Kafka listener configuration: 'ack-mode' is set to MANUAL, but class extends 'ApimsKafkaMessageReceiver'. "
                            + "Please extend 'ApimsResilientKafkaMessageReceiver' to support manual acknowledgment.");
        }
    }

    public abstract void onEvent(ConsumerRecord<String, I> consumerRecord);

    protected void process(ConsumerRecord<String, I> consumerRecord) {
        super.process(
                ApimsProcessingGroup.KAFKA_CONSUMER,
                consumerRecord.value(),
                ApimsProcessingStrategy.PROCESS_FIRST_SUPPORTED_HANDLER_ONLY);
    }

    protected void process(ConsumerRecord<String, I> consumerRecord, ApimsProcessingStrategy processingStrategy) {
        super.process(ApimsProcessingGroup.KAFKA_CONSUMER, consumerRecord.value(), processingStrategy);
    }
}
