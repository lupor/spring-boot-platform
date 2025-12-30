/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class ThreadUtilsTest {

    @Test
    void threadSleepTest() {
        assertDoesNotThrow(() -> ThreadUtils.sleep(1));
        final ThreadUtils.ThreadSleeperImpl impl = new ThreadUtils.ThreadSleeperImpl() {
            @Override
            protected void sleepImternal(long millis) throws InterruptedException {
                throw new InterruptedException("TEST");
            }
        };
        assertDoesNotThrow(() -> ThreadUtils.sleep(impl, 1));
    }
}
