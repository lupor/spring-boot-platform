/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.oauth.entity;

import lombok.Getter;

@Getter
public enum ApimsJwsAlgorithmEnum {
    HS256(32),
    HS384(48),
    HS512(64);

    private final int secretLength;

    ApimsJwsAlgorithmEnum(int secretLength) {
        this.secretLength = secretLength;
    }
}
