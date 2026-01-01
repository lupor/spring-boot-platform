/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.web;

import de.sky.newcrm.apims.spring.core.support.report.ApimsReportGeneratedHint;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ApimsContentCachingResponseWrapper extends HttpServletResponseWrapper {

    private ApimsContentCachingOutputStream apimsContentCachingOutputStream = null;

    public ApimsContentCachingResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (apimsContentCachingOutputStream == null) {
            apimsContentCachingOutputStream = new ApimsContentCachingOutputStream(super.getOutputStream());
        }
        return apimsContentCachingOutputStream;
    }

    public byte[] getBody() {
        return apimsContentCachingOutputStream == null ? new byte[0] : apimsContentCachingOutputStream.getBody();
    }

    private static class ApimsContentCachingOutputStream extends ServletOutputStream {

        private final ServletOutputStream originalServletOutputStream;
        private final ByteArrayOutputStream byteArrayOutputStream;

        public ApimsContentCachingOutputStream(ServletOutputStream originalServletOutputStream) {
            this.originalServletOutputStream = originalServletOutputStream;
            this.byteArrayOutputStream = new ByteArrayOutputStream();
        }

        @Override
        @ApimsReportGeneratedHint
        public boolean isReady() {
            return originalServletOutputStream.isReady();
        }

        @Override
        @ApimsReportGeneratedHint
        public void setWriteListener(WriteListener listener) {
            originalServletOutputStream.setWriteListener(listener);
        }

        @Override
        @ApimsReportGeneratedHint
        public void write(int b) throws IOException {
            originalServletOutputStream.write(b);
            byteArrayOutputStream.write(b);
        }

        @ApimsReportGeneratedHint
        public byte[] getBody() {
            return byteArrayOutputStream.toByteArray();
        }
    }
}
