/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.tasking.core;

import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ApimsRunnableWrapper implements Runnable {
    private ApimsExecutorContext context;
    private Runnable target;

    void prepare(ApimsExecutorContext context, Runnable target) {
        this.context = context;
        this.target = target;
        context.incrementCommandCounter();
    }

    @Override
    @ApimsReportGeneratedHint
    public void run() {
        try {
            final Runnable theTarget = target;
            theTarget.run();
        } finally {
            context.decrementCommandCounter();
        }
    }
}
