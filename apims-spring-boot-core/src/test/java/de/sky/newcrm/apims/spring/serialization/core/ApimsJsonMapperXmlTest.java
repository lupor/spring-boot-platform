///*
// * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
// * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
// */
//package de.sky.newcrm.apims.spring.serialization.core;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import de.sky.newcrm.apims.spring.exceptions.ApimsRuntimeException;
//import de.sky.newcrm.apims.spring.serialization.core.mapper.ApimsJsonMapper;
//import de.sky.newcrm.apims.spring.serialization.core.mapper.ApimsXmlMapper;
//import lombok.*;
//import org.junit.jupiter.api.Test;
//
//class ApimsJsonMapperXmlTest {
//
//    private static final String EXPECTED_XML_VALUE =
//            """
//                    <test-data>
//                      <test-key>COMMON_TEST_KEY</test-key>
//                      <test-value>COMMON_TEST_VALUE</test-value>
//                    </test-data>
//                    """;
//
//    private static final TestClass EXPECTED_OBJECT_VALUE = TestClass.builder()
//            .testKey("COMMON_TEST_KEY")
//            .testValue("COMMON_TEST_VALUE")
//            .build();
//
//    private static final String EXPECTED_XML_MAP_VALUE =
//            """
//                      <test-data>
//                        <createCartAction>
//                          <rest>
//                            <method>createCartAction</method>
//                            <link>/v3/carts</link>
//                            <params/>
//                          </rest>
//                        </createCartAction>
//                        <error>OK</error>
//                        <errorCode>INVOKE-200</errorCode>
//                        <result>
//                          <nexttransaction>
//                            <rest>
//                              <params>
//                                <multiTransactionKey>7c0f366efddab5b483f8dc6e36553e4f</multiTransactionKey>
//                                <method>addWithNoConfig</method>
//                              </params>
//                            </rest>
//                          </nexttransaction>
//                          <tags>
//                            <value>tag 1</value>
//                          </tags>
//                          <tags>
//                            <value>tag 2</value>
//                          </tags>
//                          <tags>
//                            <value>tag 3</value>
//                          </tags>
//                          <records>
//                            <id>42</id>
//                            <messages>
//                              <id>1</id>
//                              <text>testmessage 1</text>
//                            </messages>
//                            <messages>
//                              <id>2</id>
//                              <text>testmessage 2</text>
//                            </messages>
//                            <displaySequence>-1</displaySequence>
//                            <header>
//                              <id>42</id>
//                              <name>record 42</name>
//                            </header>
//                          </records>
//                        </result>
//                      </test-data>
//                    """;
//
//    ApimsXmlMapper apimsObjectMapperXml = new ApimsXmlMapper();
//
//    @Test
//    void writeValueTest() {
//        assertThrows(ApimsRuntimeException.class, () -> apimsObjectMapperXml.writeValueAsString(this));
//
//        String jsonValue = apimsObjectMapperXml.writeValueAsString(EXPECTED_OBJECT_VALUE);
//        assertNotNull(jsonValue);
//        assertNotEquals("", jsonValue);
//        assertEquals(
//                EXPECTED_XML_VALUE, jsonValue);
//    }
//
//    @Test
//    void readValueTest() {
//        assertThrows(ApimsRuntimeException.class, () -> apimsObjectMapperXml.writeValueAsString(this));
//        TestClass testClass = apimsObjectMapperXml.readValue(EXPECTED_XML_VALUE, TestClass.class);
//        assertNotNull(testClass);
//        assertEquals(EXPECTED_OBJECT_VALUE.testKey, testClass.getTestKey());
//        assertEquals(EXPECTED_OBJECT_VALUE.testValue, testClass.getTestValue());
//
//        testClass = apimsObjectMapperXml.readValue(EXPECTED_XML_VALUE, new TypeReference<>() {});
//        assertNotNull(testClass);
//        assertEquals(EXPECTED_OBJECT_VALUE.testKey, testClass.getTestKey());
//        assertEquals(EXPECTED_OBJECT_VALUE.testValue, testClass.getTestValue());
//
//        Map<String, Object> map = apimsObjectMapperXml.readMap(EXPECTED_XML_VALUE);
//        assertNotNull(map);
//        assertEquals(EXPECTED_OBJECT_VALUE.testKey, map.get("test-key"));
//        assertEquals(EXPECTED_OBJECT_VALUE.testValue, map.get("test-value"));
//    }
//
//    @Test
//    void readListValueTest() {
//        List<TestClass> list = new ArrayList<>();
//        TestClass testClass1 =
//                TestClass.builder().testKey("key1").testValue("value1").build();
//        TestClass testClass2 =
//                TestClass.builder().testKey("key2").testValue("value2").build();
//        list.add(testClass1);
//        list.add(testClass2);
//        String serialized = apimsObjectMapperXml.writeValueAsString(list);
//        List<TestClass> loadedList = apimsObjectMapperXml.readListValue(serialized, TestClass.class);
//        assertNotNull(loadedList);
//        assertEquals(list.size(), loadedList.size());
//        assertEquals(2, loadedList.size());
//        assertEquals(list.get(0).getTestKey(), loadedList.get(0).getTestKey());
//        assertEquals(list.get(0).getTestValue(), loadedList.get(0).getTestValue());
//        assertEquals(list.get(1).getTestKey(), loadedList.get(1).getTestKey());
//        assertEquals(list.get(1).getTestValue(), loadedList.get(1).getTestValue());
//    }
//
//    @Test
//    void cloneObjectTest() {
//        TestClass testClass = apimsObjectMapperXml.readValue(EXPECTED_XML_VALUE, TestClass.class);
//        TestClass testClassClone = apimsObjectMapperXml.cloneObject(testClass, TestClass.class);
//        assertNotNull(testClassClone);
//        assertEquals(testClass.getTestKey(), testClassClone.getTestKey());
//        assertEquals(testClass.getTestValue(), testClassClone.getTestValue());
//    }
//
//    @Test
//    void MapReaderTest() {
//
//        String multiTransactionKey = apimsObjectMapperXml
//                .buildMapReaderForMap(EXPECTED_XML_MAP_VALUE)
//                .selectMap("result", "nexttransaction", "rest", "params")
//                .getCurrentNodeMapValue("multiTransactionKey");
//        assertNotNull(multiTransactionKey);
//        assertEquals("7c0f366efddab5b483f8dc6e36553e4f", multiTransactionKey);
//
//        multiTransactionKey = apimsObjectMapperXml
//                .buildMapReaderForMap(EXPECTED_XML_MAP_VALUE)
//                .selectMap("resultNotExists", "nexttransactionNotExists", "restNotExists", "paramsNotExists")
//                .getCurrentNodeMapValue("multiTransactionKeyNotExists", "MY_DEFAULT");
//        assertNotNull(multiTransactionKey);
//        assertEquals("MY_DEFAULT", multiTransactionKey);
//    }
//
//    @Test
//    void MapReaderListTest() {
//
//        ApimsJsonMapper.MapReader mapReader = apimsObjectMapperXml.buildMapReaderForMap(EXPECTED_XML_MAP_VALUE);
//        Map<String, Object> message = mapReader
//                .selectMap("result")
//                .selectList("records")
//                .selectFirstListItem(item -> "42".equals(item.get("id")))
//                .selectList("messages")
//                .selectFirstListItem(null, item -> "2".equals(item.get("id")))
//                .getCurrentNode();
//        assertNotNull(message);
//        assertEquals("2", message.get("id"));
//        assertEquals("testmessage 2", message.get("text"));
//
//        message = mapReader
//                .reset()
//                .selectMap("result")
//                .selectList("records")
//                .selectFirstListItem(item -> "42".equals(item.get("id")))
//                .selectList("messages")
//                .selectFirstListItem(null, item -> "null".equals(item.get("id")))
//                .getCurrentNode();
//        assertNull(message);
//
//        message = mapReader
//                .reset()
//                .selectMap("resultNotExists")
//                .selectList("recordsNotExists")
//                .selectFirstListItem(item -> "NotExists".equals(item.get("id")))
//                .selectList("messagesNotExists")
//                .selectFirstListItem(item -> "NotExists".equals(item.get("id")))
//                .getCurrentNode();
//        assertNotNull(message);
//        assertNull(message.get("id"));
//        assertEquals(0, message.size());
//
//        List<Map<String, Object>> messages = mapReader
//                .reset()
//                .selectMap("result")
//                .selectList("records")
//                .selectFirstListItem(item -> "42".equals(item.get("id")))
//                .getCurrentNodeMapValue("messages");
//        assertNotNull(messages);
//        assertEquals(2, messages.size());
//        assertEquals("1", messages.get(0).get("id"));
//        assertEquals("2", messages.get(1).get("id"));
//
//        messages = mapReader
//                .reset()
//                .selectMap("resultNotExists")
//                .selectList("recordsNotExists")
//                .selectFirstListItem(item -> "NotExists".equals(item.get("id")))
//                .getCurrentNodeMapValue("messagesNotExists");
//        assertNull(messages);
//
//        messages = mapReader
//                .reset()
//                .selectMap("resultNotExists")
//                .selectList("recordsNotExists")
//                .selectFirstListItem(item -> "NotExists".equals(item.get("id")))
//                .getCurrentNodeMapValue("messagesNotExists", new ArrayList<>());
//        assertNotNull(messages);
//        assertEquals(0, messages.size());
//
//        messages = mapReader
//                .reset()
//                .selectMap("resultNotExists")
//                .selectList("recordsNotExists")
//                .selectFirstListItem(item -> "NotExists".equals(item.get("id")))
//                .selectList("messagesNotExists")
//                .getCurrentNode();
//        assertNotNull(messages);
//        assertEquals(0, messages.size());
//    }
//
//    @Test
//    void flattenMapTest() {
//        Map<String, Object> data = apimsObjectMapperXml.toFlattenMap(EXPECTED_XML_MAP_VALUE);
//        assertNotNull(data);
//        assertEquals(20, data.size());
//        assertEquals("1", data.get("result.records.messages[0].id"));
//        assertEquals(2, data.get("result.records.messages.size"));
//        assertEquals("testmessage 1", data.get("result.records.messages[0].text"));
//    }
//
//    @Getter
//    @Setter
//    @NoArgsConstructor
//    @AllArgsConstructor
//    @Builder
//    @JacksonXmlRootElement(localName = "test-data")
//    static class TestClass {
//        @JsonProperty("test-key")
//        private String testKey;
//
//        @JsonProperty("test-value")
//        private String testValue;
//    }
//}
