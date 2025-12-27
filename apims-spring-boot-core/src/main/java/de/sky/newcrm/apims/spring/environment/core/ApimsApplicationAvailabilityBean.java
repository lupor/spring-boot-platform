/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.environment.core;

import de.sky.newcrm.apims.spring.context.core.ApimsMdc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.availability.ApplicationAvailabilityBean;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.AvailabilityState;
import org.springframework.context.ApplicationEventPublisher;

@Slf4j
@SuppressWarnings("java:S2177")
public class ApimsApplicationAvailabilityBean extends ApplicationAvailabilityBean {

    @Autowired
    private ApimsMdc mdc;

    @Override
    public void onApplicationEvent(AvailabilityChangeEvent<?> event) {

        Class<? extends AvailabilityState> type = getStateType(event.getState());
        mdc.put(ApimsMdc.MDC_LOG_TYPE_KEY, ApimsMdc.MDC_LOG_TYPE_HEALTH);
        log.info("[HEALTH] : {}", getLogMessage(type, event));
        mdc.remove(ApimsMdc.MDC_LOG_TYPE_KEY);
        super.onApplicationEvent(event);
    }

    protected <S extends AvailabilityState> String getLogMessage(Class<S> type, AvailabilityChangeEvent<?> event) {
        AvailabilityChangeEvent<S> lastChangeEvent = getLastChangeEvent(type);
        StringBuilder message =
                new StringBuilder("Application availability state " + type.getSimpleName() + " changed");
        message.append((lastChangeEvent != null) ? " from " + lastChangeEvent.getState() : "");
        message.append(" to ").append(event.getState());
        message.append(getSourceDescription(event.getSource()));
        return message.toString();
    }

    protected String getSourceDescription(Object source) {
        if (source == null || source instanceof ApplicationEventPublisher) {
            return "";
        }
        return ": "
                + ((source instanceof Throwable) ? source : source.getClass().getName());
    }

    @SuppressWarnings("unchecked")
    protected Class<? extends AvailabilityState> getStateType(AvailabilityState state) {
        Class<?> type = (state instanceof Enum<?> e) ? e.getDeclaringClass() : state.getClass();
        return (Class<? extends AvailabilityState>) type;
    }
}
