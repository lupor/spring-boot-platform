/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.environment.core;

import de.sky.newcrm.apims.spring.exceptions.ApimsRuntimeException;
import de.sky.newcrm.apims.spring.utils.AssertUtils;
import de.sky.newcrm.apims.spring.utils.ObjectUtils;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;

@Slf4j
public class ApimsSpringContext {

    static final String APIMS_FORCED_PROPERTIES = "APIMS_FORCED_PROPERTIES";
    private static final Map<String, Object> forcedProperties = new HashMap<>();
    private static final ApimsSpringApplicationContext applicationContext = new ApimsSpringApplicationContext();
    private static ConfigurableEnvironment environment = null;
    private static final Set<String> environmentPropertyNames = new TreeSet<>();
    private static SpringApplication springApplication;

    private ApimsSpringContext() {}

    static Map<String, Object> getForcedProperties() {
        return forcedProperties;
    }

    public static ApimsSpringApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(ApplicationContext applicationContext) {
        ApimsSpringContext.applicationContext.setApplicationContext(applicationContext);
    }

    public static ConfigurableEnvironment getEnvironment() {
        if (environment == null) {
            log.debug("create fallback environment {}", ApimsConfigurableEnvironment.class.getName());
            ApimsConfigurableEnvironment fallbackEnvironment = new ApimsConfigurableEnvironment();
            fallbackEnvironment.registerForcedProperties(forcedProperties);
            setEnvironment(fallbackEnvironment);
        }
        return environment;
    }

    static void setEnvironment(ConfigurableEnvironment environment) {
        ApimsSpringContext.environment = environment;
        environmentPropertyNames.clear();
        if (environment != null) {
            for (PropertySource<?> propertySource : environment.getPropertySources()) {
                log.debug("register propertySource {}", propertySource.getName());
                if (propertySource instanceof EnumerablePropertySource<?> source) {
                    for (String propertyName : source.getPropertyNames()) {
                        if (!propertyName.endsWith("_placeholder")) {
                            environmentPropertyNames.add(propertyName);
                        }
                    }
                }
            }
        }
    }

    public static SpringApplication getSpringApplication() {
        return springApplication;
    }

    static void setSpringApplication(SpringApplication springApplication) {
        ApimsSpringContext.springApplication = springApplication;
    }

    @ApimsReportGeneratedHint
    @SuppressWarnings({"unchecked", "java:S1172"})
    public static <T> T getBeanOrClass(String name, Class<T> type) {
        AssertUtils.hasLengthCheck("name", name);
        String beanName = name;
        boolean classDefinition = beanName.contains(".");
        if (classDefinition) {
            beanName = beanName.substring(beanName.lastIndexOf(".") + 1);
        }
        beanName = beanName.length() > 1
                ? beanName.substring(0, 1).toLowerCase() + beanName.substring(1)
                : beanName.toLowerCase();
        try {
            return (T) ApimsSpringContext.getApplicationContext().getBean(beanName);
        } catch (BeansException e) {
            if (!classDefinition) {
                throw new ApimsRuntimeException("Spring bean with name '%s' not found.".formatted(beanName), e);
            }
            log.debug("Spring bean with name '{}' not found: {}, try by class {}...", beanName, e.getMessage(), name);
        }
        Class<?> clazz = ObjectUtils.getClass(name);
        try {
            return (T) ApimsSpringContext.getApplicationContext().getBean(clazz);
        } catch (BeansException e) {
            log.debug("Spring bean with class '{}' not loaded: {}, try class loading...", name, e.getMessage());
        }
        return ObjectUtils.createInstance(clazz);
    }

    public static String getProperty(String name, String defaultValue) {
        return getEnvironment().getProperty(name, defaultValue);
    }

    public static String resolvePlaceholders(String expression) {
        return resolvePlaceholders(expression, null);
    }

    public static String resolvePlaceholders(String expression, String defaultValue) {
        if (!StringUtils.hasLength(expression)) {
            return defaultValue;
        }
        if (!expression.startsWith("${") && !expression.endsWith("}")) {
            expression = "${" + expression + "}";
        }
        String value = getEnvironment().resolvePlaceholders(expression);
        return !StringUtils.hasLength(value) || "null".equalsIgnoreCase(value) ? defaultValue : value;
    }

    public static String resolveRequiredPlaceholders(String expression, String defaultValue) {
        if (!StringUtils.hasLength(expression)) {
            return defaultValue;
        }
        if (!expression.contains("${")) {
            return expression;
        }
        try {
            String value = getEnvironment().resolveRequiredPlaceholders(expression);
            return !StringUtils.hasLength(value) || "null".equalsIgnoreCase(value) ? defaultValue : value;
        } catch (IllegalArgumentException e) {
            log.warn("resolveRequiredPlaceholders failed: {}. return default value: {}", e.getMessage(), defaultValue);
            return defaultValue;
        }
    }

    public static <T> T getProperty(String key, Class<T> targetType) {
        return getEnvironment().getProperty(key, targetType);
    }

    public static <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        return getEnvironment().getProperty(key, targetType, defaultValue);
    }

    public static Map<String, String> getProperties(String root) {
        Map<String, String> map = new TreeMap<>();
        final String propertiesRoot = StringUtils.hasLength(root) ? root + "." : null;
        for (String propertyName : environmentPropertyNames) {
            if (!propertyName.endsWith("_placeholder")
                    && (propertiesRoot == null || propertyName.startsWith(propertiesRoot))) {
                String targetPropertyName =
                        propertiesRoot == null ? propertyName : propertyName.substring(propertiesRoot.length());
                map.put(targetPropertyName, getEnvironment().getProperty(propertyName));
            }
        }
        return map;
    }

    public static void overrideProperties(Map<String, Object> values) {
        values.forEach(ApimsSpringContext::overrideProperty);
    }

    public static void overrideProperty(String name, Object value) {
        forcedProperties.put(name, value);
        if (environment != null) {
            environmentPropertyNames.add(name);
        }
    }
}
