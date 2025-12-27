/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.exceptions;

@SuppressWarnings("java:S110")
public class ApimsNotInRoleException extends ApimsNotAuthorizedException {
    public ApimsNotInRoleException(String message) {
        super(message);
    }
}
