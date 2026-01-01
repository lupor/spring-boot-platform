/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.http.converter;

import org.springframework.core.Ordered;
import org.springframework.http.converter.HttpMessageConverter;

public interface ApimsHttpMessageConverter<T> extends HttpMessageConverter<T>, Ordered {

    Class<? extends HttpMessageConverter<T>> getReplacementConverterClass();

    boolean isRestEnabled();

    boolean isWebEnabled();
}
