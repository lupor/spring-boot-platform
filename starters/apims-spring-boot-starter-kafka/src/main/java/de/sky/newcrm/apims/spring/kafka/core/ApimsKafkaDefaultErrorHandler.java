/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core;

import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

public class ApimsKafkaDefaultErrorHandler extends DefaultErrorHandler {

    public static final long DEFAULT_INTERVAL = FixedBackOff.DEFAULT_INTERVAL;
    public static final long DEFAULT_MAX_FAILURES = FixedBackOff.UNLIMITED_ATTEMPTS;

    public ApimsKafkaDefaultErrorHandler() {
        this(DEFAULT_INTERVAL, DEFAULT_MAX_FAILURES);
    }

    public ApimsKafkaDefaultErrorHandler(long interval, long maxFailures) {
        super(new FixedBackOff(interval, maxFailures < 1 ? FixedBackOff.UNLIMITED_ATTEMPTS : (maxFailures - 1)));
    }
}
