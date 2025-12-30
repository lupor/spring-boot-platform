/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.aspects.core.around;

import de.sky.newcrm.apims.spring.aspects.MockedProceedingJoinPoint;
import de.sky.newcrm.apims.spring.exceptions.ApimsBusinessException;
import de.sky.newcrm.apims.spring.exceptions.BusinessException;
import de.sky.newcrm.apims.spring.utils.ObjectUtils;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AnnotationUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class ApimsAroundTest {

    @Test
    void aspectAroundHandlerDefaultImplTest() {
        ApimsAspectAroundHandlerDefaultImpl instance = new ApimsAspectAroundHandlerDefaultImpl(false, null, null);
        assertTrue(instance.getActiveCallsCount() > -1);
        instance.incrementActiveCallsCount();
        instance.decrementActiveCallsCount();
        assertTrue(instance.getActiveCallsCount() > -1);
    }


    @Test
    void aroundHelperURITest() {
        URI uri = URI.create("http://localhost.com");
        MockedProceedingJoinPoint proceedingJoinPoint =
                new MockedProceedingJoinPoint(null, null, new Object[]{uri, 42L});
        ApimsAroundContext apimsAroundContext = ApimsAroundContext.builder()
                .proceedingJoinPoint(proceedingJoinPoint)
                .build();
        assertNotNull(ApimsAroundHelper.getURI(apimsAroundContext));
        proceedingJoinPoint =
                new MockedProceedingJoinPoint(null, null, new Object[]{"whatever", "http://localhost.com"});
        apimsAroundContext = ApimsAroundContext.builder()
                .proceedingJoinPoint(proceedingJoinPoint)
                .build();
        assertNotNull(ApimsAroundHelper.getURI(apimsAroundContext));
        proceedingJoinPoint = new MockedProceedingJoinPoint(null, null, new Object[]{"https://localhost.com"});
        apimsAroundContext = ApimsAroundContext.builder()
                .proceedingJoinPoint(proceedingJoinPoint)
                .build();
        assertNotNull(ApimsAroundHelper.getURI(apimsAroundContext));
        proceedingJoinPoint =
                new MockedProceedingJoinPoint(null, null, new Object[]{"https://localhost.com?param=test"});
        apimsAroundContext = ApimsAroundContext.builder()
                .proceedingJoinPoint(proceedingJoinPoint)
                .build();
        assertNotNull(ApimsAroundHelper.getURI(apimsAroundContext));
        proceedingJoinPoint = new MockedProceedingJoinPoint(
                null, null, new Object[]{"https://localhost.com?param=test&invalid_param=42 42"});
        apimsAroundContext = ApimsAroundContext.builder()
                .proceedingJoinPoint(proceedingJoinPoint)
                .build();
        assertNotNull(ApimsAroundHelper.getURI(apimsAroundContext));
        proceedingJoinPoint = new MockedProceedingJoinPoint(null, null, new Object[]{"http:whatever 42"});
        apimsAroundContext = ApimsAroundContext.builder()
                .proceedingJoinPoint(proceedingJoinPoint)
                .build();
        assertNotNull(ApimsAroundHelper.getURI(apimsAroundContext));
        proceedingJoinPoint =
                new MockedProceedingJoinPoint(null, null, new Object[]{"https:?param=test&invalid_param=42 42"});
        apimsAroundContext = ApimsAroundContext.builder()
                .proceedingJoinPoint(proceedingJoinPoint)
                .build();
        assertNotNull(ApimsAroundHelper.getURI(apimsAroundContext));
        proceedingJoinPoint = new MockedProceedingJoinPoint(null, null, new Object[]{"https: 42 43"});
        apimsAroundContext = ApimsAroundContext.builder()
                .proceedingJoinPoint(proceedingJoinPoint)
                .build();
        assertNotNull(ApimsAroundHelper.getURI(apimsAroundContext));
        proceedingJoinPoint = new MockedProceedingJoinPoint(null, null, new Object[]{"whatever"});
        apimsAroundContext = ApimsAroundContext.builder()
                .proceedingJoinPoint(proceedingJoinPoint)
                .build();
        assertNull(ApimsAroundHelper.getURI(apimsAroundContext));
    }

    @Test
    void resolveProceedingJoinPointExceptionWithoutAnnotationTest() {
        TestApimsAspectAroundHandlerDefaultImpl instance = new TestApimsAspectAroundHandlerDefaultImpl(false);
        Exception resultException = instance.resolveProceedingJoinPointException(new IOException("Test IOException"));
        assertNotNull(resultException);
        assertTrue(resultException instanceof IOException);
        resultException = instance.resolveProceedingJoinPointException(null);
        assertNull(resultException);
    }

    @Test
    void resolveProceedingJoinPointExceptionWithAnnotationTest() {
        TestApimsAspectAroundHandlerDefaultImpl instance = new TestApimsAspectAroundHandlerDefaultImpl(true);
        Exception resultException = instance.resolveProceedingJoinPointException(new IOException("Test IOException"));
        assertNotNull(resultException);
        assertTrue(resultException instanceof RuntimeException);
        assertNotNull(resultException.getCause());
        assertTrue(resultException.getCause() instanceof IOException);

        resultException = instance.resolveProceedingJoinPointException(new NullPointerException());
        assertNotNull(resultException);
        assertTrue(resultException instanceof TestProceedingJoinPointException);
    }

    @Test
    void resolveProceedingJoinPointExceptionWithAnnotationNmTest() {
        TestApimsAspectAroundHandlerDefaultImpl instance = new TestApimsAspectAroundHandlerDefaultImpl(true);
        Exception resultException =
                instance.resolveProceedingJoinPointException(new IllegalStateException("Test IllegalStateException"));
        assertNotNull(resultException);
        assertTrue(resultException instanceof IllegalStateException);
    }

    private static class TestApimsAspectAroundHandlerDefaultImpl extends ApimsAspectAroundHandlerDefaultImpl {

        private boolean mappingsPresent = false;

        public TestApimsAspectAroundHandlerDefaultImpl(boolean mappingsPresent) {
            super(false, null, null);
            this.mappingsPresent = mappingsPresent;
        }

        @Override
        protected <A extends Annotation> A findCurrentMethodAnnotation(Class<A> annotationType) {
            Method method = ObjectUtils.findMethod(
                    TestProceedingJoinPoint.class,
                    mappingsPresent ? "executeWithExceptionMapping" : "executeWithoutExceptionMapping");
            return AnnotationUtils.findAnnotation(method, annotationType);
        }
    }

    private static class TestProceedingJoinPoint {

        public void executeWithoutExceptionMapping() {
        }

        @ApimsAroundExceptionMappings({
                @ApimsAroundExceptionMapping(
                        on = {IllegalAccessException.class, IOException.class},
                        raise = RuntimeException.class),
                @ApimsAroundExceptionMapping(
                        on = {NullPointerException.class},
                        raise = TestProceedingJoinPointException.class)
        })
        public void executeWithExceptionMapping() {
        }
    }

    @ApimsBusinessException("TEST_PROCEEDING_JOIN_POINT_EXCEPTION")
    private static class TestProceedingJoinPointException extends BusinessException {
    }
}
