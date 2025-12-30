/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class VeracodeMitigationUtilsTest {

    @Test
    void sanitizeLogValuesTest() {
        Object[] returnValues =
                VeracodeMitigationUtils.sanitizeLogValues("value 1 NL:\n, RT:\r, and some text", "value 2");
        assertNotNull(returnValues);
        assertEquals(2, returnValues.length);
        assertEquals("value 1 NL:\\n, RT:, and some text", returnValues[0]);
        assertEquals("value 2", returnValues[1]);
    }

    @Test
    void sanitizeNewFileTest() {
        assertNotNull(VeracodeMitigationUtils.sanitizeNewFile("/tmp"));
    }

    @Test
    void sanitizeUrlTest() {
        assertNotNull(VeracodeMitigationUtils.sanitizeUrl("https://test.com"));
        assertNull(VeracodeMitigationUtils.sanitizeUrl("sftp://test.com"));
    }
}
