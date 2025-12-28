/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.sky.newcrm.apims.spring.serialization.core.mapper.module.ApimsDateTimeUtcModule;
import de.sky.newcrm.apims.spring.serialization.core.mapper.module.ApimsModelEnumModule;
import java.text.SimpleDateFormat;

@SuppressWarnings({"java:S1610"})
public abstract class DefaultJacksonObjectFactory {

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    protected DefaultJacksonObjectFactory() {}

    public static PrettyPrinter createPrettyPrinterWithLinuxEol() {
        DefaultIndenter indenter = new DefaultIndenter("  ", "\n");
        return new DefaultPrettyPrinter().withObjectIndenter(indenter);
    }

    public static ObjectMapper createDefaultJsonObjectMapper() {
        ObjectMapper objectMapper = buildJacksonObjectMapperBuilder().build();
        objectMapper.setDefaultPrettyPrinter(createPrettyPrinterWithLinuxEol());
        return objectMapper;
    }

    public static ObjectMapper createDefaultXmlObjectMapper() {
        return buildJacksonObjectXmlMapperBuilder().build();
    }

    public static JacksonObjectMapperBuilder buildJacksonObjectMapperBuilder() {
        return buildJacksonObjectMapperBuilder(false);
    }

    public static JacksonObjectMapperBuilder buildJacksonObjectXmlMapperBuilder() {
        return buildJacksonObjectMapperBuilder(true);
    }

    public static JacksonObjectMapperBuilder buildJacksonObjectMapperBuilder(boolean createXmlMapper) {
        return new JacksonObjectMapperBuilder()
                .createXmlMapper(createXmlMapper)
                .indentOutput(true)
                .dateFormat(new SimpleDateFormat(DATE_TIME_PATTERN))
                .modulesToInstall(new ApimsDateTimeUtcModule(), new ApimsModelEnumModule())
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .featuresToEnable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .featuresToEnable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE)
                .featuresToEnable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    }
}
