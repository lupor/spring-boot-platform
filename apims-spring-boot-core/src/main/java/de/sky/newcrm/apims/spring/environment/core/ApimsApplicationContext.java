/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.environment.core;

import java.util.Map;
import org.jspecify.annotations.Nullable;

public interface ApimsApplicationContext {

    <T> T getBean(Class<T> requiredType);

    Object getBean(String name);

    <T> Map<String, T> getBeansOfType(@Nullable Class<T> type);
}
