/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.cache.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration(proxyBeanMethods = false)
@EnableCaching
@SuppressWarnings({"java:S6212"})
public class ApimsCachingAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ApimsCachingAutoConfiguration.class);

    public ApimsCachingAutoConfiguration() {
        log.debug("[APIMS AUTOCONFIG] Caching.");
    }

    @Primary
    @Bean(name = "cacheManager")
    @ConditionalOnMissingBean(name = "cacheManager")
    public CacheManager cacheManager(
            @Value("${apims.caching.expire-minutes:5}") int expireMinutes,
            @Value("${apims.caching.maximum-size:1000}") long maximumSize,
            @Value("${apims.caching.record-stats:false}") boolean recordStats) {
        return ApimsCachingAutoConfigurationHelper.createCacheManager(expireMinutes, maximumSize, recordStats);
    }

    @Bean()
    @ConditionalOnMissingBean(name = "secondCacheManager")
    public CacheManager secondCacheManager(
            @Value("${apims.caching.second-cache-manager-expire-minutes:10}") int expireMinutes,
            @Value("${apims.caching.second-cache-manager-maximum-size:1000}") long maximumSize,
            @Value("${apims.caching.second-cache-manager-record-stats:false}") boolean recordStats) {
        return ApimsCachingAutoConfigurationHelper.createCacheManager(expireMinutes, maximumSize, recordStats);
    }
}
