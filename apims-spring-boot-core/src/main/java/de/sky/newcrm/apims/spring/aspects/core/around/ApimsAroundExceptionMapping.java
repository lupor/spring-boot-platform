/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.aspects.core.around;

import de.sky.newcrm.apims.spring.exceptions.ApimsRuntimeException;
import java.lang.annotation.*;

/**
 * Definition to map Exceptions.
 *
 * @see ApimsAroundExceptionMappings
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ApimsAroundExceptionMapping {
    Class<? extends Exception>[] on() default {};

    Class<? extends Exception> raise() default ApimsRuntimeException.class;
}
