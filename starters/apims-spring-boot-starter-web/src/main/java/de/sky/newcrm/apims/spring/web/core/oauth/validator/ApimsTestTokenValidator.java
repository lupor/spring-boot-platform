/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.oauth.validator;

import org.springframework.core.Ordered;

public class ApimsTestTokenValidator extends ApimsConfigTokenValidator {

    public ApimsTestTokenValidator() {
        super("apims.web.auth.test-token-validator");
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
