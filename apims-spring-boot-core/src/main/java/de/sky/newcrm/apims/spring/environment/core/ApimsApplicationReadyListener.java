/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.environment.core;

import de.sky.newcrm.apims.spring.telemetry.logging.core.ApimsAroundLoggingListenerSuppress;
import de.sky.newcrm.apims.spring.telemetry.metrics.aspects.ApimsAroundMetricsListenerSuppress;
import org.springframework.core.Ordered;

public interface ApimsApplicationReadyListener extends Ordered {

    @Override
    @ApimsAroundLoggingListenerSuppress(suppressMethodCall = true)
    @ApimsAroundMetricsListenerSuppress
    default int getOrder() {
        return 0;
    }

    @SuppressWarnings("java:S112")
    void onApplicationReadyEvent() throws Exception;
}
