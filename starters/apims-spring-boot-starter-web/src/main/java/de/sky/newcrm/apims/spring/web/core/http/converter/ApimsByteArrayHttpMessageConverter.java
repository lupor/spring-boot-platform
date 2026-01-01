/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.http.converter;

import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;

public class ApimsByteArrayHttpMessageConverter extends ByteArrayHttpMessageConverter
        implements ApimsHttpMessageConverter<byte[]> {

    private final boolean restEnabled;
    private final boolean webEnabled;

    public ApimsByteArrayHttpMessageConverter(boolean restEnabled, boolean webEnabled) {
        this.restEnabled = restEnabled;
        this.webEnabled = webEnabled;
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE - 50;
    }

    @Override
    public boolean isWebEnabled() {
        return webEnabled;
    }

    @Override
    public boolean isRestEnabled() {
        return restEnabled;
    }

    @Override
    public Class<? extends HttpMessageConverter<byte[]>> getReplacementConverterClass() {
        return ByteArrayHttpMessageConverter.class;
    }
}
