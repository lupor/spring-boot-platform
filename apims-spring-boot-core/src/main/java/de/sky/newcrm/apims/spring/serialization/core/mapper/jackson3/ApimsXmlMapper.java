/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.sky.newcrm.apims.spring.exceptions.ApimsRuntimeException;
import de.sky.newcrm.apims.spring.utils.AssertUtils;
import de.sky.newcrm.apims.spring.utils.FunctionUtils;
import de.sky.newcrm.apims.spring.utils.walker.FlattenMap;
import de.sky.newcrm.apims.spring.utils.walker.FlattenMapCreator;
import de.sky.newcrm.apims.spring.utils.walker.SingleObjectFinder;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Predicate;

@SuppressWarnings({"java:S112"})
public class ApimsXmlMapper {
    protected static final XmlMapper DEFAULT_OBJECT_MAPPER_XML =
            DefaultJacksonXMLObjectFactory.createDefaultXmlObjectMapper();

    protected final ObjectMapper objectMapper;

    public ApimsXmlMapper() {
        this.objectMapper = DEFAULT_OBJECT_MAPPER_XML;
    }

    public ObjectMapper unwrap() {
        return objectMapper;
    }

    public Map<String, Object> cloneMap(Map<String, Object> payload) {
        return cloneMap(objectMapper, payload);
    }

    public static Map<String, Object> cloneMap(ObjectMapper objectMapper, Map<String, Object> payload) {
        return payload == null ? null : readMap(objectMapper, writeValueAsString(objectMapper, payload));
    }

    public <T> T cloneObject(T payload, Class<T> type) {
        return cloneObject(objectMapper, payload, type);
    }

    public static <T> T cloneObject(ObjectMapper objectMapper, T payload, Class<T> type) {
        return payload == null ? null : readValue(objectMapper, writeValueAsString(objectMapper, payload), type);
    }

    public Map<String, Object> getValueAsMap(Object object) {
        return getValueAsMap(objectMapper, object);
    }

    public String writeValueAsString(Object object) {
        return writeValueAsString(objectMapper, object);
    }

    public static String writeValueAsString(final ObjectMapper objectMapper, final Object object) {
        return FunctionUtils.execute(() -> objectMapper.writeValueAsString(object), ApimsRuntimeException.class);
    }

    public static Map<String, Object> getValueAsMap(ObjectMapper objectMapper, Object object) {
        return readMap(objectMapper, writeValueAsString(objectMapper, object));
    }

    public List<Map<String, Object>> readList(String payload) {
        return readList(objectMapper, payload);
    }

    public static List<Map<String, Object>> readList(ObjectMapper objectMapper, String payload) {
        return readValue(objectMapper, payload, new TypeReference<>() {});
    }

    public Map<String, Object> toFlattenMap(String payload) {
        return toFlattenMap(objectMapper, payload);
    }

    public static Map<String, Object> toFlattenMap(ObjectMapper objectMapper, String payload) {
        if (payload.startsWith("[")) {
            payload = "{\"records\": " + payload + "}";
        }
        Map<String, Object> data = readValue(objectMapper, payload, new TypeReference<>() {});
        return toFlattenMap(data);
    }

    public static FlattenMap toFlattenMap(Map<String, Object> data) {
        return new FlattenMapCreator<>().walk(data).getResult();
    }

    public static void resolveFlattenMap(Map<String, Object> data, Map<String, Object> target) {
        AssertUtils.notNullCheck("target", target);
        target.putAll(toFlattenMap(data));
    }

    public Map<String, Object> readMap(String payload) {
        return readMap(objectMapper, payload);
    }

    public static Map<String, Object> readMap(ObjectMapper objectMapper, String payload) {
        Map<String, Object> data = readValue(objectMapper, payload, new TypeReference<>() {});
        return new TreeMap<>(data);
    }

    public <T> T readValue(String payload, Class<T> type) {
        return readValue(objectMapper, payload, type);
    }

    public <T> T readValue(byte[] payload, Class<T> type) {
        return readValue(objectMapper, payload, type);
    }

    public static <T> T readValue(final ObjectMapper objectMapper, final String payload, final Class<T> type) {
        return FunctionUtils.execute(() -> objectMapper.readValue(payload, type), ApimsRuntimeException.class);
    }

    public static <T> T readValue(final ObjectMapper objectMapper, final byte[] payload, final Class<T> type) {
        return FunctionUtils.execute(() -> objectMapper.readValue(payload, type), ApimsRuntimeException.class);
    }

    public <T> T readValue(Map<?, ?> payload, Class<T> type) {
        return readValue(objectMapper, payload, type);
    }

    public static <T> T readValue(final ObjectMapper objectMapper, final Map<?, ?> payload, final Class<T> type) {
        return FunctionUtils.execute(
                () -> objectMapper.readValue(objectMapper.writeValueAsString(payload), type),
                ApimsRuntimeException.class);
    }

    public <T> List<T> readListValue(String payload, Class<T> type) {
        return readListValue(objectMapper, payload, type);
    }

    public <T> List<T> readListValue(byte[] payload, Class<T> type) {
        return readListValue(objectMapper, payload, type);
    }

    public static <T> List<T> readListValue(
            final ObjectMapper objectMapper, final String payload, final Class<T> type) {
        return FunctionUtils.execute(
                () -> objectMapper.readValue(
                        payload, objectMapper.getTypeFactory().constructCollectionType(List.class, type)),
                ApimsRuntimeException.class);
    }

    public static <T> List<T> readListValue(
            final ObjectMapper objectMapper, final byte[] payload, final Class<T> type) {
        return FunctionUtils.execute(
                () -> objectMapper.readValue(
                        payload, objectMapper.getTypeFactory().constructCollectionType(List.class, type)),
                ApimsRuntimeException.class);
    }

    public <T> T readValue(String payload, TypeReference<T> valueTypeRef) {
        return readValue(objectMapper, payload, valueTypeRef);
    }

    public static <T> T readValue(
            final ObjectMapper objectMapper, final String payload, final TypeReference<T> valueTypeRef) {
        return FunctionUtils.execute(() -> objectMapper.readValue(payload, valueTypeRef), ApimsRuntimeException.class);
    }

    public boolean isObjectEquals(Object payload, Object otherPayload) {
        return isObjectEquals(objectMapper, payload, otherPayload);
    }

    public static boolean isObjectEquals(ObjectMapper objectMapper, Object payload, Object otherPayload) {
        if (payload == null && otherPayload == null) {
            return true;
        } else if (payload == null || otherPayload == null) {
            return false;
        } else {
            return writeValueAsString(objectMapper, payload).equals(writeValueAsString(objectMapper, otherPayload));
        }
    }

    @SuppressWarnings("unchecked")
    public static void removeNode(Map<String, Object> map, String path) {
        AssertUtils.notNullCheck("map", map);
        AssertUtils.hasLengthCheck("path", path);
        String[] keys = StringUtils.tokenizeToStringArray(path, ".", true, true);
        if (keys.length == 1) {
            map.remove(path);
        } else {
            Map<String, Object> currentNode = map;
            for (int i = 0; i < keys.length - 1; i++) {
                currentNode = (Map<String, Object>) currentNode.get(keys[i]);
                if (currentNode == null) {
                    return;
                }
            }
            currentNode.remove(keys[keys.length - 1]);
        }
    }

    @SuppressWarnings("unchecked")
    public static void upsertNode(Map<String, Object> map, String path, Object value) {
        AssertUtils.notNullCheck("map", map);
        AssertUtils.hasLengthCheck("path", path);
        String[] keys = StringUtils.tokenizeToStringArray(path, ".", true, true);
        if (keys.length == 1) {
            map.put(path, value);
        } else {
            Map<String, Object> currentNode = map;
            for (int i = 0; i < keys.length - 1; i++) {
                currentNode =
                        (Map<String, Object>) currentNode.computeIfAbsent(keys[i], f -> new TreeMap<String, Object>());
            }
            currentNode.put(keys[keys.length - 1], value);
        }
    }

    public static Object getNodeValue(Map<String, Object> map, String path) {
        AssertUtils.notNullCheck("map", map);
        AssertUtils.hasLengthCheck("path", path);
        if (map.containsKey(path)) {
            return map.get(path);
        }
        return new SingleObjectFinder<>(item -> path.equals(item.getPath()))
                .walk(map)
                .getResult()
                .getValue();
    }

    @SuppressWarnings("unchecked")
    public static void appendList(Map<String, Object> map, String path, List<Object> value) {
        AssertUtils.notNullCheck("map", map);
        AssertUtils.hasLengthCheck("path", path);
        String[] keys = StringUtils.tokenizeToStringArray(path, ".", true, true);
        String currentKey = path;
        Map<String, Object> currentNode = map;
        List<Object> currentList = null;
        if (keys.length == 1) {
            currentList = (List<Object>) map.get(path);
        } else {
            for (int i = 0; i < keys.length - 1; i++) {
                currentNode =
                        (Map<String, Object>) currentNode.computeIfAbsent(keys[i], f -> new TreeMap<String, Object>());
            }
            currentKey = keys[keys.length - 1];
            currentList = (List<Object>) currentNode.get(currentKey);
        }
        if (currentList == null) {
            currentNode.put(currentKey, value);
        } else {
            currentList.addAll(value);
        }
    }

    public MapReader buildMapReaderForMap(String payload) {
        return new MapReader(readMap(payload));
    }

    public MapReader buildMapReaderForList(String payload) {
        return new MapReader(readList(payload));
    }

    public MapReader build(List<Map<String, Object>> rootObject) {
        return new MapReader(rootObject);
    }

    public MapReader build(Map<String, Object> rootObject) {
        return new MapReader(rootObject);
    }

    public static class MapReader {

        private final Object rootObject;
        private Object currentNode;

        public static MapReader buildForMap(ObjectMapper objectMapper, String payload) {
            return new MapReader(readMap(objectMapper, payload));
        }

        public static MapReader buildForList(ObjectMapper objectMapper, String payload) {
            return new MapReader(readList(objectMapper, payload));
        }

        public static MapReader build(List<Map<String, Object>> rootObject) {
            return new MapReader(rootObject);
        }

        public static MapReader build(Map<String, Object> rootObject) {
            return new MapReader(rootObject);
        }

        public MapReader(Map<String, Object> rootObject) {
            this.rootObject = rootObject == null ? new HashMap<>() : rootObject;
            this.currentNode = this.rootObject;
        }

        public MapReader(List<Map<String, Object>> rootObject) {
            this.rootObject = rootObject == null ? new ArrayList<>() : rootObject;
            this.currentNode = this.rootObject;
        }

        protected MapReader(Object rootObject) {
            this.rootObject = rootObject;
            this.currentNode = this.rootObject;
        }

        @SuppressWarnings("unchecked")
        public <T> T getRootObject() {
            return (T) rootObject;
        }

        public void setCurrentNode(Object currentNode) {
            this.currentNode = currentNode;
        }

        @SuppressWarnings("unchecked")
        public <T> T getCurrentNode() {
            return (T) currentNode;
        }

        public <T> T getCurrentNodeMapValue(String key) {
            return getCurrentNodeMapValue(key, null);
        }

        @SuppressWarnings("unchecked")
        public <T> T getCurrentNodeMapValue(String key, T defaultValue) {
            Map<String, Object> map = getCurrentNodeAsMap();
            if (key.contains(".") && !map.containsKey(key)) {
                String pathValue = key.substring(0, key.lastIndexOf("."));
                String keyValue = key.substring(key.lastIndexOf(".") + 1);
                return newReaderForCurrentNode().selectMap(pathValue).getCurrentNodeMapValue(keyValue, defaultValue);
            }
            T value = map == null ? null : (T) map.get(key);
            return value == null ? defaultValue : value;
        }

        @SuppressWarnings("unchecked")
        public Map<String, Object> getCurrentNodeAsMap() {
            return (Map<String, Object>) currentNode;
        }

        public MapReader newReaderForCurrentNode() {
            return new MapReader(currentNode);
        }

        public MapReader reset() {
            return currentNode(rootObject);
        }

        public MapReader currentNode(Object currentNode) {
            setCurrentNode(currentNode);
            return this;
        }

        public MapReader selectList(String key) {
            return selectList(key, new ArrayList<>());
        }

        @SuppressWarnings("unchecked")
        public MapReader selectList(String key, List<?> defaultValue) {
            currentNode = currentNode == null
                    ? defaultValue
                    : ((Map<String, Object>) currentNode).getOrDefault(key, defaultValue);
            if (!(currentNode instanceof List<?>)) {
                // handle single records (xml mapper)
                List<Object> list = new ArrayList<>();
                list.add(currentNode);
                currentNode = list;
            }
            return this;
        }

        public MapReader selectFirstListItem(Predicate<Map<String, Object>> predicate) {
            return selectFirstListItem(new HashMap<>(), predicate);
        }

        public MapReader selectFirstListItem(
                Map<String, Object> defaultValue, Predicate<Map<String, Object>> predicate) {
            List<Map<String, Object>> list = getCurrentNode();
            for (Map<String, Object> item : list) {
                if (predicate == null || predicate.test(item)) {
                    currentNode = item;
                    return this;
                }
            }
            currentNode = defaultValue;
            return this;
        }

        public MapReader selectMap(String... key) {
            for (String k : key) {
                selectMap(k, new HashMap<>());
            }
            return this;
        }

        @SuppressWarnings("unchecked")
        public MapReader selectMap(String key, Map<String, Object> defaultValue) {
            String[] keys = StringUtils.tokenizeToStringArray(key, ".", true, true);
            for (String subKey : keys) {
                currentNode = currentNode == null
                        ? defaultValue
                        : ((Map<String, Object>) currentNode).getOrDefault(subKey, defaultValue);
            }
            return this;
        }
    }
}
