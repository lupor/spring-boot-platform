/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core.serializers;

import de.sky.newcrm.apims.spring.kafka.core.integration.ApimsKafkaSchemaClient;
import de.sky.newcrm.apims.spring.serialization.core.mapper.legacy.ObjectMapperUtils;
import de.sky.newcrm.apims.spring.telemetry.logging.core.ApimsAroundLoggingListenerSuppress;
import de.sky.newcrm.apims.spring.telemetry.metrics.aspects.ApimsAroundMetricsListenerSuppress;
import de.sky.newcrm.apims.spring.utils.AssertUtils;
import lombok.RequiredArgsConstructor;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.specific.SpecificRecord;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import tech.allegro.schema.json2avro.converter.JsonAvroConverter;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@ApimsAroundMetricsListenerSuppress
@ApimsAroundLoggingListenerSuppress(suppressMethodCall = true)
@SuppressWarnings({"java:S6212"})
public class ApimsAvroRecordProducer {

    private final ApimsKafkaSchemaClient apimsKafkaSchemaClient;

    public ApimsAvroRecord createRecordForTopic(String topic, String jsonBody) {
        AssertUtils.notNullCheck("apimsKafkaSchemaClient", apimsKafkaSchemaClient);
        AssertUtils.hasLengthCheck("topic", topic);
        String schemaValue = apimsKafkaSchemaClient.getSchema(topic).getSchema();
        return createRecordForSchema(schemaValue, jsonBody);
    }

    public ApimsAvroRecord createRecordForSchema(String schemaValue, String jsonBody) {
        AssertUtils.hasLengthCheck("schemaValue", schemaValue);
        Schema schema = parseSchema(schemaValue);
        return createRecordForSchema(schema, jsonBody);
    }

    public ApimsAvroRecord createRecordForSchema(Schema schema, String jsonBody) {
        AssertUtils.notNullCheck("schema", schema);
        GenericData.Record genericRecord = convertToGenericDataRecord(schema, jsonBody);
        return new ApimsAvroRecord(schema, genericRecord);
    }

    public GenericData.Record convertToGenericDataRecord(Schema schema, String jsonBody) {
        if (jsonBody == null) {
            return null;
        }
        JsonAvroConverter converter = new JsonAvroConverter(
                ObjectMapperUtils.getApimsObjectMapperJson().unwrap());
        return converter.convertToGenericDataRecord(jsonBody.getBytes(StandardCharsets.UTF_8), schema);
    }

    public <T extends SpecificRecordBase & SpecificRecord> T createSpecificRecordForTopic(
            String topic, Class<T> type, String jsonBody) {
        AssertUtils.notNullCheck("apimsKafkaSchemaClient", apimsKafkaSchemaClient);
        AssertUtils.hasLengthCheck("topic", topic);
        String schemaValue = apimsKafkaSchemaClient.getSchema(topic).getSchema();
        return createSpecificRecordForSchema(schemaValue, type, jsonBody);
    }

    public <T extends SpecificRecordBase & SpecificRecord> T createSpecificRecordForSchema(
            String schemaValue, Class<T> type, String jsonBody) {
        AssertUtils.hasLengthCheck("schemaValue", schemaValue);
        Schema schema = parseSchema(schemaValue);
        return createSpecificRecordForSchema(schema, type, jsonBody);
    }

    public <T extends SpecificRecordBase & SpecificRecord> T createSpecificRecordForSchema(
            Schema schema, Class<T> type, String jsonBody) {
        AssertUtils.notNullCheck("schema", schema);
        return convertToSpecificRecord(schema, type, jsonBody);
    }

    public <T extends SpecificRecordBase & SpecificRecord> T convertToSpecificRecord(
            Schema schema, Class<T> type, String jsonBody) {
        if (jsonBody == null) {
            return null;
        }
        JsonAvroConverter converter = new JsonAvroConverter(
                ObjectMapperUtils.getApimsObjectMapperJson().unwrap());
        return converter.convertToSpecificRecord(jsonBody.getBytes(StandardCharsets.UTF_8), type, schema);
    }

    public Schema parseSchema(String schemaValue) {
        return new org.apache.avro.Schema.Parser().parse(schemaValue);
    }
}
