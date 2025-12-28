/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.telemetry.metrics.core.aspects;

import java.lang.annotation.*;

/**
 * Annotation to suppress the metrics calculation.
 *
 * @see ApimsAroundMetricsListener
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ApimsAroundMetricsListenerSuppress {}
