/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core.serializers;

import de.sky.newcrm.apims.spring.core.support.report.ApimsReportGeneratedHint;
import de.sky.newcrm.apims.spring.core.utils.FunctionUtils;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ApimsKafkaSerializer extends KafkaAvroSerializer {

    protected String encoding = StandardCharsets.UTF_8.name();
    protected boolean keyCase = false;
    protected boolean serializeStringValueWithoutSchemaIdEnabled = false;
    protected Set<String> serializeStringValueWithoutSchemaIdTopics = new HashSet<>();

    public ApimsKafkaSerializer() {}

    public ApimsKafkaSerializer(SchemaRegistryClient client) {
        super(client);
    }

    public ApimsKafkaSerializer(SchemaRegistryClient client, Map<String, ?> props) {
        super(client, props);
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        keyCase = isKey;
        encoding = ApimsKafkaAvroUtils.getConfigValue(
                configs, isKey, "serializer.encoding", StandardCharsets.UTF_8.name());
        String baseKey = "apims.kafka.serializer";
        serializeStringValueWithoutSchemaIdEnabled = !isKey
                && Boolean.parseBoolean(ApimsKafkaAvroUtils.getConfigValue(
                        configs, false, baseKey + ".serialize-string-value-without-schema-id-enabled", "false"));
        if (serializeStringValueWithoutSchemaIdEnabled) {
            String[] topics = StringUtils.tokenizeToStringArray(
                    ApimsKafkaAvroUtils.getConfigValue(
                            configs, false, baseKey + ".serialize-string-value-without-schema-id-topics", "*"),
                    ",",
                    true,
                    true);
            serializeStringValueWithoutSchemaIdTopics.addAll(
                    Arrays.stream(topics).toList());
        }
        super.configure(configs, isKey);
    }

    @Override
    @ApimsReportGeneratedHint
    public byte[] serialize(String topic, Object value) {
        return serialize(topic, null, value);
    }

    @Override
    @ApimsReportGeneratedHint
    public byte[] serialize(String topic, Headers headers, Object value) {
        if (isSerializeToString(topic, value)) {
            return serializeStringWithoutSchemaId((String) value);
        } else {
            return super.serialize(topic, headers, value);
        }
    }

    protected boolean isSerializeToString(String topic, Object value) {
        return serializeStringValueWithoutSchemaIdEnabled
                && (value instanceof String)
                && (serializeStringValueWithoutSchemaIdTopics.contains("*")
                        || serializeStringValueWithoutSchemaIdTopics.contains(topic));
    }

    protected byte[] serializeStringWithoutSchemaId(String payload) {
        return FunctionUtils.executeIfNotNull(
                payload, null, () -> payload.getBytes(encoding), SerializationException.class);
    }
}
