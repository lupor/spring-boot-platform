/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core.mapper.legacy.module;

import com.fasterxml.jackson.databind.JsonSerializer;
import de.sky.newcrm.apims.spring.serialization.core.mapper.legacy.ApimsObjectMapperConfig;
import de.sky.newcrm.apims.spring.utils.DateTimeUtc;
import org.springframework.format.annotation.DateTimeFormat;

public abstract class ApimsDateTimeFormatAnnotationSerializer<T> extends JsonSerializer<T> {

    private final DateTimeFormat source;

    protected ApimsDateTimeFormatAnnotationSerializer(DateTimeFormat source) {
        this.source = source;
    }

    protected DateTimeUtc.ISO getDateTimeUtcISO() {
        if (source == null) {
            return getDateTimeUtcISODefault();
        }
        DateTimeFormat.ISO iso = source.iso();
        if (DateTimeFormat.ISO.DATE_TIME.equals(iso)) {
            return DateTimeUtc.ISO.DATE_TIME;
        } else if (DateTimeFormat.ISO.DATE.equals(iso)) {
            return isWriteIsoDateWithTimezone() ? DateTimeUtc.ISO.DATE_TIME : DateTimeUtc.ISO.DATE;
        } else if (DateTimeFormat.ISO.TIME.equals(iso)) {
            return DateTimeUtc.ISO.TIME;
        }
        return getDateTimeUtcISODefault();
    }

    protected abstract DateTimeUtc.ISO getDateTimeUtcISODefault();

    protected boolean isIsoDate() {
        return source != null && DateTimeFormat.ISO.DATE.equals(source.iso());
    }

    protected boolean isWriteIsoDateWithTimezone() {
        return isIsoDate() && getApimsObjectMapperConfig().isDateTimeSerializerWriteIsoDateWithTimezone();
    }

    private ApimsObjectMapperConfig getApimsObjectMapperConfig() {
        return ApimsObjectMapperConfig.getInstance();
    }
}
