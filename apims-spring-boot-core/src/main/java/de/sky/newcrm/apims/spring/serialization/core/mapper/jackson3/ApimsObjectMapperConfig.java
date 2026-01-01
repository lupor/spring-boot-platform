/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3;

import de.sky.newcrm.apims.spring.environment.core.ApimsSpringContext;

public class ApimsObjectMapperConfig {

    private static ApimsObjectMapperConfig instance;

    public static ApimsObjectMapperConfig getInstance() {
        if (instance == null) {
            instance = new ApimsObjectMapperConfig();
        }
        return instance;
    }

    public static void resetInstance() {
        instance = new ApimsObjectMapperConfig();
    }

    private ApimsObjectMapperConfig() {}

    private Boolean dateTimeSerializerWriteIsoDateWithTimezone;

    public boolean isDateTimeSerializerWriteIsoDateWithTimezone() {
        if (dateTimeSerializerWriteIsoDateWithTimezone == null) {
            dateTimeSerializerWriteIsoDateWithTimezone =
                    Boolean.parseBoolean(getProperty("date-time-serializer-write-iso-date-with-timezone", "false"));
        }
        return dateTimeSerializerWriteIsoDateWithTimezone;
    }

    protected String getProperty(String key, String defaultValue) {
        return ApimsSpringContext.getProperty("apims.object-mapper-config." + key, defaultValue);
    }
}
