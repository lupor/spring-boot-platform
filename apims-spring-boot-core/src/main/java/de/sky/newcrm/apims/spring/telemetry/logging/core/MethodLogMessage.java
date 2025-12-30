/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.telemetry.logging.core;

import de.sky.newcrm.apims.spring.aspects.core.around.ApimsAroundContext;
import java.text.DecimalFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

@Builder
@Getter
@Setter
public class MethodLogMessage {

    private static final double NANOS_TO_SECOND_SCALE = 1000D * 1000D * 1000D;

    private boolean newestImplementation;
    private boolean beforeCase;
    private String logIntro;

    @Builder.Default
    private LogMessageDetails details = new LogMessageDetails();

    @Value("${apims.aspects.listeners.logging.new-format-enabled:true}")
    private boolean newLogFormat;

    public String getMessage() {
        StringBuilder msg = new StringBuilder(500);
        msg.append(logIntro).append(" ");
        getDetails().appendDetailsMessage(newestImplementation, beforeCase, msg);
        return msg.toString();
    }

    public MethodLogMessage setDuration(long startTimeNs) {
        final long durationNs = System.nanoTime() - startTimeNs;
        final String durationSecondsValue = new DecimalFormat("0.000000").format(durationNs / NANOS_TO_SECOND_SCALE);
        getDetails().setDurationSecondsValue(durationSecondsValue);
        return this;
    }

    public void setSuccess(String resultContent) {
        getDetails().setResultIdentifier("OK");
        getDetails().setArgs(resultContent);
    }

    public void setError(Exception resultError) {
        getDetails().setResultIdentifier("ERROR");
        getDetails().setAdditionalArgs(resultError.getClass().getSimpleName());
        getDetails().setArgs(ApimsLoggingHelper.getExceptionMessage(resultError));
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

    protected String getLoglineTabString(ApimsAroundContext context, String directionChar) {

        int tabsCount = context.getActiveCallsCount();
        StringBuilder value = new StringBuilder().append("|");
        for (int i = 1; i < 7; i++) {
            value.append(i > tabsCount ? "-" : directionChar);
        }
        return value.toString();
    }
}
