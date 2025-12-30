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

import java.util.Date;

@SuppressWarnings({"java:S110"})
public class ApimsDateSerializer extends ValueSerializer<Date> {

    @Override
    public ValueSerializer<?> createContextual(SerializationContext serializers, BeanProperty property)
            throws JacksonException {
        DateTimeFormat source = property == null ? null : property.getMember().getAnnotation(DateTimeFormat.class);
        if (source != null) {
            return new DateTimeFormatAnnotationDateSerializer(source);
        }
        return this;
    }

    @Override
    public void serialize(Date value, JsonGenerator g, SerializationContext provider) {
        // Use DATE_TIME_COMPLETE as default to match expected output in tests (e.g. 2024-06-06T00:00:00Z)
        g.writeString(DateTimeUtc.format(value, DateTimeUtc.ISO.DATE_TIME));
    }

    private static class DateTimeFormatAnnotationDateSerializer extends ApimsDateTimeFormatAnnotationSerializer<Date> {

        public DateTimeFormatAnnotationDateSerializer(DateTimeFormat source) {
            super(source);
        }

        @Override
        protected DateTimeUtc.ISO getDateTimeUtcISODefault() {
            return DateTimeUtc.ISO.DATE_TIME;
        }

        @Override
        public void serialize(Date value, JsonGenerator gen, SerializationContext serializers) {
            if (isWriteIsoDateWithTimezone()) {
                value = DateTimeUtc.resetTimeByDate(value);
            }
            gen.writeString(DateTimeUtc.format(value, getDateTimeUtcISO()));
        }
    }
}
