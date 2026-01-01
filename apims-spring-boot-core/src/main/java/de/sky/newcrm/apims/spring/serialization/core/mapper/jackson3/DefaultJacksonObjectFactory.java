/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3.module.ApimsDateTimeUtcModule;
import de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3.module.ApimsModelEnumModule;
import tools.jackson.core.util.DefaultIndenter;
import tools.jackson.core.util.DefaultPrettyPrinter;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.text.SimpleDateFormat;

@SuppressWarnings({"java:S1610"})
public abstract class DefaultJacksonObjectFactory {

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    protected DefaultJacksonObjectFactory() {
    }

    public static DefaultPrettyPrinter createDefaultJsonPrettyPrinter() {
        DefaultIndenter unixIndenter = new DefaultIndenter("  ", "\n");
        DefaultPrettyPrinter pp = new DefaultPrettyPrinter();
        pp.indentArraysWith(unixIndenter);
        pp.indentObjectsWith(unixIndenter);
        return pp;
    }

    public static JsonMapper createDefaultJsonObjectMapper() {
        return createDefaultJsonMapperBuilder()
                .build();
    }

    public static JsonMapper.Builder createDefaultJsonMapperBuilder() {
        return JsonMapper.builder()
                .defaultPrettyPrinter(createDefaultJsonPrettyPrinter())
                .enable(SerializationFeature.INDENT_OUTPUT)
                .addModule(new ApimsDateTimeUtcModule())
                .addModule(new ApimsModelEnumModule())// Fea
                .defaultDateFormat(new SimpleDateFormat(DATE_TIME_PATTERN))
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE)
                .disable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    }
}
