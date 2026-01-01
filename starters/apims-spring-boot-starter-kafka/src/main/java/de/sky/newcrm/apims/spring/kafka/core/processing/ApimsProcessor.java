/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core.processing;

import de.sky.newcrm.apims.spring.exceptions.InvalidRequestDataBusinessException;
import de.sky.newcrm.apims.spring.utils.AssertUtils;
import de.sky.newcrm.apims.spring.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.OrderComparator;

import java.lang.reflect.Method;
import java.util.List;

@Slf4j
public class ApimsProcessor {

    private final List<ApimsProcessingHandler<?, ?>> handlers;

    public ApimsProcessor(List<ApimsProcessingHandler<?, ?>> handlers) {
        handlers.sort(new OrderComparator());
        this.handlers = handlers;
    }

    public int process(ApimsProcessingGroup processingGroup, Object inputEntity) {
        return process(processingGroup, inputEntity, ApimsProcessingStrategy.PROCESS_FIRST_SUPPORTED_HANDLER_ONLY);
    }

    @SuppressWarnings("java:S135")
    public int process(
            ApimsProcessingGroup processingGroup, Object inputEntity, ApimsProcessingStrategy processingStrategy) {
        return process(processingGroup.name(), inputEntity, processingStrategy);
    }

    @SuppressWarnings("java:S135")
    public int process(
            final String processingGroup, final Object inputEntity, final ApimsProcessingStrategy processingStrategy) {

        AssertUtils.notNullCheck("inputEntity", inputEntity);
        final List<ApimsProcessingHandler<?, ?>> groupHandlers = handlers.stream()
                .filter(h -> processingGroup.equalsIgnoreCase(h.getProcessingGroupName()))
                .toList();
        int executedHandlerCount = 0;
        for (ApimsProcessingHandler<?, ?> handler : groupHandlers) {

            if (executedHandlerCount > 0
                    && ApimsProcessingStrategy.PROCESS_FIRST_SUPPORTED_HANDLER_ONLY.equals(processingStrategy)) {
                break;
            }
            Method supportsInboundData =
                    ObjectUtils.findMethod(handler.getClass(), "supportsInboundData", inputEntity.getClass());
            if (supportsInboundData == null) {
                continue;
            }
            if (!((boolean) ObjectUtils.invokeMethod(supportsInboundData, handler, inputEntity))) {
                log.trace(
                        "processing handler {} dosnt support input entity of type {}. skip.",
                        handler.getClass(),
                        inputEntity.getClass());
                continue;
            }
            executedHandlerCount++;
            try {
                InvalidRequestDataBusinessException.createValidator()
                        .validateAnnotations("inputEntity", inputEntity)
                        .throwIfContainsViolations();
                ObjectUtils.invokeMethod(
                        ObjectUtils.findMethod(handler.getClass(), "validateInboundData"), handler, inputEntity);
            } catch (Exception e) {
                ObjectUtils.invokeMethod(
                        ObjectUtils.findMethod(handler.getClass(), "handleValidateInboundDataException"),
                        handler,
                        inputEntity,
                        e);
                continue;
            }
            Object entity = ObjectUtils.invokeMethod(
                    ObjectUtils.findMethod(handler.getClass(), "convertInboundData", inputEntity.getClass()),
                    handler,
                    inputEntity);
            AssertUtils.notNullCheck("convertedEntity", inputEntity);
            try {
                InvalidRequestDataBusinessException.createValidator()
                        .validateAnnotations("convertedEntity", entity)
                        .throwIfContainsViolations();
                ObjectUtils.invokeMethod(
                        ObjectUtils.findMethod(handler.getClass(), "validateProcessingData"), handler, entity);
            } catch (Exception e) {
                ObjectUtils.invokeMethod(
                        ObjectUtils.findMethod(handler.getClass(), "handleValidateProcessingDataException"),
                        handler,
                        inputEntity,
                        entity,
                        e);
                continue;
            }
            try {
                ObjectUtils.invokeMethod(
                        ObjectUtils.findMethod(handler.getClass(), "process", entity.getClass()), handler, entity);
            } catch (Exception e) {
                ObjectUtils.invokeMethod(
                        ObjectUtils.findMethod(handler.getClass(), "handleProcessException"),
                        handler,
                        inputEntity,
                        entity,
                        e);
            }
        }
        return executedHandlerCount;
    }
}
