/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.sky.newcrm.apims.spring.environment.core.ApimsMockedSpringContext;
import de.sky.newcrm.apims.spring.exceptions.ApimsRuntimeException;
import de.sky.newcrm.apims.spring.serialization.core.mapper.ApimsJsonMapper;
import de.sky.newcrm.apims.spring.serialization.core.mapper.ApimsObjectMapperConfig;
import de.sky.newcrm.apims.spring.serialization.core.mapper.DefaultJacksonObjectFactory;
import de.sky.newcrm.apims.spring.serialization.core.mapper.ObjectMapperUtils;
import de.sky.newcrm.apims.spring.utils.DateTimeUtc;
import de.sky.newcrm.apims.spring.utils.ObjectUtils;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.*;
import lombok.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.format.annotation.DateTimeFormat;
import tools.jackson.core.JacksonException;
import tools.jackson.core.exc.UnexpectedEndOfInputException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;

class ApimsJsonMapperJsonTest {

    private static final String EXPECTED_JSON_VALUE =
            """
                    {
                      "testKey" : "COMMON_TEST_KEY",
                      "testValue" : "COMMON_TEST_VALUE"
                    }""";

    private static final TestClass EXPECTED_OBJECT_VALUE = TestClass.builder()
            .testKey("COMMON_TEST_KEY")
            .testValue("COMMON_TEST_VALUE")
            .build();

    private static final String EXPECTED_JSON_MAP_VALUE =
            """
                    {
                      "result": {
                        "nexttransaction": {
                          "rest": {
                            "params": {
                              "multiTransactionKey": "7c0f366efddab5b483f8dc6e36553e4f",
                              "method": "addWithNoConfig"
                            }
                          }
                        },
                        "tags" : [
                          {
                            "value" : "tag 1"
                          },
                          {
                            "value" : "tag 2"
                          },
                          {
                            "value" : "tag 3"
                          }
                        ],
                        "records": [
                          {
                            "id" : "42",
                            "messages": [
                              {
                                "id": "1",
                                "text": "testmessage 1"
                              },
                              {
                                "id": "2",
                                "text": "testmessage 2"
                              }
                            ],
                            "displaySequence": -1,
                            "header" : {
                              "id" : "42",
                              "name" : "record 42"
                            }
                          }
                        ]
                      },
                      "createCartAction": {
                        "rest": {
                          "method": "createCartAction",
                          "link": "/v3/carts",
                          "params": {
                            "cartContextKey": null
                          }
                        }
                      },
                      "errorCode": "INVOKE-200",
                      "error": "OK"
                    }
                    """;

    @BeforeEach
    void setUp() {
        ApimsMockedSpringContext.INSTANCE.resetTestApplicationContext();
        ApimsObjectMapperConfig.resetInstance();
    }

    @Test
    @SuppressWarnings({"java:S5778", "java:S5961"})
    void commonTest() throws Exception {
        ObjectMapperUtils objectMapperUtils = new ObjectMapperUtils() {};
        assertNotNull(objectMapperUtils);
        DefaultJacksonObjectFactory defaultJacksonObjectFactory = new DefaultJacksonObjectFactory() {};
        assertNotNull(defaultJacksonObjectFactory);

        assertNotNull(ObjectMapperUtils.getApimsObjectMapperJson().unwrap());
        TestClass testClassRef = ObjectMapperUtils.readValue(EXPECTED_JSON_VALUE, new TypeReference<>() {});
        assertNotNull(testClassRef);
        TestClass testClass = ObjectMapperUtils.readValue(EXPECTED_JSON_VALUE, TestClass.class);
        Map<String, Object> detailData = new HashMap<>();
        detailData.put("date", new Date());
        detailData.put("string", "value");
        Map<String, Object> subDetailsData = new HashMap<>();
        subDetailsData.put("integer", 1);
        detailData.put("subDetails", subDetailsData);

        Map<String, Object> data = ObjectMapperUtils.getValueAsMap(testClass);
        data.put("details", detailData);
        List<Map<String, Object>> dataList = new ArrayList<>();
        dataList.add(data);
        List<TestClass> testClassList = new ArrayList<>();
        testClassList.add(testClass);

        assertNull(ObjectMapperUtils.cloneMap(null));
        assertNotNull(ObjectMapperUtils.cloneMap(data));

        assertNull(ObjectMapperUtils.cloneObject(null, TestClass.class));
        assertNotNull(ObjectMapperUtils.cloneObject(testClass, TestClass.class));

        String wrongPayload = "{";
        String payload = ObjectMapperUtils.writeValueAsString(dataList);
        dataList = ObjectMapperUtils.readList(payload);
        assertNotNull(dataList);
        assertFalse(dataList.isEmpty());

        payload = ObjectMapperUtils.writeValueAsString(testClassList);
        assertNotNull(ObjectMapperUtils.readListValue(payload, TestClass.class));
        assertThrows(MismatchedInputException.class, () -> ObjectMapperUtils.readListValue(wrongPayload, TestClass.class));
        assertNotNull(ObjectMapperUtils.readListValue(payload.getBytes(StandardCharsets.UTF_8), TestClass.class));
        assertThrows(
                MismatchedInputException.class,
                () -> ObjectMapperUtils.readListValue(wrongPayload.getBytes(StandardCharsets.UTF_8), TestClass.class));

        payload = ObjectMapperUtils.writeValueAsString(testClass);
        assertThrows(UnexpectedEndOfInputException.class, () -> ObjectMapperUtils.readValue(wrongPayload, TestClass.class));
        assertNotNull(ObjectMapperUtils.readValue(payload, TestClass.class));
        assertNotNull(ObjectMapperUtils.readValue(payload.getBytes(StandardCharsets.UTF_8), TestClass.class));
        assertThrows(
                UnexpectedEndOfInputException.class,
                () -> ObjectMapperUtils.readValue(wrongPayload.getBytes(StandardCharsets.UTF_8), TestClass.class));

        assertNotNull(ObjectMapperUtils.readValue(data, TestClass.class));

        Map<String, Object> data2 = ObjectMapperUtils.cloneMap(data);
        assertTrue(ObjectMapperUtils.isObjectEquals(data, data2));
        assertTrue(ObjectMapperUtils.isObjectEquals(null, null));
        assertFalse(ObjectMapperUtils.isObjectEquals(data, null));
        assertFalse(ObjectMapperUtils.isObjectEquals(null, data2));

        assertNull(ObjectMapperUtils.getNodeValue(data2, "details.notexists.integer"));
        assertNotNull(ObjectMapperUtils.getNodeValue(data2, "details.subDetails.integer"));
        assertNotNull(ObjectMapperUtils.getNodeValue(data2, "details.date"));
        assertNotNull(ObjectMapperUtils.getNodeValue(data2, "details"));

        assertDoesNotThrow(() -> ObjectMapperUtils.removeNode(data2, "details.notexists.integer"));
        assertDoesNotThrow(() -> ObjectMapperUtils.removeNode(data2, "details.subDetails.integer"));
        assertDoesNotThrow(() -> ObjectMapperUtils.removeNode(data2, "details.date"));
        assertDoesNotThrow(() -> ObjectMapperUtils.removeNode(data2, "details"));

        List<Object> list = new ArrayList<>();
        list.add("test1");
        list.add("test2");
        assertDoesNotThrow(() -> ObjectMapperUtils.appendList(data2, "list", list));
        assertDoesNotThrow(() -> ObjectMapperUtils.appendList(data2, "list", List.of("test3")));
        assertDoesNotThrow(() -> ObjectMapperUtils.appendList(data2, "list2.sublist", list));
        assertDoesNotThrow(() -> ObjectMapperUtils.appendList(data2, "list2.sublist", List.of("test3")));

        assertDoesNotThrow(() -> ObjectMapperUtils.upsertNode(data2, "details", detailData));
        assertDoesNotThrow(() -> ObjectMapperUtils.upsertNode(data2, "details.subDetails.integer", 2));
        assertDoesNotThrow(() -> ObjectMapperUtils.upsertNode(data2, "details.subDetails.new.integer", 2));
        assertDoesNotThrow(() -> ObjectMapperUtils.upsertNode(data2, "details.date", new Date()));
        assertDoesNotThrow(() -> ObjectMapperUtils.upsertNode(data2, "details.new", "new"));

        ObjectMapper objectMapper = mock(ObjectMapper.class);
        MismatchedInputException jsonProcessingException = mock(MismatchedInputException.class);
        when(objectMapper.writeValueAsString(any())).thenThrow(jsonProcessingException);
        assertThrows(
                RuntimeException.class,
                () -> ApimsJsonMapper.readValue(objectMapper, (Map<String, Object>) null, TestClass.class));
    }

    @Test
    void commonMapReaderTest() {
        Map<String, Object> map = ObjectMapperUtils.readMap(EXPECTED_JSON_VALUE);
        List<Map<String, Object>> list = List.of(map);
        assertNotNull(ObjectMapperUtils.MapReader.buildForMap(EXPECTED_JSON_VALUE));
        assertNotNull(ObjectMapperUtils.MapReader.buildForList("[" + EXPECTED_JSON_VALUE + "]"));
        assertNotNull(ObjectMapperUtils.MapReader.build(map));
        assertNotNull(ObjectMapperUtils.MapReader.build(list));
    }

    @Test
    void writeValueTest() {
        String jsonValue = ObjectMapperUtils.writeValueAsString(EXPECTED_OBJECT_VALUE);
        assertNotNull(jsonValue);
        assertNotEquals("", jsonValue);
        assertEquals(EXPECTED_JSON_VALUE, jsonValue);
    }

    @Test
    void readValueTest() {
        TestClass testClass = ObjectMapperUtils.readValue(EXPECTED_JSON_VALUE, TestClass.class);
        assertNotNull(testClass);
        assertEquals(EXPECTED_OBJECT_VALUE.testKey, testClass.getTestKey());
        assertEquals(EXPECTED_OBJECT_VALUE.testValue, testClass.getTestValue());

        testClass = ObjectMapperUtils.readValue(EXPECTED_JSON_VALUE, new TypeReference<>() {});
        assertNotNull(testClass);
        assertEquals(EXPECTED_OBJECT_VALUE.testKey, testClass.getTestKey());
        assertEquals(EXPECTED_OBJECT_VALUE.testValue, testClass.getTestValue());

        Map<String, Object> map = ObjectMapperUtils.readMap(EXPECTED_JSON_VALUE);
        assertNotNull(map);
        assertEquals(EXPECTED_OBJECT_VALUE.testKey, map.get("testKey"));
        assertEquals(EXPECTED_OBJECT_VALUE.testValue, map.get("testValue"));
    }

    @Test
    void readListValueTest() {
        List<TestClass> list = new ArrayList<>();
        TestClass testClass1 =
                TestClass.builder().testKey("key1").testValue("value1").build();
        TestClass testClass2 =
                TestClass.builder().testKey("key2").testValue("value2").build();
        list.add(testClass1);
        list.add(testClass2);
        String serialized = ObjectMapperUtils.writeValueAsString(list);
        List<TestClass> loadedList = ObjectMapperUtils.readListValue(serialized, TestClass.class);
        assertNotNull(loadedList);
        assertEquals(list.size(), loadedList.size());
        assertEquals(2, loadedList.size());
        assertEquals(list.get(0).getTestKey(), loadedList.get(0).getTestKey());
        assertEquals(list.get(0).getTestValue(), loadedList.get(0).getTestValue());
        assertEquals(list.get(1).getTestKey(), loadedList.get(1).getTestKey());
        assertEquals(list.get(1).getTestValue(), loadedList.get(1).getTestValue());
    }

    @Test
    void cloneObjectTest() {
        TestClass testClass = ObjectMapperUtils.readValue(EXPECTED_JSON_VALUE, TestClass.class);
        TestClass testClassClone = ObjectMapperUtils.cloneObject(testClass, TestClass.class);
        assertNotNull(testClassClone);
        assertEquals(testClass.getTestKey(), testClassClone.getTestKey());
        assertEquals(testClass.getTestValue(), testClassClone.getTestValue());
    }

    @Test
    void MapReaderTest() {
        String multiTransactionKey = ObjectMapperUtils.MapReader.buildForMap(EXPECTED_JSON_MAP_VALUE)
                .selectMap("result.nexttransaction.rest.params")
                .getCurrentNodeMapValue("multiTransactionKey");
        assertNotNull(multiTransactionKey);
        assertEquals("7c0f366efddab5b483f8dc6e36553e4f", multiTransactionKey);

        multiTransactionKey = ObjectMapperUtils.MapReader.buildForMap(EXPECTED_JSON_MAP_VALUE)
                .selectMap("resultNotExists.nexttransactionNotExists.restNotExists.paramsNotExists")
                .getCurrentNodeMapValue("multiTransactionKeyNotExists", "MY_DEFAULT");
        assertNotNull(multiTransactionKey);
        assertEquals("MY_DEFAULT", multiTransactionKey);
    }

    @Test
    void MapReaderListTest() {
        ObjectMapperUtils.MapReader mapReader = ObjectMapperUtils.MapReader.buildForMap(EXPECTED_JSON_MAP_VALUE);
        Map<String, Object> message = mapReader
                .selectMap("result")
                .selectList("records")
                .selectFirstListItem(item -> "42".equals(item.get("id")))
                .selectList("messages")
                .selectFirstListItem(null, item -> "2".equals(item.get("id")))
                .getCurrentNode();
        assertNotNull(message);
        assertEquals("2", message.get("id"));
        assertEquals("testmessage 2", message.get("text"));

        message = mapReader
                .reset()
                .selectMap("result")
                .selectList("records")
                .selectFirstListItem(item -> "42".equals(item.get("id")))
                .selectList("messages")
                .selectFirstListItem(null, item -> "null".equals(item.get("id")))
                .getCurrentNode();
        assertNull(message);

        message = mapReader
                .reset()
                .selectMap("resultNotExists")
                .selectList("recordsNotExists")
                .selectFirstListItem(item -> "NotExists".equals(item.get("id")))
                .selectList("messagesNotExists")
                .selectFirstListItem(item -> "NotExists".equals(item.get("id")))
                .getCurrentNode();
        assertNotNull(message);
        assertNull(message.get("id"));
        assertEquals(0, message.size());

        List<Map<String, Object>> messages = mapReader
                .reset()
                .selectMap("result")
                .selectList("records")
                .selectFirstListItem(item -> "42".equals(item.get("id")))
                .getCurrentNodeMapValue("messages");
        assertNotNull(messages);
        assertEquals(2, messages.size());
        assertEquals("1", messages.get(0).get("id"));
        assertEquals("2", messages.get(1).get("id"));

        messages = mapReader
                .reset()
                .selectMap("resultNotExists")
                .selectList("recordsNotExists")
                .selectFirstListItem(item -> "NotExists".equals(item.get("id")))
                .getCurrentNodeMapValue("messagesNotExists");
        assertNull(messages);

        messages = mapReader
                .reset()
                .selectMap("resultNotExists")
                .selectList("recordsNotExists")
                .selectFirstListItem(item -> "NotExists".equals(item.get("id")))
                .getCurrentNodeMapValue("messagesNotExists", new ArrayList<>());
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = mapReader
                .reset()
                .selectMap("resultNotExists")
                .selectList("recordsNotExists")
                .selectFirstListItem(item -> "NotExists".equals(item.get("id")))
                .selectList("messagesNotExists")
                .getCurrentNode();
        assertNotNull(messages);
        assertEquals(0, messages.size());
    }

    @Test
    void flattenMapTest() {
        Map<String, Object> data = ObjectMapperUtils.toFlattenMap(EXPECTED_JSON_MAP_VALUE);
        assertNotNull(data);
        Map<String, Object> data2 = ObjectMapperUtils.toFlattenMap(ObjectMapperUtils.readMap(EXPECTED_JSON_MAP_VALUE));
        assertNotNull(data2);
        assertTrue(ObjectUtils.isEquals(data, data2, true));
        assertEquals(21, data.size());
        assertEquals(1, data.get("result.records.size"));
        assertEquals(2, data.get("result.records[0].messages.size"));
        assertEquals("1", data.get("result.records[0].messages[0].id"));
        assertEquals("testmessage 1", data.get("result.records[0].messages[0].text"));
        assertEquals(-1, data.get("result.records[0].displaySequence"));
        data = ObjectMapperUtils.toFlattenMap("[" + EXPECTED_JSON_MAP_VALUE + "]");
        assertNotNull(data);
        assertEquals(22, data.size());
        assertEquals(1, data.get("records.size"));
        assertEquals(1, data.get("records[0].result.records.size"));
        assertEquals(2, data.get("records[0].result.records[0].messages.size"));
        assertEquals("1", data.get("records[0].result.records[0].messages[0].id"));
        assertEquals("testmessage 1", data.get("records[0].result.records[0].messages[0].text"));
        assertEquals(-1, data.get("records[0].result.records[0].displaySequence"));
    }

    @Test
    void dateFormatTest() {
        Date date = DateTimeUtc.parseDate("2024-06-06T01:42:42Z");
        OffsetDateTime offsetDateTime = DateTimeUtc.parseOffsetDateTime("2024-06-06T01:42:42Z");
        TestClass testClass = TestClass.builder()
                .testKey("k")
                .testValue("v")
                .creationDate(date)
                .creationDateTime(date)
                .updateDate(offsetDateTime)
                .updateDateTime(offsetDateTime)
                .build();

        ApimsMockedSpringContext.INSTANCE.overrideTestProperty(
                "apims.object-mapper-config.date-time-serializer-write-iso-date-with-timezone", "false");
        ApimsObjectMapperConfig.resetInstance();

        Map<String, Object> data = ObjectMapperUtils.readMap(ObjectMapperUtils.writeValueAsString(testClass));
        assertNotNull(data);
        assertEquals(6, data.size());
        assertEquals("2024-06-06", data.get("creationDate"));
        assertEquals("2024-06-06T01:42:42Z", data.get("creationDateTime"));
        assertEquals("2024-06-06", data.get("updateDate"));
        assertEquals("2024-06-06T01:42:42Z", data.get("updateDateTime"));

        ApimsMockedSpringContext.INSTANCE.resetTestApplicationContext();
        ApimsObjectMapperConfig.resetInstance();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    static class TestClass {

        private String testKey;
        private String testValue;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private Date creationDate;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private Date creationDateTime;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private OffsetDateTime updateDate;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private OffsetDateTime updateDateTime;
    }
}
