/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.environment.core;

import de.sky.newcrm.apims.spring.context.core.CoreBinderSupport;
import de.sky.newcrm.apims.spring.environment.config.ApimsCoreProperties;
import de.sky.newcrm.apims.spring.utils.FunctionUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public abstract class ApimsCoreConfigurationCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        final ApimsCoreProperties properties = new CoreBinderSupport().getProps(context);
        return FunctionUtils.executeIfNotNull(properties, false, () -> this.matches(properties));
    }

    protected abstract boolean matches(ApimsCoreProperties properties);
}
