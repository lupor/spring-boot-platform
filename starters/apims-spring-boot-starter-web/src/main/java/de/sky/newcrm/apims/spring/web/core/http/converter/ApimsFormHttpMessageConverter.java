/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.http.converter;

import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.MultiValueMap;

public class ApimsFormHttpMessageConverter extends FormHttpMessageConverter
        implements ApimsHttpMessageConverter<MultiValueMap<String, ?>> {

    private final boolean restEnabled;
    private final boolean webEnabled;

    public ApimsFormHttpMessageConverter(boolean restEnabled, boolean webEnabled) {
        this.restEnabled = restEnabled;
        this.webEnabled = webEnabled;
    }

    @Override
    public boolean isWebEnabled() {
        return webEnabled;
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE - 30;
    }

    @Override
    public boolean isRestEnabled() {
        return restEnabled;
    }

    @Override
    public Class<? extends HttpMessageConverter<MultiValueMap<String, ?>>> getReplacementConverterClass() {
        return FormHttpMessageConverter.class;
    }
}
