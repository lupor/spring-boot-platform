/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.oauth.entity;

public enum ApimsConfigKeySourceTypeEnum {
    BYTE_SECRET,
    BYTE_SECRET_BASE_64,
    FILE_LOCATION,
    RESOURCE_LOCATION,
    TRUSTED,
    URL_LOCATION
}
