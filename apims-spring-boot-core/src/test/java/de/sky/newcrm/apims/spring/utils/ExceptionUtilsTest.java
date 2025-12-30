/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import static de.sky.newcrm.apims.spring.utils.ExceptionUtils.MAX_MESSAGES;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;

import de.sky.newcrm.apims.spring.exceptions.ApimsRuntimeException;
import de.sky.newcrm.apims.spring.exceptions.ApimsUndeclaredThrowableException;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

class ExceptionUtilsTest {

    @Test
    void exceptionTest() {
        assertNull(ExceptionUtils.getExceptionMessage(null));
        assertNull(ExceptionUtils.getFirstExceptionMessage(null));
        assertNull(ExceptionUtils.getLastExceptionMessage(null));
        assertNull(ExceptionUtils.getExceptionInfo(null));
        assertEquals("", ExceptionUtils.getExceptionAsString(null));

        IOException exception1 = new IOException("socket exception");
        IllegalStateException exception2 = new IllegalStateException("connection not created", exception1);
        ApimsRuntimeException exception = new ApimsRuntimeException(exception2);
        assertEquals("connection not created", ExceptionUtils.getFirstExceptionMessage(exception));
        assertEquals("socket exception", ExceptionUtils.getLastExceptionMessage(exception));

        exception = new ApimsRuntimeException("main exception", exception2);
        assertEquals("main exception", ExceptionUtils.getFirstExceptionMessage(exception));
        assertEquals("socket exception", ExceptionUtils.getLastExceptionMessage(exception));
        assertEquals(
                "main exception\nconnection not created\nsocket exception",
                ExceptionUtils.getExceptionMessage(exception));
        assertEquals(
                "de.sky.newcrm.apims.spring.exceptions.ApimsRuntimeException",
                ExceptionUtils.getExceptionMessage(new ApimsRuntimeException()));
        assertNotNull(ExceptionUtils.getExceptionInfo(exception));
        assertTrue(StringUtils.hasLength(ExceptionUtils.getExceptionAsString(exception)));
    }

    @Test
    void exceptionMaxTest() {
        ApimsRuntimeException exception = new ApimsRuntimeException("exception");
        for (int i = 0; i < MAX_MESSAGES + 1; i++) {
            exception = new ApimsRuntimeException("", exception);
        }
        ApimsRuntimeException mainException = new ApimsRuntimeException("main exception", exception);

        assertEquals(
                "de.sky.newcrm.apims.spring.exceptions.ApimsRuntimeException",
                ExceptionUtils.getFirstExceptionMessage(exception));
        assertEquals(
                "de.sky.newcrm.apims.spring.exceptions.ApimsRuntimeException",
                ExceptionUtils.getLastExceptionMessage(exception));
        assertEquals(
                "de.sky.newcrm.apims.spring.exceptions.ApimsRuntimeException",
                ExceptionUtils.getExceptionMessage(exception));

        assertNotNull(ExceptionUtils.getExceptionInfo(mainException));
        assertEquals("main exception", ExceptionUtils.getFirstExceptionMessage(mainException));
        assertEquals("main exception", ExceptionUtils.getLastExceptionMessage(mainException));
        assertEquals("main exception", ExceptionUtils.getExceptionMessage(mainException));
    }

    @Test
    void resolveUndeclaredThrowableExceptionTest() {
        IOException noRuntimeException = new IOException("socket exception");
        IllegalStateException runtimeException =
                new IllegalStateException("connection not created", noRuntimeException);

        assertNull(ExceptionUtils.resolveUndeclaredThrowableException(null));

        Exception resolvedException = ExceptionUtils.resolveUndeclaredThrowableException(noRuntimeException);
        assertTrue(resolvedException instanceof IOException);

        resolvedException = ExceptionUtils.resolveUndeclaredThrowableException(
                new UndeclaredThrowableException(noRuntimeException));
        assertTrue(resolvedException instanceof IOException);

        resolvedException = ExceptionUtils.resolveUndeclaredThrowableException(
                new UndeclaredThrowableException(new UndeclaredThrowableException(noRuntimeException)));
        assertTrue(resolvedException instanceof IOException);

        resolvedException = ExceptionUtils.resolveUndeclaredThrowableException(runtimeException);
        assertTrue(resolvedException instanceof IllegalStateException);

        resolvedException =
                ExceptionUtils.resolveUndeclaredThrowableException(new UndeclaredThrowableException(runtimeException));
        assertTrue(resolvedException instanceof IllegalStateException);

        resolvedException = ExceptionUtils.resolveUndeclaredThrowableException(
                new UndeclaredThrowableException(new UndeclaredThrowableException(runtimeException)));
        assertTrue(resolvedException instanceof IllegalStateException);

        Throwable resolvedThrowable = ExceptionUtils.resolveUndeclaredThrowable(new OutOfMemoryError("test"));
        assertTrue(resolvedThrowable instanceof OutOfMemoryError);

        resolvedThrowable = ExceptionUtils.resolveUndeclaredThrowable(
                new UndeclaredThrowableException(new OutOfMemoryError("test")));
        assertTrue(resolvedThrowable instanceof OutOfMemoryError);
    }

    @Test
    void resolveAsRuntimeExceptionTest() {
        IOException noRuntimeException = new IOException("socket exception");
        IllegalStateException runtimeException =
                new IllegalStateException("connection not created", noRuntimeException);

        RuntimeException resolvedRuntimeException = ExceptionUtils.resolveAsRuntimeException(
                new UndeclaredThrowableException(new OutOfMemoryError("test")));
        assertTrue(resolvedRuntimeException instanceof ApimsUndeclaredThrowableException);

        resolvedRuntimeException = ExceptionUtils.resolveAsRuntimeException(
                new UndeclaredThrowableException(new OutOfMemoryError("test")), ApimsRuntimeException.class);
        assertTrue(resolvedRuntimeException instanceof ApimsRuntimeException);

        resolvedRuntimeException = ExceptionUtils.resolveAsRuntimeException(noRuntimeException);
        assertTrue(resolvedRuntimeException instanceof ApimsUndeclaredThrowableException);

        resolvedRuntimeException =
                ExceptionUtils.resolveAsRuntimeException(noRuntimeException, ApimsRuntimeException.class);
        assertTrue(resolvedRuntimeException instanceof ApimsRuntimeException);

        resolvedRuntimeException =
                ExceptionUtils.resolveAsRuntimeException(new UndeclaredThrowableException(runtimeException));
        assertTrue(resolvedRuntimeException instanceof IllegalStateException);

        resolvedRuntimeException = ExceptionUtils.resolveAsRuntimeException(
                new UndeclaredThrowableException(runtimeException), ApimsRuntimeException.class);
        assertTrue(resolvedRuntimeException instanceof IllegalStateException);

        resolvedRuntimeException = ExceptionUtils.resolveAsRuntimeException(runtimeException);
        assertTrue(resolvedRuntimeException instanceof IllegalStateException);

        resolvedRuntimeException =
                ExceptionUtils.resolveAsRuntimeException(runtimeException, ApimsRuntimeException.class);
        assertTrue(resolvedRuntimeException instanceof IllegalStateException);

        resolvedRuntimeException =
                ExceptionUtils.resolveAsRuntimeException(new UndeclaredThrowableException(runtimeException));
        assertTrue(resolvedRuntimeException instanceof IllegalStateException);

        resolvedRuntimeException = ExceptionUtils.resolveAsRuntimeException(
                new UndeclaredThrowableException(runtimeException), ApimsRuntimeException.class);
        assertTrue(resolvedRuntimeException instanceof IllegalStateException);

        resolvedRuntimeException =
                ExceptionUtils.resolveAsRuntimeException(new ApimsUndeclaredThrowableException(runtimeException));
        assertTrue(resolvedRuntimeException instanceof ApimsUndeclaredThrowableException);

        resolvedRuntimeException = ExceptionUtils.resolveAsRuntimeException(
                new ApimsUndeclaredThrowableException(runtimeException), ApimsRuntimeException.class);
        assertTrue(resolvedRuntimeException instanceof ApimsUndeclaredThrowableException);
    }
}
