/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.environment.core;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;

class EnvTest {

    @AfterAll
    static void cleanup() {
        ApimsSpringContext.getForcedProperties().clear();
        ApimsSpringContext.getEnvironment().getPropertySources().remove("application.yml");
        ApimsSpringContext.getEnvironment().getPropertySources().remove("configsets/v1.0.0/apims-spring-default.yml");
        ApimsSpringContext.getEnvironment().getPropertySources().remove(ApimsSpringContext.APIMS_FORCED_PROPERTIES);
    }

    @Test
    void serviceStartupListenerTest() {
        ApimsServiceStartupListener instance = () -> {};
        assertEquals(0, instance.getOrder());
    }

    @Test
    void environmentPostProcessorCodeCoverageTest() {
        ApimsEnvironmentPostProcessor instance = new ApimsEnvironmentPostProcessor();
        StandardEnvironment environment = new StandardEnvironment();
        assertNull(instance.resolveAppConfigProfile(null));
        assertEquals("", instance.resolveAppConfigProfile(""));
        assertEquals("dev", instance.resolveAppConfigProfile("dev"));
        assertEquals("dev-int", instance.resolveAppConfigProfile("dev-int"));
        assertEquals("dev-domain", instance.resolveAppConfigProfile("dev-test"));
        assertNull(instance.getEnv("test", null));
        assertNotNull(instance.getEnv("test", "default"));
        assertNotNull(instance.getEnv("java.io.tmpdir", "default"));
        assertNotNull(instance.getEnv("USER", "default"));

        assertNull(instance.findFirstYamlFilePropertySource(environment));
    }

    @Test
    void environmentPostProcessorTest() {
        ApimsEnvironmentPostProcessor instance = new ApimsEnvironmentPostProcessor();
        StandardEnvironment environment = new StandardEnvironment();
        SpringApplication application = mock(SpringApplication.class);
        IllegalStateException e = assertThrows(
                IllegalStateException.class, () -> instance.postProcessEnvironment(environment, application));
        assertEquals(
                "[Assertion failed] - argument/field 'application.yml' is required; it must not be null",
                e.getMessage());
        instance.loadYaml("testdata/env/not-exists.yml", false);
        assertThrows(
                IllegalStateException.class, () -> instance.loadYaml("testdata/env/not-exists-mandatory.yml", true));
        PropertySource<?> propertySource = instance.loadYaml("application.yml", "testdata/env/application.yml", true);
        assertNotNull(propertySource);
        environment.getPropertySources().addLast(propertySource);
        instance.postProcessEnvironment(environment, application);
    }

    @Test
    void apimsSpringContextCodeCoverageTest() {
        StandardEnvironment environment = new StandardEnvironment();
        final Map<String, Object> forcedProperties = ApimsSpringContext.getForcedProperties();
        final MapPropertySource forcedPropertiesSource =
                new MapPropertySource(ApimsSpringContext.APIMS_FORCED_PROPERTIES, forcedProperties);
        environment.getPropertySources().addFirst(forcedPropertiesSource);
        ApimsSpringContext.setEnvironment(environment);
        ApimsSpringContext.setSpringApplication(mock(SpringApplication.class));
        assertNotNull(ApimsSpringContext.getEnvironment());
        assertNotNull(ApimsSpringContext.getSpringApplication());
        assertEquals("default", ApimsSpringContext.resolvePlaceholders(null, "default"));
        assertEquals("default", ApimsSpringContext.resolvePlaceholders("${test.value:null}", "default"));
        assertEquals("default", ApimsSpringContext.resolveRequiredPlaceholders(null, "default"));
        assertEquals("default", ApimsSpringContext.resolveRequiredPlaceholders("${test.value:}", "default"));
        assertEquals("default", ApimsSpringContext.resolveRequiredPlaceholders("${test.value:null}", "default"));
        assertEquals("test.value", ApimsSpringContext.resolveRequiredPlaceholders("test.value", "default"));
        assertNotNull(ApimsSpringContext.getProperties(null));
        ApimsSpringContext.overrideProperty("test.forced.string-property", "value");
        ApimsSpringContext.overrideProperty("test.forced.boolean-property", true);
        assertEquals("value", ApimsSpringContext.getProperty("test.forced.string-property", "default"));
        assertEquals("value", ApimsSpringContext.resolvePlaceholders("test.forced.string-property", "default"));
        assertEquals("value", ApimsSpringContext.resolvePlaceholders("test.forced.string-property"));
        assertEquals(Boolean.TRUE, ApimsSpringContext.getProperty("test.forced.boolean-property", Boolean.class));
        assertEquals(
                Boolean.TRUE,
                ApimsSpringContext.getProperty("test.forced.boolean-property-not-exists", Boolean.class, true));
        assertEquals(
                "value", ApimsSpringContext.resolveRequiredPlaceholders("${test.forced.string-property}", "default"));
    }
}
