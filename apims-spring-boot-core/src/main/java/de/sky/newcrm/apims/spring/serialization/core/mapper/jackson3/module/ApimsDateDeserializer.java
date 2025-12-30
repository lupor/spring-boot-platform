/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3.module;


import de.sky.newcrm.apims.spring.utils.DateTimeUtc;
import org.springframework.util.StringUtils;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

import java.util.Date;

@SuppressWarnings({"java:S110"})
public class ApimsDateDeserializer extends ValueDeserializer<Date> {

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            String value = p.getString().trim();
            return DateTimeUtc.parseDate(value);
        }
        if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            return new Date(p.getLongValue());
        }

        return (Date) ctxt.handleUnexpectedToken(Date.class, p);
    }
}
