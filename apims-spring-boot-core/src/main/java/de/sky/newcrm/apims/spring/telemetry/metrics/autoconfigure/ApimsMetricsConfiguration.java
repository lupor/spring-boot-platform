/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.telemetry.metrics.autoconfigure;

import de.sky.newcrm.apims.spring.environment.config.ApimsCoreProperties;
import de.sky.newcrm.apims.spring.telemetry.metrics.aspects.ApimsAroundMetricsListener;
import de.sky.newcrm.apims.spring.telemetry.metrics.core.ApimsMeterRegistry;
import de.sky.newcrm.apims.spring.telemetry.metrics.core.ApimsMeterRegistryConfigurer;
import io.micrometer.core.aop.CountedAspect;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration(proxyBeanMethods = false)
// TODO: Maybe i need to take the core aspects configuration into account?
// @AutoConfigureBefore(ApimsAspectsConfiguration.class)
@EnableConfigurationProperties(ApimsCoreProperties.class)
public class ApimsMetricsConfiguration {

    private final ApimsCoreProperties apimsCoreProperties;

    public ApimsMetricsConfiguration(ApimsCoreProperties apimsCoreProperties) {
        this.apimsCoreProperties = apimsCoreProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    ApimsMeterRegistry meterRegistry(MeterRegistry meterRegistry) {
        return new ApimsMeterRegistry(new ApimsMeterRegistryConfigurer()
                .configure(meterRegistry, apimsCoreProperties.getMetrics().getCommonTags()));
    }

    @Bean
    @ConditionalOnMissingBean
    CountedAspect countedAspect(MeterRegistry meterRegistry) {
        return new CountedAspect(meterRegistry);
    }

    @Bean
    @ConditionalOnMissingBean
    TimedAspect timedAspect(MeterRegistry meterRegistry) {
        return new TimedAspect(meterRegistry);
    }

    @Bean
    @ConditionalOnMissingBean
    public ApimsAroundMetricsListener apimsAroundMetricsListener(
            ApimsMeterRegistry apimsMeterRegistry,
            @Value("${apims.aspects.listeners.metrics.ignored-components:}") String ignoredComponents) {
        Set<String> ignoredSet =
                new HashSet<>(Arrays.asList(StringUtils.tokenizeToStringArray(ignoredComponents, ",", true, true)));
        return new ApimsAroundMetricsListener(apimsMeterRegistry, ignoredSet);
    }
}
