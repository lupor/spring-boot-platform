/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3.module;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import de.sky.newcrm.apims.spring.utils.DateTimeUtc;

import java.io.IOException;
import java.time.OffsetDateTime;

public class ApimsOffsetDateTimeDeserializer extends ValueDeserializer<OffsetDateTime> {

    @Override
    public OffsetDateTime deserialize(JsonParser p, DeserializationContext ctxt) {
        return DateTimeUtc.parseOffsetDateTime(p.getString().trim());
    }
}
