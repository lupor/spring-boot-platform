/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core.processing;

import de.sky.newcrm.apims.spring.exceptions.ApimsRuntimeException;
import de.sky.newcrm.apims.spring.telemetry.logging.core.ApimsAroundLoggingListenerSuppress;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;

public abstract class ApimsProcessingHandler<I, E> implements Ordered, InitializingBean {

    @Override
    @ApimsAroundLoggingListenerSuppress(suppressMethodCall = true)
    public int getOrder() {
        return 0;
    }

    @Override
    @ApimsAroundLoggingListenerSuppress(suppressMethodCall = true)
    public void afterPropertiesSet() throws Exception {}

    @ApimsAroundLoggingListenerSuppress(suppressMethodCall = true)
    public String getProcessingGroupName() {
        return ApimsProcessingGroup.UNKNOWN.name();
    }

    @ApimsAroundLoggingListenerSuppress(suppressMethodCall = true)
    public abstract boolean supportsInboundData(@NotNull I inboundEntity);

    @NotNull
    public abstract E convertInboundData(@NotNull I inboundEntity);

    public void validateInboundData(@Valid @NotNull I inboundEntity) {
        // additional validations
    }

    public abstract void handleValidateInboundDataException(@NotNull I inboundEntity, @NotNull Exception exception);

    protected void defaultHandleValidateInboundDataException(@NotNull I inboundEntity, @NotNull Exception exception) {
        throw exception instanceof RuntimeException e ? e : new ApimsRuntimeException(exception);
    }

    public void validateProcessingData(@Valid @NotNull E processingEntity) {
        // additional validations
    }

    public abstract void handleValidateProcessingDataException(
            @NotNull I inboundEntity, @NotNull E processingEntity, @NotNull Exception exception);

    protected void defaultHandleValidateProcessingDataException(
            @NotNull I inboundEntity, @NotNull E processingEntity, @NotNull Exception exception) {
        throw exception instanceof RuntimeException e ? e : new ApimsRuntimeException(exception);
    }

    public abstract void process(@NotNull E processingEntity);

    public abstract void handleProcessException(
            @NotNull I inboundEntity, @NotNull E processingEntity, @NotNull Exception exception);

    protected void defaultHandleProcessException(
            @NotNull I inboundEntity, @NotNull E processingEntity, @NotNull Exception exception) {
        throw exception instanceof RuntimeException e ? e : new ApimsRuntimeException(exception);
    }
}
