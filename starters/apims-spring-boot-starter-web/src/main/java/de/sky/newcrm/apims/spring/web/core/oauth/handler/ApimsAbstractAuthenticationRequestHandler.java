/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.oauth.handler;

import de.sky.newcrm.apims.spring.utils.HttpRequestUtils;
import de.sky.newcrm.apims.spring.web.core.oauth.ApimsPreAuthenticatedAuthenticationToken;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ApimsAbstractAuthenticationRequestHandler implements ApimsAuthenticationRequestHandler {

    private final Map<String, ApimsPreAuthenticatedAuthenticationToken> registry = new ConcurrentHashMap<>();

    protected ApimsPreAuthenticatedAuthenticationToken getAuthentication(String key) {
        ApimsPreAuthenticatedAuthenticationToken authentication = registry.get(key);
        if (authentication != null && authentication.isExpired()) {
            registry.remove(key);
            authentication = null;
        }
        return authentication;
    }

    protected void saveAuthentication(String key, ApimsPreAuthenticatedAuthenticationToken authentication) {
        if (authentication.isExpired()) {
            registry.remove(key);
        } else {
            registry.put(key, authentication);
        }
    }

    protected String getBearerToken(HttpServletRequest request) {
        String[] values = getAuthorizationTokenValue(request, HttpHeaders.AUTHORIZATION);
        return "Bearer".equals(values[0]) ? values[1] : null;
    }

    protected String[] getAuthorizationTokenValue(HttpServletRequest request, String headerName) {
        return HttpRequestUtils.getAuthorizationValue(getFirstHeaderValue(request, headerName), null);
    }

    protected String getFirstHeaderValue(HttpServletRequest request, String headerName) {
        return HttpRequestUtils.getFirstHttpRequestValue(request, headerName);
    }
}
