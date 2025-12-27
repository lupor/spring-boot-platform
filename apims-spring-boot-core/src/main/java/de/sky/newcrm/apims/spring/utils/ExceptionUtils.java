/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import static org.springframework.util.StringUtils.hasText;

import de.sky.newcrm.apims.spring.exceptions.ApimsUndeclaredThrowableException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.StringUtils;

@SuppressWarnings({"java:S1610", "java:S3776", "java:S6201", "java:S6212"})
public abstract class ExceptionUtils {

    static final int MAX_MESSAGES = 100;

    private ExceptionUtils() {}

    /**
     * Gets the exception as string.
     *
     * @param e the e
     * @return the exception as string
     */
    public static String getExceptionInfo(Throwable e) {
        if (e == null) {
            return null;
        }
        return "THROWABLE_INFO:\n" + getLastExceptionMessage(e) + "\n\nSTACKTRACE:\n" + getExceptionAsString(e);
    }

    /**
     * Gets the exception as string.
     *
     * @param e the e
     * @return the exception as string
     */
    public static String getExceptionAsString(Throwable e) {

        if (e == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.flush();
        pw.close();
        return sw.toString();
    }

    /**
     * Gets the exception message.
     *
     * @param e the e
     * @return the exception message
     */
    public static String getExceptionMessage(Throwable e) {

        List<String> msgList = new ArrayList<>();
        final String defaultMessage = e == null ? null : e.getClass().getName();
        int i = 0;
        while (e != null && i < MAX_MESSAGES) {
            String s = e.getMessage();
            if (hasText(s)) {
                msgList.add(s);
            }
            e = e.getCause();
            i++;
        }
        return msgList.isEmpty() ? defaultMessage : StringUtils.collectionToDelimitedString(msgList, "\n");
    }

    /**
     * Gets the first exception message.
     *
     * @param e the e
     * @return the exception message
     */
    public static String getFirstExceptionMessage(Throwable e) {

        final String defaultMessage = e == null ? null : e.getClass().getName();
        int i = 0;
        while (e != null && i < MAX_MESSAGES) {
            String s = e.getMessage();
            if (hasText(s)) {
                return s;
            }
            e = e.getCause();
            i++;
        }
        return defaultMessage;
    }

    /**
     * Gets the last exception message.
     *
     * @param e the e
     * @return the exception message
     */
    public static String getLastExceptionMessage(Throwable e) {

        String defaultMessage = e == null ? null : e.getClass().getName();
        String validMessage = null;
        int i = 0;
        while (e != null && i < MAX_MESSAGES) {
            String s = e.getMessage();
            defaultMessage = e.getClass().getName();
            if (hasText(s)) {
                validMessage = s;
            }
            e = e.getCause();
            i++;
        }
        return validMessage == null ? defaultMessage : validMessage;
    }

    public static Exception resolveUndeclaredThrowableException(Exception e) {
        Throwable resolvedException = resolveUndeclaredThrowable(e);
        return resolvedException instanceof Exception e1 ? e1 : e;
    }

    public static Throwable resolveUndeclaredThrowable(Throwable e) {
        Throwable resolvedException = e;
        while (resolvedException instanceof UndeclaredThrowableException) {
            resolvedException = resolvedException.getCause();
        }
        return resolvedException == null ? e : resolvedException;
    }

    public static RuntimeException resolveAsRuntimeException(Throwable e) {
        return resolveAsRuntimeException(e, null);
    }

    public static RuntimeException resolveAsRuntimeException(
            Throwable e, Class<? extends RuntimeException> targetRuntimeExceptionIfNeeded) {
        if (e instanceof ApimsUndeclaredThrowableException exception) {
            return exception;
        }
        Throwable resolvedException = ExceptionUtils.resolveUndeclaredThrowable(e);
        if (resolvedException instanceof RuntimeException exception) {
            return exception;
        } else if (targetRuntimeExceptionIfNeeded != null) {
            return ObjectUtils.createInstance(ObjectUtils.CreateInstanceDefinition.builder()
                    .clazz(targetRuntimeExceptionIfNeeded)
                    .constructorTypes(new Class[] {Throwable.class})
                    .constructorArgs(new Object[] {e})
                    .build());
        }
        return new ApimsUndeclaredThrowableException(resolvedException);
    }
}
