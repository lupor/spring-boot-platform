/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.oauth.principal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Getter
public class ApimsUserPrincipal {

    private final Date expiration;
    private final String objectId;
    private final String name;
    private final String email;
    private final String domain;
    private final String service;
    private final Set<String> roles;
}
