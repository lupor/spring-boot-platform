/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.oauth.handler;

import com.nimbusds.jwt.JWT;

import de.sky.newcrm.apims.spring.web.core.oauth.ApimsPreAuthenticatedAuthenticationToken;
import de.sky.newcrm.apims.spring.web.core.oauth.exception.ApimsOAuthException;
import de.sky.newcrm.apims.spring.web.core.oauth.principal.ApimsUserPrincipal;
import de.sky.newcrm.apims.spring.web.core.oauth.principal.ApimsUserPrincipalManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.security.core.Authentication;

@Slf4j
@RequiredArgsConstructor
public class ApimsAuthenticationRequestJwtHandler extends ApimsAbstractAuthenticationRequestHandler {

    private final ApimsUserPrincipalManager principalManager;

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 500;
    }

    @Override
    public Authentication getAuthentication(HttpServletRequest request) throws ApimsOAuthException {
        String token = getBearerToken(request);
        ApimsPreAuthenticatedAuthenticationToken authentication = getAuthentication(token);
        if (authentication == null) {
            JWT jwt = principalManager.parseJwtToken(token);
            if (jwt == null) {
                return null;
            } else if (!principalManager.isValidJwtToken(jwt)) {
                log.trace("|------ [______AUTH] : No valid jwt token found. Header token was: {}", token);
                return null;
            }
            final ApimsUserPrincipal userPrincipal = principalManager.buildUserPrincipal(jwt, request);
            authentication = new ApimsPreAuthenticatedAuthenticationToken(
                    userPrincipal, null, principalManager.toSimpleGrantedAuthoritySet(userPrincipal));
            saveAuthentication(token, authentication);
        }
        return authentication;
    }
}
