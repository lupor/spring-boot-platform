/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.oauth.handler;

import de.sky.newcrm.apims.spring.web.core.oauth.ApimsPreAuthenticatedAuthenticationToken;
import de.sky.newcrm.apims.spring.web.core.oauth.exception.ApimsOAuthException;
import de.sky.newcrm.apims.spring.web.core.oauth.principal.ApimsUserPrincipal;
import de.sky.newcrm.apims.spring.web.core.oauth.principal.ApimsUserPrincipalManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class ApimsAuthenticationRequestTrustedServicesHandler extends ApimsAbstractAuthenticationRequestHandler {

    private static final String OWN_DOMAIN_KEY = "owndomain";

    private final ApimsUserPrincipalManager principalManager;
    private final Map<String, Set<String>> trustedServicesAndRoles;
    private final Map<String, Set<String>> trustedDomainsAndRoles;

    @Value("${apims.app.domain:")
    private String ownDomain;

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 1000;
    }

    @Override
    public Authentication getAuthentication(HttpServletRequest request) throws ApimsOAuthException {
        String domain = getFirstHeaderValue(request, "x-sky-app-domain");
        String service = getFirstHeaderValue(request, "x-sky-app-name");
        if (!StringUtils.hasLength(domain) || !StringUtils.hasLength(service)) {
            return null;
        }
        Set<String> roles = new HashSet<>();
        if (trustedServicesAndRoles.containsKey(service)) {
            roles = new HashSet<>(trustedServicesAndRoles.get(service));
        } else if (ownDomain.equalsIgnoreCase(domain) && trustedDomainsAndRoles.containsKey(OWN_DOMAIN_KEY)) {
            roles = new HashSet<>(trustedDomainsAndRoles.get(OWN_DOMAIN_KEY));
        } else if (trustedDomainsAndRoles.containsKey(service)) {
            roles = new HashSet<>(trustedDomainsAndRoles.get(domain));
        }
        ApimsUserPrincipal userPrincipal =
                principalManager.buildUserPrincipal(null, service, service, service, domain, service, roles);
        return new ApimsPreAuthenticatedAuthenticationToken(
                userPrincipal, null, principalManager.toSimpleGrantedAuthoritySet(userPrincipal));
    }
}
