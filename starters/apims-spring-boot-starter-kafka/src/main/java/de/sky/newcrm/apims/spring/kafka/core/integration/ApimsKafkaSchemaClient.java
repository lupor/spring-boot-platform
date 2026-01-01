/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core.integration;

import de.sky.newcrm.apims.spring.integration.ApimsClient;
import de.sky.newcrm.apims.spring.kafka.core.integration.entity.SchemaResponse;

public interface ApimsKafkaSchemaClient extends ApimsClient {

    SchemaResponse getSchema(String topic);

    SchemaResponse getSchema(String topic, String version);
}
