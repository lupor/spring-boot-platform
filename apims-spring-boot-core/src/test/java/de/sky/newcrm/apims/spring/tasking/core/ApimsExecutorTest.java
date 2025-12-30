/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.tasking.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class ApimsExecutorTest {

    @Test
    void executorAwaitTest() throws InterruptedException {
        int commands = 10;
        ApimsExecutor executor = new ApimsExecutor(commands);
        for (int i = 0; i < commands; i++) {
            executor.execute(() -> doIt());
        }
        long counter = executor.await(commands * 1500, 1000);
        assertEquals(0, counter);
    }

    @Test
    void executorAwaitNotCompleteTest() throws InterruptedException {
        int commands = 10;
        ApimsExecutor executor = new ApimsExecutor(commands);
        for (int i = 0; i < commands; i++) {
            executor.execute(() -> doIt());
        }
        long counter = executor.await(1, 1);
        assertNotEquals(0, counter);
    }

    @SuppressWarnings("java:S2925")
    private void doIt() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignore) {
            // ignore
        }
    }
}
