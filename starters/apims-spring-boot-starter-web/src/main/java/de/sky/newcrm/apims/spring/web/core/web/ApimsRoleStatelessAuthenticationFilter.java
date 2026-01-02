/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.web;

import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import de.sky.newcrm.apims.spring.web.core.oauth.handler.ApimsAuthenticationRequestHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class ApimsRoleStatelessAuthenticationFilter extends OncePerRequestFilter {

    private final List<ApimsAuthenticationRequestHandler> authenticationRequestHandlerList;

    @Override
    @ApimsReportGeneratedHint
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (alreadyAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }
        Authentication authentication = getAuthentication(request);
        if (authentication == null) {
            filterChain.doFilter(request, response);
        } else {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            try {
                filterChain.doFilter(request, response);
            } finally {
                SecurityContextHolder.clearContext();
            }
        }
    }

    protected boolean alreadyAuthenticated() {
        return Optional.of(SecurityContextHolder.getContext())
                .map(org.springframework.security.core.context.SecurityContext::getAuthentication)
                .map(Authentication::isAuthenticated)
                .orElse(false);
    }

    @ApimsReportGeneratedHint
    protected Authentication getAuthentication(HttpServletRequest request) {
        Authentication authentication = null;
        Map<Class<?>, String> failedHandlerMap = new HashMap<>();
        Class<?> successHandlerClass = null;
        for (ApimsAuthenticationRequestHandler handler : authenticationRequestHandlerList) {
            try {
                authentication = handler.getAuthentication(request);
            } catch (Exception e) {
                failedHandlerMap.put(handler.getClass(), e.getMessage());
            }
            if (authentication != null) {
                successHandlerClass = handler.getClass();
                break;
            }
        }
        if (log.isTraceEnabled()) {
            if (authentication == null) {
                for (Map.Entry<Class<?>, String> entry : failedHandlerMap.entrySet()) {
                    log.trace(
                            "|------ [______AUTH] : Request verification failed ({}): {}",
                            entry.getKey().getSimpleName(),
                            entry.getValue());
                }
            } else {
                log.trace(
                        "|------ [______AUTH] : Request verification success ({}). {}",
                        successHandlerClass.getSimpleName(),
                        authentication);
            }
        }
        return authentication;
    }
}
