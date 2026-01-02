/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.oauth.exception;

@SuppressWarnings("java:S110")
public class ApimsOAuthTokenNotValidException extends ApimsOAuthException {

    public ApimsOAuthTokenNotValidException() {}

    public ApimsOAuthTokenNotValidException(String message) {
        super(message);
    }

    public ApimsOAuthTokenNotValidException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApimsOAuthTokenNotValidException(Throwable cause) {
        super(cause);
    }
}
