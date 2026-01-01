/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.util.DefaultXmlPrettyPrinter;
import de.sky.newcrm.apims.spring.serialization.core.mapper.legacy.module.ApimsDateTimeUtcModule;
import de.sky.newcrm.apims.spring.serialization.core.mapper.legacy.module.ApimsModelEnumModule;

import java.text.SimpleDateFormat;

import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;

@SuppressWarnings({"java:S1610"})
public abstract class DefaultJacksonXMLObjectFactory {

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    protected DefaultJacksonXMLObjectFactory() {
    }

    public static DefaultXmlPrettyPrinter createDefaultXMLPrettyPrinter() {
        DefaultXmlPrettyPrinter printer = new DefaultXmlPrettyPrinter();
        printer.withCustomNewLine("\n");
        return new DefaultXmlPrettyPrinter();
    }

    public static XmlMapper createDefaultXmlObjectMapper() {
        return XmlMapper.builder()
                .defaultPrettyPrinter(createDefaultXMLPrettyPrinter())
                .enable(INDENT_OUTPUT)
                .defaultDateFormat(new SimpleDateFormat(DATE_TIME_PATTERN))
                // Modules
                .addModule(new ApimsDateTimeUtcModule())
                .addModule(new ApimsModelEnumModule())// Features to Disable
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .enable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE)
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .defaultPropertyInclusion(JsonInclude.Value.construct(
                        JsonInclude.Include.NON_NULL,
                        JsonInclude.Include.USE_DEFAULTS))
                .build();
    }
}
