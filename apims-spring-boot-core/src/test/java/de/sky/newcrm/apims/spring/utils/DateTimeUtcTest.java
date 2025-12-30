/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.TimeZone;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

@Slf4j
@SuppressWarnings("java:S5961")
class DateTimeUtcTest {

    @Test
    void commonTest() {
        assertNull(DateTimeUtc.format((Date) null));
        assertNull(DateTimeUtc.format((OffsetDateTime) null));
        assertNull(DateTimeUtc.format((LocalDate) null));
        assertNull(DateTimeUtc.format((LocalTime) null));

        assertNull(DateTimeUtc.parseDate(null));
        assertNull(DateTimeUtc.parseOffsetDateTime(null));
        assertNull(DateTimeUtc.parseLocalDate(null));
        assertNull(DateTimeUtc.parseLocalTime(null));

        assertNotNull(DateTimeUtc.getDateFormat("dd.MM.yyyy"));
        assertNotNull(DateTimeUtc.getDateFormat(DateTimeUtc.ISO.DATE_TIME_COMPLETE));
        assertNotNull(DateTimeUtc.getDateFormat(DateTimeUtc.ISO.NONE));
        assertNotNull(DateTimeUtc.getDateFormat((String) null));

        assertNotNull(DateTimeUtc.format(System.currentTimeMillis()));
        assertNotNull(DateTimeUtc.format(new Date(), "UTC"));
        assertNotNull(DateTimeUtc.format(new Date(), TimeZone.getTimeZone("UTC")));
        assertNotNull(DateTimeUtc.format(new Date(), DateTimeUtc.ISO.DATE_TIME_COMPLETE, null));
        assertNotNull(DateTimeUtc.format(new Date(), DateTimeUtc.ISO.DATE_TIME_COMPLETE_ZULU, null));

        assertNull(DateTimeUtc.resetTimeByDate(null));
        assertNull(DateTimeUtc.resetTimeByOffsetDateTime(null));
    }

    @Test
    void isoTest() {
        assertEquals("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", DateTimeUtc.getIsoPattern(null));
        assertEquals("yyyy-MM-dd", DateTimeUtc.getIsoPattern(DateTimeUtc.ISO.DATE));
        assertEquals("HH:mm:ssXXX", DateTimeUtc.getIsoPattern(DateTimeUtc.ISO.TIME));
        assertEquals("HH:mm:ss.SSSXXX", DateTimeUtc.getIsoPattern(DateTimeUtc.ISO.TIME_COMPLETE));
        assertEquals("HH:mm:ss", DateTimeUtc.getIsoPattern(DateTimeUtc.ISO.TIME_ONLY));
        assertEquals("yyyy-MM-dd'T'HH:mm:ssXXX", DateTimeUtc.getIsoPattern(DateTimeUtc.ISO.DATE_TIME));
        assertEquals("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", DateTimeUtc.getIsoPattern(DateTimeUtc.ISO.DATE_TIME_COMPLETE));
        assertEquals("yyyy-MM-dd'T'HH:mm:ss.SSSZ", DateTimeUtc.getIsoPattern(DateTimeUtc.ISO.DATE_TIME_COMPLETE_ZULU));
        assertEquals("yyyy-MM-dd'T'HH:mm:ss", DateTimeUtc.getIsoPattern(DateTimeUtc.ISO.DATE_TIME_ONLY));
    }

    @Test
    void resetDateByDateTest() {
        Date value = DateTimeUtc.resetTimeByDate(DateTimeUtc.parseDate("2024-06-06T01:42:42Z"));
        assertEquals("2024-06-06T00:00:00Z", DateTimeUtc.format(value));
    }

    @Test
    void resetDateByOffsetDateTimeTest() {
        OffsetDateTime value =
                DateTimeUtc.resetTimeByOffsetDateTime(DateTimeUtc.parseOffsetDateTime("2024-06-06T01:42:42Z"));
        assertEquals("2024-06-06T00:00:00Z", DateTimeUtc.format(value));
    }

    @Test
    void parseDateTimeWithInvalidInputTest() {
        assertThrows(
                DateTimeUtc.ParseException.class,
                () -> dateTest("2023-04-T07:00:00Z", DateTimeUtc.ISO.DATE_TIME_COMPLETE, null));
        assertThrows(
                DateTimeUtc.ParseException.class,
                () -> dateTest("2023--12T07:00:00Z", DateTimeUtc.ISO.DATE_TIME_COMPLETE, null));
        assertThrows(
                DateTimeUtc.ParseException.class,
                () -> dateTest("-04-12T07:00:00Z", DateTimeUtc.ISO.DATE_TIME_COMPLETE, null));
        assertThrows(
                DateTimeUtc.ParseException.class,
                () -> dateTest("2023-04-1T07:00:00Z", DateTimeUtc.ISO.DATE_TIME_COMPLETE, null));
        assertThrows(
                DateTimeUtc.ParseException.class,
                () -> dateTest("2023-4-12T07:00:00Z", DateTimeUtc.ISO.DATE_TIME_COMPLETE, null));
        assertThrows(
                DateTimeUtc.ParseException.class,
                () -> dateTest("23-04-12T07:00:00Z", DateTimeUtc.ISO.DATE_TIME_COMPLETE, null));
        assertThrows(
                DateTimeUtc.ParseException.class,
                () -> dateTest("2023-04-T07:00:Z", DateTimeUtc.ISO.DATE_TIME_COMPLETE, null));
        assertThrows(
                DateTimeUtc.ParseException.class,
                () -> dateTest("2023-04-T07::00Z", DateTimeUtc.ISO.DATE_TIME_COMPLETE, null));
        assertThrows(
                DateTimeUtc.ParseException.class,
                () -> dateTest("2023-04-T:00:00Z", DateTimeUtc.ISO.DATE_TIME_COMPLETE, null));
        assertThrows(
                DateTimeUtc.ParseException.class,
                () -> dateTest("2023-04-T07:00:0Z", DateTimeUtc.ISO.DATE_TIME_COMPLETE, null));
        assertThrows(
                DateTimeUtc.ParseException.class,
                () -> dateTest("2023-04-T07:0:00Z", DateTimeUtc.ISO.DATE_TIME_COMPLETE, null));
        assertThrows(
                DateTimeUtc.ParseException.class,
                () -> dateTest("2023-04-T0:00:00Z", DateTimeUtc.ISO.DATE_TIME_COMPLETE, null));
        assertThrows(
                DateTimeUtc.ParseException.class,
                () -> dateTest("2023-04-12T07:00:00-01000", DateTimeUtc.ISO.DATE_TIME_COMPLETE, null));
        assertThrows(
                DateTimeUtc.ParseException.class,
                () -> dateTest("2023-04-12T07:00:00-01000", DateTimeUtc.ISO.DATE_TIME_COMPLETE, null));
        assertThrows(
                DateTimeUtc.ParseException.class,
                () -> dateTest("2023-04-12T07:00:00+02:0000", DateTimeUtc.ISO.DATE_TIME_COMPLETE, null));
        assertThrows(
                DateTimeUtc.ParseException.class,
                () -> dateTest("2023-04-12T10:00:00+0200:00", DateTimeUtc.ISO.DATE_TIME_COMPLETE, null));
        assertThrows(
                DateTimeUtc.ParseException.class,
                () -> dateTest("2023-04-12T10:00:00+0200:00", DateTimeUtc.ISO.DATE_TIME_COMPLETE, null));
        assertThrows(
                DateTimeUtc.ParseException.class,
                () -> dateTest("2023-04-12T10:00:00722999999+02:00", DateTimeUtc.ISO.DATE_TIME_COMPLETE, null));
    }

    @Test
    void parseDateTimeTest() {
        for (DateTimeUtc.ISO iso : DateTimeUtc.ISO.values()) {
            boolean dateEnabled = false;
            boolean dateDelimiterEnabled = false;
            boolean timeEnabled = false;
            boolean timeMillisEnabled = false;
            boolean timeZoneEnabled = false;
            if (DateTimeUtc.ISO.DATE.equals(iso)) {
                dateEnabled = true;
            } else if (DateTimeUtc.ISO.TIME.equals(iso)) {
                timeEnabled = true;
                timeZoneEnabled = true;
            } else if (DateTimeUtc.ISO.TIME_ONLY.equals(iso)) {
                timeEnabled = true;
            } else if (DateTimeUtc.ISO.TIME_COMPLETE.equals(iso)) {
                timeEnabled = true;
                timeMillisEnabled = true;
                timeZoneEnabled = true;
            } else if (DateTimeUtc.ISO.DATE_TIME.equals(iso)) {
                dateEnabled = true;
                dateDelimiterEnabled = true;
                timeEnabled = true;
                timeZoneEnabled = true;
            } else if (DateTimeUtc.ISO.DATE_TIME_ONLY.equals(iso)) {
                dateEnabled = true;
                dateDelimiterEnabled = true;
                timeEnabled = true;
            } else if (DateTimeUtc.ISO.DATE_TIME_COMPLETE.equals(iso) || DateTimeUtc.ISO.NONE.equals(iso)) {
                dateEnabled = true;
                dateDelimiterEnabled = true;
                timeEnabled = true;
                timeMillisEnabled = true;
                timeZoneEnabled = true;
            } else if (DateTimeUtc.ISO.DATE_TIME_COMPLETE_ZULU.equals(iso)) {
                dateEnabled = true;
                dateDelimiterEnabled = true;
                timeEnabled = true;
                timeMillisEnabled = true;
                timeZoneEnabled = true;
            }
            String expectedWithMillisValue = calulateExpectedValue(
                    dateEnabled,
                    dateDelimiterEnabled,
                    timeEnabled,
                    timeMillisEnabled,
                    timeZoneEnabled,
                    "2023-04-12",
                    "T",
                    "08:00:00",
                    ".722",
                    "Z");
            if (!StringUtils.hasLength(expectedWithMillisValue)) {
                continue;
            }
            String expectedValue = calulateExpectedValue(
                    dateEnabled,
                    dateDelimiterEnabled,
                    timeEnabled,
                    timeMillisEnabled,
                    timeZoneEnabled,
                    "2023-04-12",
                    "T",
                    "08:00:00",
                    ".000",
                    "Z");
            // testAll("2023-04-12", iso, expectedValue);
            // test iso input date and time
            testAll("2023-04-12T07:00:00-01:00", iso, expectedValue);
            testAll("2023-04-12T07:00:00-0100", iso, expectedValue);
            testAll("2023-04-12T07:00:00-01", iso, expectedValue);
            testAll("2023-04-12T10:00:00+02", iso, expectedValue);
            testAll("2023-04-12T10:00:00+02:00", iso, expectedValue);
            testAll("2023-04-12T10:00+02:00", iso, expectedValue);
            testAll("2023-04-12T10+02:00", iso, expectedValue);
            testAll("2023-04-12T10:00:00+0200", iso, expectedValue);
            testAll("2023-04-12T07:00:00.722999999-01:00", iso, expectedWithMillisValue);
            testAll("2023-04-12T07:00:00.722999999-0100", iso, expectedWithMillisValue);
            testAll("2023-04-12T10:00:00.722999999+02:00", iso, expectedWithMillisValue);
            testAll("2023-04-12T10:00:00.722999999+0200", iso, expectedWithMillisValue);
            testAll("2023-04-12T08:00:00.722999999Z", iso, expectedWithMillisValue);
            testAll("2023-04-12T08:00:00Z", iso, expectedValue);
            testAll("2023-04-12T08:00Z", iso, expectedValue);
            testAll("2023-04-12T08Z", iso, expectedValue);
            testAll("2023-04-12T08:00:00", iso, expectedValue);
            testAll("2023-04-12T08:00", iso, expectedValue);
            // test german input date and time
            testAll("12.04.2023 10:00:00+02:00", iso, expectedValue);
            testAll("12.04.2023 10:00:00+0200", iso, expectedValue);
            testAll("12.04.2023 10:00:00.722999999+02:00", iso, expectedWithMillisValue);
            testAll("12.04.2023 10:00:00.722999999+0200", iso, expectedWithMillisValue);
            testAll("12.04.2023 08:00:00Z", iso, expectedValue);
            testAll("12.04.2023 08:00Z", iso, expectedValue);
            testAll("12.04.2023 08:00:00", iso, expectedValue);
            testAll("12.04.2023 08:00", iso, expectedValue);
            testAll("12.04.2023 08", iso, expectedValue);
            // test input date only
            expectedValue = calulateExpectedValue(
                    dateEnabled,
                    dateDelimiterEnabled,
                    timeEnabled,
                    timeMillisEnabled,
                    timeZoneEnabled,
                    "2023-04-12",
                    "T",
                    "00:00:00",
                    ".000",
                    "Z");
            testAll("2023-04-12Z", iso, expectedValue);
            testAll("2023-04-12", iso, expectedValue);
            testAll("2023-04-12T", iso, expectedValue);
            testAll("2023-04-12T00", iso, expectedValue);
            testAll("2023-04-12T00:00", iso, expectedValue);
            testAll("12.04.2023", iso, expectedValue);
            testAll("12.04.2023 ", iso, expectedValue);
            testAll("12.04.2023 00", iso, expectedValue);
            testAll("12.04.2023 00:00", iso, expectedValue);
            // test input time only
            expectedValue = calulateExpectedValue(
                    dateEnabled,
                    dateDelimiterEnabled,
                    timeEnabled,
                    timeMillisEnabled,
                    timeZoneEnabled,
                    "1970-01-01",
                    "T",
                    "08:00:00",
                    ".000",
                    "Z");
            expectedWithMillisValue = calulateExpectedValue(
                    dateEnabled,
                    dateDelimiterEnabled,
                    timeEnabled,
                    timeMillisEnabled,
                    timeZoneEnabled,
                    "1970-01-01",
                    "T",
                    "08:00:00",
                    ".722",
                    "Z");
            testAll("10:00:00+02:00", iso, expectedValue);
            testAll("10:00:00+0200", iso, expectedValue);
            testAll("10:00:00.722999999+02:00", iso, expectedWithMillisValue);
            testAll("10:00:00.722999999+0200", iso, expectedWithMillisValue);
            testAll("08:00:00Z", iso, expectedValue);
            testAll("08:00Z", iso, expectedValue);
            testAll("08:00:00", iso, expectedValue);
            testAll("08:00", iso, expectedValue);
            // special input
            expectedValue = calulateExpectedValue(
                    dateEnabled,
                    dateDelimiterEnabled,
                    timeEnabled,
                    timeMillisEnabled,
                    timeZoneEnabled,
                    "2023-01-01",
                    "T",
                    "00:00:00",
                    ".000",
                    "Z");
            testAll("2023", iso, expectedValue, !DateTimeUtc.ISO.DATE_TIME_COMPLETE_ZULU.equals(iso), false);

            if (DateTimeUtc.ISO.DATE.equals(iso)) {
                // special local date
                localDateTest("2023", iso, expectedValue);
            } else if (DateTimeUtc.ISO.TIME_ONLY.equals(iso)) {
                // special local time
                expectedValue = calulateExpectedValue(
                        dateEnabled,
                        dateDelimiterEnabled,
                        timeEnabled,
                        timeMillisEnabled,
                        timeZoneEnabled,
                        "1970-01-01",
                        "T",
                        "08:00:00",
                        ".000",
                        "Z");
                localTimeTest("08", iso, expectedValue);
                localTimeTest("08:00", iso, expectedValue);
            }
        }
    }

    private String calulateExpectedValue(
            boolean dateEnabled,
            boolean dateDelimiterEnabled,
            boolean timeEnable,
            boolean timeMillsEnabled,
            boolean timeZoneEnabled,
            String dateValue,
            String dateDelimiter,
            String timeValue,
            String timeMillisValue,
            String timeZoneValue) {
        String expected = "";
        if (dateEnabled) {
            expected = dateValue;
            if (dateDelimiterEnabled) {
                expected += dateDelimiter;
            }
        }
        if (timeEnable) {
            expected += timeValue;
            if (timeMillsEnabled) {
                expected += timeMillisValue;
            }
            if (timeZoneEnabled) {
                expected += timeZoneValue;
            }
        }
        return expected;
    }

    void testAll(String value, DateTimeUtc.ISO formatIso, String expectedValue) {
        testAll(
                value,
                formatIso,
                expectedValue,
                !DateTimeUtc.ISO.DATE_TIME_COMPLETE_ZULU.equals(formatIso),
                !DateTimeUtc.ISO.DATE_TIME_COMPLETE_ZULU.equals(formatIso));
    }

    void testAll(
            String value,
            DateTimeUtc.ISO formatIso,
            String expectedValue,
            boolean testOffsetDateTime,
            boolean testLocalDateTime) {
        if (!StringUtils.hasLength(expectedValue)) {
            return;
        }
        log.info("testAll: '{}', '{}', '{}'", value, formatIso.name(), expectedValue);
        dateTest(value, formatIso, expectedValue);
        if (testOffsetDateTime) {
            offsetDateTimeTest(value, formatIso, expectedValue);
        }
        if (testLocalDateTime) {
            boolean millisExpected = DateTimeUtc.ISO.DATE_TIME_COMPLETE.equals(formatIso)
                    || DateTimeUtc.ISO.TIME_COMPLETE.equals(formatIso);
            String expectedLocalDateValue =
                    expectedValue.replace("08:00:00", "00:00:00").replace(".722", ".000");
            if (!millisExpected) {
                expectedLocalDateValue = expectedLocalDateValue.replace(".000", "");
            }
            if (DateTimeUtc.ISO.NONE.equals(formatIso) && expectedLocalDateValue.contains("-")) {
                expectedLocalDateValue = expectedLocalDateValue.substring(0, 10);
            }
            log.info("localDateTest: '{}', '{}', '{}'", value, formatIso.name(), expectedLocalDateValue);
            localDateTest(value, formatIso, expectedLocalDateValue);

            String expectedLocalTimeValue = expectedValue.replace("2023-04-12", "1970-01-01");
            if (!millisExpected) {
                expectedLocalTimeValue =
                        expectedLocalTimeValue.replace(".000", "").replace(".722", "");
            }
            if (DateTimeUtc.ISO.NONE.equals(formatIso) && expectedLocalTimeValue.contains(":")) {
                expectedLocalTimeValue = expectedLocalTimeValue.substring(
                        expectedLocalTimeValue.indexOf(":") - 2, expectedLocalTimeValue.lastIndexOf(":") + 3);
            }
            log.info("localTimeTest: '{}', '{}', '{}'", value, formatIso.name(), expectedLocalTimeValue);
            localTimeTest(value, formatIso, expectedLocalTimeValue);
        }
    }

    void dateTest(String value, DateTimeUtc.ISO formatIso, String expectedValue) {
        Date date = DateTimeUtc.parseDate(value);
        String s = DateTimeUtc.format(date, formatIso);
        assertEquals(expectedValue, s);
        if (DateTimeUtc.ISO.NONE.equals(formatIso)) {
            assertNotNull(DateTimeUtc.format(date, (DateTimeUtc.ISO) null));
        }
    }

    void offsetDateTimeTest(String value, DateTimeUtc.ISO formatIso, String expectedValue) {
        OffsetDateTime date = DateTimeUtc.parseOffsetDateTime(value);
        String s = DateTimeUtc.format(date, formatIso);
        assertEquals(expectedValue, s);
        if (DateTimeUtc.ISO.NONE.equals(formatIso)) {
            assertNotNull(DateTimeUtc.format(date, null));
        }
    }

    void localDateTest(String value, DateTimeUtc.ISO formatIso, String expectedValue) {
        LocalDate date = DateTimeUtc.parseLocalDate(value);
        String s = DateTimeUtc.format(date, formatIso);
        assertEquals(expectedValue, s);
        if (DateTimeUtc.ISO.NONE.equals(formatIso)) {
            assertNotNull(DateTimeUtc.format(date, null));
        }
    }

    void localTimeTest(String value, DateTimeUtc.ISO formatIso, String expectedValue) {
        LocalTime date = DateTimeUtc.parseLocalTime(value);
        String s = DateTimeUtc.format(date, formatIso);
        assertEquals(expectedValue, s);
        if (DateTimeUtc.ISO.NONE.equals(formatIso)) {
            assertNotNull(DateTimeUtc.format(date, null));
        }
    }
}
