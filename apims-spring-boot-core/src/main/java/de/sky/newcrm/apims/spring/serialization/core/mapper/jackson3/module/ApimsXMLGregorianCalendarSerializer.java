/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3.module;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;
import de.sky.newcrm.apims.spring.utils.DateTimeUtc;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;

@SuppressWarnings({"java:S110"})
public class ApimsXMLGregorianCalendarSerializer extends ValueSerializer<XMLGregorianCalendar> {

    @Override
    public void serialize(XMLGregorianCalendar value, JsonGenerator gen, SerializationContext serializers) {
        gen.writeString(DateTimeUtc.format(value.toGregorianCalendar().getTime(), DateTimeUtc.ISO.DATE_TIME_COMPLETE));
    }
}
