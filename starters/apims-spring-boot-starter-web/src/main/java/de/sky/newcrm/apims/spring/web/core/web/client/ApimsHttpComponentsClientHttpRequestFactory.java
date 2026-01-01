/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.web.client;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

@SuppressWarnings("all")
public class ApimsHttpComponentsClientHttpRequestFactory extends HttpComponentsClientHttpRequestFactory {

    /**
     * Create a new instance of the {@code HttpComponentsClientHttpRequestFactory}
     * with the given {@link CloseableHttpClient} instance.
     *
     * @param httpClient the HttpClient instance to use for this request factory
     */
    public ApimsHttpComponentsClientHttpRequestFactory(CloseableHttpClient httpClient) {
        super(httpClient);
    }
}
