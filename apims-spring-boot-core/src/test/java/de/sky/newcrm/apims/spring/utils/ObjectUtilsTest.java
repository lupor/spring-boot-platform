/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.*;

import de.sky.newcrm.apims.spring.exceptions.ApimsRuntimeException;
import de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3.ObjectMapperUtils;
import de.sky.newcrm.apims.spring.telemetry.logging.core.ApimsAroundLoggingListenerSuppress;
import jakarta.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

class ObjectUtilsTest {

    @Test
    void methodTest() {
        TestData testData = new TestData();
        Method method = ObjectUtils.findMethod(TestData.class, "getPair", String.class);
        assertNull(method);
        method = ObjectUtils.findMethod(TestData.class, "getPair");
        assertNotNull(method);
        TestData.TestDataPair pair = ObjectUtils.invokeMethod(method, testData);
        assertNotNull(pair);
        pair = ObjectUtils.invokeMethodAndMakeAccessible(method, testData, true);
        assertNotNull(pair);
        method = ObjectUtils.findMethod(TestData.class, "setPair", String.class);
        assertNull(method);
        method = ObjectUtils.findMethod(TestData.class, "setPair");
        assertNotNull(method);
        method = ObjectUtils.findMethod(TestData.class, "setPair", TestData.TestDataPair.class);
        assertNotNull(method);
        ObjectUtils.invokeMethodAndMakeAccessible(method, testData, true, new TestData.TestDataPair());
        ObjectUtils.invokeMethod(method, testData, new TestData.TestDataPair());

        Method[] methods = ObjectUtils.getDeclaredMethods(TestData.class);
        assertNotNull(methods);
        assertTrue(methods.length > 1);
    }

    @Test
    void annotationTest() {
        Component component = ObjectUtils.findClassAnnotation(TestData.class, Component.class);
        assertNull(component);
        Validated validated = ObjectUtils.findClassAnnotation(TestData.class, Validated.class);
        assertNotNull(validated);
        validated = ObjectUtils.findClassAnnotation(TestData.class, Validated.class, true);
        assertNotNull(validated);

        List<Method> methods =
                ObjectUtils.findAnnotatedMethods(TestData.TestDataPair.class, ApimsAroundLoggingListenerSuppress.class);
        assertNotNull(methods);
        assertTrue(methods.isEmpty());
        methods = ObjectUtils.findAnnotatedMethods(TestData.class, ApimsAroundLoggingListenerSuppress.class);
        assertNotNull(methods);
        assertFalse(methods.isEmpty());
        Method method = methods.get(0);
        assertNotNull(method);
        assertEquals("getPair", method.getName());

        List<Field> fields = ObjectUtils.findAnnotatedFields(TestData.class, Id.class);
        assertNotNull(fields);
        assertTrue(fields.isEmpty());
        fields = ObjectUtils.findAnnotatedFields(TestData.class, Id.class, true);
        assertNotNull(fields);
        assertTrue(fields.isEmpty());
        fields = ObjectUtils.findAnnotatedFields(TestData.TestDataPair.class, Id.class);
        assertNotNull(fields);
        assertFalse(fields.isEmpty());
        fields = ObjectUtils.findAnnotatedFields(TestData.TestDataPair.class, Id.class, true);
        assertNotNull(fields);
        assertFalse(fields.isEmpty());
        Field field = ObjectUtils.findAnnotatedField(TestData.class, Id.class);
        assertNull(field);
        field = ObjectUtils.findAnnotatedField(TestData.TestDataPair.class, Id.class);
        assertNotNull(field);
        assertEquals("key", field.getName());
    }

    @Test
    void fieldTest() {
        TestData testData = new TestData();
        Field field = ObjectUtils.findField(TestData.class, "notExists", true);
        assertNull(field);
        assertFalse(ObjectUtils.hasField(TestData.class, "notExists", TestData.TestDataPair.class));
        assertTrue(ObjectUtils.hasField(TestData.class, "pair", TestData.TestDataPair.class));
        field = ObjectUtils.findField(TestData.class, "pair");
        assertNotNull(field);
        field = ObjectUtils.findField(TestData.class, "pair", true);
        assertNotNull(field);
        assertEquals("pair", field.getName());
        TestData.TestDataPair pair = ObjectUtils.getField("pair", testData);
        assertNotNull(pair);

        pair = ObjectUtils.getField(field, testData);
        assertNotNull(pair);
        pair = ObjectUtils.getField(field, testData, true);
        assertNotNull(pair);
        pair = ObjectUtils.getField(field, TestData.TestDataPair.class, testData);
        assertNotNull(pair);
        pair = ObjectUtils.getField("pair", TestData.TestDataPair.class, testData);
        assertNotNull(pair);
        pair = ObjectUtils.getField(TestData.class, "notExists", TestData.TestDataPair.class, testData);
        assertNull(pair);

        ObjectUtils.setField(testData, "pair", new TestData.TestDataPair());
        field = ObjectUtils.findField(TestData.class, "pair");
        ObjectUtils.setField(field, testData, new TestData.TestDataPair());
    }

    @Test
    @SuppressWarnings("java:S5778")
    void classTest() {
        assertThrows(ApimsRuntimeException.class, () -> ObjectUtils.getClass("notExistsClass"));

        Class<?> clazz = ObjectUtils.getClass(TestData.class.getName());
        assertNotNull(clazz);
        TestData testData = ObjectUtils.createInstance(clazz);
        assertNotNull(testData);
        testData = ObjectUtils.createInstance(TestData.class.getName());
        assertNotNull(testData);
        testData = ObjectUtils.createInstance(ObjectUtils.CreateInstanceDefinition.builder()
                .className(TestData.class.getName())
                .build());
        assertNotNull(testData);
        testData = ObjectUtils.createInstance(ObjectUtils.CreateInstanceDefinition.builder()
                .className(TestData.class.getName())
                .constructorTypes(new Class<?>[] {TestData.TestDataPair.class})
                .constructorArgs(new Object[] {new TestData.TestDataPair()})
                .fieldData(Map.of("pair", new TestData.TestDataPair()))
                .build());
        assertNotNull(testData);
        testData = ObjectUtils.createInstance(ObjectUtils.CreateInstanceDefinition.builder()
                .className(TestData.class.getName())
                .constructorTypes(new Class<?>[] {TestData.TestDataPair.class})
                .fieldData(Map.of("pair", new TestData.TestDataPair()))
                .build());
        assertNotNull(testData);

        assertThrows(
                ApimsRuntimeException.class,
                () -> ObjectUtils.createInstance(ObjectUtils.CreateInstanceDefinition.builder()
                        .className("notExistsClass")
                        .build()));
    }

    @Test
    void equalsTest() {
        Map<String, Object> map1 = MapUtils.ofTreeMapEntries(entry("k1", "v1"), entry("k2", "v2"));
        Map<String, Object> map2 = ObjectMapperUtils.cloneMap(map1);
        assertFalse(ObjectUtils.isEquals(map1, null));
        assertFalse(ObjectUtils.isEquals(null, map2));
        assertTrue(ObjectUtils.isEquals(null, null));
        assertTrue(ObjectUtils.isEquals(map1, map2));
        assertTrue(ObjectUtils.isEquals(map1, map2, true));
        map2.put("k3", "v3");
        assertFalse(ObjectUtils.isEquals(map1, map2, true));
    }

    @Test
    void collectionsTest() {
        List<TestData> list = new ArrayList<>();
        list.add(new TestData());
        Collection<?> collection = ObjectUtils.asCollection(list);
        assertNotNull(collection);
        assertFalse(collection.isEmpty());

        TestData[] testData = list.toArray(new TestData[0]);
        collection = ObjectUtils.asCollection(testData);
        assertNotNull(collection);
        assertFalse(collection.isEmpty());

        collection = ObjectUtils.asCollection(list.get(0));
        assertNotNull(collection);
        assertFalse(collection.isEmpty());
    }

    @Test
    void findValueTest() {

        TestData testData = new TestData();
        testData.getPair().setKey("k");
        testData.getPair().setValue("v");
        String v = ObjectUtils.getPropertyValue(testData, "pair.value");
        assertEquals("v", v);
        String k = ObjectUtils.getFieldValue(testData, "pair.key");
        assertEquals("k", k);

        v = ObjectUtils.getPropertyValue(null, "pair.key");
        assertNull(v);
        assertThrows(NotReadablePropertyException.class, () -> ObjectUtils.getPropertyValue(testData, "pair.key2"));
        v = ObjectUtils.getPropertyValue(testData, "pair.key2", true);
        assertNull(v);
        k = ObjectUtils.getFieldValue(null, "pair.key");
        assertNull(k);
        k = ObjectUtils.getFieldValue(testData, "pair.key2");
        assertNull(k);

        testData.setPair(null);
        v = ObjectUtils.getPropertyValue(testData, "pair.key");
        assertNull(v);
        k = ObjectUtils.getFieldValue(testData, "pair.key");
        assertNull(k);
    }

    @Test
    void getValueConditionTest() {
        String value = "value";
        String defaultValue = "defaultValue";
        Optional<String> optionalValue = Optional.of(value);
        assertEquals(value, ObjectUtils.getIfNotNull(value, defaultValue));
        assertEquals(defaultValue, ObjectUtils.getIfNotNull(null, defaultValue));
        assertEquals(value, ObjectUtils.getIfHasLength(value, defaultValue));
        assertEquals(defaultValue, ObjectUtils.getIfHasLength(null, defaultValue));
        assertEquals(defaultValue, ObjectUtils.getIfHasLength("", defaultValue));
        assertEquals(value, ObjectUtils.getIfHasText(value, defaultValue));
        assertEquals(defaultValue, ObjectUtils.getIfHasText(null, defaultValue));
        assertEquals(defaultValue, ObjectUtils.getIfHasText("\t", defaultValue));
        assertEquals(value, ObjectUtils.getOrElse(optionalValue, defaultValue));
        assertEquals(defaultValue, ObjectUtils.getOrElse(Optional.empty(), defaultValue));
    }

    @Test
    void createInstanceTest() {
        Class<?> clazz = TestData.class;
        TestData instance = ObjectUtils.createInstanceByDefinitions(
                ObjectUtils.CreateInstanceDefinition.builder()
                        .clazz(clazz)
                        .constructorTypes(new Class<?>[] {String.class, Throwable.class})
                        .constructorArgs(new Object[] {"test", new IllegalStateException("TEST")})
                        .build(),
                ObjectUtils.CreateInstanceDefinition.builder().clazz(clazz).build());
        assertNotNull(instance);
        ObjectUtils.CreateInstanceDefinition[] definitions = new ObjectUtils.CreateInstanceDefinition[] {
            ObjectUtils.CreateInstanceDefinition.builder()
                    .clazz(clazz)
                    .constructorTypes(new Class<?>[] {String.class, Throwable.class})
                    .constructorArgs(new Object[] {"test", new IllegalStateException("TEST")})
                    .build(),
            ObjectUtils.CreateInstanceDefinition.builder()
                    .clazz(clazz)
                    .constructorTypes(new Class<?>[] {Throwable.class})
                    .constructorArgs(new Object[] {new IllegalStateException("TEST")})
                    .build()
        };
        ApimsRuntimeException e =
                assertThrows(ApimsRuntimeException.class, () -> ObjectUtils.createInstanceByDefinitions(definitions));
        assertNotNull(e.getCause());
        assertTrue(e.getCause() instanceof NoSuchMethodException);

        assertNull(ObjectUtils.createInstanceByDefinitions());
    }

    @Validated
    private static class TestData {

        private TestDataPair pair = new TestDataPair();

        public TestData() {}

        public TestData(TestDataPair pair) {
            this.pair = pair;
        }

        @ApimsAroundLoggingListenerSuppress
        public TestDataPair getPair() {
            return pair;
        }

        public void setPair(TestDataPair pair) {
            this.pair = pair;
        }

        private static class TestDataPair {
            @Id
            private String key;

            private String value;

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }
    }
}
