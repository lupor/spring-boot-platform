/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.cache.autoconfigure;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.util.StringUtils;

public class ApimsCachingAutoConfigurationHelper {

    private ApimsCachingAutoConfigurationHelper() {}

    public static CacheManager createCacheManager(int expireMinutes, long maximumSize, boolean recordStats) {
        return createCacheManager(expireMinutes, maximumSize, recordStats, null);
    }

    public static CacheManager createCacheManager(
            int expireMinutes, long maximumSize, boolean recordStats, String cacheNames) {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeine(createConfig(expireMinutes, maximumSize, recordStats));
        String[] cacheNameList = StringUtils.tokenizeToStringArray(cacheNames, ",", true, true);
        if (cacheNameList.length != 0) {
            caffeineCacheManager.setCacheNames(Arrays.asList(cacheNameList));
        }
        return caffeineCacheManager;
    }

    public static Caffeine<Object, Object> createConfig(int expireMinutes, long maximumSize, boolean recordStats) {
        Caffeine<Object, Object> caffeineConfig = Caffeine.newBuilder()
                .expireAfterWrite(expireMinutes, TimeUnit.MINUTES)
                .maximumSize(maximumSize);
        if (recordStats) {
            caffeineConfig.recordStats();
        }
        return caffeineConfig;
    }
}
