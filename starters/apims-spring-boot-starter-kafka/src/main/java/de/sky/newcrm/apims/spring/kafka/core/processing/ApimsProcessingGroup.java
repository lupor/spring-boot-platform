/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core.processing;

public enum ApimsProcessingGroup {
    UNKNOWN,
    CONTROLLER,
    KAFKA_CONSUMER,
    KAFKA_PRODUCER,
    REST_CLIENT,
    SALESFORCE
}
