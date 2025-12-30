/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.constraints.Max;
import java.util.Map;
import org.junit.jupiter.api.Test;

class AnnotationUtilsTest {

    @Test
    void annotationTest() {
        Max annotation = AnnotationUtils.createAnnotation(Max.class, Map.of("value", 42L));
        assertNotNull(annotation);
        assertEquals(42L, annotation.value());
    }

    @Test
    void missingAnnotationValueTest() {
        IllegalStateException e =
                assertThrows(IllegalStateException.class, () -> AnnotationUtils.createAnnotation(Max.class));
        assertEquals("Missing annotation value(s) for value", e.getMessage());
    }

    @Test
    void incompatibleAnnotationTypeTest() {
        Map<String, Object> annotationValues = Map.of("value", 42);
        IllegalStateException e = assertThrows(
                IllegalStateException.class, () -> AnnotationUtils.createAnnotation(Max.class, annotationValues));
        assertEquals("Incompatible annotation type(s) provided for value", e.getMessage());
    }
}
