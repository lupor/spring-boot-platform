/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.environment.core;


@SuppressWarnings("java:S6548")
public class ApimsMockedSpringContext {

    public static final ApimsMockedSpringContext INSTANCE = new ApimsMockedSpringContext();
    private ApimsMockedApplicationContext mockedApplicationContext = null;

    private ApimsMockedSpringContext() {}

    public ApimsMockedApplicationContext getMockedApplicationContext() {
        if (mockedApplicationContext == null) {
            mockedApplicationContext = new ApimsMockedApplicationContext();
        }
        return mockedApplicationContext;
    }

    public ApimsMockedSpringContext init() {
        getMockedApplicationContext();
        return this;
    }

    public ApimsMockedSpringContext resetTestApplicationContext() {
        unregisterAllTestBeans();
        mockedApplicationContext = null;
        ApimsSpringContext.getApplicationContext().setFallbackApplicationContext(null);
        return this;
    }

    public ApimsMockedSpringContext unregisterAllTestBeans() {
        if (mockedApplicationContext != null) {
            mockedApplicationContext.unregisterAllBeans();
        }
        return this;
    }

    public ApimsMockedSpringContext registerTestBean(String name, Class<?> beanType, Object bean) {
        getMockedApplicationContext().registerBean(name, beanType, bean);
        return this;
    }

    public ApimsMockedSpringContext unRegisterTestBean(String name) {
        getMockedApplicationContext().unRegisterBean(name);
        return this;
    }

    public ApimsMockedSpringContext overrideTestProperty(String name, Object value) {
        ApimsSpringContext.overrideProperty(name, value);
        return this;
    }
}
