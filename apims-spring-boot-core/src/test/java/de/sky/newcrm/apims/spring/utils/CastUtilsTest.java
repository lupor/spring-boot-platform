/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import org.junit.jupiter.api.Test;

class CastUtilsTest {

    @Test
    void castTest() {
        new CastUtils() {};
        assertNull(CastUtils.getValue(String.class, null));
        assertNull(CastUtils.getValue(null, null));
        assertNotNull(CastUtils.getValue(String.class, "test"));
        assertNotNull(CastUtils.getValue(Object.class, BigInteger.valueOf(42)));

        assertForType(String.class);
        assertForType(Long.class);
        assertForType(Double.class);
        assertForType(Float.class);
        assertForType(Integer.class);
        assertForType(Short.class);
        assertForType(Boolean.class);
        assertForType(Date.class);
        assertForType(Calendar.class);

        assertThrows(Exception.class, () -> CastUtils.getValue(Date.class, true));
        assertThrows(Exception.class, () -> CastUtils.getValue(Calendar.class, "invalid"));
    }

    protected void assertForType(Class<?> type) {
        boolean canHandleBooleanValues = true;
        boolean canHandleDateTimeValues = true;

        if (Integer.class.equals(type)) {
            canHandleDateTimeValues = false;
        } else if (Short.class.equals(type)) {
            canHandleDateTimeValues = false;
        } else if (Boolean.class.equals(type)) {
            canHandleDateTimeValues = false;
        } else if (Date.class.equals(type)) {
            canHandleBooleanValues = false;
        } else if (Calendar.class.equals(type)) {
            canHandleBooleanValues = false;
        }

        assertNotNull(CastUtils.getValue(type, 42L));
        assertNotNull(CastUtils.getValue(type, 42));
        assertNotNull(CastUtils.getValue(type, "2042"));
        assertNotNull(CastUtils.getValue(type, new StringBuilder("2042")));
        assertNotNull(CastUtils.getValue(type, new StringBuffer("2042")));

        if (canHandleBooleanValues) {
            assertNotNull(CastUtils.getValue(type, true));
            assertNotNull(CastUtils.getValue(type, false));
        }
        if (canHandleDateTimeValues) {
            assertNotNull(CastUtils.getValue(type, new Date()));
            assertNotNull(CastUtils.getValue(type, new Timestamp(new Date().getTime())));
            assertNotNull(CastUtils.getValue(type, Calendar.getInstance()));
        }
    }
}
