/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.aspects.core.around;

import org.aspectj.lang.ProceedingJoinPoint;

public interface ApimsAspectAroundHandler {

    @SuppressWarnings("java:S112")
    Object aroundMethod(ApimsAspectType type, ProceedingJoinPoint pjp) throws Throwable;
}
