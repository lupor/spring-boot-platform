/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.environment.core;

import java.util.Map;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;

@SuppressWarnings({"java:S1192"})
public class ApimsSpringApplicationContext implements ApimsApplicationContext {

    private ApplicationContext applicationContext;
    private ApimsApplicationContext fallbackApplicationContext;

    ApimsSpringApplicationContext() {}

    void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.fallbackApplicationContext = null;
    }

    void setFallbackApplicationContext(ApimsApplicationContext fallbackApplicationContext) {
        this.fallbackApplicationContext = fallbackApplicationContext;
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        if (fallbackApplicationContext != null) {
            return fallbackApplicationContext.getBean(requiredType);
        } else if (applicationContext != null) {
            return applicationContext.getBean(requiredType);
        }
        throw new IllegalStateException(
                "[Assertion failed] - spring application context is required; it must not be null");
    }

    @Override
    public Object getBean(String name) {
        if (fallbackApplicationContext != null) {
            return fallbackApplicationContext.getBean(name);
        } else if (applicationContext != null) {
            return applicationContext.getBean(name);
        }
        throw new IllegalStateException(
                "[Assertion failed] - spring application context is required; it must not be null");
    }

    @Override
    public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type) {
        if (fallbackApplicationContext != null) {
            return fallbackApplicationContext.getBeansOfType(type);
        } else if (applicationContext != null) {
            return applicationContext.getBeansOfType(type);
        }
        throw new IllegalStateException(
                "[Assertion failed] - spring application context is required; it must not be null");
    }
}
