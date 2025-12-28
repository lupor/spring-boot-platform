/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.async.autoconfigure;

import de.sky.newcrm.apims.spring.async.config.ApimsAsyncConfig;
import de.sky.newcrm.apims.spring.environment.core.ApimsSpringContext;
import de.sky.newcrm.apims.spring.utils.ObjectUtils;
import org.junit.jupiter.api.Test;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ApimsAsyncAutoConfigurationTest {

    @Test
    void codeCoverageTest() {

        ApimsSpringContext.overrideProperty("apims.app.mocks.async-mock-enabled", "false");
        ApimsAsyncConfig apimsProperties = new ApimsAsyncConfig();
        ApimsAsyncAutoConfiguration configuration = new ApimsAsyncAutoConfiguration(apimsProperties);
        assertNotNull(configuration.getAsyncExecutor());
        AsyncUncaughtExceptionHandler handler = configuration.getAsyncUncaughtExceptionHandler();
        assertNotNull(handler);
        Method method = ObjectUtils.findMethod(this.getClass(), "codeCoverageTest");
        handler.handleUncaughtException(new IllegalStateException("handleUncaughtException test"), method);
    }
}
