/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core.mapper.legacy.module;

import com.fasterxml.jackson.databind.module.SimpleModule;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.Date;

public class ApimsDateTimeUtcModule extends SimpleModule {

    public ApimsDateTimeUtcModule() {
        registerSerializer();
        registerDeserializer();
    }

    protected void registerSerializer() {
        addSerializer(Date.class, new ApimsDateSerializer());
        addSerializer(LocalDate.class, new ApimsLocalDateSerializer());
        addSerializer(OffsetDateTime.class, new ApimsOffsetDateTimeSerializer());
        addSerializer(Calendar.class, new ApimsCalendarSerializer());
        addSerializer(XMLGregorianCalendar.class, new ApimsXMLGregorianCalendarSerializer());
    }

    protected void registerDeserializer() {
        addDeserializer(Date.class, new ApimsDateDeserializer());
        addDeserializer(LocalDate.class, new ApimsLocalDateDeserializer());
        addDeserializer(OffsetDateTime.class, new ApimsOffsetDateTimeDeserializer());
        addDeserializer(Calendar.class, new ApimsCalendarDeserializer());
        addDeserializer(XMLGregorianCalendar.class, new ApimsXMLGregorianCalendarDeserializer());
    }
}
