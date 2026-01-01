/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core.serializers;

import de.sky.newcrm.apims.spring.core.support.report.ApimsReportGeneratedHint;
import de.sky.newcrm.apims.spring.core.utils.FunctionUtils;
import io.confluent.kafka.schemaregistry.ParsedSchema;
import io.confluent.kafka.schemaregistry.client.SchemaMetadata;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"java:S135", "java:S1141", "java:S3776", "java:S6201", "java:S6212"})
public class ApimsKafkaDeserializer extends KafkaAvroDeserializer {

    protected String encoding = StandardCharsets.UTF_8.name();
    protected boolean enabled = true;
    protected boolean deserializeToGenericsAllowed = true;
    protected boolean deserializeValueWithoutSchemaIdToString = true;
    protected boolean keyCase = false;
    private static final Map<Integer, Schema> schemas = new ConcurrentHashMap<>(100);

    @ApimsReportGeneratedHint
    public ApimsKafkaDeserializer() {}

    @ApimsReportGeneratedHint
    public ApimsKafkaDeserializer(SchemaRegistryClient client) {
        super(client);
    }

    @ApimsReportGeneratedHint
    public ApimsKafkaDeserializer(SchemaRegistryClient client, Map<String, ?> props) {
        super(client, props);
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        keyCase = isKey;
        encoding = ApimsKafkaAvroUtils.getConfigValue(
                configs, isKey, "deserializer.encoding", StandardCharsets.UTF_8.name());
        String baseKey = "apims.kafka.deserializer";
        enabled = !isKey
                && Boolean.parseBoolean(
                        ApimsKafkaAvroUtils.getConfigValue(configs, false, baseKey + ".enabled", "true"));
        deserializeValueWithoutSchemaIdToString = enabled
                && Boolean.parseBoolean(ApimsKafkaAvroUtils.getConfigValue(
                        configs, false, baseKey + ".deserialize-value-without-schema-id-to-string", "true"));
        deserializeToGenericsAllowed = Boolean.parseBoolean(ApimsKafkaAvroUtils.getConfigValue(
                configs, false, baseKey + ".deserialize-to-generics-allowed", "false"));
        super.configure(configs, isKey);
    }

    @Override
    @ApimsReportGeneratedHint
    public Object deserialize(String topic, byte[] payload) {
        return deserialize(topic, payload, null);
    }

    @Override
    @ApimsReportGeneratedHint
    public Object deserialize(String topic, byte[] payload, Schema readerSchema) {
        return deserialize(topic, (Headers) null, payload, readerSchema);
    }

    @Override
    @ApimsReportGeneratedHint
    public Object deserialize(String topic, Headers headers, byte[] payload) {
        return deserialize(topic, headers, payload, null);
    }

    @Override
    @ApimsReportGeneratedHint
    public Object deserialize(String topic, Headers headers, byte[] payload, Schema readerSchema) {
        if (payload == null) {
            return null;
        }
        if (!enabled) {
            return super.deserialize(topic, headers, payload, readerSchema);
        }
        int schemaId = getSchemaId(payload);
        if (schemaId == 0) {
            if (deserializeValueWithoutSchemaIdToString) {
                return deserializeToString(payload);
            }
            return super.deserialize(topic, headers, payload, readerSchema);
        }
        try {
            return super.deserialize(topic, headers, payload, readerSchema);
        } catch (SerializationException e) {
            // fallback by older schema versions
            try {
                Schema targetSchema = getCachedSchema(schemaId);
                if (targetSchema != null) {
                    try {
                        return deserializeDelegate(topic, payload, targetSchema);
                    } catch (SerializationException ex) {
                        // unregister and retry
                        unregisterCachedSchema(schemaId);
                    }
                }
                String subjectName = topic + "-value";
                List<Integer> versionList = getAllVersions(subjectName);
                versionList.sort((o1, o2) -> Integer.compare(o2, o1)); // desc
                SchemaMetadata schemaMetadata;
                Schema schema = null;
                Object object = null;
                boolean deserializeOk = false;
                for (Integer version : versionList) {
                    schemaMetadata = getSchemaMetadata(subjectName, version);
                    if (schemaMetadata == null
                            || (!deserializeToGenericsAllowed && schemaId == schemaMetadata.getId())) {
                        continue;
                    }
                    ParsedSchema parsedSchema = getSchemaById(schemaMetadata.getId());
                    if (parsedSchema == null) {
                        continue;
                    }
                    try {
                        schema = (Schema) parsedSchema.rawSchema();
                        if (schema == null) {
                            continue;
                        }
                        object = deserializeDelegate(topic, payload, schema);
                        boolean genericItemFound =
                                object instanceof GenericData || object instanceof GenericData.Record;
                        if (object != null && (deserializeToGenericsAllowed || !genericItemFound)) {
                            deserializeOk = true;
                            break;
                        }
                    } catch (SerializationException ex) {
                        // ignore
                    }
                }
                if (deserializeOk) {
                    registerCachedSchema(schemaId, schema);
                    return object;
                }
                return super.deserialize(topic, headers, payload, null);
            } catch (IOException | RestClientException ex) {
                throw e;
            }
        }
    }

    protected int getSchemaId(byte[] payload) {
        ByteBuffer buffer = getByteBufferOrNull(payload);
        return buffer == null ? 0 : buffer.getInt();
    }

    protected ByteBuffer getByteBufferOrNull(byte[] payload) {
        ByteBuffer buffer = ByteBuffer.wrap(payload);
        if (buffer.get() != 0) {
            return null;
        } else {
            return buffer;
        }
    }

    protected String deserializeToString(byte[] payload) {
        return FunctionUtils.executeIfNotNull(
                payload, null, () -> new String(payload, encoding), SerializationException.class);
    }

    @ApimsReportGeneratedHint
    protected Object deserializeDelegate(String topic, byte[] payload, Schema readerSchema) {
        return super.deserialize(topic, payload, readerSchema);
    }

    @ApimsReportGeneratedHint
    protected List<Integer> getAllVersions(String subject) throws IOException, RestClientException {
        return getSchemaRegistry().getAllVersions(subject);
    }

    @ApimsReportGeneratedHint
    protected SchemaMetadata getSchemaMetadata(String subject, int version) throws IOException, RestClientException {
        return getSchemaRegistry().getSchemaMetadata(subject, version);
    }

    @Override
    @ApimsReportGeneratedHint
    public ParsedSchema getSchemaById(int id) throws IOException, RestClientException {
        return getSchemaRegistry().getSchemaById(id);
    }

    @ApimsReportGeneratedHint
    protected SchemaRegistryClient getSchemaRegistry() {
        return schemaRegistry;
    }

    public static Schema getCachedSchema(int id) {
        return schemas.get(id);
    }

    public static void registerCachedSchema(int id, Schema schema) {
        schemas.put(id, schema);
    }

    @ApimsReportGeneratedHint
    public static Schema unregisterCachedSchema(int id) {
        return schemas.remove(id);
    }

    @ApimsReportGeneratedHint
    public static void clearSchemaCache() {
        schemas.clear();
    }
}
