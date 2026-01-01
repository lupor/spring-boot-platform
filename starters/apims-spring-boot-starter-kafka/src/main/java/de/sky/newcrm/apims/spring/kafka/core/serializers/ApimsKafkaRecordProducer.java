/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core.serializers;

import de.sky.newcrm.apims.spring.core.env.ApimsSpringContext;
import de.sky.newcrm.apims.spring.core.kafka.entity.ApimsKafkaRecord;
import de.sky.newcrm.apims.spring.core.objectmapper.ObjectMapperUtils;
import lombok.RequiredArgsConstructor;
import org.apache.avro.generic.GenericContainer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@SuppressWarnings({"java:S6212"})
public class ApimsKafkaRecordProducer {

    public ApimsKafkaRecord createApimsKafkaRecord(ConsumerRecord<?, ?> cr) {
        List<ApimsKafkaRecord.Header> headers = new ArrayList<>();
        if (cr.headers() != null) {
            for (Header header : cr.headers()) {
                headers.add(ApimsKafkaRecord.Header.builder()
                        .key(header.key())
                        .value(header.value() == null ? null : new String(header.value(), StandardCharsets.UTF_8))
                        .build());
            }
        }
        Object value = cr.value();
        String bodyType = value == null ? null : value.getClass().getName();
        String body = null;
        if (value != null) {
            if (value instanceof String s) {
                body = s;
            } else if (value instanceof GenericContainer) {
                body = String.valueOf(value);
            } else {
                body = ObjectMapperUtils.writeValueAsString(value);
            }
        }

        ApimsKafkaRecord.ApimsKafkaRecordBuilder recordBuilder = ApimsKafkaRecord.builder()
                .appEnv(resolvePlaceholders("${apims.app.env:}"))
                .appName(resolvePlaceholders("${apims.app.name:}"))
                .appDomain(resolvePlaceholders("${apims.app.domain:}"))
                .partition(cr.partition())
                .offset(cr.offset())
                .topic(cr.topic())
                .timestamp(cr.timestamp())
                .timestampType(cr.timestampType().name)
                .serializedKeySize(cr.serializedKeySize())
                .serializedValueSize(cr.serializedValueSize())
                .leaderEpoch(cr.leaderEpoch().orElse(null))
                .key(String.valueOf(cr.key()))
                .headers(headers)
                .bodyType(bodyType)
                .body(body);

        ApimsKafkaRecord apimsKafkaRecord = recordBuilder.build();
        String originalTopic = apimsKafkaRecord.getLastHeaderValue(KafkaHeaders.ORIGINAL_TOPIC);
        apimsKafkaRecord.setOriginalTopic(originalTopic == null ? apimsKafkaRecord.getTopic() : originalTopic);
        apimsKafkaRecord.setRetryTopic(apimsKafkaRecord.getLastHeaderValue(KafkaHeaders.PREFIX + "retry-topic"));
        return apimsKafkaRecord;
    }

    protected String resolvePlaceholders(String expression) {
        return ApimsSpringContext.resolvePlaceholders(expression);
    }
}
