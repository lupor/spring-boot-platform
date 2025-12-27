/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.aspects.core.around;

import java.lang.annotation.*;

/**
 * Annotation to map Exceptions.
 *
 * @see ApimsAspectAroundHandlerDefaultImpl
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ApimsAroundExceptionMappings {

    ApimsAroundExceptionMapping[] value() default {};
}
