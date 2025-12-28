/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.async.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ApimsPoolConfig {
    private int corePoolSize = 1;
    private int maxPoolSize = Integer.MAX_VALUE;
    private int keepAliveSeconds = 60;
    private int queueCapacity = Integer.MAX_VALUE;
    private boolean allowCoreThreadTimeOut = false;
    private boolean prestartAllCoreThreads = false;
    private String threadNamePrefix = "ApimsAsyncThread-";

    protected ApimsPoolConfig() {
        this(1, Integer.MAX_VALUE, 60, Integer.MAX_VALUE, false, false, "ApimsAsyncThread-");
    }

    protected ApimsPoolConfig(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    protected ApimsPoolConfig(
            int corePoolSize,
            int maxPoolSize,
            int keepAliveSeconds,
            int queueCapacity,
            boolean allowCoreThreadTimeOut,
            boolean prestartAllCoreThreads,
            String threadNamePrefix) {
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.keepAliveSeconds = keepAliveSeconds;
        this.queueCapacity = queueCapacity;
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
        this.prestartAllCoreThreads = prestartAllCoreThreads;
        this.threadNamePrefix = threadNamePrefix;
    }
}
