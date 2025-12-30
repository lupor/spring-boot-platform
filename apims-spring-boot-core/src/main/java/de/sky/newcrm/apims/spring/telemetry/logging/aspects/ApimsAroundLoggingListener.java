/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.telemetry.logging.aspects;

import de.sky.newcrm.apims.spring.aspects.core.around.ApimsAroundContext;
import de.sky.newcrm.apims.spring.aspects.core.around.ApimsAroundListener;
import de.sky.newcrm.apims.spring.exceptions.ApimsBusinessException;
import de.sky.newcrm.apims.spring.telemetry.logging.core.ApimsLoggingHelper;
import de.sky.newcrm.apims.spring.telemetry.logging.core.MethodLogMessage;
import de.sky.newcrm.apims.spring.utils.VeracodeMitigationUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.core.Ordered;
import org.springframework.web.client.HttpClientErrorException;

@SuppressWarnings({"java:S3776", "java:S6212"})
@Slf4j
public class ApimsAroundLoggingListener implements ApimsAroundListener {

    private static final double NANOS_TO_SECOND_SCALE = 1000D * 1000D * 1000D;
    private final ApimsLoggingHelper apimsLoggingHelper;

    public ApimsAroundLoggingListener(ApimsLoggingHelper apimsLoggingHelper) {
        this.apimsLoggingHelper = apimsLoggingHelper;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 50;
    }

    @Override
    public void beforeAroundMethod(ApimsAroundContext context) {

        if (context.isApimsAroundLoggingListenerSuppressed()) {
            return;
        }
        context.getData().put(apimsLoggingHelper.calculateContextDataTimerKey(context), System.nanoTime());
        final Logger logger = context.getLogger();
        if (context.getActiveCallsCount() > 1 && !log.isDebugEnabled()) {
            return;
        }
        MethodLogMessage methodLogMessage = apimsLoggingHelper.createBeforeMethodLogMessage(context);
        final StringBuilder args = new StringBuilder(500);
        apimsLoggingHelper.appendArgs(context, args);

        methodLogMessage.getDetails().setArgs(args.toString());
        if (context.getActiveCallsCount() == 1 || log.isDebugEnabled()) {
            logger.info("{}", VeracodeMitigationUtils.sanitizeLogValues(methodLogMessage.getMessage()));
        }
    }

    @Override
    @SuppressWarnings("java:S2629")
    public void afterAroundMethod(ApimsAroundContext context, Object result, Exception resultError) {

        if (context.isApimsAroundLoggingListenerSuppressed()) {
            return;
        }

        if (context.getActiveCallsCount() > 1 && !log.isDebugEnabled()) {
            return;
        }

        final long startTimeNs =
                (long) context.getData().remove(apimsLoggingHelper.calculateContextDataTimerKey(context));

        MethodLogMessage methodLogMessage =
                apimsLoggingHelper.createAfterMethodLogMessage(context).setDuration(startTimeNs);

        final Logger logger = context.getLogger();
        if (resultError == null) {
            if (context.getActiveCallsCount() == 1 || log.isDebugEnabled()) {
                String resultContent =
                        context.isVoidMethod() ? "void" : apimsLoggingHelper.getResultContent(result, true);
                methodLogMessage.setSuccess(resultContent);
                logger.info("{}", VeracodeMitigationUtils.sanitizeLogValues(methodLogMessage.getMessage()));
            }
        } else {
            methodLogMessage.setError(resultError);
            boolean logAsError = false;
            if (context.getActiveCallsCount() == 1) {
                ApimsBusinessException annotation = resultError.getClass().getAnnotation(ApimsBusinessException.class);
                logAsError = annotation == null || annotation.logAsError();
                if (resultError instanceof HttpClientErrorException.NotFound) logAsError = false;
            }
            if ((context.getActiveCallsCount() == 1 || log.isDebugEnabled())) {
                if (logAsError) {
                    logger.error("{}", VeracodeMitigationUtils.sanitizeLogValues(methodLogMessage.getMessage()));
                } else {
                    logger.warn("{}", VeracodeMitigationUtils.sanitizeLogValues(methodLogMessage.getMessage()));
                }
            }
        }
    }
}
