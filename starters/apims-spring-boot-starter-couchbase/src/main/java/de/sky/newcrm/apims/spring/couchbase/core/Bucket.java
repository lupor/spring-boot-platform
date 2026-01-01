/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.couchbase.core;

import java.lang.annotation.*;
import org.springframework.data.annotation.QueryAnnotation;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Documented
@QueryAnnotation
public @interface Bucket {

    /**
     * The target bucket.
     * Expressions must resolve to a String.
     * SpEL is NOT supported.
     * Default '' -> spring.couchbase.bucket
     *
     * @return the bucket name.
     */
    String value() default "";
}
