/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.context.core;

import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;

public interface GenericBinderSupportInterface<T> {
    Binder getBinder(Environment environment);

    T getProps(ConditionContext context);

    T getProps(Environment environment);

    @ApimsReportGeneratedHint
    String getProperty(ConditionContext context, String key, String defaultValue);

    @ApimsReportGeneratedHint
    String getProperty(Environment environment, String key, String defaultValue);
}
