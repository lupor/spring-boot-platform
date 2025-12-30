/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.environment.core;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;

@SuppressWarnings("java:S5778")
@Slf4j
class ApimsMockedSpringContextTest {

    @Test
    void mockedSpringContextTest() {

        ApimsMockedSpringContext.INSTANCE.unregisterAllTestBeans();
        ApimsSpringContext.setApplicationContext(null);
        ApimsSpringContext.setEnvironment(null);

        assertThrows(IllegalStateException.class, () -> ApimsSpringContext.getApplicationContext()
                .getBean(TestBean.class));
        assertThrows(IllegalStateException.class, () -> ApimsSpringContext.getApplicationContext()
                .getBean("testBean"));
        assertThrows(IllegalStateException.class, () -> ApimsSpringContext.getApplicationContext()
                .getBeansOfType(TestBean.class));

        TestBean testBeanMock = mock(TestBean.class);
        when(testBeanMock.getValue()).thenReturn("mockValue");
        ApimsMockedSpringContext.INSTANCE.registerTestBean("testBeanMock", TestBean.class, testBeanMock);
        assertNotNull(ApimsSpringContext.getApplicationContext().getBean(TestBean.class));
        assertEquals(
                "mockValue",
                ApimsSpringContext.getApplicationContext()
                        .getBean(TestBean.class)
                        .getValue());
        ApimsMockedSpringContext.INSTANCE.registerTestBean(
                "testBean", TestBean.class, TestBean.builder().value("value").build());
        TestBean testBean =
                (TestBean) ApimsSpringContext.getApplicationContext().getBean("testBean");
        assertNotNull(testBean);
        assertEquals("value", testBean.getValue());
        assertThrows(NoUniqueBeanDefinitionException.class, () -> ApimsSpringContext.getApplicationContext()
                .getBean(TestBean.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> ApimsSpringContext.getApplicationContext()
                .getBean("not-existing-bean"));
        assertThrows(NoSuchBeanDefinitionException.class, () -> ApimsSpringContext.getApplicationContext()
                .getBean(String.class));
        assertEquals(
                2,
                ApimsSpringContext.getApplicationContext()
                        .getBeansOfType(TestBean.class)
                        .size());

        ApimsMockedSpringContext.INSTANCE.overrideTestProperty("test-key", "test-value");
        assertEquals("test-value", ApimsSpringContext.getProperty("test-key", "default-value"));
        assertEquals("default-value", ApimsSpringContext.getProperty("test-key-not-exists", "default-value"));

        ApimsMockedSpringContext.INSTANCE.unregisterAllTestBeans();
        assertThrows(NoSuchBeanDefinitionException.class, () -> ApimsSpringContext.getApplicationContext()
                .getBean(TestBean.class));
        ApimsMockedSpringContext.INSTANCE.resetTestApplicationContext();
        assertThrows(IllegalStateException.class, () -> ApimsSpringContext.getApplicationContext()
                .getBean(TestBean.class));
    }

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class TestBean {
        private String value;
    }
}
