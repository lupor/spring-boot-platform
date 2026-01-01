/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core.sender;

import de.sky.newcrm.apims.spring.core.kafka.entity.ApimsKafkaRecord;
import de.sky.newcrm.apims.spring.core.kafka.serializers.ApimsAvroRecord;
import de.sky.newcrm.apims.spring.core.kafka.serializers.ApimsAvroRecordProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
@SuppressWarnings({"java:S6212"})
public class ApimsKafkaGenericMessageSender extends ApimsKafkaMessageSender<Object, Object> {

    private final ApimsAvroRecordProducer apimsAvroRecordProducer;

    @SuppressWarnings({"java:S135", "java:S1871"})
    protected void send(String topic, ApimsKafkaRecord apimsKafkaRecord) {
        ApimsAvroRecord apimsAvroRecord = apimsAvroRecordProducer.createRecordForTopic(
                apimsKafkaRecord.getOriginalTopic(), apimsKafkaRecord.getBody());
        ProducerRecord<Object, Object> producerRecord =
                new ProducerRecord<>(topic, apimsKafkaRecord.getKey(), apimsAvroRecord);
        for (ApimsKafkaRecord.Header header : apimsKafkaRecord.getHeaders()) {
            producerRecord.headers().add(header.getKey(), header.getValue().getBytes(StandardCharsets.UTF_8));
        }
        beforeSend(topic, apimsKafkaRecord);
        send(producerRecord);
        afterSend(topic, apimsKafkaRecord);
    }

    protected void beforeSend(String topic, ApimsKafkaRecord apimsKafkaRecord) {
        if (log.isDebugEnabled()) {
            log.debug("[KAFKA GENERIC SEND] : '{}' send to topic '{}'...", apimsKafkaRecord.getKey(), topic);
        }
    }

    protected void afterSend(String topic, ApimsKafkaRecord apimsKafkaRecord) {
        if (log.isDebugEnabled()) {
            log.debug("[KAFKA GENERIC SEND] : '{}' send to topic '{}': OK", apimsKafkaRecord.getKey(), topic);
        }
    }
}
