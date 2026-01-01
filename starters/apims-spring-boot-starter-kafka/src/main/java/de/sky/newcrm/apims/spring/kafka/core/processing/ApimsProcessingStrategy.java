/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core.processing;

public enum ApimsProcessingStrategy {
    PROCESS_ALL_HANDLERS,
    PROCESS_FIRST_SUPPORTED_HANDLER_ONLY
}
