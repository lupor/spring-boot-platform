/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.telemetry.mdc.autoconfigure;

import de.sky.newcrm.apims.spring.telemetry.mdc.config.ApimsMdcConfig;
import de.sky.newcrm.apims.spring.telemetry.mdc.core.ApimsMdc;
import de.sky.newcrm.apims.spring.telemetry.metrics.config.ApimsMetricsConfig;
import de.sky.newcrm.apims.spring.telemetry.metrics.core.ApimsMeterRegistry;
import de.sky.newcrm.apims.spring.telemetry.metrics.core.ApimsMeterRegistryConfigurer;
import de.sky.newcrm.apims.spring.telemetry.metrics.aspects.ApimsAroundMetricsListener;
import io.micrometer.core.aop.CountedAspect;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ApimsMdcConfig.class)
@SuppressWarnings({"java:S6212"})
public class ApimsMdcAutoconfiguration {

    private static final Logger log = LoggerFactory.getLogger(ApimsMdcAutoconfiguration.class);

    private final ApimsMdcConfig apimsMdcConfig;

    public ApimsMdcAutoconfiguration(ApimsMdcConfig apimsMdcConfig) {
        log.debug("[APIMS AUTOCONFIG] Mdc.");
        this.apimsMdcConfig = apimsMdcConfig;
    }


    @Bean
    @ConditionalOnMissingBean
    ApimsMdc apimsMdc() {
        return new ApimsMdc(apimsMdcConfig.getGlobalFields());
    }
}
