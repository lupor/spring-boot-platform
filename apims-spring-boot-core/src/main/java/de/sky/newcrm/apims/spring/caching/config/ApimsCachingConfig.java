/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.caching.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("apims.caching")
@Getter
@Setter
public class ApimsCachingConfig {
    private int expireMinutes = 5;
    private long maximumSize = 1000;
    private boolean recordStats = false;
    private int secondCacheManagerExpireMinutes = 30;
    private long secondCacheManagerMaximumSize = 1000;
    private boolean secondCacheManagerRecordStats = false;
}
