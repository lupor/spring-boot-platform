/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;

class HttpRequestUtilsTest {

    @Test
    void authHeaderTest() {
        testBasicAuthHeader("apims-apigee@sky.de.care", "test", StandardCharsets.UTF_8);
        testBasicAuthHeader("apims-apigee@sky.de.care", "test", null);
        testBasicAuthHeader("", "", null);
        assertThrows(IllegalArgumentException.class, () -> testBasicAuthHeader(null, null, null));
    }

    @Test
    void requestHttpHeadersTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, "json");
        Map<String, String> values = HttpRequestUtils.getHttpRequestValues(headers);
        assertNotNull(values);
        assertFalse(values.isEmpty());
        assertTrue(values.containsKey(HttpHeaders.ACCEPT));
        assertEquals("json", values.get(HttpHeaders.ACCEPT));
        assertEquals("json", HttpRequestUtils.getHttpRequestValue(headers, HttpHeaders.ACCEPT));
    }

    @Test
    void requestHttpServletRequestHeadersTest() {
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.addHeader(HttpHeaders.ACCEPT, "json");
        servletRequest.addHeader(HttpHeaders.ACCEPT_LANGUAGE, "de");
        servletRequest.addHeader(HttpHeaders.ACCEPT_LANGUAGE, "en");

        Map<String, String> values = HttpRequestUtils.getHttpRequestValues(servletRequest);
        assertNotNull(values);
        assertFalse(values.isEmpty());
        assertTrue(values.containsKey(HttpHeaders.ACCEPT));
        assertEquals("json", values.get(HttpHeaders.ACCEPT));
    }

    @Test
    void requestHttpServletRequestHeaderTest() {
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.addHeader(HttpHeaders.ACCEPT, "json");
        servletRequest.addHeader(HttpHeaders.ACCEPT_LANGUAGE, "de");
        servletRequest.addHeader(HttpHeaders.ACCEPT_LANGUAGE, "en");
        assertEquals("de", HttpRequestUtils.getFirstHttpRequestValue(servletRequest, HttpHeaders.ACCEPT_LANGUAGE));
    }

    @Test
    void requestHttpServletRequestNullTest() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeaderNames()).thenReturn(null);
        Map<String, String> values = HttpRequestUtils.getHttpRequestValues(request);
        assertNotNull(values);
        assertTrue(values.isEmpty());
        assertNotNull(HttpRequestUtils.getHttpRequestValues((HttpServletRequest) null));
    }

    @Test
    void encodeUrlParamTest() {
        String value = "ABCÜÄÖ[]/*";
        assertEquals("ABC%C3%9C%C3%84%C3%96%5B%5D%2F%2A", HttpRequestUtils.encodeUrlParam(value));
        assertEquals(
                "ABC%C3%9C%C3%84%C3%96%5B%5D%2F*",
                HttpRequestUtils.encodeUrlParam(value, StandardCharsets.UTF_8.toString(), false));
        assertEquals("", HttpRequestUtils.encodeUrlParam(null));
        assertEquals("", HttpRequestUtils.encodeUrlParam(""));
    }

    void testBasicAuthHeader(String username, String password, Charset charset) {

        String authHeaderValue = HttpRequestUtils.encodeBasicAuthorizationHeaderValue(username, password, charset);
        Map<String, String> headers = Map.of(HttpHeaders.AUTHORIZATION, authHeaderValue);
        String[] authValues = HttpRequestUtils.getBasicAuthorizationValues(headers, charset);
        assertNotNull(authValues);
        assertEquals(2, authValues.length);
        assertEquals(username, authValues[0]);
        assertEquals(password, authValues[1]);

        authValues = HttpRequestUtils.getAuthorizationValue(headers, charset);
        assertNotNull(authValues);
        assertEquals(2, authValues.length);
        assertEquals("Basic", authValues[0]);
        assertNotNull(authValues[1]);

        authHeaderValue = authHeaderValue.replaceAll("Basic", "Bearer");
        headers = Map.of(HttpHeaders.AUTHORIZATION, authHeaderValue);
        authValues = HttpRequestUtils.getBasicAuthorizationValues(headers, charset);
        assertNotNull(authValues);
        assertEquals(2, authValues.length);
        assertNull(authValues[0]);
        assertNull(authValues[1]);

        authValues = HttpRequestUtils.getAuthorizationValue(headers, charset);
        assertNotNull(authValues);
        assertEquals(2, authValues.length);
        assertEquals("Bearer", authValues[0]);
        assertNotNull(authValues[1]);
    }
}
