/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.oauth.exception;


import de.sky.newcrm.apims.spring.exceptions.ApimsRuntimeException;

public class ApimsOAuthException extends ApimsRuntimeException {

    public ApimsOAuthException() {}

    public ApimsOAuthException(String message) {
        super(message);
    }

    public ApimsOAuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApimsOAuthException(Throwable cause) {
        super(cause);
    }
}
