/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.environment.core;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ApimsAppTypeEnum {
    UNKNOWN,
    CLI,
    SERVICE,
    STREAM;

    @JsonCreator
    public static ApimsAppTypeEnum fromValue(String value) {
        for (ApimsAppTypeEnum b : ApimsAppTypeEnum.values()) {
            if (b.name().equalsIgnoreCase(value)) {
                return b;
            }
        }
        return UNKNOWN;
    }
}
