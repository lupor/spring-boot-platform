/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.entities;

public class PasswordEntity extends SecureString {

    public PasswordEntity() {}

    public PasswordEntity(String securedValue) {
        super(securedValue);
    }
}
