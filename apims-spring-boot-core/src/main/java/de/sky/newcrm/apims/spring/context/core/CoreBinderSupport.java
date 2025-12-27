/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.context.core;

import de.sky.newcrm.apims.spring.environment.config.ApimsCoreProperties;

public class CoreBinderSupport extends GenericBinderSupport<ApimsCoreProperties, CoreBinderSupport> {
    public static final String CORE_CONFIG_NAMESPACE = "apims";

    public CoreBinderSupport() {
        super(ApimsCoreProperties.class, CORE_CONFIG_NAMESPACE, CoreBinderSupport::new);
    }
}
