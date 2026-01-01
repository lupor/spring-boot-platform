/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core;

import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import de.sky.newcrm.apims.spring.exceptions.ApimsRuntimeException;
import de.sky.newcrm.apims.spring.kafka.core.entity.ApimsKafkaRecord;
import de.sky.newcrm.apims.spring.kafka.core.serializers.ApimsKafkaRecordProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.Acknowledgment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@SuppressWarnings({"java:S6212"})
public class ApimsDeadLetterHandler {

    private final ApimsKafkaRecordProducer kafkaRecordProducer;
    private final List<ApimsDeadLetterAction> actions;

    @Value("${spring.kafka.listener.ack-mode}")
    private String ackMode;

    @ApimsReportGeneratedHint
    public void handleDltMessage(Object message, Acknowledgment acknowledgement) {
        if (actions != null && !actions.isEmpty() && message instanceof ConsumerRecord<?, ?> consumerRecord) {
            Map<Class<?>, String> mandatoryExceptions = new HashMap<>();
            ApimsKafkaRecord apimsKafkaRecord = kafkaRecordProducer.createApimsKafkaRecord(consumerRecord);
            for (ApimsDeadLetterAction receiver : actions) {
                try {
                    receiver.handleDltRecord(apimsKafkaRecord);
                } catch (Exception e) {
                    log.error(receiver.getClass().getSimpleName() + ".handleDltRecord failed.", e);
                    if (receiver.isMandatory()) {
                        mandatoryExceptions.put(receiver.getClass(), e.getMessage());
                    }
                }
            }
            if (!mandatoryExceptions.isEmpty()) {
                throw new ApimsRuntimeException("handleDltMessage failed by mandatory action.");
            }
            if ("MANUAL".equalsIgnoreCase(ackMode)) {
                acknowledgement.acknowledge();
            }
        }
    }
}
