/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.context.core;

import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import java.util.function.Supplier;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;

public abstract class GenericBinderSupport<T, U> implements GenericBinderSupportInterface<T> {
    private final Class<T> configClassType;
    private final String configNamespace;
    private final Supplier<U> supplier;

    public GenericBinderSupport(Class<T> configClassType, final String configNamespace, Supplier<U> supplier) {
        this.configClassType = configClassType;
        this.configNamespace = configNamespace;
        this.supplier = supplier;
    }

    public Binder getBinder(Environment environment) {
        return Binder.get(environment);
    }

    public T getProps(ConditionContext context) {
        return getProps(context.getEnvironment());
    }

    public T getProps(Environment environment) {
        return getBinder(environment).bind(configNamespace, configClassType).orElse(null);
    }

    @ApimsReportGeneratedHint
    @Override
    public String getProperty(ConditionContext context, String key, String defaultValue) {
        return getProperty(context.getEnvironment(), key, defaultValue);
    }

    @ApimsReportGeneratedHint
    public String getProperty(Environment environment, String key, String defaultValue) {
        return environment.getProperty(key, defaultValue);
    }

    public U getInstance() {
        return supplier.get();
    }
}
