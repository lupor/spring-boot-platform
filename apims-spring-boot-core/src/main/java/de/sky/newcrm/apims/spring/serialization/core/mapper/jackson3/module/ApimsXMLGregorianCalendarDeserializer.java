/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3.module;

import de.sky.newcrm.apims.spring.utils.DateTimeUtc;
import lombok.SneakyThrows;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ApimsXMLGregorianCalendarDeserializer extends ValueDeserializer<XMLGregorianCalendar> {

    @Override
    @SneakyThrows
    public XMLGregorianCalendar deserialize(JsonParser p, DeserializationContext ctxt) {
        Date date = DateTimeUtc.parseDate(p.getString().trim());
        if (date == null) {
            return null;
        }
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
    }
}
