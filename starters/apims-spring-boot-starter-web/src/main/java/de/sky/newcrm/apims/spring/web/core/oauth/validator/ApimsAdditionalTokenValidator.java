/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.oauth.validator;

import org.springframework.core.Ordered;

public class ApimsAdditionalTokenValidator extends ApimsConfigTokenValidator {

    public ApimsAdditionalTokenValidator() {
        super("apims.web.auth.additional-token-validator");
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 400;
    }
}
