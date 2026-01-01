/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core.sender;

import de.sky.newcrm.apims.spring.kafka.core.entity.ApimsKafkaRecord;
import de.sky.newcrm.apims.spring.kafka.core.serializers.ApimsAvroRecordProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.KafkaHeaders;

@Slf4j
public class ApimsKafkaGenericMessageToRetryTopicSender extends ApimsKafkaGenericMessageSender {

    public ApimsKafkaGenericMessageToRetryTopicSender(ApimsAvroRecordProducer apimsAvroRecordProducer) {
        super(apimsAvroRecordProducer);
    }

    public void sendToRetryTopic(ApimsKafkaRecord apimsKafkaRecord) {
        sendToTopic(apimsKafkaRecord.getRetryTopic(), apimsKafkaRecord);
    }

    public void sendToMainTopic(ApimsKafkaRecord apimsKafkaRecord) {
        sendToTopic(apimsKafkaRecord.getOriginalTopic(), apimsKafkaRecord);
    }

    protected void sendToTopic(String topic, ApimsKafkaRecord apimsKafkaRecord) {

        apimsKafkaRecord.setHeaderValue(
                "central_retry_topic-attempts",
                String.valueOf(apimsKafkaRecord.getLastHeaderValue("central_retry_topic-attempts", 0) + 1));
        apimsKafkaRecord.getHeaders().removeIf(header -> KafkaHeaders.EXCEPTION_MESSAGE.equals(header.getKey()));
        apimsKafkaRecord.getHeaders().removeIf(header -> KafkaHeaders.EXCEPTION_STACKTRACE.equals(header.getKey()));
        apimsKafkaRecord.getHeaders().removeIf(header -> "retry_topic-attempts".equals(header.getKey()));
        apimsKafkaRecord.getHeaders().removeIf(header -> "retry_topic-original-timestamp".equals(header.getKey()));
        apimsKafkaRecord.getHeaders().removeIf(header -> "retry_topic-backoff-timestamp".equals(header.getKey()));
        send(topic, apimsKafkaRecord);
    }

    @Override
    protected void beforeSend(String topic, ApimsKafkaRecord apimsKafkaRecord) {
        if (log.isInfoEnabled()) {
            log.info("|------ [KAFKARETRY] : '{}' send to retry '{}'...", apimsKafkaRecord.getKey(), topic);
        }
    }

    @Override
    protected void afterSend(String topic, ApimsKafkaRecord apimsKafkaRecord) {
        if (log.isInfoEnabled()) {
            log.info("|------ [KAFKARETRY] : '{}' send to retry '{}': OK", apimsKafkaRecord.getKey(), topic);
        }
    }
}
