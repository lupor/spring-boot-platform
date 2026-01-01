/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core.serializers;

import java.util.Map;

class ApimsKafkaAvroUtils {

    private ApimsKafkaAvroUtils() {}

    static String getConfigValue(Map<String, ?> configs, boolean isKey, String name, String defaultValue) {
        String configKey = (isKey ? "key." : "value.") + name;
        Object value = configs.get(configKey);
        if (value == null) {
            value = configs.get(name);
        }
        return value == null ? defaultValue : String.valueOf(value);
    }
}
