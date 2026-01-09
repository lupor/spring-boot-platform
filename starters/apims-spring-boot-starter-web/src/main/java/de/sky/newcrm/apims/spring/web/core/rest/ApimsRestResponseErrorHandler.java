/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.rest;

import de.sky.newcrm.apims.spring.web.core.exceptions.ApimsRestClientBusinessExceptionHandler;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings({"java:S6212"})
public class ApimsRestResponseErrorHandler extends DefaultResponseErrorHandler {

    private ApimsRestClientBusinessExceptionHandler businessExceptionHandler;
    private final Set<Integer> checkBusinessExceptionCodes =
            new HashSet<>(List.of(HttpStatus.BAD_REQUEST.value(), HttpStatus.UNPROCESSABLE_ENTITY.value()));

    public ApimsRestResponseErrorHandler() {
        this(new ApimsRestClientBusinessExceptionHandler());
    }

    public ApimsRestResponseErrorHandler(ApimsRestClientBusinessExceptionHandler businessExceptionHandler) {
        this.businessExceptionHandler = businessExceptionHandler;
    }

    @Override
    @SuppressWarnings("java:S6201")
    protected void handleError(
            ClientHttpResponse response, HttpStatusCode statusCode, @Nullable URI url, @Nullable HttpMethod method)
            throws IOException {

        if (!checkBusinessExceptionCodes.contains(statusCode.value())) {
            super.handleError(response, statusCode, url, method);
            return;
        }
        Charset charset = getCharset(response);
        String body = new String(getResponseBody(response), charset == null ? StandardCharsets.UTF_8 : charset);
        Exception e = businessExceptionHandler.parseException(body);
        if (!(e instanceof RuntimeException)) {
            super.handleError(response, statusCode, url, method);
        } else {
            throw (RuntimeException) e;
        }
    }
}
