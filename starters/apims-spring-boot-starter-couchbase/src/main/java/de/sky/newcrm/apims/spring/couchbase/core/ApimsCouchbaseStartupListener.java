/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.couchbase.core;



import de.sky.newcrm.apims.spring.environment.core.ApimsApplicationReadyListener;
import de.sky.newcrm.apims.spring.exceptions.ApimsRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@SuppressWarnings({"java:S6212"})
public class ApimsCouchbaseStartupListener implements ApimsApplicationReadyListener {

    private final ApimsCouchbaseHealthIndicator couchbaseHealthIndicator;

    @Value("${apims.app.mocks.couchbase-mock-enabled:false}")
    private boolean mocksEnabled;

    @Value("${apims.couchbase.startup-check-warn-only:false}")
    private boolean startupCheckWarnOnly;

    private boolean applicationReadyEventConsumed = false;

    public ApimsCouchbaseStartupListener(ApimsCouchbaseHealthIndicator couchbaseHealthIndicator) {
        this.couchbaseHealthIndicator = couchbaseHealthIndicator;
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    @Override
    public synchronized void onApplicationReadyEvent() throws Exception {
        if (mocksEnabled || applicationReadyEventConsumed) {
            applicationReadyEventConsumed = true;
            return;
        }
        checkCouchbase();
        applicationReadyEventConsumed = true;
    }

    protected void checkCouchbase() {
        if (couchbaseHealthIndicator != null) {
            Health health = couchbaseHealthIndicator.health();
            if (Status.DOWN.equals(health.getStatus()) || Status.OUT_OF_SERVICE.equals(health.getStatus())) {
                if (startupCheckWarnOnly) {
                    log.warn("couchbase health indicator startup check failed. status: {}", health.getStatus());
                } else {
                    throw new ApimsRuntimeException(
                            "couchbase health indicator startup check failed. status: " + health.getStatus());
                }
            }
        }
    }
}
