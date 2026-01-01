/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.rest;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

@SuppressWarnings({"java:S6201"})
public class ApimsRestClientHttpRequestContentInterceptor implements ApimsRestClientHttpRequestInterceptor {

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE - 50;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        ClientHttpResponse response = execution.execute(request, body);
        // force read body. spring ignores the response body on http status >= 400
        return response instanceof ApimsWrappedClientHttpResponse awchr
                ? awchr
                : new ApimsWrappedClientHttpResponse(response);
    }
}
