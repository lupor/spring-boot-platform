/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.environment.autoconfigure;

import de.sky.newcrm.apims.spring.environment.core.ApimsApplicationAvailabilityBean;
import de.sky.newcrm.apims.spring.environment.core.ApimsSpringApplicationEventInfoListener;
import de.sky.newcrm.apims.spring.environment.core.ApimsSpringEnvironmentInfo;
import de.sky.newcrm.apims.spring.environment.core.ApimsValueResolver;
import de.sky.newcrm.apims.spring.exceptions.ApimsBaseExceptionPreloader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration(proxyBeanMethods = false)
public class ApimsEnvConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ApimsEnvConfiguration.class);

    public ApimsEnvConfiguration() {
        log.debug("[APIMS AUTOCONFIG] Env.");
    }

    @Bean
    @ConditionalOnMissingBean()
    public ApimsValueResolver apimsValueResolver() {
        log.debug("[APIMS AUTOCONFIG] Env:apimsValueResolver.");
        return new ApimsValueResolver();
    }

    @Bean
    @ConditionalOnMissingBean()
    public ApimsSpringEnvironmentInfo apimsEnvironmentInfoBean(Environment environment) {
        return new ApimsSpringEnvironmentInfo(environment);
    }

    @Bean
    @ConditionalOnMissingBean()
    public ApimsSpringApplicationEventInfoListener apimsSpringApplicationEventInfoListener() {
        log.debug("[APIMS AUTOCONFIG] Env:ApimsSpringApplicationEventInfoListener.");
        return new ApimsSpringApplicationEventInfoListener();
    }

    @Bean
    @ConditionalOnMissingBean()
    public ApimsApplicationAvailabilityBean apimsApplicationAvailabilityBean() {
        log.debug("[APIMS AUTOCONFIG] Env:ApimsApplicationAvailabilityBean.");
        return new ApimsApplicationAvailabilityBean();
    }

    @Bean
    @ConditionalOnMissingBean()
    public ApimsBaseExceptionPreloader apimsBaseExceptionPreloader() {
        log.debug("[APIMS AUTOCONFIG] Env:ApimsBaseExceptionPreloader.");
        return new ApimsBaseExceptionPreloader();
    }
}
