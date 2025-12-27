/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.telemetry.metrics.core;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ApimsMeterRegistry {

    public static final String APIMS_EXECUTION_ERROR_COUNTER_NAME = "apims_exec_error_counter";
    public static final String APIMS_EXECUTION_COUNTER_NAME = "apims_exec_counter";
    public static final String APIMS_EXECUTION_TIMER_NAME = "apims_exec_timer";
    public static final String APIMS_EXECUTION_OUTBOUND_ERROR_COUNTER_NAME = "apims_exec_outbound_error_counter";
    public static final String APIMS_EXECUTION_OUTBOUND_COUNTER_NAME = "apims_exec_outbound_counter";
    public static final String APIMS_EXECUTION_OUTBOUND_TIMER_NAME = "apims_exec_outbound_timer";

    private final MeterRegistry meterRegistry;

    public Counter counter(String name, Tag... tags) {
        return counter(name, Arrays.asList(tags));
    }

    public Counter counter(String name, List<Tag> tags) {
        return meterRegistry.counter(name, tags);
    }

    public void counted(String name, Tag... tags) {
        counted(name, 1D, Arrays.asList(tags));
    }

    public void counted(String name, double amount, Tag... tags) {
        counted(name, amount, Arrays.asList(tags));
    }

    public void counted(String name, double amount, List<Tag> tags) {
        counter(name, tags).increment(amount);
    }

    public Timer.Sample createTimerSample() {
        return Timer.start(meterRegistry);
    }

    public long timer(Timer.Sample sample, String name, Tag... tags) {
        return timer(sample, name, Arrays.asList(tags));
    }

    public long timer(Timer.Sample sample, String name, List<Tag> tags) {
        return sample.stop(meterRegistry.timer(name, tags));
    }

    public MeterRegistry unwrap() {
        return meterRegistry;
    }
}
