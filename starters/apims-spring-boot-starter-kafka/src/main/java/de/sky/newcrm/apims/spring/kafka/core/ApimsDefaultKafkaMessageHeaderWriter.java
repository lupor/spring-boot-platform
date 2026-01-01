/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core;

import java.util.Map;

public class ApimsDefaultKafkaMessageHeaderWriter extends ApimsAbstractKafkaMessageHeaderWriter {

    public ApimsDefaultKafkaMessageHeaderWriter(Map<String, String> headers, Map<String, String> additonalHeaders) {
        super(headers, additonalHeaders);
    }
}
