/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core.mapper.legacy.module;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import de.sky.newcrm.apims.spring.utils.DateTimeUtc;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Date;

@SuppressWarnings({"java:S110"})
public class ApimsDateDeserializer extends DateDeserializers.DateDeserializer {

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            String value = p.getText().trim();
            if (StringUtils.hasLength(value) && (value.contains("-") || value.contains(":"))) {
                try {
                    return DateTimeUtc.parseDate(value);
                } catch (Exception ignore) {
                    // ignore
                }
            }
        }
        return super.deserialize(p, ctxt);
    }
}
