/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.exceptions;

public class ApimsNotAuthorizedException extends ApimsRuntimeException {

    public ApimsNotAuthorizedException(String message) {
        super(message);
    }
}
