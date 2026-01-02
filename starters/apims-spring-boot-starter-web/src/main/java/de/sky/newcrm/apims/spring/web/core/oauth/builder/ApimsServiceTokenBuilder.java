/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.oauth.builder;

import de.sky.newcrm.apims.spring.utils.DigestUtils;
import de.sky.newcrm.apims.spring.web.core.entities.SecretEntity;
import de.sky.newcrm.apims.spring.web.core.oauth.entity.Model;
import de.sky.newcrm.apims.spring.web.core.oauth.exception.ApimsOAuthCreateTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class ApimsServiceTokenBuilder {

    @Value("${apims.app.env:}")
    private String appEnv;

    @Value("${apims.app.domain:}")
    private String appDomain;

    @Value("${apims.app.name:}")
    private String appName;

    private final Map<String, Set<String>> trustedServicesAndRoles;
    private final Map<String, Set<String>> trustedDomainsAndRoles;

    public String createToken(String domain, String service, SecretEntity secret)
            throws ApimsOAuthCreateTokenException {
        Set<String> roles = new HashSet<>();
        if (trustedServicesAndRoles.containsKey(service)) {
            roles = new HashSet<>(trustedServicesAndRoles.get(service));
        } else if (trustedDomainsAndRoles.containsKey(domain)) {
            roles = new HashSet<>(trustedDomainsAndRoles.get(domain));
        }
        final String audienceValue = appEnv + "." + appDomain + "." + appName + ".x-sky-app-name:" + service;
        final String audience = DigestUtils.md5DigestAsHex(audienceValue.getBytes(StandardCharsets.UTF_8));
        Map<String, Object> data = new HashMap<>();
        data.put(Model.JwtClaimNames.AUDIENCE, audience);
        data.put(Model.JwtClaimNames.DOMAIN, domain);
        data.put(Model.JwtClaimNames.ENVIRONMENT, appEnv);
        data.put(Model.JwtClaimNames.ISSUER, "de.sky.apims." + appDomain + "." + appName);
        data.put(Model.JwtClaimNames.NAME, service);
        data.put(Model.JwtClaimNames.ROLES, roles);
        data.put(Model.JwtClaimNames.SERVICE, service);
        return new ApimsJwtBuilder().createToken(secret, data);
    }
}
