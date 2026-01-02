/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.oauth.validator;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import de.sky.newcrm.apims.spring.web.core.oauth.exception.ApimsOAuthTokenNotValidException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;

import java.util.HashSet;
import java.util.Set;

public interface ApimsTokenValidator extends Ordered {

    boolean canValidate(JWT token);

    JWTClaimsSet validate(JWT token, Set<String> runtimeAudiences) throws ApimsOAuthTokenNotValidException;

    default Set<String> getRuntimeAudiences(HttpServletRequest request) {
        return new HashSet<>();
    }
}
