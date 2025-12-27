/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

public class ThreadUtils {

    private ThreadUtils() {}

    public static void sleep(long millis) {
        sleep(new ThreadSleeperImpl(), millis);
    }

    public static void sleep(ThreadSleeper sleeper, long millis) {
        sleeper.sleep(millis);
    }

    public interface ThreadSleeper {
        void sleep(long millis);
    }

    public static class ThreadSleeperImpl implements ThreadSleeper {
        @Override
        public void sleep(long millis) {
            try {
                sleepImternal(millis);
            } catch (InterruptedException e) {
                handle(e);
            }
        }

        protected void sleepImternal(long millis) throws InterruptedException {
            Thread.sleep(millis);
        }

        @SuppressWarnings("java:S1172")
        protected void handle(InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
