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
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import de.sky.newcrm.apims.spring.utils.DateTimeUtc;
import java.io.IOException;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

@SuppressWarnings({"java:S110"})
public class ApimsDateSerializer extends DateSerializer {

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property)
            throws JsonMappingException {
        DateTimeFormat source = property == null ? null : property.getMember().getAnnotation(DateTimeFormat.class);
        if (source != null) {
            return new DateTimeFormatAnnotationDateSerializer(source);
        }
        return super.createContextual(serializers, property);
    }

    @Override
    public void serialize(Date value, JsonGenerator g, SerializerProvider provider) throws IOException {
        g.writeString(DateTimeUtc.format(value));
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
        public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (isWriteIsoDateWithTimezone()) {
                value = DateTimeUtc.resetTimeByDate(value);
            }
            gen.writeString(DateTimeUtc.format(value, getDateTimeUtcISO()));
        }
    }
}
