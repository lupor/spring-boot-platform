/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core;

import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

import java.nio.charset.StandardCharsets;

public interface ApimsKafkaMessageHeaderReader {

    void readHeader(Headers headers);

    default String getHeader(Headers kafkaHeaders, String key) {
        Header header = kafkaHeaders.lastHeader(key);
        byte[] value = header == null ? null : header.value();
        return value == null ? null : new String(value, StandardCharsets.UTF_8);
    }
}
