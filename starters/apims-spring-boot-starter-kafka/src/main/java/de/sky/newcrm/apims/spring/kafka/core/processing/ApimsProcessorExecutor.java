/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core.processing;

import de.sky.newcrm.apims.spring.exceptions.ApimsRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("java:S6813")
public abstract class ApimsProcessorExecutor<I> {

    @Autowired
    private ApimsProcessor apimsProcessor;

    protected void process(ApimsProcessingGroup processingGroup, I inputEntity) {
        process(processingGroup, inputEntity, ApimsProcessingStrategy.PROCESS_FIRST_SUPPORTED_HANDLER_ONLY);
    }

    protected void process(
            ApimsProcessingGroup processingGroup, I inputEntity, ApimsProcessingStrategy processingStrategy) {

        int handled = 0;
        try {
            handled = apimsProcessor.process(processingGroup, inputEntity, processingStrategy);
        } catch (Exception e) {
            onProcessingException(inputEntity, e);
        }
        if (handled == 0) {
            onProcessingSkipped(inputEntity);
        }
    }

    protected void onProcessingException(I inputEntity, Exception exception) {
        throw exception instanceof RuntimeException e ? e : new ApimsRuntimeException(exception);
    }

    protected void onProcessingSkipped(I inputEntity) {}
}
