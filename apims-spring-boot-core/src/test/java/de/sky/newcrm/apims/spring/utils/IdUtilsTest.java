/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class IdUtilsTest {

    @Test
    void nextIdTest() {
        assertNotEquals(0L, IdUtils.nextLong());
        assertEquals(32, IdUtils.nextId().length());
        assertEquals(32, IdUtils.nextId(true).length());
        assertEquals(16, IdUtils.nextId(false).length());
    }

    @Test
    void removeAndTrimAllWhiteSpacesTest() {
        final String[] removeValues = {"&", "-", "+", "_", ",", ";", "|", "(", ")", "[", "]", " and ", " And "};
        final String value = "Test and Anda&_otherText(test)";
        assertEquals("TestAndaotherTexttest", IdUtils.removeAndTrimAllWhiteSpaces(value, false, removeValues));
        assertEquals("TESTANDAOTHERTEXTTEST", IdUtils.removeAndTrimAllWhiteSpaces(value, true, removeValues));
        assertEquals("TestandAnda&_otherText(test)", IdUtils.removeAndTrimAllWhiteSpaces(value, false));
        assertEquals("TESTANDANDA&_OTHERTEXT(TEST)", IdUtils.removeAndTrimAllWhiteSpaces(value, true));
        assertEquals(
                "TestandAnda&_otherText(test)", IdUtils.removeAndTrimAllWhiteSpaces(value, false, (String[]) null));
        assertEquals("TESTANDANDA&_OTHERTEXT(TEST)", IdUtils.removeAndTrimAllWhiteSpaces(value, true, (String[]) null));
        assertEquals("TestandAnda&_otherText(test)", IdUtils.removeAndTrimAllWhiteSpaces(value, false, null, null));
        assertEquals("TESTANDANDA&_OTHERTEXT(TEST)", IdUtils.removeAndTrimAllWhiteSpaces(value, true, null, null));
    }

    @Test
    void encodeIdTest() {
        String value = IdUtils.nextId();
        String enc = IdUtils.encodeId(value);
        String dec = IdUtils.decodeId(enc);
        assertEquals(value, dec);
        assertNull(IdUtils.encodeId(null));
        assertNull(IdUtils.decodeId(null));
    }

    @Test
    void asciiToHexTest() {
        String value = IdUtils.nextId();
        String enc = IdUtils.asciiToHex(value);
        String dec = IdUtils.hexToAscii(enc);
        assertEquals(value, dec);
        assertNull(IdUtils.asciiToHex(null));
        assertNull(IdUtils.hexToAscii(null));
    }
}
