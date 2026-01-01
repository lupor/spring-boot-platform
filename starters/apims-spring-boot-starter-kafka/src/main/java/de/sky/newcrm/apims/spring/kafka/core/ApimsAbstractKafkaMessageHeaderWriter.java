/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core;

import org.apache.kafka.common.header.Headers;

import java.util.Map;
import java.util.TreeMap;

public abstract class ApimsAbstractKafkaMessageHeaderWriter implements ApimsKafkaMessageHeaderWriter {

    private final Map<String, String> headers = new TreeMap<>();

    protected ApimsAbstractKafkaMessageHeaderWriter(Map<String, String> headers, Map<String, String> additonalHeaders) {
        this.headers.putAll(headers);
        this.headers.putAll(additonalHeaders);
    }

    @Override
    public void writeHeaders(Headers kafkaHeaders) {
        writeHeadersMap(kafkaHeaders);
    }

    protected void writeHeadersMap(Headers kafkaHeaders) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            addHeaderIfNotExists(kafkaHeaders, entry.getKey(), entry.getValue());
        }
    }
}
