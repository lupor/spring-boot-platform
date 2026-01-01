/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.rest;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

@SuppressWarnings({"java:S6201"})
public class ApimsRestClientHttpRequestHeaderInterceptor implements ApimsRestClientHttpRequestInterceptor {

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 10;
    }

    private final Map<String, String> headers = new TreeMap<>();

    public ApimsRestClientHttpRequestHeaderInterceptor(
            Map<String, String> headers, Map<String, String> additonalHeaders) {
        this.headers.putAll(headers);
        this.headers.putAll(additonalHeaders);
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {

        writeHeaderMap(request);
        return execution.execute(request, body);
    }

    protected void writeHeaderMap(HttpRequest request) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            request.getHeaders().set(entry.getKey(), entry.getValue());
        }
    }
}
