/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core.sender;

import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import de.sky.newcrm.apims.spring.environment.core.ApimsSpringContext;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

public abstract class ApimsKafkaMessageSender<K, V> {

    private KafkaTemplate<K, V> kafkaTemplate = null;

    protected ApimsKafkaMessageSender() {}

    protected ApimsKafkaMessageSender(KafkaTemplate<K, V> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @SuppressWarnings({"unchecked", "java:S3740"})
    @ApimsReportGeneratedHint
    protected synchronized KafkaTemplate<K, V> getKafkaTemplate() {
        if (kafkaTemplate == null) {
            Map<String, KafkaTemplate> beans =
                    ApimsSpringContext.getApplicationContext().getBeansOfType(KafkaTemplate.class);
            int beansCount = beans.size();
            for (Map.Entry<String, KafkaTemplate> entry : beans.entrySet()) {
                // special case unit tests
                if (beansCount == 1 || !"kafkaTemplate".equalsIgnoreCase(entry.getKey())) {
                    kafkaTemplate = entry.getValue();
                    break;
                }
            }
            // fallback with error message
            if (kafkaTemplate == null) {
                kafkaTemplate = ApimsSpringContext.getApplicationContext().getBean(KafkaTemplate.class);
            }
        }
        return kafkaTemplate;
    }

    protected void send(String topic, K key, V value) {
        ProducerRecord<K, V> producerRecord = new ProducerRecord<>(topic, key, value);
        send(producerRecord);
    }

    protected void send(ProducerRecord<K, V> producerRecord) {
        getKafkaTemplate().send(producerRecord);
    }
}
