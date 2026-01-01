/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core.serializers;

import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.kafka.common.header.Headers;

import java.util.Map;

@Slf4j
@SuppressWarnings({"java:S6201", "java:S6212"})
public class ApimsKafkaStringDeserializer extends ApimsKafkaDeserializer {

    @ApimsReportGeneratedHint
    public ApimsKafkaStringDeserializer() {}

    @ApimsReportGeneratedHint
    public ApimsKafkaStringDeserializer(SchemaRegistryClient client) {
        super(client);
    }

    @ApimsReportGeneratedHint
    public ApimsKafkaStringDeserializer(SchemaRegistryClient client, Map<String, ?> props) {
        super(client, props);
    }

    @Override
    @ApimsReportGeneratedHint
    public Object deserialize(String topic, Headers headers, byte[] payload, Schema readerSchema) {
        if (payload == null) {
            return null;
        }
        int schemaId = getSchemaId(payload);
        if (schemaId == 0) {
            return deserializeToString(payload);
        }
        Object object = super.deserialize(topic, headers, payload, readerSchema);
        if (object == null) {
            return null;
        }
        if (object instanceof String) {
            return object;
        }
        // see: org.apache.avro.specific.SpecificRecordBase
        return String.valueOf(object);
    }
}
