/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.environment.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SpringBootBypassLoggerFactory {

    public static final String SPRING_BOOT_LOGGER_PACKAGE = "org.springframework.boot.apims";

    private SpringBootBypassLoggerFactory() {}

    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger("%s.%s".formatted(SPRING_BOOT_LOGGER_PACKAGE, clazz.getSimpleName()));
    }
}
