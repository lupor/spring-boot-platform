/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.async.autoconfigure;

import de.sky.newcrm.apims.spring.async.config.ApimsAsyncConfig;
import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import de.sky.newcrm.apims.spring.environment.core.ApimsSpringContext;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

@AutoConfiguration(before = {org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration.class})
@ConditionalOnProperty(prefix = "apims.async", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableAsync
@Configuration
@EnableConfigurationProperties(ApimsAsyncConfig.class)
@SuppressWarnings({"java:S6212"})
public class ApimsAsyncAutoConfiguration implements AsyncConfigurer {

    private static final Logger log = LoggerFactory.getLogger(ApimsAsyncAutoConfiguration.class);

    private final ApimsAsyncConfig apimsAsyncConfig;

    public ApimsAsyncAutoConfiguration(ApimsAsyncConfig apimsAsyncConfig) {
        log.debug("[APIMS AUTOCONFIG] Async.");
        this.apimsAsyncConfig = apimsAsyncConfig;
    }

    @Override
    public Executor getAsyncExecutor() {
        final boolean mockEnabled = Boolean.parseBoolean(getProperty("apims.app.mocks.async-mock-enabled", "false"));
        return ApimsAsyncAutoConfigurationHelper.createExecuter(apimsAsyncConfig, mockEnabled);
    }

    @Override
    @SuppressWarnings("java:S2629")
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (Throwable ex, Method method, Object... params) -> log.error(
                String.format("Exception in method %s.%s", method.getClass().getName(), method.getName()), ex);
    }

    @ApimsReportGeneratedHint
    protected String getProperty(String name, String defaultValue) {
        return ApimsSpringContext.getProperty(name, defaultValue);
    }
}
