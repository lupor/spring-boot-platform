/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core.mapper.legacy;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Map;

@SuppressWarnings({"java:S112"})
public class ObjectMapperUtils {

    private static final ApimsObjectMapperJson APIMS_OBJECT_MAPPER_JSON = new ApimsObjectMapperJson();
    private static final ApimsObjectMapperXml APIMS_OBJECT_MAPPER_XML = new ApimsObjectMapperXml();
    private static final ApimsObjectMapper APIMS_DEFAULT_OBJECT_MAPPER = APIMS_OBJECT_MAPPER_JSON;

    protected ObjectMapperUtils() {}

    public static ApimsObjectMapperJson getApimsObjectMapperJson() {
        return APIMS_OBJECT_MAPPER_JSON;
    }

    public static ApimsObjectMapperXml getApimsObjectMapperXml() {
        return APIMS_OBJECT_MAPPER_XML;
    }

    public static Map<String, Object> cloneMap(Map<String, Object> payload) {
        return APIMS_DEFAULT_OBJECT_MAPPER.cloneMap(payload);
    }

    public static <T> T cloneObject(T payload, Class<T> type) {
        return APIMS_DEFAULT_OBJECT_MAPPER.cloneObject(payload, type);
    }

    public static Map<String, Object> getValueAsMap(Object object) {
        return APIMS_DEFAULT_OBJECT_MAPPER.getValueAsMap(object);
    }

    public static String writeValueAsString(Object object) {
        return APIMS_DEFAULT_OBJECT_MAPPER.writeValueAsString(object);
    }

    public static List<Map<String, Object>> readList(String payload) {
        return APIMS_DEFAULT_OBJECT_MAPPER.readList(payload);
    }

    public static Map<String, Object> readMap(String payload) {
        return APIMS_DEFAULT_OBJECT_MAPPER.readMap(payload);
    }

    public static Map<String, Object> toFlattenMap(String payload) {
        return APIMS_DEFAULT_OBJECT_MAPPER.toFlattenMap(payload);
    }

    public static Map<String, Object> toFlattenMap(Map<String, Object> map) {
        return ApimsObjectMapper.toFlattenMap(map);
    }

    public static <T> T readValue(String payload, Class<T> type) {
        return APIMS_DEFAULT_OBJECT_MAPPER.readValue(payload, type);
    }

    public static <T> T readValue(Map<?, ?> payload, Class<T> type) {
        return APIMS_DEFAULT_OBJECT_MAPPER.readValue(payload, type);
    }

    public static <T> T readValue(String payload, TypeReference<T> valueTypeRef) {
        return APIMS_DEFAULT_OBJECT_MAPPER.readValue(payload, valueTypeRef);
    }

    public static <T> T readValue(byte[] payload, Class<T> type) {
        return APIMS_DEFAULT_OBJECT_MAPPER.readValue(payload, type);
    }

    public static <T> List<T> readListValue(String payload, Class<T> type) {
        return APIMS_DEFAULT_OBJECT_MAPPER.readListValue(payload, type);
    }

    public static <T> List<T> readListValue(byte[] payload, Class<T> type) {
        return APIMS_DEFAULT_OBJECT_MAPPER.readListValue(payload, type);
    }

    public static boolean isObjectEquals(Object payload, Object otherPayload) {
        return APIMS_DEFAULT_OBJECT_MAPPER.isObjectEquals(payload, otherPayload);
    }

    public static Object getNodeValue(Map<String, Object> map, String path) {
        return ApimsObjectMapper.getNodeValue(map, path);
    }

    public static void removeNode(Map<String, Object> map, String path) {
        ApimsObjectMapper.removeNode(map, path);
    }

    public static void upsertNode(Map<String, Object> map, String path, Object value) {
        ApimsObjectMapper.upsertNode(map, path, value);
    }

    public static void appendList(Map<String, Object> map, String path, List<Object> value) {
        ApimsObjectMapper.appendList(map, path, value);
    }

    public static class MapReader extends ApimsObjectMapper.MapReader {

        public static MapReader buildForMap(String payload) {
            return new MapReader(readMap(payload));
        }

        public static MapReader buildForList(String payload) {
            return new MapReader(readList(payload));
        }

        public static MapReader build(List<Map<String, Object>> rootObject) {
            return new MapReader(rootObject);
        }

        public static MapReader build(Map<String, Object> rootObject) {
            return new MapReader(rootObject);
        }

        public MapReader(Map<String, Object> rootObject) {
            super(rootObject);
        }

        public MapReader(List<Map<String, Object>> rootObject) {
            super(rootObject);
        }

        public MapReader(Object rootObject) {
            super(rootObject);
        }
    }
}
