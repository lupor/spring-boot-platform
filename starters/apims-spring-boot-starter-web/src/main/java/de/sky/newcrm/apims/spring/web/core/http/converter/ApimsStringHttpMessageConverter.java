/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.http.converter;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ApimsStringHttpMessageConverter extends StringHttpMessageConverter
        implements ApimsHttpMessageConverter<String> {

    private final boolean restEnabled;
    private final boolean webEnabled;

    public ApimsStringHttpMessageConverter(boolean restEnabled, boolean webEnabled) {
        this(StandardCharsets.UTF_8, restEnabled, webEnabled);
    }

    protected ApimsStringHttpMessageConverter(Charset defaultCharset, boolean restEnabled, boolean webEnabled) {
        super(defaultCharset);
        this.restEnabled = restEnabled;
        this.webEnabled = webEnabled;
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
    public int getOrder() {
        return LOWEST_PRECEDENCE - 40;
    }

    @Override
    public Class<? extends HttpMessageConverter<String>> getReplacementConverterClass() {
        return StringHttpMessageConverter.class;
    }
}
