/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.cache.autoconfigure;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

class ApimsCachingAutoConfigurationTest {
    @Test
    void createCacheManagerTest() {

        ApimsCachingAutoConfiguration configuration = new ApimsCachingAutoConfiguration();
        CacheManager cacheManager = configuration.cacheManager(1, 10, false);
        assertNotNull(cacheManager);
        List<String> cacheNames = new ArrayList<>(cacheManager.getCacheNames());
        assertNotNull(cacheNames);
        assertTrue(cacheNames.isEmpty());
        Cache cache = cacheManager.getCache("test-cache-1");
        assertNotNull(cache);

        cacheManager =
                ApimsCachingAutoConfigurationHelper.createCacheManager(2, 100, true, "test-cache-1, test-cache-2");
        assertNotNull(cacheManager);
        cacheNames = new ArrayList<>(cacheManager.getCacheNames());
        assertNotNull(cacheNames);
        assertEquals(2, cacheNames.size());
        cache = cacheManager.getCache("test-cache-1");
        assertNotNull(cache);
    }
}
