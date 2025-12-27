/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import de.sky.newcrm.apims.spring.exceptions.ApimsRuntimeException;
import lombok.*;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DateTimeUtc {

    private static final String COMPLETE_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    private static final String FIRST_DATE = "1970-01-01";
    private static final String FIRST_DATE_WITH_T = FIRST_DATE + "T";
    private static final TimeZone TIMEZONE_UTC = TimeZone.getTimeZone("UTC");
    private static final int DEFAULT_FORMATTER_STYLE = DateFormat.DEFAULT;
    private static final ZoneOffset ZONE_OFFSET_UTC = ZoneOffset.UTC;
    private static final Locale DEFAULT_FORMATTER_LOCALE = Locale.US;
    private static final Map<ISO, String> ISO_PATTERNS;

    static {
        Map<ISO, String> formats = new EnumMap<>(ISO.class);
        formats.put(ISO.DATE, "yyyy-MM-dd");
        formats.put(ISO.TIME, "HH:mm:ssXXX");
        formats.put(ISO.TIME_COMPLETE, "HH:mm:ss.SSSXXX");
        formats.put(ISO.TIME_ONLY, "HH:mm:ss");
        formats.put(ISO.DATE_TIME, "yyyy-MM-dd'T'HH:mm:ssXXX");
        formats.put(ISO.DATE_TIME_COMPLETE, COMPLETE_DATE_TIME_PATTERN);
        formats.put(ISO.DATE_TIME_COMPLETE_ZULU, "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        formats.put(ISO.DATE_TIME_ONLY, "yyyy-MM-dd'T'HH:mm:ss");
        formats.put(ISO.NONE, COMPLETE_DATE_TIME_PATTERN);
        ISO_PATTERNS = Collections.unmodifiableMap(formats);
    }

    private DateTimeUtc() {}

    public static String getIsoPattern(ISO iso) {
        return ISO_PATTERNS.get(iso == null ? ISO.NONE : iso);
    }

    public static Date resetTimeByDate(Date value) {
        return value == null ? null : parseDate(format(value, ISO.DATE));
    }

    public static OffsetDateTime resetTimeByOffsetDateTime(OffsetDateTime value) {
        return value == null ? null : parseOffsetDateTime(format(value, ISO.DATE));
    }

    public static OffsetDateTime parseOffsetDateTime(String value) {
        return StringUtils.hasLength(value) ? DateTimeParser.create(value).getAsOffsetDateTime() : null;
    }

    public static LocalDate parseLocalDate(String value) {
        return StringUtils.hasLength(value) ? DateTimeParser.create(value).getAsLocalDate() : null;
    }

    public static LocalTime parseLocalTime(String value) {
        if (StringUtils.hasLength(value) && value.length() < 3) {
            value += ":00";
        }
        return StringUtils.hasLength(value) ? DateTimeParser.create(value).getAsLocalTime() : null;
    }

    public static Date parseDate(String value) {
        return StringUtils.hasLength(value) ? DateTimeParser.create(value).getAsDate() : null;
    }

    public static String format(OffsetDateTime value) {
        return format(value, ISO.DATE_TIME);
    }

    public static String format(OffsetDateTime value, ISO iso) {
        if (value == null) {
            return null;
        }
        return getDateTimeFormatter(iso).format(value);
    }

    public static String format(LocalDate value) {
        return format(value, ISO.DATE);
    }

    public static String format(LocalDate value, ISO iso) {
        if (value == null) {
            return null;
        }
        String v = getDateTimeFormatter(ISO.DATE).format(value);
        iso = iso == null || ISO.NONE.equals(iso) ? ISO.DATE : iso;
        if (ISO.DATE.equals(iso)) {
            return v;
        } else if (ISO.DATE_TIME.equals(iso)) {
            v += "T00:00:00Z";
        } else if (ISO.DATE_TIME_COMPLETE.equals(iso)) {
            v += "T00:00:00.000Z";
        } else if (ISO.DATE_TIME_ONLY.equals(iso)) {
            v += "T00:00:00";
        } else if (ISO.TIME.equals(iso)) {
            v = "00:00:00Z";
        } else if (ISO.TIME_COMPLETE.equals(iso)) {
            v = "00:00:00.000Z";
        } else { // ISO.TIME_ONLY
            v = "00:00:00";
        }
        return v;
    }

    public static String format(LocalTime value) {
        return format(value, ISO.TIME_ONLY);
    }

    public static String format(LocalTime value, ISO iso) {
        if (value == null) {
            return null;
        }
        String v = DateTimeFormatter.ISO_LOCAL_TIME.withZone(ZONE_OFFSET_UTC).format(value);
        boolean containsMillis = v.contains(".");
        String withoutMillis = containsMillis ? v.substring(0, v.indexOf(".")) : v;
        String timeWithMillis = containsMillis ? v : (v + ".000");
        iso = iso == null || ISO.NONE.equals(iso) ? ISO.TIME_ONLY : iso;
        if (ISO.TIME_ONLY.equals(iso)) {
            return withoutMillis;
        } else if (ISO.TIME.equals(iso)) {
            v = withoutMillis + "Z";
        } else if (ISO.TIME_COMPLETE.equals(iso)) {
            v = timeWithMillis + "Z";
        } else if (ISO.DATE_TIME.equals(iso)) {
            v = FIRST_DATE_WITH_T + withoutMillis + "Z";
        } else if (ISO.DATE_TIME_COMPLETE.equals(iso)) {
            v = FIRST_DATE_WITH_T + timeWithMillis + "Z";
        } else if (ISO.DATE_TIME_ONLY.equals(iso)) {
            v = FIRST_DATE_WITH_T + withoutMillis;
        } else { // ISO.DATE
            v = FIRST_DATE;
        }
        return v;
    }

    public static String format(long value) {
        return format(new Date(value), ISO.DATE_TIME);
    }

    public static String format(Date value) {
        return format(value, ISO.DATE_TIME);
    }

    public static String format(Date value, ISO iso) {
        return format(value, iso, TIMEZONE_UTC);
    }

    public static String format(Date value, TimeZone timeZone) {
        return format(value, ISO.DATE_TIME, timeZone);
    }

    public static String format(Date value, String timeZone) {
        return format(value, ISO.DATE_TIME, TimeZone.getTimeZone(timeZone));
    }

    public static String format(Date value, ISO iso, TimeZone timeZone) {
        if (value == null) {
            return null;
        }
        DateFormat dateFormat = getDateFormat(iso);
        if (timeZone != null) {
            dateFormat.setTimeZone(timeZone);
        }
        String resultValue = dateFormat.format(value);
        if (ISO.DATE_TIME_COMPLETE_ZULU.equals(iso) && resultValue.contains("+")) {
            resultValue = resultValue.substring(0, resultValue.lastIndexOf("+")) + "Z";
        }
        return resultValue;
    }

    public static DateTimeFormatter getDateTimeFormatter(ISO iso) {
        return DateTimeFormatter.ofPattern(getIsoPattern(iso)).withZone(ZONE_OFFSET_UTC);
    }

    public static DateFormat getDateFormat(ISO iso) {
        return getDateFormat(iso, null);
    }

    public static DateFormat getDateFormat(String pattern) {
        return getDateFormat(null, pattern);
    }

    private static DateFormat getDateFormat(ISO iso, String pattern) {
        if (StringUtils.hasLength(pattern)) {
            return new SimpleDateFormat(pattern);
        }
        if (iso != null) {
            return new SimpleDateFormat(ISO_PATTERNS.get(iso));
        }
        return DateFormat.getDateInstance(DEFAULT_FORMATTER_STYLE, DEFAULT_FORMATTER_LOCALE);
    }

    /**
     * Common ISO date time format patterns.
     */
    public enum ISO {

        /**
         * The most common ISO Date Format {@code yyyy-MM-dd} &mdash; for example,
         * "2000-10-31".
         */
        DATE,

        /**
         * The most common ISO Time Format {@code HH:mm:ss.XXX} &mdash; for example,
         * "01:30:00-05:00".
         */
        TIME,

        /**
         * The most common ISO Time Format {@code HH:mm:ss.SSSXXX} &mdash; for example,
         * "01:30:00.000-05:00".
         */
        TIME_COMPLETE,

        /**
         * The most common ISO Time Format {@code HH:mm:ss} &mdash; for example,
         * "01:30:00".
         */
        TIME_ONLY,

        /**
         * The most common ISO Date Time Format {@code yyyy-MM-dd'T'HH:mm:ssXXX}
         * &mdash; for example, "2000-10-31T01:30:00+01:00".
         */
        DATE_TIME,

        /**
         * The most common ISO Date Time Format {@code yyyy-MM-dd'T'HH:mm:ss.SSSXXX}
         * &mdash; for example, "2000-10-31T01:30:00.000-05:00".
         */
        DATE_TIME_COMPLETE,

        /**
         * The most common ISO Date Time Format {@code yyyy-MM-dd'T'HH:mm:ss.SSSZ}
         * &mdash; for example, "2000-10-31T01:30:00.000Z".
         */
        DATE_TIME_COMPLETE_ZULU,

        /**
         * The most common ISO Date Time Format {@code yyyy-MM-dd'T'HH:mm:ss}
         * &mdash; for example, "2000-10-31T01:30:00".
         */
        DATE_TIME_ONLY,

        /**
         * Indicates that no ISO-based format pattern should be applied.
         */
        NONE
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DateTimeParser {

        private Integer day;
        private Integer month;
        private Integer year;
        private Integer hours;
        private Integer minutes;
        private Integer seconds;
        private Integer nanos;
        private String timeZoneSign;
        private Integer timeZoneHours;
        private Integer timeZoneMinutes;

        public static DateTimeParser create(String value) {
            DateTimeParser dateTimeParser = new DateTimeParser();
            dateTimeParser.parseValue(value);
            return dateTimeParser;
        }

        protected void assertLength(String value, int len, String fieldName) {
            if (value.length() != len) {
                throw new ParseException("Invalid input: len of " + fieldName + " must be " + len);
            }
        }

        private void parseValue(String value) {
            String datePart = null;
            String timePart = null;
            String tPosValue = "";
            int tPos = value.indexOf("T");
            if (tPos != -1) {
                tPosValue = "T";
            } else {
                tPos = value.indexOf(" ");
                if (tPos != -1) {
                    tPosValue = " ";
                }
            }

            if (tPos != -1) {
                datePart = value.substring(0, tPos);
                timePart = value.endsWith(tPosValue) ? null : value.substring(tPos + 1);
            } else if (value.contains(":")) {
                timePart = value;
            } else {
                datePart = value;
                if (datePart.length() > 10) {
                    timePart = "00:00:00" + datePart.substring(10);
                    datePart = datePart.substring(0, 10);
                }
            }
            parseDatePart(datePart);
            parseTimePart(timePart);
            resolveDefaults();
        }

        private void parseDatePart(String datePart) {
            if (!StringUtils.hasLength(datePart)) {
                return;
            }
            datePart = datePart.replace(".", "-");
            String[] dateValues = StringUtils.delimitedListToStringArray(datePart, "-");
            boolean yearInFront = dateValues.length == 1 || dateValues[0].length() == 4;
            if (dateValues.length > 2) {
                if (yearInFront) {
                    assertLength(dateValues[2], 2, "day");
                    setDay(Integer.parseInt(dateValues[2]));
                } else {
                    assertLength(dateValues[2], 4, "year");
                    setYear(Integer.parseInt(dateValues[2]));
                }
            }
            if (dateValues.length > 1) {
                assertLength(dateValues[1], 2, "month");
                setMonth(Integer.parseInt(dateValues[1]));
            }
            if (yearInFront) {
                assertLength(dateValues[0], 4, "year");
                setYear(Integer.parseInt(dateValues[0]));
            } else {
                assertLength(dateValues[0], 2, "day");
                setDay(Integer.parseInt(dateValues[0]));
            }
        }

        @SuppressWarnings("java:S3776")
        private void parseTimePart(String timePart) {
            if (!StringUtils.hasLength(timePart)) {
                return;
            }
            int timeZoneSignPos = timePart.indexOf("Z");
            if (timeZoneSignPos == -1) {
                timeZoneSignPos = timePart.indexOf("+");
                if (timeZoneSignPos == -1) {
                    timeZoneSignPos = timePart.indexOf("-");
                }
            }
            if (timeZoneSignPos != -1) {
                setTimeZoneSign(timePart.substring(timeZoneSignPos, timeZoneSignPos + 1));
                String timeZonePart = timePart.substring(timeZoneSignPos + 1);
                timePart = timePart.substring(0, timeZoneSignPos);
                if (timeZonePart.length() > 3 && !timeZonePart.contains(":")) {
                    timeZonePart = timeZonePart.substring(0, 2) + ":" + timeZonePart.substring(2);
                }
                if (timeZonePart.length() > 5) {
                    timeZonePart = timeZonePart.substring(0, 4);
                }
                String[] timeZoneValues = StringUtils.delimitedListToStringArray(timeZonePart, ":");
                if (timeZoneValues.length > 1) {
                    assertLength(timeZoneValues[1], 2, "timezone minutes");
                    setTimeZoneMinutes(Integer.parseInt(timeZoneValues[1]));
                }
                if (timeZoneValues.length > 0) {
                    assertLength(timeZoneValues[0], 2, "timezone hours");
                    setTimeZoneHours(Integer.parseInt(timeZoneValues[0]));
                }
            }

            String[] timeValues = StringUtils.delimitedListToStringArray(timePart, ":");
            if (timeValues.length > 2) {
                String secondsPart = timeValues[2];
                if (secondsPart.length() > 2) {
                    int dotPos = secondsPart.indexOf(".");
                    if (dotPos != -1) {
                        String nanosPart = secondsPart.substring(dotPos + 1);
                        setNanos(Integer.parseInt(nanosPart));
                        secondsPart = secondsPart.substring(0, dotPos);
                    }
                }
                assertLength(secondsPart, 2, "seconds");
                setSeconds(Integer.parseInt(secondsPart));
            }
            if (timeValues.length > 1) {
                assertLength(timeValues[1], 2, "minutes");
                setMinutes(Integer.parseInt(timeValues[1]));
            }
            assertLength(timeValues[0], 2, "hours");
            setHours(Integer.parseInt(timeValues[0]));
        }

        public void resolveDefaults() {
            year = resolveDefault(year, 1970);
            month = resolveDefault(month, 1);
            day = resolveDefault(day, 1);
            hours = resolveDefault(hours, 0);
            minutes = resolveDefault(minutes, 0);
            seconds = resolveDefault(seconds, 0);
            nanos = resolveDefault(nanos, 0);
            timeZoneHours = resolveDefault(timeZoneHours, 0);
            timeZoneMinutes = resolveDefault(timeZoneMinutes, 0);
            if (timeZoneSign == null) {
                timeZoneSign = "Z";
            }
        }

        private String getFormatted(Integer value, int length) {
            String text = String.valueOf(value);
            if (text.length() > length) {
                return text.substring(0, length);
            } else if (text.length() == length) {
                return text;
            } else {
                return new String(new char[length - text.length()]).replace('\0', '0') + text;
            }
        }

        private Integer resolveDefault(Integer value, Integer defaultValue) {
            return value == null ? defaultValue : value;
        }

        public String getAsString() {
            StringBuilder buf = new StringBuilder();
            buf.append(getFormatted(year, 4))
                    .append("-")
                    .append(getFormatted(month, 2))
                    .append("-")
                    .append(getFormatted(day, 2))
                    .append("T")
                    .append(getFormatted(hours, 2))
                    .append(":")
                    .append(getFormatted(minutes, 2))
                    .append(":")
                    .append(getFormatted(seconds, 2))
                    .append(".")
                    .append(getFormatted(nanos, 3))
                    .append(timeZoneSign);

            if (!"Z".equals(timeZoneSign)) {
                buf.append(getFormatted(timeZoneHours, 2)).append(":").append(getFormatted(timeZoneMinutes, 2));
            }

            return buf.toString();
        }

        public Date getAsDate() {
            SimpleDateFormat format = new SimpleDateFormat(COMPLETE_DATE_TIME_PATTERN);
            format.setTimeZone(TIMEZONE_UTC);
            return FunctionUtils.execute(() -> format.parse(getAsString()), ApimsRuntimeException.class);
        }

        public LocalDate getAsLocalDate() {
            return getAsOffsetDateTime()
                    .atZoneSameInstant(TIMEZONE_UTC.toZoneId())
                    .toLocalDate();
        }

        public LocalTime getAsLocalTime() {
            return getAsOffsetDateTime()
                    .atZoneSameInstant(TIMEZONE_UTC.toZoneId())
                    .toLocalTime();
        }

        public OffsetDateTime getAsOffsetDateTime() {
            final String value = getAsString();
            return FunctionUtils.execute(() -> OffsetDateTime.parse(value), ApimsRuntimeException.class);
        }
    }

    public static class ParseException extends ApimsRuntimeException {
        public ParseException(String message) {
            super(message);
        }
    }
}
