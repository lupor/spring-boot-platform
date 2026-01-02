/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.entities;

public class UserNameEntity extends SecureString {

    public UserNameEntity() {}

    public UserNameEntity(String securedValue) {
        super(securedValue);
    }
}
