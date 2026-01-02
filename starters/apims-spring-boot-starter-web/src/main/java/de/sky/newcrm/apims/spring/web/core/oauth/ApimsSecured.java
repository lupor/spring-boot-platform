/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.oauth;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ApimsSecured {
    /**
     * Returns the list of security configuration attributes (e.g.&nbsp;ROLE_USER,
     * ROLE_ADMIN).
     * @return String[] The secure method attributes
     */
    String[] value();
}
