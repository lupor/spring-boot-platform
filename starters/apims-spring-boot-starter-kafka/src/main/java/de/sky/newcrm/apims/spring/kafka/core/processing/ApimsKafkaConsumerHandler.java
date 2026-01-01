/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core.processing;

import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import de.sky.newcrm.apims.spring.exceptions.ApimsRuntimeException;
import de.sky.newcrm.apims.spring.exceptions.InvalidRequestDataBusinessException;
import de.sky.newcrm.apims.spring.exceptions.NoRetryableException;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public abstract class ApimsKafkaConsumerHandler<I, E> extends ApimsProcessingHandler<I, E> {

    protected ApimsKafkaConsumerHandler() {}

    @Override
    public final String getProcessingGroupName() {
        return ApimsProcessingGroup.KAFKA_CONSUMER.name();
    }

    @Override
    @ApimsReportGeneratedHint
    public void handleValidateInboundDataException(I inboundEntity, Exception exception) {
        defaultHandleValidateInboundDataException(inboundEntity, exception);
    }

    @Override
    @ApimsReportGeneratedHint
    protected void defaultHandleValidateInboundDataException(I inboundEntity, Exception exception) {
        // default kafka: map InvalidRequestDataBusinessException to NoRetryableException
        // no retry, if configured: send to deadletter topic
        if (exception instanceof InvalidRequestDataBusinessException) {
            throw new NoRetryableException(exception);
        }
        super.defaultHandleValidateInboundDataException(inboundEntity, exception);
    }

    @Override
    @ApimsReportGeneratedHint
    public void handleValidateProcessingDataException(I inboundEntity, E processingEntity, Exception exception) {
        defaultHandleValidateProcessingDataException(inboundEntity, processingEntity, exception);
    }

    @Override
    @ApimsReportGeneratedHint
    protected void defaultHandleValidateProcessingDataException(
            I inboundEntity, E processingEntity, Exception exception) {
        // default kafka: map InvalidRequestDataBusinessException to ApimsRuntimeException
        // means: wrong inbound validation or wrong mapper (see: validateInboundData, convertInboundData)
        // if configured: send to retry topic
        // if central dead letter solution is activated (future impl): Map to NoRetryableException
        if (exception instanceof InvalidRequestDataBusinessException) {
            throw new ApimsRuntimeException(exception);
        }
        super.defaultHandleValidateInboundDataException(inboundEntity, exception);
    }

    @Override
    @ApimsReportGeneratedHint
    public void handleProcessException(I inboundEntity, E processingEntity, Exception exception) {
        super.defaultHandleProcessException(inboundEntity, processingEntity, exception);
    }
}
