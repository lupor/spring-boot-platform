/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.telemetry.logging;

import de.sky.newcrm.apims.spring.aspects.core.around.ApimsAroundContext;
import de.sky.newcrm.apims.spring.exceptions.BusinessException;
import de.sky.newcrm.apims.spring.flow.ApimsFlowContext;
import de.sky.newcrm.apims.spring.serialization.core.serializer.ApimsAroundObjectSerializer;
import de.sky.newcrm.apims.spring.utils.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
public class ApimsLoggingHelper {

    private static final String CONTEXT_DATA_TIMER_IDENTIFIER = "timer";

    private final ApimsAroundObjectSerializer apimsAroundObjectSerializer;

    @Value("${apims.aspects.listeners.logging.new-format-enabled:true}")
    private boolean newLogFormat;

    public ApimsLoggingHelper(ApimsAroundObjectSerializer apimsAroundObjectSerializer) {
        this.apimsAroundObjectSerializer = apimsAroundObjectSerializer;
    }

    protected static String getExceptionMessage(Exception e) {
        String message;
        if (e instanceof BusinessException) {
            message = e.toString();
        } else {
            message = ExceptionUtils.getLastExceptionMessage(e);
        }
        return message;
    }

    protected boolean isAppendArgsSuppressed() {
        ApimsAroundLoggingListenerSuppress apimsAroundLoggingListenerSuppress =
                findApimsAroundLoggingListenerSuppressAnnotation();
        return apimsAroundLoggingListenerSuppress != null
                && !apimsAroundLoggingListenerSuppress.suppressMethodCall()
                && apimsAroundLoggingListenerSuppress.suppressArgs();
    }

    protected boolean isReturnValueSuppressed() {
        ApimsAroundLoggingListenerSuppress apimsAroundLoggingListenerSuppress =
                findApimsAroundLoggingListenerSuppressAnnotation();
        return apimsAroundLoggingListenerSuppress != null
                && !apimsAroundLoggingListenerSuppress.suppressMethodCall()
                && apimsAroundLoggingListenerSuppress.suppressReturnValue();
    }

    protected ApimsAroundLoggingListenerSuppress findApimsAroundLoggingListenerSuppressAnnotation() {
        return ApimsFlowContext.get()
                .findCurrentMethodOrClassAnnotation(ApimsAroundLoggingListenerSuppress.class, false);
    }

    protected MethodLogMessage createBeforeMethodLogMessage(ApimsAroundContext context) {
        return MethodLogMessage.builder()
                .newestImplementation(newLogFormat)
                .beforeCase(true)
                .logIntro(getLoglineTabString(context, ">") + " " + context.getLoglineIntro())
                .build();
    }

    protected MethodLogMessage createAfterMethodLogMessage(ApimsAroundContext context) {
        return MethodLogMessage.builder()
                .newestImplementation(newLogFormat)
                .beforeCase(false)
                .logIntro(getLoglineTabString(context, "<") + " " + context.getLoglineIntro())
                .build();
    }

    protected void appendArgs(ApimsAroundContext context, StringBuilder msg) {
        Object[] args = context.getProceedingJoinPoint().getArgs();
        if (args.length != 0) {
            msg.append(getArgsContent(args, true));
        }
    }

    protected String getArgsContent(Object object, boolean checkIsAppendArgsSuppressed) {
        return checkIsAppendArgsSuppressed && isAppendArgsSuppressed() ? "___suppressed___" : getArgsContent(object);
    }

    protected String getArgsContent(Object object) {
        return serializeObject(object);
    }

    protected String getResultContent(Object object, boolean checkIsReturnValueSuppressed) {
        return getResultContent(object, checkIsReturnValueSuppressed, isReturnValueSuppressed());
    }

    protected String getResultContent(
            Object object, boolean checkIsReturnValueSuppressed, boolean returnValueSuppressed) {
        return checkIsReturnValueSuppressed && returnValueSuppressed ? "___suppressed___" : getResultContent(object);
    }

    protected String getResultContent(Object object) {
        return serializeObject(object);
    }

    protected String serializeObject(Object object) {
        return apimsAroundObjectSerializer.serialize(object);
    }

    protected String getLoglineTabString(ApimsAroundContext context, String directionChar) {

        int tabsCount = context.getActiveCallsCount();
        StringBuilder value = new StringBuilder().append("|");
        for (int i = 1; i < 7; i++) {
            value.append(i > tabsCount ? "-" : directionChar);
        }
        return value.toString();
    }

    protected String calculateContextDataTimerKey(ApimsAroundContext context) {
        return this.getClass().getSimpleName() + "." + context.getShortSignature() + "."
                + CONTEXT_DATA_TIMER_IDENTIFIER;
    }
}
