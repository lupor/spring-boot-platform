/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core.mapper.module;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import de.sky.newcrm.apims.spring.utils.DateTimeUtc;
import java.io.IOException;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@SuppressWarnings({"java:S110"})
public class ApimsOffsetDateTimeSerializer extends JsonSerializer<OffsetDateTime> implements ContextualSerializer {

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property)
            throws JsonMappingException {
        DateTimeFormat source = property == null ? null : property.getMember().getAnnotation(DateTimeFormat.class);
        return new DateTimeFormatAnnotationOffsetDateTimeSerializer(source);
    }

    @Override
    public void serialize(OffsetDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
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
        public void serialize(OffsetDateTime value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {
            if (isWriteIsoDateWithTimezone()) {
                value = DateTimeUtc.resetTimeByOffsetDateTime(value);
            }
            gen.writeString(DateTimeUtc.format(value, getDateTimeUtcISO()));
        }
    }
}
