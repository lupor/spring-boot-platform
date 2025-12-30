/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3.module;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;
import de.sky.newcrm.apims.spring.utils.DateTimeUtc;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.IOException;
import java.time.OffsetDateTime;

@SuppressWarnings({"java:S110"})
public class ApimsOffsetDateTimeSerializer extends ValueSerializer<OffsetDateTime> {

    @Override
    public ValueSerializer<?> createContextual(SerializationContext serializers, BeanProperty property)
            throws JacksonException {
        DateTimeFormat source = property == null ? null : property.getMember().getAnnotation(DateTimeFormat.class);
        return new DateTimeFormatAnnotationOffsetDateTimeSerializer(source);
    }

    @Override
    public void serialize(OffsetDateTime value, JsonGenerator gen, SerializationContext serializers) {
        gen.writeString(DateTimeUtc.format(value, DateTimeUtc.ISO.DATE_TIME_COMPLETE));
    }

    private static class DateTimeFormatAnnotationOffsetDateTimeSerializer
            extends ApimsDateTimeFormatAnnotationSerializer<OffsetDateTime> {

        public DateTimeFormatAnnotationOffsetDateTimeSerializer(DateTimeFormat source) {
            super(source);
        }

        @Override
        protected DateTimeUtc.ISO getDateTimeUtcISODefault() {
            return DateTimeUtc.ISO.DATE_TIME_COMPLETE;
        }

        @Override
        public void serialize(OffsetDateTime value, JsonGenerator gen, SerializationContext serializers) {
            if (isWriteIsoDateWithTimezone()) {
                value = DateTimeUtc.resetTimeByOffsetDateTime(value);
            }
            gen.writeString(DateTimeUtc.format(value, getDateTimeUtcISO()));
        }
    }
}
