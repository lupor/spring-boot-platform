/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.async.autoconfigure;

import de.sky.newcrm.apims.spring.async.config.ApimsPoolConfig;
import de.sky.newcrm.apims.spring.tasking.core.ApimsExecutor;
import de.sky.newcrm.apims.spring.utils.ObjectUtils;

public class ApimsAsyncAutoConfigurationHelper {

    private ApimsAsyncAutoConfigurationHelper() {}

    public static ApimsExecutor createExecuter(ApimsPoolConfig p, boolean mockEnabled) {

        if (mockEnabled) {
            return ObjectUtils.createInstance("de.sky.newcrm.apims.spring.core.mocks.ApimsMockedExecutor");
        }
        return new ApimsExecutor(
                p.getCorePoolSize(),
                p.getMaxPoolSize(),
                p.getKeepAliveSeconds(),
                p.getQueueCapacity(),
                p.isAllowCoreThreadTimeOut(),
                p.isPrestartAllCoreThreads(),
                p.getThreadNamePrefix());
    }
}
