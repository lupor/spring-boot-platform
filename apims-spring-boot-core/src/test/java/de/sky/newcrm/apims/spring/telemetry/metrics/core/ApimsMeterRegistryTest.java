/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.telemetry.metrics.core;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ApimsMeterRegistryTest {

    @Test
    void apimsMeterRegistryConfigurerTest() {
        ApimsMeterRegistryConfigurer instance = new ApimsMeterRegistryConfigurer();
        MeterRegistry meterRegistry = new SimpleMeterRegistry();
        Map<String, String> commonTags = new HashMap<>(Map.of(
                "key", "value",
                "apims_domain", ""));
        assertNotNull(instance.configure(meterRegistry, commonTags));
        assertEquals("value", instance.resolveTagValue("apims_domain", "value"));
        assertEquals("default", instance.resolveTagValue("apims_domain", ""));
        assertEquals("", instance.resolveTagValue("other", ""));
    }

    @Test
    void apimsMeterRegistryTest() {
        MeterRegistry meterRegistry = new SimpleMeterRegistry();
        ApimsMeterRegistry instance = new ApimsMeterRegistry(meterRegistry);
        instance.counter("counter", Tag.of("tag", "value"));
        Timer.Sample sample = instance.createTimerSample();
        assertNotEquals(0L, instance.timer(sample, "timer", Tag.of("tag", "value")));
        assertNotNull(instance.unwrap());
    }
}
