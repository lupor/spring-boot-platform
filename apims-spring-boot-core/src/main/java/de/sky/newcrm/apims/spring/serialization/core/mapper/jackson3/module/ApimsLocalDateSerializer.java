/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3.module;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;
import de.sky.newcrm.apims.spring.utils.DateTimeUtc;

import java.io.IOException;
import java.time.LocalDate;

@SuppressWarnings({"java:S110"})
public class ApimsLocalDateSerializer extends ValueSerializer<LocalDate> {

    @Override
    public void serialize(LocalDate value, JsonGenerator gen, SerializationContext serializers) {
        gen.writeString(DateTimeUtc.format(value));
    }
}
