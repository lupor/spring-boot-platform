/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3.module;

import de.sky.newcrm.apims.spring.utils.DateTimeUtc;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import lombok.SneakyThrows;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ApimsCalendarDeserializer extends ValueDeserializer<Calendar> {

    @Override
    @SneakyThrows
    public Calendar deserialize(JsonParser p, DeserializationContext ctxt) {
        Date date = DateTimeUtc.parseDate(p.getString().trim());
        if (date == null) {
            return null;
        }
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar;
    }
}
