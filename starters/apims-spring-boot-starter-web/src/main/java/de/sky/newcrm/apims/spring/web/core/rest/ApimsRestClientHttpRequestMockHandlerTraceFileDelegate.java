/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.rest;

import com.veracode.annotation.FilePathCleanser;

import de.sky.newcrm.apims.spring.utils.FunctionUtils;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

// TODO: Testing related
public abstract class ApimsRestClientHttpRequestMockHandlerTraceFileDelegate
        extends ApimsRestClientHttpRequestMockHandler {

    private final ApimsRestTraceFileHandler traceFileHandler;

    protected ApimsRestClientHttpRequestMockHandlerTraceFileDelegate() {
        traceFileHandler = new ApimsRestTraceFileHandler(false, new ApimsRestTraceFileStorage(), false);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        loadResourceTraceFiles(getResourceTraceFiles());
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body) throws IOException {
        return traceFileHandler.loadMockTraceFile(request, body);
    }

    protected abstract String[] getResourceTraceFiles();

    @FilePathCleanser
    protected void loadResourceTraceFiles(final String[] resourceFiles) {
        FunctionUtils.executeIfNotNull(resourceFiles, () -> {
            final Path[] files = Arrays.stream(resourceFiles)
                    .map(s -> new File(s).toPath())
                    .toList()
                    .toArray(new Path[0]);
            traceFileHandler.getStorage().addMockFiles(ApimsRestTraceFile.class, files);
        });
    }
}
