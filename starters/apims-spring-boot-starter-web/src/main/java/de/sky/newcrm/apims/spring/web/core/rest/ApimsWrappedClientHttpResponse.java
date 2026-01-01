/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class ApimsWrappedClientHttpResponse implements ClientHttpResponse {

    private final int rawStatusCode;
    private final String statusText;
    private final HttpHeaders headers;
    private final byte[] responseBody;

    public ApimsWrappedClientHttpResponse(ClientHttpResponse clientHttpResponse) throws IOException {
        rawStatusCode = clientHttpResponse.getStatusCode().value();
        statusText = clientHttpResponse.getStatusText();
        responseBody = StreamUtils.copyToByteArray(clientHttpResponse.getBody());
        headers = new HttpHeaders();
        for (Map.Entry<String, List<String>> entry :
                clientHttpResponse.getHeaders().headerSet()) {
            for (String value : entry.getValue()) {
                headers.add(entry.getKey(), value);
            }
        }
        clientHttpResponse.close();
    }

    public byte[] getResponseBody() {
        return responseBody;
    }

    @Override
    public HttpStatus getStatusCode() throws IOException {
        return HttpStatus.valueOf(rawStatusCode);
    }

    @Override
    public String getStatusText() throws IOException {
        return statusText;
    }

    @Override
    public void close() {
        // nothing to do
    }

    @Override
    public InputStream getBody() throws IOException {
        return responseBody == null ? InputStream.nullInputStream() : new ByteArrayInputStream(responseBody);
    }

    @Override
    public HttpHeaders getHeaders() {
        return headers;
    }
}
