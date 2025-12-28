/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.flow;

import org.jspecify.annotations.Nullable;

import java.lang.annotation.Annotation;

@SuppressWarnings("java:S1452")
public interface ApimsFlowContext {

    static ApimsFlowContext get() {
        return ApimsFlowContextHolder.getFlowContext();
    }

    ApimsFlowMethodReference getInboundMethodReference();

    ApimsFlowMethodReference getCurrentMethodReference();

    <A extends Annotation> A findCurrentMethodOrClassAnnotation(
            @Nullable Class<A> annotationType, boolean topClassOnly);

    <A extends Annotation> A findCurrentMethodAnnotation(@Nullable Class<A> annotationType);

    <A extends Annotation> A findCurrentMethodParamAnnotation(@Nullable Class<A> annotationType, int paramIndex);

    <A extends Annotation> A findCurrentClassAnnotation(@Nullable Class<A> annotationType, boolean topClassOnly);
}
