/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.http.converter;

import org.springframework.core.OrderComparator;
import org.springframework.http.converter.HttpMessageConverter;

import java.util.List;

public class ApimsHttpMessageConvertersConfigurer {

    public void configureRestTemplateConverters(
            List<HttpMessageConverter<?>> converters, List<? extends ApimsHttpMessageConverter<?>> apimsConverters) {
        configureConverters(converters, apimsConverters, true, false);
    }

    @SuppressWarnings({"java:S3958", "java:S1612"})
    public void configureWebConverters(List<HttpMessageConverter<?>> converters) {
        List<? extends ApimsHttpMessageConverter<?>> apimsConverters = converters.stream()
                .filter(ApimsHttpMessageConverter.class::isInstance)
                .map(c -> (ApimsHttpMessageConverter<?>) c)
                .toList();
        configureConverters(converters, apimsConverters, false, true);
    }

    @SuppressWarnings({"java:S3958", "java:S1612"})
    protected void configureConverters(
            List<HttpMessageConverter<?>> converters,
            List<? extends ApimsHttpMessageConverter<?>> apimsConverters,
            boolean restEnabled,
            boolean webEnabled) {

        converters.removeIf(ApimsHttpMessageConverter.class::isInstance);

        for (ApimsHttpMessageConverter<?> apimsHttpMessageConverter : apimsConverters) {
            if (apimsHttpMessageConverter.isRestEnabled() == restEnabled
                    || apimsHttpMessageConverter.isWebEnabled() == webEnabled) {
                Class<? extends HttpMessageConverter<?>> replacmentClass =
                        apimsHttpMessageConverter.getReplacementConverterClass();
                if (replacmentClass != null) {
                    converters.removeIf(c -> c.getClass().equals(replacmentClass));
                }
                converters.removeIf(c -> c.getClass().equals(apimsHttpMessageConverter.getClass()));
                converters.add(apimsHttpMessageConverter);
            }
        }
        converters.sort(new OrderComparator());
    }
}
