/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.context.autoconfigure;

import de.sky.newcrm.apims.spring.context.core.ApimsSpringContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@SuppressWarnings({"java:S6212"})
public class ApimsSpringContextAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ApimsSpringContextAutoConfiguration.class);

    public ApimsSpringContextAutoConfiguration() {
        log.debug("[APIMS AUTOCONFIG] Spring context.");
    }

    @Bean
    @ConditionalOnMissingBean
    public ApimsSpringContextProvider apimsSpringContextProvider() {
        return new ApimsSpringContextProvider();
    }
}
