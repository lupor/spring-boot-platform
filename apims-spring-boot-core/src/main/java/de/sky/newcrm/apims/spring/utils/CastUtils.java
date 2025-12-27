/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

@SuppressWarnings({"java:S1610", "java:S3776", "java:S6201", "java:S6212"})
public abstract class CastUtils {

    protected CastUtils() {}

    @SuppressWarnings("unchecked")
    public static <T> T getValue(Class<T> preferredJavaType, Object value) throws ClassCastException {
        return (T) castTo(preferredJavaType, value);
    }

    public static Object castTo(Class<?> returnType, Object value) throws ClassCastException {
        if (returnType == null || value == null) {
            return value;
        }
        if (returnType.equals(value.getClass())) {
            return value;
        }

        try {
            if (String.class.equals(returnType)) {
                if (value instanceof Timestamp) {
                    return DateTimeUtc.format((Date) value, DateTimeUtc.ISO.DATE_TIME_COMPLETE);
                } else if (value instanceof Date) {
                    return DateTimeUtc.format((Date) value);
                } else if (value instanceof Calendar) {
                    return DateTimeUtc.format(((Calendar) value).getTimeInMillis());
                } else {
                    return String.valueOf(value);
                }
            } else if (Long.class.equals(returnType)) {
                if (value instanceof Number) {
                    return ((Number) value).longValue();
                } else if (value instanceof String) {
                    return Long.valueOf((String) value);
                } else if (value instanceof Boolean) {
                    return ((boolean) value) ? 1L : 0L;
                } else if (value instanceof Date) {
                    return ((Date) value).getTime();
                } else if (value instanceof Calendar) {
                    return ((Calendar) value).getTimeInMillis();
                } else if (value instanceof StringBuilder || value instanceof StringBuffer) {
                    return Long.valueOf(String.valueOf(value));
                }
            } else if (Double.class.equals(returnType)) {
                if (value instanceof Number) {
                    return ((Number) value).doubleValue();
                } else if (value instanceof String) {
                    return Double.valueOf((String) value);
                } else if (value instanceof Boolean) {
                    return ((boolean) value) ? 1D : 0D;
                } else if (value instanceof Date) {
                    return ((Date) value).getTime();
                } else if (value instanceof Calendar) {
                    return ((Calendar) value).getTimeInMillis();
                } else if (value instanceof StringBuilder || value instanceof StringBuffer) {
                    return Double.valueOf(String.valueOf(value));
                }
            } else if (Float.class.equals(returnType)) {
                if (value instanceof Number) {
                    return ((Number) value).floatValue();
                } else if (value instanceof String) {
                    return Float.valueOf((String) value);
                } else if (value instanceof Boolean) {
                    return ((boolean) value) ? 1F : 0F;
                } else if (value instanceof Date) {
                    return ((Date) value).getTime();
                } else if (value instanceof Calendar) {
                    return ((Calendar) value).getTimeInMillis();
                } else if (value instanceof StringBuilder || value instanceof StringBuffer) {
                    return Float.valueOf(String.valueOf(value));
                }
            } else if (Integer.class.equals(returnType)) {
                if (value instanceof String) {
                    return Integer.valueOf((String) value);
                } else if (value instanceof Boolean) {
                    return ((boolean) value) ? 1 : 0;
                } else if (value instanceof Number) {
                    return ((Number) value).intValue();
                } else if (value instanceof StringBuilder || value instanceof StringBuffer) {
                    return Integer.valueOf(String.valueOf(value));
                }
            } else if (Short.class.equals(returnType)) {
                if (value instanceof Number) {
                    return ((Number) value).shortValue();
                } else if (value instanceof String) {
                    return Short.valueOf((String) value);
                } else if (value instanceof Boolean) {
                    return ((boolean) value) ? 1 : 0;
                } else if (value instanceof StringBuilder || value instanceof StringBuffer) {
                    return Short.valueOf(String.valueOf(value));
                }
            } else if (Boolean.class.equals(returnType)) {
                if (value instanceof Number) {
                    return (((Number) value).longValue() != 0);
                } else if (value instanceof String) {
                    return Boolean.parseBoolean((String) value);
                } else if (value instanceof StringBuilder || value instanceof StringBuffer) {
                    return Boolean.valueOf(String.valueOf(value));
                }
            } else if (Date.class.equals(returnType)) {
                if (value instanceof Number) {
                    return new Date(((Number) value).longValue());
                } else if (value instanceof Date) {
                    return new Date((((Date) value).getTime()));
                } else if (value instanceof Calendar) {
                    return new Date(((Calendar) value).getTimeInMillis());
                } else if ("oracle.sql.DATE".equalsIgnoreCase(value.getClass().getName())
                        || "oracle.sql.TIMESTAMP"
                                .equalsIgnoreCase(value.getClass().getName())
                        || value instanceof String
                        || value instanceof StringBuilder
                        || value instanceof StringBuffer) {
                    return DateTimeUtc.parseDate(String.valueOf(value));
                }
            } else if (Calendar.class.equals(returnType)) {
                if (value instanceof Number) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(((Number) value).longValue());
                    return calendar;
                } else if (value instanceof Calendar) {
                    return value;
                } else if (value instanceof Date) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(((Date) value));
                    return calendar;
                } else if ("oracle.sql.DATE".equalsIgnoreCase(value.getClass().getName())
                        || "oracle.sql.TIMESTAMP"
                                .equalsIgnoreCase(value.getClass().getName())
                        || value instanceof String
                        || value instanceof StringBuilder
                        || value instanceof StringBuffer) {
                    Calendar calender = null;
                    Date date = DateTimeUtc.parseDate(String.valueOf(value));
                    if (date != null) {
                        calender = Calendar.getInstance();
                        calender.setTime(date);
                    }
                    return calender;
                }
            } else if (Object.class.equals(returnType)) {
                return value;
            }

        } catch (Exception e) {
            raiseClassCastException(returnType, value, e);
        }

        raiseClassCastException(returnType, value, null);
        return null;
    }

    private static void raiseClassCastException(Class<?> returnType, Object value, Exception additionalException)
            throws ClassCastException {
        String additionalMsg = additionalException == null
                ? ""
                : String.format(
                        " Cause: %s : %s",
                        additionalException.getClass().getSimpleName(),
                        ExceptionUtils.getExceptionMessage(additionalException));

        String returnTypeName = returnType.getSimpleName();
        throw new ClassCastException(String.format(
                "Cast from %s to %s not possible. Method : 'public static %s castTo%s(Object value)'.%s",
                value.getClass().getSimpleName(), returnTypeName, returnTypeName, returnTypeName, additionalMsg));
    }
}
