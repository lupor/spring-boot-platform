/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.tasking.core;

import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import de.sky.newcrm.apims.spring.environment.core.ApimsSpringContext;
import de.sky.newcrm.apims.spring.utils.ThreadUtils;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@SuppressWarnings({"java:S6212"})
public class ApimsExecutor implements Executor {

    public static final long DEFAULT_WAITING_SLEEP_TIME_MILLIS = 1000;
    public static final long DEFAULT_MAX_WAITING_TIME_MILLIS = -1;

    private int corePoolSize = 1;

    private int maxPoolSize = Integer.MAX_VALUE;

    private int keepAliveSeconds = 60;

    private int queueCapacity = Integer.MAX_VALUE;

    private boolean allowCoreThreadTimeOut = false;

    private boolean prestartAllCoreThreads = false;

    private String threadNamePrefix = "ApimsTaskExecuterThread-";

    private Executor delegate;

    private final ApimsExecutorContext context =
            new ApimsExecutorContext(UUID.randomUUID().toString());

    public ApimsExecutor() {}

    public ApimsExecutor(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    @ApimsReportGeneratedHint
    public ApimsExecutor(int corePoolSize, int maxPoolSize) {
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
    }

    @ApimsReportGeneratedHint
    public ApimsExecutor(int corePoolSize, int maxPoolSize, int queueCapacity, String threadNamePrefix) {
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.queueCapacity = queueCapacity;
        this.threadNamePrefix = threadNamePrefix;
    }

    public ApimsExecutor(
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

    @Override
    public void execute(Runnable command) {
        final AtomicReference<ApimsRunnableWrapper> apimsRunnableWrapper = new AtomicReference<>();
        try {
            apimsRunnableWrapper.set(ApimsSpringContext.getApplicationContext().getBean(ApimsRunnableWrapper.class));
        } catch (Exception e) {
            apimsRunnableWrapper.set(new ApimsRunnableWrapper());
        }
        apimsRunnableWrapper.get().prepare(context, command);
        final Context ctx = Context.current();
        getDelegate().execute(() -> {
            try (Scope scope = ctx.makeCurrent()) {
                apimsRunnableWrapper.get().run();
            }
        });
    }

    public long getRunningCommandCounter() {
        return context.getRunningCommandCounter();
    }

    public long await() {
        return await(DEFAULT_MAX_WAITING_TIME_MILLIS, DEFAULT_WAITING_SLEEP_TIME_MILLIS);
    }

    public long await(long maxWaitingTimeMillis, long waitingSleepTimeMillis) {
        waitingSleepTimeMillis = waitingSleepTimeMillis < 0 ? 5000 : waitingSleepTimeMillis;
        long maxWaitingTime = maxWaitingTimeMillis < 1 ? -1 : System.currentTimeMillis() + maxWaitingTimeMillis;
        while (maxWaitingTime == -1 || System.currentTimeMillis() < maxWaitingTime) {
            long counter = getRunningCommandCounter();
            if (counter == 0) {
                break;
            }
            if (log.isTraceEnabled()) {
                log.trace("Running Commands: {}. sleep {} millis", counter, waitingSleepTimeMillis);
            }
            ThreadUtils.sleep(waitingSleepTimeMillis);
        }
        return getRunningCommandCounter();
    }

    protected Executor getDelegate() {
        return delegate == null ? createDelegate() : delegate;
    }

    protected synchronized Executor createDelegate() {

        if (delegate == null) {

            if (corePoolSize < 1) {
                corePoolSize = 1;
            }
            if (maxPoolSize < 0) {
                maxPoolSize = Integer.MAX_VALUE;
            }
            if (keepAliveSeconds < 0) {
                keepAliveSeconds = 60;
            }
            if (queueCapacity < 0) {
                queueCapacity = Integer.MAX_VALUE;
            }
            if (maxPoolSize == 1) {
                delegate = Executors.newSingleThreadExecutor();
            } else if (corePoolSize == maxPoolSize) {
                delegate = Executors.newFixedThreadPool(corePoolSize);
            } else {
                ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
                executor.setCorePoolSize(corePoolSize);
                executor.setMaxPoolSize(maxPoolSize);
                executor.setKeepAliveSeconds(keepAliveSeconds);
                executor.setQueueCapacity(corePoolSize);
                executor.setAllowCoreThreadTimeOut(allowCoreThreadTimeOut);
                executor.setPrestartAllCoreThreads(prestartAllCoreThreads);
                executor.setThreadNamePrefix(threadNamePrefix);
                executor.initialize();
                delegate = executor;
            }
        }
        return delegate;
    }
}
