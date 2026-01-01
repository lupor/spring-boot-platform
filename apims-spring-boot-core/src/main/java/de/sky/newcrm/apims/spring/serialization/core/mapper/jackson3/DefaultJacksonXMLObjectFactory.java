/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3;

import static tools.jackson.databind.SerializationFeature.INDENT_OUTPUT;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3.module.ApimsDateTimeUtcModule;
import de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3.module.ApimsModelEnumModule;
import java.text.SimpleDateFormat;
import tools.jackson.core.util.DefaultIndenter;
import tools.jackson.core.util.DefaultPrettyPrinter;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.dataformat.xml.XmlMapper;

@SuppressWarnings({"java:S1610"})
public abstract class DefaultJacksonXMLObjectFactory {

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    protected DefaultJacksonXMLObjectFactory() {}

    public static DefaultPrettyPrinter createDefaultXMLPrettyPrinter() {
        DefaultIndenter unixIndenter = new DefaultIndenter("  ", "\n");
        DefaultPrettyPrinter pp = new DefaultPrettyPrinter();
        pp.indentArraysWith(unixIndenter);
        pp.indentObjectsWith(unixIndenter);
        return pp;
    }

    public static XmlMapper createDefaultXmlObjectMapper() {
        return customizeXmlMapperBuilder(XmlMapper.builder()).build();
    }

    public static XmlMapper.Builder customizeXmlMapperBuilder(XmlMapper.Builder builder) {
        return builder.defaultPrettyPrinter(createDefaultXMLPrettyPrinter())
                .enable(INDENT_OUTPUT)
                .defaultDateFormat(new SimpleDateFormat(DATE_TIME_PATTERN))
                // Modules
                .addModule(new ApimsDateTimeUtcModule())
                .addModule(new ApimsModelEnumModule()) // Features to Disable
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .enable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE)
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL));
    }
}
