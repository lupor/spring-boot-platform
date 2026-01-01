/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.web;

import de.sky.newcrm.apims.spring.core.mdc.ApimsMdc;
import de.sky.newcrm.apims.spring.core.support.exception.ApimsRuntimeException;
import de.sky.newcrm.apims.spring.core.support.report.ApimsReportGeneratedHint;
import de.sky.newcrm.apims.spring.core.utils.FunctionUtils;
import de.sky.newcrm.apims.spring.core.utils.VeracodeMitigationUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;

@Slf4j
@SuppressWarnings({"java:S6212", "java:S6813"})
public class ApimsRequestLoggingFilter extends OncePerRequestFilter {

    private static final Set<String> excludes = Set.of("/health");

    @Autowired(required = false)
    private ApimsMdc mdc;

    /**
     * The default value prepended to the log message written <i>before</i> a request is
     * processed.
     */
    public static final String DEFAULT_BEFORE_MESSAGE_PREFIX = "Before request [";

    /**
     * The default value appended to the log message written <i>before</i> a request is
     * processed.
     */
    public static final String DEFAULT_BEFORE_MESSAGE_SUFFIX = "]";

    /**
     * The default value prepended to the log message written <i>after</i> a request is
     * processed.
     */
    public static final String DEFAULT_AFTER_MESSAGE_PREFIX = "After request [";

    /**
     * The default value appended to the log message written <i>after</i> a request is
     * processed.
     */
    public static final String DEFAULT_AFTER_MESSAGE_SUFFIX = "]";

    private static final int DEFAULT_MAX_PAYLOAD_LENGTH = 1024;

    private boolean includeQueryString = false;

    private boolean includeClientInfo = false;

    private boolean includeSkyHeaders = true;

    private boolean includeHeaders = false;

    private boolean includePayload = false;

    private boolean includeResponseStatus = true;

    String[] headerPredicateHeaders = new String[] {"x-sky", "x-b3"};

    private Predicate<String> headerPredicate = createHeaderPredicate();

    private int maxPayloadLength = DEFAULT_MAX_PAYLOAD_LENGTH;

    private String beforeMessagePrefix = DEFAULT_BEFORE_MESSAGE_PREFIX;

    private String beforeMessageSuffix = DEFAULT_BEFORE_MESSAGE_SUFFIX;

    private String afterMessagePrefix = DEFAULT_AFTER_MESSAGE_PREFIX;

    private String afterMessageSuffix = DEFAULT_AFTER_MESSAGE_SUFFIX;

    /**
     * Set whether the query string should be included in the log message.
     * <p>Should be configured using an {@code <init-param>} for parameter name
     * "includeQueryString" in the filter definition in {@code web.xml}.
     */
    public void setIncludeQueryString(boolean includeQueryString) {
        this.includeQueryString = includeQueryString;
    }

    /**
     * Return whether the query string should be included in the log message.
     */
    protected boolean isIncludeQueryString() {
        return this.includeQueryString;
    }

    /**
     * Set whether the client address and session id should be included in the
     * log message.
     * <p>Should be configured using an {@code <init-param>} for parameter name
     * "includeClientInfo" in the filter definition in {@code web.xml}.
     */
    public void setIncludeClientInfo(boolean includeClientInfo) {
        this.includeClientInfo = includeClientInfo;
    }

    /**
     * Return whether the client address and session id should be included in the
     * log message.
     */
    protected boolean isIncludeClientInfo() {
        return this.includeClientInfo;
    }

    /**
     * Set whether the request headers should be included in the log message.
     * <p>Should be configured using an {@code <init-param>} for parameter name
     * "includeHeaders" in the filter definition in {@code web.xml}.
     *
     * @since 4.3
     */
    public void setIncludeHeaders(boolean includeHeaders) {
        this.includeHeaders = includeHeaders;
    }

    /**
     * Return whether the request headers should be included in the log message.
     *
     * @since 4.3
     */
    protected boolean isIncludeHeaders() {
        return this.includeHeaders;
    }

    /**
     * Return whether the sky request headers should be included in the log message.
     */
    public boolean isIncludeSkyHeaders() {
        return includeSkyHeaders;
    }

    /**
     * Set whether the sky request headers should be included in the log message.
     */
    public void setIncludeSkyHeaders(boolean includeSkyHeaders) {
        this.includeSkyHeaders = includeSkyHeaders;
    }

    /**
     * Set whether the request payload (body) should be included in the log message.
     * <p>Should be configured using an {@code <init-param>} for parameter name
     * "includePayload" in the filter definition in {@code web.xml}.
     *
     * @since 3.0
     */
    public void setIncludePayload(boolean includePayload) {
        this.includePayload = includePayload;
    }

    /**
     * Return whether the request payload (body) should be included in the log message.
     *
     * @since 3.0
     */
    protected boolean isIncludePayload() {
        return this.includePayload;
    }

    public boolean isIncludeResponseStatus() {
        return includeResponseStatus;
    }

    public void setIncludeResponseStatus(boolean includeResponseStatus) {
        this.includeResponseStatus = includeResponseStatus;
    }

    public void setHeaderPredicateHeaders(String[] headerPredicateHeaders) {
        this.headerPredicateHeaders = headerPredicateHeaders;
    }

    /**
     * Configure a predicate for selecting which headers should be logged if
     * {@link #setIncludeHeaders(boolean)} is set to {@code true}.
     * <p>By default this is not set in which case all headers are logged.
     *
     * @param headerPredicate the predicate to use
     * @since 5.2
     */
    public void setHeaderPredicate(Predicate<String> headerPredicate) {
        this.headerPredicate = headerPredicate;
    }

    /**
     * The configured {@link #setHeaderPredicate(Predicate) headerPredicate}.
     *
     * @since 5.2
     */
    protected Predicate<String> getHeaderPredicate() {
        return this.headerPredicate;
    }

    /**
     * Set the maximum length of the payload body to be included in the log message.
     * Default is 50 characters.
     *
     * @since 3.0
     */
    public void setMaxPayloadLength(int maxPayloadLength) {
        Assert.isTrue(maxPayloadLength >= 0, "'maxPayloadLength' should be larger than or equal to 0");
        this.maxPayloadLength = maxPayloadLength;
    }

    /**
     * Return the maximum length of the payload body to be included in the log message.
     *
     * @since 3.0
     */
    protected int getMaxPayloadLength() {
        return this.maxPayloadLength;
    }

    /**
     * Set the value that should be prepended to the log message written
     * <i>before</i> a request is processed.
     */
    public void setBeforeMessagePrefix(String beforeMessagePrefix) {
        this.beforeMessagePrefix = beforeMessagePrefix;
    }

    /**
     * Set the value that should be appended to the log message written
     * <i>before</i> a request is processed.
     */
    public void setBeforeMessageSuffix(String beforeMessageSuffix) {
        this.beforeMessageSuffix = beforeMessageSuffix;
    }

    /**
     * Set the value that should be prepended to the log message written
     * <i>after</i> a request is processed.
     */
    public void setAfterMessagePrefix(String afterMessagePrefix) {
        this.afterMessagePrefix = afterMessagePrefix;
    }

    /**
     * Set the value that should be appended to the log message written
     * <i>after</i> a request is processed.
     */
    public void setAfterMessageSuffix(String afterMessageSuffix) {
        this.afterMessageSuffix = afterMessageSuffix;
    }

    /**
     * The default value is "false" so that the filter may log a "before" message
     * at the start of request processing and an "after" message at the end from
     * when the last asynchronously dispatched thread is exiting.
     */
    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    /**
     * Forwards the request to the next filter in the chain and delegates down to the subclasses
     * to perform the actual request logging both before and after the request is processed.
     *
     * @see #beforeRequest
     * @see #afterRequest
     */
    @Override
    @ApimsReportGeneratedHint
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        boolean isFirstRequest = !isAsyncDispatch(request);
        HttpServletRequest requestToUse = request;
        HttpServletResponse responseToUse = response;

        boolean shouldLog = shouldLog(requestToUse);
        if (shouldLog && isIncludePayload() && isFirstRequest) {
            if (!(requestToUse instanceof ApimsContentCachingRequestWrapper)) {
                requestToUse = new ApimsContentCachingRequestWrapper(request);
            }
            if (!(responseToUse instanceof ApimsContentCachingResponseWrapper)) {
                responseToUse = new ApimsContentCachingResponseWrapper(response);
            }
        }

        if (shouldLog && isFirstRequest) {
            beforeRequest(requestToUse, getBeforeMessage(requestToUse));
        }
        try {
            filterChain.doFilter(requestToUse, responseToUse);
        } finally {
            if (shouldLog && isFirstRequest && !isAsyncStarted(requestToUse)) {
                afterRequest(requestToUse, getAfterMessage(requestToUse, responseToUse));
            }
        }
    }

    /**
     * Get the message to write to the log before the request.
     *
     * @see #createMessage
     */
    private String getBeforeMessage(HttpServletRequest request) {
        return createMessage(request, null, this.beforeMessagePrefix, this.beforeMessageSuffix);
    }

    /**
     * Get the message to write to the log after the request.
     *
     * @see #createMessage
     */
    private String getAfterMessage(HttpServletRequest request, HttpServletResponse response) {
        return createMessage(request, response, this.afterMessagePrefix, this.afterMessageSuffix);
    }

    /**
     * Create a log message for the given request, prefix and suffix.
     * <p>If {@code includeQueryString} is {@code true}, then the inner part
     * of the log message will take the form {@code request_uri?query_string};
     * otherwise the message will simply be of the form {@code request_uri}.
     * <p>The final message is composed of the inner part as described and
     * the supplied prefix and suffix.
     * <p>
     */
    @SuppressWarnings({"java:S2259", "java:S3776", "java:S6541"})
    @ApimsReportGeneratedHint
    protected String createMessage(
            HttpServletRequest request, HttpServletResponse response, String prefix, String suffix) {
        StringBuilder msg = new StringBuilder();
        msg.append(prefix);
        msg.append(request.getMethod()).append(' ');
        msg.append(request.getRequestURI());

        if (isIncludeQueryString()) {
            String queryString = request.getQueryString();
            if (queryString != null) {
                msg.append('?').append(queryString);
            }
        }

        if (response != null) {
            if (isIncludeResponseStatus()) {
                msg.append(" [HTTP-STATUS] : ").append(HttpStatus.valueOf(response.getStatus()));
            }
            if (isIncludePayload()) {
                String payload = getMessagePayload(response);
                if (StringUtils.hasLength(payload)) {
                    msg.append(", payload=").append(payload);
                }
            }
            msg.append(suffix);
            return msg.toString();
        }

        if (isIncludeSkyHeaders()) {
            Map<String, String> headers = new TreeMap<>();
            Enumeration<String> names = request.getHeaderNames();
            while (names.hasMoreElements()) {
                String header = names.nextElement();
                if (header.toLowerCase().startsWith("x-sky-")) {
                    headers.put(header, request.getHeader(header));
                }
            }
            msg.append(", x-sky-headers=").append(headers);
        }
        if (isIncludeClientInfo()) {
            String client = request.getRemoteAddr();
            if (StringUtils.hasLength(client)) {
                msg.append(", client=").append(client);
            }
            String user = request.getRemoteUser();
            if (user != null) {
                msg.append(", user=").append(user);
            }
        }

        if (isIncludeHeaders()) {
            HttpHeaders headers = new ServletServerHttpRequest(request).getHeaders();
            Enumeration<String> names = request.getHeaderNames();
            while (names.hasMoreElements()) {
                String header = names.nextElement();
                if (!getHeaderPredicate().test(header)) {
                    headers.set(header, "masked");
                }
            }
            msg.append(", headers=").append(headers);
        }

        if (isIncludePayload()) {
            String payload = getMessagePayload(request);
            if (StringUtils.hasLength(payload)) {
                msg.append(", payload=").append(payload);
            }
        }

        msg.append(suffix);
        return msg.toString();
    }

    @Nullable
    @ApimsReportGeneratedHint
    protected String getMessagePayload(HttpServletRequest request) {
        ApimsContentCachingRequestWrapper wrapper =
                WebUtils.getNativeRequest(request, ApimsContentCachingRequestWrapper.class);
        if (wrapper != null) {
            return FunctionUtils.execute(
                    () -> getBody(wrapper.getBody(), wrapper.getCharacterEncoding()), ApimsRuntimeException.class);
        }
        return null;
    }

    @Nullable
    @ApimsReportGeneratedHint
    protected String getMessagePayload(HttpServletResponse response) {
        final ApimsContentCachingResponseWrapper wrapper =
                WebUtils.getNativeResponse(response, ApimsContentCachingResponseWrapper.class);
        if (wrapper != null) {
            return FunctionUtils.execute(
                    () -> getBody(wrapper.getBody(), wrapper.getCharacterEncoding()), ApimsRuntimeException.class);
        }
        return null;
    }

    private String getBody(final byte[] body, final String characterEncoding) throws UnsupportedEncodingException {
        int length = Math.min(body.length, getMaxPayloadLength());
        return new String(body, 0, length, characterEncoding);
    }

    /**
     * Determine whether to call the {@link #beforeRequest}/{@link #afterRequest}
     * methods for the current request, i.e. whether logging is currently active
     * (and the log message is worth building).
     * <p>The default implementation always returns {@code true}. Subclasses may
     * override this with a log level check.
     *
     * @param request current HTTP request
     * @return {@code true} if the before/after method should get called;
     * {@code false} otherwise
     * @since 4.1.5
     */
    protected boolean shouldLog(HttpServletRequest request) {
        return !excludes.contains(request.getRequestURI());
    }

    /**
     * Writes a log message before the request is processed.
     */
    @SuppressWarnings({"java:S1172"})
    protected void beforeRequest(HttpServletRequest request, String message) {
        FunctionUtils.executeIfNotNull(mdc, () -> mdc.putGlobalFields());
        log.info("{}", VeracodeMitigationUtils.sanitizeLogValues(message));
    }

    /**
     * Writes a log message after the request is processed.
     */
    @SuppressWarnings({"java:S1172"})
    protected void afterRequest(HttpServletRequest request, String message) {
        log.info("{}", VeracodeMitigationUtils.sanitizeLogValues(message));
    }

    protected Predicate<String> createHeaderPredicate() {
        return header -> {
            for (String h : headerPredicateHeaders) {
                if (header.startsWith(h)) {
                    return true;
                }
            }
            return false;
        };
    }
}
