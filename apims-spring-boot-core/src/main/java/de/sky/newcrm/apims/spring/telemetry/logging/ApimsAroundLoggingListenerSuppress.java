/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.telemetry.logging;

import java.lang.annotation.*;

/**
 * Annotation to suppress the method return value and args.
 *
 * @see ApimsAroundLoggingListener
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ApimsAroundLoggingListenerSuppress {

    boolean suppressReturnValue() default true;

    boolean suppressArgs() default true;

    boolean suppressMethodCall() default false;
}
