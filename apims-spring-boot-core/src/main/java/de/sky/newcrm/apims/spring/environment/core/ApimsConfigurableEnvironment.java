/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.environment.core;

import java.util.Map;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;

class ApimsConfigurableEnvironment extends StandardEnvironment {

    ApimsConfigurableEnvironment() {}

    void registerForcedProperties(Map<String, Object> forcedProperties) {
        getPropertySources()
                .addFirst(new MapPropertySource(ApimsSpringContext.APIMS_FORCED_PROPERTIES, forcedProperties));
    }
}
