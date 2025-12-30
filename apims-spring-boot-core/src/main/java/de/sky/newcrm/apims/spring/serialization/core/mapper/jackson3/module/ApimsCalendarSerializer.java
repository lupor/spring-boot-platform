/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3.module;

import de.sky.newcrm.apims.spring.utils.DateTimeUtc;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.util.Calendar;

@SuppressWarnings({"java:S110"})
public class ApimsCalendarSerializer extends ValueSerializer<Calendar> {

    @Override
    public void serialize(Calendar value, JsonGenerator gen, SerializationContext context) {
        gen.writeString(DateTimeUtc.format(value.getTime(), DateTimeUtc.ISO.DATE_TIME_COMPLETE));
    }
}
