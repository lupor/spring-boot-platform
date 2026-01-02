/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.oauth.entity;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Builder
@Getter
@Setter
public class ApimsKeySource {

    private ApimsConfigKeySourceTypeEnum keySourceType;
    private String keySourceValue;
    private JWKSource<SecurityContext> jwtSource;
    private Date lastRefresh;

    @Builder.Default
    private Map<String, String> additionalParams = new HashMap<>();
}
