/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.http.converter;

import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractJacksonHttpMessageConverter;
import org.springframework.http.converter.AbstractSmartHttpMessageConverter;
import tools.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

// TODO: FIXME: Migrate to jackson 3 with AbstractJacksonHttpMessageConverter
@SuppressWarnings({"java:S1192"})
public class ApimsMappingJackson2HttpMessageConverter extends AbstractJacksonHttpMessageConverter<tools.jackson.databind.ObjectMapper>
        implements ApimsHttpMessageConverter<Object> {

    private final boolean restEnabled;
    private final boolean webEnabled;

    public ApimsMappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        this(objectMapper, true, true);
    }

    public ApimsMappingJackson2HttpMessageConverter(
            ObjectMapper objectMapper, boolean restEnabled, boolean webEnabled) {
        this(objectMapper, restEnabled, webEnabled, true);
    }

    public ApimsMappingJackson2HttpMessageConverter(
            ObjectMapper objectMapper, boolean restEnabled, boolean webEnabled, boolean prettyPrint) {
        super(objectMapper);
        this.restEnabled = restEnabled;
        this.webEnabled = webEnabled;
        setSupportedMediaTypes(getDefaultSupportedMediaTypes());
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

    protected List<MediaType> getDefaultSupportedMediaTypes() {
        return Arrays.asList(
                MediaType.APPLICATION_JSON,
                new MediaType("application", "json"),
                new MediaType("application", "*+json"),
                new MediaType("application", "octetstream"),
                new MediaType("text", "json"));
    }
}
