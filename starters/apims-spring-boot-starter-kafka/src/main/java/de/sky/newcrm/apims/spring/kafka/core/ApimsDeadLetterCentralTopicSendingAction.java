/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core;

import de.sky.newcrm.apims.spring.core.env.ApimsSpringContext;
import de.sky.newcrm.apims.spring.core.kafka.entity.ApimsKafkaRecord;
import de.sky.newcrm.apims.spring.core.objectmapper.ObjectMapperUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class ApimsDeadLetterCentralTopicSendingAction implements ApimsDeadLetterAction {

    @Value("${apims.kafka.consumer.dlt.central-dlt-topic-enabled:false}")
    boolean enabled;

    @Value("${apims.kafka.consumer.dlt.central-dlt-topic-name:}")
    String topic;

    private KafkaTemplate<Object, Object> kafkaTemplate = null;

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    @Override
    public boolean isMandatory() {
        return true;
    }

    @SuppressWarnings("unchecked")
    protected synchronized KafkaTemplate<Object, Object> getKafkaTemplate() {
        if (kafkaTemplate == null) {
            kafkaTemplate = ApimsSpringContext.getApplicationContext().getBean(KafkaTemplate.class);
        }
        return kafkaTemplate;
    }

    @Override
    public void handleDltRecord(ApimsKafkaRecord apimsKafkaRecord) {
        if (!enabled || apimsKafkaRecord == null || !StringUtils.hasLength(topic)) {
            return;
        }
        log.info("[CENTRAL DEADLETTER] : send message to central deadletter topic '{}'...", topic);
        String key = apimsKafkaRecord.getKey();
        if (!StringUtils.hasLength(key)) {
            key = UUID.randomUUID().toString();
        }
        ProducerRecord<Object, Object> producerRecord =
                new ProducerRecord<>(topic, key, ObjectMapperUtils.writeValueAsString(apimsKafkaRecord));
        getKafkaTemplate().send(producerRecord);
        log.info("[CENTRAL DEADLETTER] : send message to central deadletter topic '{}': OK", topic);
    }
}
