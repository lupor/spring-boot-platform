/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.exceptions;

public class NoRetryableException extends RuntimeException {

    public NoRetryableException() {}

    public NoRetryableException(String message) {
        super(message);
    }

    public NoRetryableException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoRetryableException(Throwable cause) {
        super(cause);
    }

    public NoRetryableException(
            String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
