/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core.mapper.module;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import de.sky.newcrm.apims.spring.utils.DateTimeUtc;
import java.io.IOException;
import java.util.Calendar;

@SuppressWarnings({"java:S110"})
public class ApimsCalendarSerializer extends JsonSerializer<Calendar> {

    @Override
    public void serialize(Calendar value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(DateTimeUtc.format(value.getTime(), DateTimeUtc.ISO.DATE_TIME_COMPLETE));
    }
}
