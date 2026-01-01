/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core;

import org.springframework.kafka.retrytopic.DestinationTopic;

public class ApimsDestinationDltPoisonNoOpsTopic extends DestinationTopic {

    @SuppressWarnings("java:S4449")
    public ApimsDestinationDltPoisonNoOpsTopic(String destinationName, String suffix) {
        super(
                destinationName,
                new DestinationTopic.Properties(0, suffix, null, 0, 0, null, null, (integer, throwable) -> false, 0));
    }

    @Override
    public boolean isDltTopic() {
        return false;
    }

    @Override
    public boolean isNoOpsTopic() {
        return true;
    }

    @Override
    public boolean isReusableRetryTopic() {
        return false;
    }

    @Override
    public boolean isMainTopic() {
        return false;
    }
}
