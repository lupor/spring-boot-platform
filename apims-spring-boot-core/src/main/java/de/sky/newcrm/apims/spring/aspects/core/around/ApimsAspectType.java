/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.aspects.core.around;

public enum ApimsAspectType {
    COMPONENT,
    CONTROLLER,
    SFTP,
    MESSAGE,
    KAFKA,
    PUBSUB,
    REPOSITORY,
    RESTCLIENT,
    RESTCONTROLLER("CONTROLLER"),
    SERVICE,
    SOAPCLIENT,
    STORAGE,
    UNKNOWN;

    private final String internalValue;

    ApimsAspectType() {
        this(null);
    }

    ApimsAspectType(String internalValue) {
        this.internalValue = internalValue == null ? name() : internalValue;
    }

    public String internalValue() {
        return internalValue;
    }

    @Override
    public String toString() {
        return name();
    }
}
