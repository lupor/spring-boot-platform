/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecureObject<T> implements SecureEntity<T> {

    private T secureValue;

    public SecureObject() {}

    public SecureObject(T secureValue) {
        this.secureValue = secureValue;
    }
}
