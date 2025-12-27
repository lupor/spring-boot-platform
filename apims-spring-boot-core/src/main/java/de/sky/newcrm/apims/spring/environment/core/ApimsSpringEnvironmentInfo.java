/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.environment.core;

import java.util.Map;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.util.Assert;

@RequiredArgsConstructor
@Slf4j
public class ApimsSpringEnvironmentInfo {

    private final Environment environment;

    public Environment getEnvironment() {
        return environment;
    }

    @SuppressWarnings({"java:S112", "java:S2629", "java:S3457", "java:S3776", "java:S6201"})
    public Map<String, EnvironmentItem> getResolvedEnvironmentInfoMap() {
        Assert.notNull(environment, "[Assertion failed] - argument 'environment' is required; it must not be null");
        Map<String, EnvironmentItem> map = new TreeMap<>();
        if (!(environment instanceof ConfigurableEnvironment)) {
            log.trace("Environment is not instanceof ConfigurableEnvironment.");
            return map;
        }
        ConfigurableEnvironment env = (ConfigurableEnvironment) environment;
        String[] systemEnvironmentPrefixList =
                new String[] {"APIMS", "LOGGING", "MANAGEMENT", "SERVER", "SPRING", "SPRINGDOC"};
        PropertySource<?> systemEnvironment = env.getPropertySources().get("systemEnvironment");
        if (systemEnvironment != null) {
            for (String propertyName : ((EnumerablePropertySource<?>) systemEnvironment).getPropertyNames()) {
                for (String prefix : systemEnvironmentPrefixList) {
                    if (propertyName.startsWith(prefix)) {
                        map.computeIfAbsent(
                                propertyName,
                                key -> new EnvironmentItem(
                                        systemEnvironment.getName(), environment.getProperty(propertyName)));
                    }
                }
            }
        }
        for (PropertySource<?> propertySource : env.getPropertySources()) {
            log.trace(String.format(
                    "check propertySource '%s' of type '%s'...", propertySource.getName(), propertySource.getClass()));
            if (propertySource instanceof OriginTrackedMapPropertySource
                    || ApimsSpringContext.APIMS_FORCED_PROPERTIES.equals(propertySource.getName())) {
                for (String propertyName : ((EnumerablePropertySource<?>) propertySource).getPropertyNames()) {
                    map.computeIfAbsent(
                            propertyName,
                            key -> new EnvironmentItem(
                                    propertySource.getName(), environment.getProperty(propertyName)));
                }
            }
        }
        return map;
    }

    public static class EnvironmentItem {

        private String origin;
        private String value;

        public EnvironmentItem(String origin, String value) {
            this.origin = origin;
            this.value = value;
        }

        @ApimsReportGeneratedHint
        public String getOrigin() {
            return origin;
        }

        @ApimsReportGeneratedHint
        public void setOrigin(String origin) {
            this.origin = origin;
        }

        @ApimsReportGeneratedHint
        public String getValue() {
            return value;
        }

        @ApimsReportGeneratedHint
        public void setValue(String value) {
            this.value = value;
        }
    }
}
