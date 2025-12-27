/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.exceptions;

public class InvalidResponseDataException extends ApimsRuntimeException {

    public InvalidResponseDataException() {}

    public InvalidResponseDataException(String message) {
        super(message);
    }

    public InvalidResponseDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidResponseDataException(Throwable cause) {
        super(cause);
    }

    public InvalidResponseDataException(
            String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
