/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.tasking.core;

import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;

class ApimsExecutorContext {

    private String id;
    private long runningCommandCounter = 0;
    private final Object lock = new Object();

    ApimsExecutorContext(String id) {
        this.id = id;
    }

    void incrementCommandCounter() {
        synchronized (lock) {
            runningCommandCounter++;
        }
    }

    @ApimsReportGeneratedHint
    void decrementCommandCounter() {
        synchronized (lock) {
            if (runningCommandCounter < 1) {
                runningCommandCounter = 0;
            } else {
                runningCommandCounter--;
            }
        }
    }

    @ApimsReportGeneratedHint
    String getId() {
        return id;
    }

    long getRunningCommandCounter() {
        synchronized (lock) {
            return runningCommandCounter;
        }
    }
}
