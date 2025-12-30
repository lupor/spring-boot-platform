/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;

import de.sky.newcrm.apims.spring.exceptions.ApimsRuntimeException;
import de.sky.newcrm.apims.spring.exceptions.ApimsUndeclaredThrowableException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"java:S5961", "java:S5778"})
@Slf4j
class FunctionUtilsTest {

    private boolean voidCallFunctionExecuted = false;

    @BeforeEach
    void setUp() {
        voidCallFunctionExecuted = false;
    }

    @Test
    void acceptConsumerTest() {
        assertDoesNotThrow(() -> FunctionUtils.INSTANCE.acceptIfCondition(false, false, null, this::acceptObjectValue));
        assertDoesNotThrow(() -> FunctionUtils.INSTANCE.acceptIfCondition(false, true, null, this::acceptObjectValue));
        assertDoesNotThrow(() -> FunctionUtils.INSTANCE.acceptIfCondition(true, false, null, this::acceptObjectValue));
        assertDoesNotThrow(() -> FunctionUtils.INSTANCE.acceptIfCondition(true, true, null, this::acceptObjectValue));
        assertDoesNotThrow(
                () -> FunctionUtils.INSTANCE.acceptIfCondition(true, false, "noException", this::acceptObjectValue));
        assertDoesNotThrow(
                () -> FunctionUtils.INSTANCE.acceptIfCondition(true, true, "noException", this::acceptObjectValue));
        assertThrows(
                IllegalStateException.class,
                () -> FunctionUtils.INSTANCE.acceptIfCondition(true, false, "raiseException", this::acceptObjectValue));
        assertDoesNotThrow(
                () -> FunctionUtils.INSTANCE.acceptIfCondition(true, true, "raiseException", this::acceptObjectValue));
        assertThrows(
                ApimsUndeclaredThrowableException.class,
                () -> FunctionUtils.INSTANCE.acceptIfCondition(
                        true, false, "raiseIoException", this::acceptObjectValue));
        assertDoesNotThrow(() ->
                FunctionUtils.INSTANCE.acceptIfCondition(true, true, "raiseIoException", this::acceptObjectValue));

        assertDoesNotThrow(() -> FunctionUtils.INSTANCE.acceptIfHasText(null, this::acceptObjectValue));
        assertDoesNotThrow(() -> FunctionUtils.INSTANCE.acceptIfHasText("noException", this::acceptObjectValue));
        assertThrows(
                IllegalStateException.class,
                () -> FunctionUtils.INSTANCE.acceptIfHasText("raiseException", this::acceptObjectValue));
        assertThrows(
                ApimsUndeclaredThrowableException.class,
                () -> FunctionUtils.INSTANCE.acceptIfHasText("raiseIoException", this::acceptObjectValue));

        assertDoesNotThrow(() -> FunctionUtils.INSTANCE.acceptIfNotNull(null, this::acceptObjectValue));
        assertDoesNotThrow(() -> FunctionUtils.INSTANCE.acceptIfNotNull("noException", this::acceptObjectValue));
        assertThrows(
                IllegalStateException.class,
                () -> FunctionUtils.INSTANCE.acceptIfNotNull("raiseException", this::acceptObjectValue));
        assertThrows(
                ApimsUndeclaredThrowableException.class,
                () -> FunctionUtils.INSTANCE.acceptIfNotNull("raiseIoException", this::acceptObjectValue));

        assertDoesNotThrow(() -> FunctionUtils.INSTANCE.acceptIfNotEmpty(null, this::acceptArrayValue));
        assertDoesNotThrow(
                () -> FunctionUtils.INSTANCE.acceptIfNotEmpty(new Object[] {"noException"}, this::acceptArrayValue));
        assertThrows(
                IllegalStateException.class,
                () -> FunctionUtils.INSTANCE.acceptIfNotEmpty(new Object[] {"raiseException"}, this::acceptArrayValue));
        assertThrows(
                ApimsUndeclaredThrowableException.class,
                () -> FunctionUtils.INSTANCE.acceptIfNotEmpty(
                        new Object[] {"raiseIoException"}, this::acceptArrayValue));

        assertDoesNotThrow(() -> FunctionUtils.INSTANCE.acceptIfNotEmpty(null, this::acceptListValue));
        assertDoesNotThrow(
                () -> FunctionUtils.INSTANCE.acceptIfNotEmpty(List.of("noException"), this::acceptListValue));
        assertThrows(
                IllegalStateException.class,
                () -> FunctionUtils.INSTANCE.acceptIfNotEmpty(List.of("raiseException"), this::acceptListValue));
        assertThrows(
                ApimsUndeclaredThrowableException.class,
                () -> FunctionUtils.INSTANCE.acceptIfNotEmpty(List.of("raiseIoException"), this::acceptListValue));
    }

    @Test
    void acceptBiConsumerTest() {
        assertDoesNotThrow(
                () -> FunctionUtils.INSTANCE.acceptIfCondition(false, false, null, null, this::acceptObjectsValue));
        assertDoesNotThrow(
                () -> FunctionUtils.INSTANCE.acceptIfCondition(false, true, null, null, this::acceptObjectsValue));
        assertDoesNotThrow(
                () -> FunctionUtils.INSTANCE.acceptIfCondition(true, false, null, null, this::acceptObjectsValue));
        assertDoesNotThrow(
                () -> FunctionUtils.INSTANCE.acceptIfCondition(true, true, null, null, this::acceptObjectsValue));
        assertDoesNotThrow(() ->
                FunctionUtils.INSTANCE.acceptIfCondition(true, false, null, "noException", this::acceptObjectsValue));
        assertDoesNotThrow(() ->
                FunctionUtils.INSTANCE.acceptIfCondition(true, true, null, "noException", this::acceptObjectsValue));
        assertThrows(
                IllegalStateException.class,
                () -> FunctionUtils.INSTANCE.acceptIfCondition(
                        true, false, null, "raiseException", this::acceptObjectsValue));
        assertDoesNotThrow(() ->
                FunctionUtils.INSTANCE.acceptIfCondition(true, true, null, "raiseException", this::acceptObjectsValue));
        assertThrows(
                ApimsUndeclaredThrowableException.class,
                () -> FunctionUtils.INSTANCE.acceptIfCondition(
                        true, null, "raiseIoException", this::acceptObjectsValue));
        assertDoesNotThrow(() ->
                FunctionUtils.INSTANCE.acceptIfCondition(false, "key", "raiseIoException", this::acceptObjectsValue));
        assertDoesNotThrow(
                () -> FunctionUtils.INSTANCE.acceptIfCondition(true, "key", "noException", this::acceptObjectsValue));
        assertDoesNotThrow(() -> FunctionUtils.INSTANCE.acceptIfCondition(
                true, true, "key", "raiseIoException", this::acceptObjectsValue));

        assertDoesNotThrow(() -> FunctionUtils.INSTANCE.acceptIfHasText(null, null, this::acceptObjectsValue));
        assertDoesNotThrow(() -> FunctionUtils.INSTANCE.acceptIfHasText(null, "noException", this::acceptObjectsValue));
        assertThrows(
                IllegalStateException.class,
                () -> FunctionUtils.INSTANCE.acceptIfHasText(null, "raiseException", this::acceptObjectsValue));
        assertThrows(
                ApimsUndeclaredThrowableException.class,
                () -> FunctionUtils.INSTANCE.acceptIfHasText(null, "raiseIoException", this::acceptObjectsValue));

        assertDoesNotThrow(() -> FunctionUtils.INSTANCE.acceptIfNotNull(null, null, this::acceptObjectsValue));
        assertDoesNotThrow(() -> FunctionUtils.INSTANCE.acceptIfNotNull(null, "noException", this::acceptObjectsValue));
        assertThrows(
                IllegalStateException.class,
                () -> FunctionUtils.INSTANCE.acceptIfNotNull(null, "raiseException", this::acceptObjectsValue));
        assertThrows(
                ApimsUndeclaredThrowableException.class,
                () -> FunctionUtils.INSTANCE.acceptIfNotNull(null, "raiseIoException", this::acceptObjectsValue));
    }

    @Test
    void callTest() {
        assertDoesNotThrow(() -> FunctionUtils.call(this::callFunction));
        assertEquals("OK", FunctionUtils.call(this::callFunction));
        assertThrows(IllegalStateException.class, () -> FunctionUtils.call(this::callFunctionWithException));
        assertThrows(
                ApimsUndeclaredThrowableException.class, () -> FunctionUtils.call(this::callFunctionWithIoException));
        assertThrows(
                ApimsRuntimeException.class,
                () -> FunctionUtils.call(this::callFunctionWithIoException, ApimsRuntimeException.class));
        assertDoesNotThrow(() -> FunctionUtils.call(() -> "OK"));
        assertEquals("OK", FunctionUtils.call(() -> "OK"));
    }

    @Test
    void applyTest() {
        final String value = "noException";
        assertNull(FunctionUtils.apply(null, this::executionWithParam));
        assertEquals(value, FunctionUtils.apply(value, this::executionWithParam));
        assertThrows(
                IllegalStateException.class, () -> FunctionUtils.apply("raiseException", this::executionWithParam));
        assertThrows(
                ApimsUndeclaredThrowableException.class,
                () -> FunctionUtils.apply("raiseIoException", this::executionWithParam));
    }

    @Test
    void executeTest() {
        final String value = "noException";
        assertDoesNotThrow(() -> FunctionUtils.execute(this::callFunction));
        assertDoesNotThrow(() -> FunctionUtils.execute(this::voidCallFunction));
        assertDoesNotThrow(() -> FunctionUtils.execute(this::voidCallFunctionWithIoException, true));
        assertDoesNotThrow(() -> FunctionUtils.execute(this::voidCallFunctionWithException, true));
        assertDoesNotThrow(() -> FunctionUtils.execute(
                e -> {
                    log.info("Test Error {}", e.getMessage());
                    return true;
                },
                this::voidCallFunctionWithException,
                this::voidCallFunctionWithException));
        assertDoesNotThrow(() -> FunctionUtils.execute(
                e -> {
                    log.info("Test Error {}", e.getMessage());
                    return false;
                },
                this::voidCallFunctionWithException,
                this::voidCallFunctionWithException));

        assertDoesNotThrow(() -> FunctionUtils.executeIfCondition(false, false, this::voidCallFunctionWithIoException));
        assertDoesNotThrow(() -> FunctionUtils.executeIfCondition(false, true, this::voidCallFunctionWithIoException));
        assertThrows(
                IllegalStateException.class,
                () -> FunctionUtils.executeIfCondition(true, false, this::voidCallFunctionWithException));
        assertDoesNotThrow(() -> FunctionUtils.executeIfCondition(true, true, this::voidCallFunctionWithIoException));

        assertDoesNotThrow(() -> FunctionUtils.execute(() -> voidCallFunctionWithParams("key", value)));

        assertNull(FunctionUtils.execute(() -> executionWithParam(null)));
        assertEquals(value, FunctionUtils.execute(() -> executionWithParam(value)));
        assertEquals(value, FunctionUtils.execute(() -> value));
        assertThrows(
                IllegalStateException.class,
                () -> FunctionUtils.execute(this::voidCallFunctionWithException, ApimsRuntimeException.class));
        assertThrows(
                ApimsRuntimeException.class,
                () -> FunctionUtils.execute(this::voidCallFunctionWithIoException, ApimsRuntimeException.class));
        assertThrows(
                IllegalStateException.class, () -> FunctionUtils.execute(() -> executionWithParam("raiseException")));
        assertNotNull(
                FunctionUtils.execute(() -> executionWithParam("raiseException"), e -> "defaultResultIfException"));
        assertDoesNotThrow(() -> FunctionUtils.execute(() -> executionWithParam("raiseException"), true));
        assertThrows(
                IllegalStateException.class,
                () -> FunctionUtils.execute(() -> executionWithParam("raiseException"), ApimsRuntimeException.class));
        assertThrows(
                IllegalStateException.class,
                () -> FunctionUtils.execute(() -> executionSneakyWithParam("raiseException")));
        assertThrows(
                ApimsUndeclaredThrowableException.class,
                () -> FunctionUtils.execute(() -> executionSneakyWithParam("raiseIoException")));
        assertDoesNotThrow(() -> FunctionUtils.execute(() -> executionWithParam("raiseIoException"), true));
        assertThrows(
                ApimsRuntimeException.class,
                () -> FunctionUtils.execute(() -> executionWithParam("raiseIoException"), ApimsRuntimeException.class));
        assertThrows(IllegalStateException.class, () -> FunctionUtils.execute(this::voidCallFunctionWithException));
        assertThrows(
                ApimsUndeclaredThrowableException.class,
                () -> FunctionUtils.execute(this::voidCallFunctionWithIoException));
        assertDoesNotThrow(() -> FunctionUtils.execute(this::voidCallFunctionWithIoException, true));
        assertThrows(
                IllegalStateException.class,
                () -> FunctionUtils.execute(() -> voidCallFunctionWithParams("key", "raiseException")));
        assertDoesNotThrow(
                () -> FunctionUtils.execute(() -> voidCallFunctionWithParams("key", "raiseException"), true));
        assertThrows(
                ApimsUndeclaredThrowableException.class,
                () -> FunctionUtils.execute(() -> voidCallFunctionWithParams("key", "raiseIoException")));
        assertDoesNotThrow(
                () -> FunctionUtils.execute(() -> voidCallFunctionWithParams("key", "raiseIoException"), true));

        assertEquals("OK", FunctionUtils.executeIfCondition(true, "NOK", () -> executionWithParam("OK")));
        assertEquals("NOK", FunctionUtils.executeIfCondition(false, "NOK", () -> executionWithParam("OK")));

        assertThrows(IOException.class, () -> checkSneakyException("raiseIoException"));
        assertThrows(IllegalStateException.class, () -> checkSneakyException("raiseException"));
    }

    @Test
    void executeIfConditionTest() {
        assertThrows(
                ApimsUndeclaredThrowableException.class,
                () -> FunctionUtils.executeIfNotNull(
                        "inValue", "defaultValue", () -> executionWithParam("raiseIoException")));
        assertEquals(
                "errorValue",
                FunctionUtils.executeIfNotNull(
                        "inValue", "defaultValue", () -> executionWithParam("raiseIoException"), e -> "errorValue"));
        assertNull(FunctionUtils.executeIfNotNull(
                "inValue", "defaultValue", () -> executionWithParam("raiseIoException"), e -> null));
        assertEquals(
                "defaultValue",
                FunctionUtils.executeIfNotNull(
                        null, "defaultValue", () -> executionWithParam("raiseIoException"), e -> "errorValue"));
        assertEquals(
                "errorValue",
                FunctionUtils.executeIfCondition(
                        true, "defaultValue", () -> executionWithParam("raiseIoException"), e -> "errorValue"));
        assertEquals(
                "defaultValue",
                FunctionUtils.executeIfCondition(
                        false, "defaultValue", () -> executionWithParam("raiseIoException"), e -> "errorValue"));

        assertEquals("notNullValue", FunctionUtils.executeIfNull("notNullValue", () -> executionWithParam("value")));
        assertEquals("value", FunctionUtils.executeIfNull(null, () -> executionWithParam("value")));

        assertDoesNotThrow(() -> FunctionUtils.executeIfNull("notNullValue", this::voidCallFunction));
        assertFalse(voidCallFunctionExecuted);
        assertDoesNotThrow(() -> FunctionUtils.executeIfNull(null, this::voidCallFunction));
        assertTrue(voidCallFunctionExecuted);

        voidCallFunctionExecuted = false;
        assertDoesNotThrow(() -> FunctionUtils.executeIfNotNull(null, this::voidCallFunction));
        assertFalse(voidCallFunctionExecuted);
        assertDoesNotThrow(() -> FunctionUtils.executeIfNotNull("notNullValue", this::voidCallFunction));
        assertTrue(voidCallFunctionExecuted);
    }

    @Test
    void executeCatchAllTest() {
        assertDoesNotThrow(() -> FunctionUtils.execute(this::voidCallFunction, true));
        assertDoesNotThrow(() -> FunctionUtils.execute(this::voidCallFunctionWithIoException, true));
        assertDoesNotThrow(() -> FunctionUtils.execute(this::voidCallFunctionWithException, true));
        assertDoesNotThrow(() -> FunctionUtils.execute(
                e -> true, this::voidCallFunctionWithException, this::voidCallFunctionWithException));
    }

    void acceptObjectValue(Object value) throws IOException {
        checkException(value);
    }

    void acceptObjectsValue(Object key, Object value) throws IOException {
        checkException(value);
    }

    void acceptArrayValue(Object[] value) throws IOException {
        checkException(value[0]);
    }

    void acceptListValue(List<Object> value) throws IOException {
        checkException(value.get(0));
    }

    String callFunction() {
        return "OK";
    }

    String callFunctionWithException() throws Exception {
        checkException("raiseException");
        return null;
    }

    String callFunctionWithIoException() throws IOException {
        checkException("raiseIoException");
        return null;
    }

    void voidCallFunction() {
        voidCallFunctionExecuted = true;
    }

    void voidCallFunctionWithException() throws Exception {
        checkException("raiseException");
    }

    void voidCallFunctionWithIoException() throws Exception {
        checkException("raiseIoException");
    }

    String executionWithParam(String value) throws Exception {
        checkException(value);
        return value;
    }

    String executionSneakyWithParam(String value) throws Exception {
        checkSneakyException(value);
        return value;
    }

    String voidCallFunctionWithParams(String key, String value) throws Exception {
        checkException(value);
        return key;
    }

    void checkException(Object value) throws IOException {
        if ("raiseException".equals(value)) {
            throw new IllegalStateException("test");
        } else if ("raiseIoException".equals(value)) {
            throw new IOException("test");
        }
    }

    @SneakyThrows
    void checkSneakyException(Object value) {
        if ("raiseException".equals(value)) {
            throw new IllegalStateException("test");
        } else if ("raiseIoException".equals(value)) {
            throw new IOException("test");
        }
    }
}
