/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.http.converter;

import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractJacksonHttpMessageConverter;
import org.springframework.http.converter.AbstractSmartHttpMessageConverter;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;


@SuppressWarnings({"java:S1192"})
public class ApimsMappingJackson2XmlHttpMessageConverter extends AbstractJacksonHttpMessageConverter<ObjectMapper>
        implements ApimsHttpMessageConverter<Object> {

    private final boolean restEnabled;
    private final boolean webEnabled;

    public ApimsMappingJackson2XmlHttpMessageConverter(
            ObjectMapper objectMapper, boolean restEnabled, boolean webEnabled) {
        this(objectMapper, restEnabled, webEnabled, true);
    }

    public ApimsMappingJackson2XmlHttpMessageConverter(
            ObjectMapper objectMapper, boolean restEnabled, boolean webEnabled, boolean prettyPrint) {
        super(prettyPrint ? objectMapper.rebuild().enable(SerializationFeature.INDENT_OUTPUT).build() : objectMapper,
                MediaType.APPLICATION_XML, MediaType.TEXT_XML, new MediaType("application", "*+xml"));
        this.restEnabled = restEnabled;
        this.webEnabled = webEnabled;
    }

    @Override
    public boolean isRestEnabled() {
        return restEnabled;
    }

    @Override
    public boolean isWebEnabled() {
        return webEnabled;
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Class<? extends AbstractSmartHttpMessageConverter<Object>> getReplacementConverterClass() {
        return (Class) AbstractJacksonHttpMessageConverter.class;
    }
}
