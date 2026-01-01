/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core;

import org.apache.kafka.common.header.Headers;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

public interface ApimsKafkaMessageHeaderWriter {

    void writeHeaders(Headers headers);

    default void addHeaderIfNotExists(Headers kafkaHeaders, String key, String value) {
        if (StringUtils.hasLength(value) && kafkaHeaders.lastHeader(key) == null) {
            kafkaHeaders.add(key, value.getBytes(StandardCharsets.UTF_8));
        }
    }
}
