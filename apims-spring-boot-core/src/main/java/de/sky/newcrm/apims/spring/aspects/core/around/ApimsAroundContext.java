/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.aspects.core.around;

import de.sky.newcrm.apims.spring.flow.ApimsFlowContext;
import de.sky.newcrm.apims.spring.telemetry.logging.core.ApimsAroundLoggingListenerSuppress;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;

@Getter
@Setter
@Builder
public class ApimsAroundContext {

    private ProceedingJoinPoint proceedingJoinPoint;
    private Class<?> declaringType;
    private ApimsAspectType type;
    private String signature;
    private String shortSignature;
    private boolean voidMethod;
    private String returnType;
    private String loglineIntro;
    private Map<String, Object> data;
    private int activeCallsCount;
    private boolean createNewSpan;
    private int spanTagMaxLength;
    private Logger logger;
    private int spanTagCount;

    public boolean isApimsAroundLoggingListenerSuppressed() {
        ApimsAroundLoggingListenerSuppress apimsAroundLoggingListenerSuppress =
                findApimsAroundLoggingListenerSuppressAnnotation();
        return apimsAroundLoggingListenerSuppress != null && apimsAroundLoggingListenerSuppress.suppressMethodCall();
    }

    public ApimsAroundLoggingListenerSuppress findApimsAroundLoggingListenerSuppressAnnotation() {
        return ApimsFlowContext.get()
                .findCurrentMethodOrClassAnnotation(ApimsAroundLoggingListenerSuppress.class, false);
    }
}
