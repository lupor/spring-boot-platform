/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.web;

import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.util.FileCopyUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ApimsContentCachingRequestWrapper extends HttpServletRequestWrapper {

    private byte[] content = null;

    public ApimsContentCachingRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public byte[] getBody() throws IOException {
        if (content == null) {
            content = FileCopyUtils.copyToByteArray(super.getInputStream());
        }
        return content;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new ApimsContentCachingInputStream(getBody());
    }

    private static class ApimsContentCachingInputStream extends ServletInputStream {

        private final InputStream delegate;

        public ApimsContentCachingInputStream(byte[] body) {
            this.delegate = new ByteArrayInputStream(body);
        }

        @Override
        @ApimsReportGeneratedHint
        public boolean isFinished() {
            return false;
        }

        @Override
        @ApimsReportGeneratedHint
        public boolean isReady() {
            return true;
        }

        @Override
        @ApimsReportGeneratedHint
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException();
        }

        @Override
        @ApimsReportGeneratedHint
        public int read() throws IOException {
            return this.delegate.read();
        }

        @Override
        @ApimsReportGeneratedHint
        public int read(byte[] b, int off, int len) throws IOException {
            return this.delegate.read(b, off, len);
        }

        @Override
        @ApimsReportGeneratedHint
        public int read(byte[] b) throws IOException {
            return this.delegate.read(b);
        }

        @Override
        @ApimsReportGeneratedHint
        public long skip(long n) throws IOException {
            return this.delegate.skip(n);
        }

        @Override
        @ApimsReportGeneratedHint
        public int available() throws IOException {
            return this.delegate.available();
        }

        @Override
        @ApimsReportGeneratedHint
        public void close() throws IOException {
            this.delegate.close();
        }

        @Override
        @ApimsReportGeneratedHint
        public synchronized void mark(int readlimit) {
            this.delegate.mark(readlimit);
        }

        @Override
        @ApimsReportGeneratedHint
        public synchronized void reset() throws IOException {
            this.delegate.reset();
        }

        @Override
        @ApimsReportGeneratedHint
        public boolean markSupported() {
            return this.delegate.markSupported();
        }
    }
}
