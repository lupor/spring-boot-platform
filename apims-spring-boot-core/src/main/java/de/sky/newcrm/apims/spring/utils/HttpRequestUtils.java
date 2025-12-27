/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import de.sky.newcrm.apims.spring.exceptions.ApimsRuntimeException;
import de.sky.newcrm.apims.spring.utils.matcher.DefaultStringIncludeExcludeMatcher;
import de.sky.newcrm.apims.spring.utils.matcher.StringMatcher;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public interface HttpRequestUtils {

    StringMatcher MASK_HTTP_HEADER_MATCHER = new DefaultStringIncludeExcludeMatcher(
            List.of("*"),
            List.of(HttpHeaders.AUTHORIZATION.toLowerCase(), "*secret*", "*token*", "*cert*", "*api-*", "*api_*"));

    StringMatcher HEADERS_NOT_BE_RECORDED =
            new DefaultStringIncludeExcludeMatcher(List.of("*"), List.of("X-B3-*", "*traceparent*", "*tracestate*"));

    static String encodeBasicAuthorizationHeaderValue(String userName, String password, @Nullable Charset charset) {
        return "Basic " + HttpHeaders.encodeBasicAuth(userName, password, charset);
    }

    static String getHttpRequestValue(HttpHeaders headers, String name) {
        List<String> values = headers.get(name);
        return values == null ? null : StringUtils.collectionToDelimitedString(values, ";");
    }

    static String[] getBasicAuthorizationValues(Map<String, String> headers, @Nullable Charset charset) {
        return getBasicAuthorizationValues(headers.get(HttpHeaders.AUTHORIZATION), charset);
    }

    static String[] getBasicAuthorizationValues(String authorizationHeaderValue, @Nullable Charset charset) {

        String[] authValue = getAuthorizationValue(authorizationHeaderValue, charset);
        if ("Basic".equalsIgnoreCase(authValue[0])) {
            String[] params = StringUtils.split(authValue[1], ":");
            if (params != null) {
                return params;
            }
        }
        return new String[] {null, null};
    }

    static String[] getAuthorizationValue(String authorizationHeaderValue, @Nullable Charset charset) {

        if (charset == null) {
            charset = StandardCharsets.ISO_8859_1;
        }
        String[] value = new String[] {null, null};
        String[] authValues = StringUtils.tokenizeToStringArray(authorizationHeaderValue, " ", true, true);
        if (authValues.length == 1) {
            value[0] = "";
            value[1] = authValues[0];
        } else if (authValues.length > 1) {
            String type = authValues[0];
            String tokenValue = authValues[1];
            value[0] = type;
            if ("Basic".equalsIgnoreCase(type)) {
                value[1] = new String(Base64.getDecoder().decode(tokenValue.getBytes(charset)), charset);
            } else {
                value[1] = authValues[1];
            }
        }
        return value;
    }

    static String[] getAuthorizationValue(Map<String, String> headers, @Nullable Charset charset) {
        return getAuthorizationValue(headers.get(HttpHeaders.AUTHORIZATION), charset);
    }

    static Map<String, String> getHttpRequestValues(HttpHeaders headers) {
        Map<String, String> map = new TreeMap<>();
        for (Map.Entry<String, List<String>> entry : headers.headerSet()) {
            map.put(entry.getKey(), StringUtils.collectionToDelimitedString(entry.getValue(), ";"));
        }
        return map;
    }

    static String getFirstHttpRequestValue(HttpServletRequest request, String name) {
        if (request != null) {
            Enumeration<?> names = request.getHeaderNames();
            if (names != null) {
                while (names.hasMoreElements()) {
                    String headerName = (String) names.nextElement();
                    if (headerName.equalsIgnoreCase(name)) {
                        return request.getHeaders(headerName).nextElement();
                    }
                }
            }
        }
        return null;
    }

    @SuppressWarnings("java:S3776")
    static Map<String, String> getHttpRequestValues(HttpServletRequest request) {
        Map<String, String> headers = new TreeMap<>();
        if (request != null) {
            Enumeration<?> names = request.getHeaderNames();
            if (names != null) {
                while (names.hasMoreElements()) {
                    String headerName = (String) names.nextElement();
                    for (Enumeration<?> headerValues = request.getHeaders(headerName);
                            headerValues.hasMoreElements(); ) {
                        String headerValue = (String) headerValues.nextElement();
                        if (!headers.containsKey(headerName)) {
                            headers.put(headerName, headerValue);
                        } else {
                            headers.put(headerName, headers.get(headerName) + "; " + headerValue);
                        }
                    }
                }
            }
        }
        return headers;
    }

    static String encodeUrlParam(String value) {
        return encodeUrlParam(value, StandardCharsets.UTF_8.toString());
    }

    static String encodeUrlParam(String value, String encoding) {
        return encodeUrlParam(value, encoding, true);
    }

    static String encodeUrlParam(final String value, final String encoding, boolean encodeStar) {
        if (!StringUtils.hasLength(value)) {
            return "";
        }
        String encoded = FunctionUtils.execute(() -> URLEncoder.encode(value, encoding), ApimsRuntimeException.class);
        if (encodeStar && encoded.contains("*")) {
            encoded = encoded.replace("*", "%2A");
        }
        return encoded;
    }

    static boolean isUrlEncoded(String value) {
        return isUrlEncoded(value, StandardCharsets.UTF_8.toString());
    }

    static boolean isUrlEncoded(String value, final String encoding) {
        String decoded = decodeUrlParam(value, encoding);
        return !decoded.equals(value);
    }

    static String decodeUrlParam(String value) {
        return decodeUrlParam(value, StandardCharsets.UTF_8.toString());
    }

    static String decodeUrlParam(String value, String encoding) {
        try {
            return value.contains("%") ? URLDecoder.decode(value, encoding) : value;
        } catch (Exception e) {
            return value;
        }
    }

    static boolean isSensitiveHeaderName(String name) {
        return !MASK_HTTP_HEADER_MATCHER.matches(name.toLowerCase());
    }

    static boolean isHeaderNotBeRecorded(String name) {
        return !HEADERS_NOT_BE_RECORDED.matches(name.toLowerCase());
    }

    static String maskSensitiveHeader(String name, String value) {
        return isSensitiveHeaderName(name) ? "___masked___" : value;
    }

    static Map<String, String> maskSensitiveHeaders(Map<String, String> headers) {
        Map<String, String> maskedHeaders = new HashMap<>();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            maskedHeaders.put(entry.getKey(), maskSensitiveHeader(entry.getKey(), entry.getValue()));
        }
        return maskedHeaders;
    }
}
