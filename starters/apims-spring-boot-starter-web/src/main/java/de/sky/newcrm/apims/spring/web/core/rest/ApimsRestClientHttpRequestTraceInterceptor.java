/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;


// TODO: Testing related
@Slf4j
@SuppressWarnings({"java:S1075", "java:S6201", "java:S2095", "java:S6212"})
public class ApimsRestClientHttpRequestTraceInterceptor implements ApimsRestClientHttpRequestInterceptor {

    private final boolean includePayload;
    private final ApimsRestTraceFileHandler apimsTraceFileHandler;

    public ApimsRestClientHttpRequestTraceInterceptor(
            boolean includePayload, ApimsRestTraceFileHandler apimsTraceFileHandler) {
        this.includePayload = includePayload;
        this.apimsTraceFileHandler = apimsTraceFileHandler;
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE - 40;
    }

    @Override
    @ApimsReportGeneratedHint
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        if (!log.isTraceEnabled() && !getTraceFileHandler().isRecordTraceFilesEnabled()) {
            return execution.execute(request, body);
        }
        String id = UUID.randomUUID().toString();
        ApimsRestTraceFile mockFile = null;
        try {
            mockFile = apimsTraceFileHandler.startRecording(request, body);
            beforeExecution(id, request, body);
        } catch (Exception e) {
            log.warn("beforeExecution failed: {}", e.getMessage());
        }
        ClientHttpResponse response = execution.execute(request, body);

        ApimsWrappedClientHttpResponse wrappedClientHttpResponse =
                response instanceof ApimsWrappedClientHttpResponse awchr ? awchr : null;
        if (wrappedClientHttpResponse == null) {
            wrappedClientHttpResponse =
                    includePayload || mockFile != null ? new ApimsWrappedClientHttpResponse(response) : null;
        }
        try {
            apimsTraceFileHandler.endRecording(mockFile, wrappedClientHttpResponse);
            afterExecution(id, wrappedClientHttpResponse == null ? response : wrappedClientHttpResponse);

        } catch (Exception e) {
            log.warn("afterExecution failed: {}", e.getMessage());
        }

        return wrappedClientHttpResponse == null ? response : wrappedClientHttpResponse;
    }

    @SuppressWarnings({"java:S4042"})
    @ApimsReportGeneratedHint
    public void beforeExecution(String id, HttpRequest request, byte[] body) {
        if (!log.isTraceEnabled()) {
            return;
        }
        StringBuilder logMessage = new StringBuilder();
        logMessage
                .append("\n--- REQUEST START | ")
                .append(id)
                .append(" | ")
                .append(LocalDateTime.now())
                .append("\n")
                .append(request.getMethod())
                .append(" ")
                .append(request.getURI())
                .append("\n");
        appendHeaders(logMessage, request.getHeaders()).append("\n\n");
        if (includePayload && body != null && body.length != 0) {
            logMessage.append(new String(body));
        }
        log.trace(logMessage.toString());
    }

    @SuppressWarnings({"java:S6201"})
    @ApimsReportGeneratedHint
    protected void afterExecution(String id, ClientHttpResponse response) throws IOException {
        if (!log.isTraceEnabled()) {
            return;
        }
        StringBuilder logMessage = new StringBuilder();
        logMessage
                .append("\n--- RESPONSE START | ")
                .append(id)
                .append(" | ")
                .append(LocalDateTime.now())
                .append("\n")
                .append(response.getStatusCode())
                .append("\n");
        appendHeaders(logMessage, response.getHeaders()).append("\n\n");
        if (includePayload && response instanceof ApimsWrappedClientHttpResponse httpResponse) {
            byte[] body = httpResponse.getResponseBody();
            logMessage.append(new String(body));
        }
        log.trace(logMessage.toString());
    }

    protected ApimsRestTraceFileHandler getTraceFileHandler() {
        return apimsTraceFileHandler;
    }

    @ApimsReportGeneratedHint
    protected StringBuilder appendHeaders(StringBuilder sb, HttpHeaders httpHeaders) {
        for (Map.Entry<String, List<String>> entry : httpHeaders.entrySet()) {
            sb.append(entry.getKey())
                    .append(": ")
                    .append(StringUtils.collectionToCommaDelimitedString(entry.getValue()))
                    .append("\n");
        }
        return sb;
    }
}
