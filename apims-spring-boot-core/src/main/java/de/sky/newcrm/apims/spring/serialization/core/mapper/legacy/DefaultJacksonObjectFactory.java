/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core.mapper.legacy;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.sky.newcrm.apims.spring.serialization.core.mapper.legacy.module.ApimsDateTimeUtcModule;
import de.sky.newcrm.apims.spring.serialization.core.mapper.legacy.module.ApimsModelEnumModule;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.text.SimpleDateFormat;

@SuppressWarnings({"java:S1610"})
@Deprecated
public abstract class DefaultJacksonObjectFactory {

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    protected DefaultJacksonObjectFactory() {}

    @Deprecated
    public static PrettyPrinter createPrettyPrinterWithLinuxEol() {
        DefaultIndenter indenter = new DefaultIndenter("  ", "\n");
        return new DefaultPrettyPrinter().withObjectIndenter(indenter);
    }

    @Deprecated
    public static ObjectMapper createDefaultJsonObjectMapper() {
        ObjectMapper objectMapper = buildJackson2ObjectMapperBuilder().build();
        objectMapper.setDefaultPrettyPrinter(createPrettyPrinterWithLinuxEol());
        return objectMapper;
    }

    @Deprecated
    public static ObjectMapper createDefaultXmlObjectMapper() {
        return buildJackson2ObjectXmlMapperBuilder().build();
    }

    @Deprecated
    public static Jackson2ObjectMapperBuilder buildJackson2ObjectMapperBuilder() {
        return buildJackson2ObjectMapperBuilder(false);
    }

    @Deprecated
    public static Jackson2ObjectMapperBuilder buildJackson2ObjectXmlMapperBuilder() {
        return buildJackson2ObjectMapperBuilder(true);
    }

    @Deprecated
    public static Jackson2ObjectMapperBuilder buildJackson2ObjectMapperBuilder(boolean createXmlMapper) {
        return new Jackson2ObjectMapperBuilder()
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
