/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.environment.core;

import org.springframework.core.Ordered;

public interface ApimsServiceStartupListener extends Ordered {

    @Override
    default int getOrder() {
        return 0;
    }

    @SuppressWarnings("java:S112")
    void execute() throws Exception;
}
