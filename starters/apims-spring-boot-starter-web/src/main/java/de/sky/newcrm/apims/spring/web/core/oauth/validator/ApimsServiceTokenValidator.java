/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.oauth.validator;

import de.sky.newcrm.apims.spring.utils.DigestUtils;
import de.sky.newcrm.apims.spring.utils.HttpRequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class ApimsServiceTokenValidator extends ApimsConfigTokenValidator {

    @Value("${apims.app.env:}")
    private String appEnv;

    @Value("${apims.app.domain:}")
    private String appDomain;

    @Value("${apims.app.name:}")
    private String appName;

    public ApimsServiceTokenValidator() {
        super("apims.web.auth.service-token-validator");
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 600;
    }

    @Override
    public Set<String> getRuntimeAudiences(HttpServletRequest request) {
        if (!isExplicitAudienceCheck()) {
            return super.getRuntimeAudiences(request);
        }
        final String service = String.valueOf(HttpRequestUtils.getFirstHttpRequestValue(request, "x-sky-app-name"));
        final Set<String> audiences = new HashSet<>(super.getRuntimeAudiences(request));
        final String value = appEnv + "." + appDomain + "." + appName + ".x-sky-app-name:" + service;
        audiences.add(DigestUtils.md5DigestAsHex(value.getBytes(StandardCharsets.UTF_8)));
        return audiences;
    }
}
