/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.aspects.core.around;

import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;

@Aspect
@AllArgsConstructor
@Order(200)
public class ApimsComponentAspect implements ApimsAspect {

    private final ApimsAspectAroundHandler handler;

    @Pointcut("within(@org.springframework.stereotype.Component *)")
    private void anyComponent() {
        // pointcut marker
    }

    @Pointcut("execution(public * de.sky.newcrm..*.*(..))")
    protected void anyMethod() {
        // pointcut marker
    }

    @Around("anyComponent() && anyMethod()")
    public Object aroundMethod(ProceedingJoinPoint pjp) throws Throwable {

        return handler.aroundMethod(ApimsAspectType.COMPONENT, pjp);
    }
}
