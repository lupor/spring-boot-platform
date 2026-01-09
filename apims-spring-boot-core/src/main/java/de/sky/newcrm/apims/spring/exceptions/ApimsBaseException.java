/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.exceptions;

import de.sky.newcrm.apims.spring.utils.ExceptionUtils;
import de.sky.newcrm.apims.spring.utils.VeracodeMitigationUtils;
import org.slf4j.Logger;
import org.springframework.core.NestedRuntimeException;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public abstract class ApimsBaseException extends NestedRuntimeException implements ApimsDetailsAwareException {

    public static final String DETAILS_KEY_ERROR_CODE = "code";
    public static final String DETAILS_KEY_MESSAGE = "message";
    public static final String DETAILS_KEY_EXCEPTION = "exception";

    private final Map<String, Serializable> details = new TreeMap<>();

    protected ApimsBaseException() {
        super("");
    }

    public String getErrorCode() {
        return BusinessExceptionErrorCodes.calculateErrorCode(this, true);
    }

    @Override
    public Map<String, Serializable> getDetails() {
        return details;
    }

    public String getDetailMessage() {
        return getDetailAsString(DETAILS_KEY_MESSAGE);
    }

    @SuppressWarnings("unchecked")
    public <T extends ApimsBaseException> T setDetailMessage(String value) {
        setDetail(DETAILS_KEY_MESSAGE, value);
        return (T) this;
    }

    public String getDetailAsString(String key) {
        Serializable s = getDetail(key);
        return s == null ? null : String.valueOf(s);
    }

    @SuppressWarnings("unchecked")
    public <T extends Serializable> T getDetail(String key) {
        return (T) getDetails().get(key);
    }

    @SuppressWarnings("unchecked")
    public <T extends ApimsBaseException> T setDetail(String key, Serializable value) {
        getDetails().put(key, value);
        return (T) this;
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final <T extends ApimsBaseException> T setDetails(Map.Entry<String, Serializable>... entries) {
        for (Map.Entry<String, Serializable> entry : entries) {
            setDetail(entry.getKey(), entry.getValue());
        }
        return (T) this;
    }

    @Override
    @SuppressWarnings({"java:S6201"})
    public String getMessage() {

        StringBuilder buf =
                new StringBuilder(100).append("\"").append(getErrorCode()).append("\"");
        if (!getDetails().isEmpty())
            buf.append(" : ")
                    .append(getDetails().keySet().stream()
                            .map(key -> {
                                Object object = getDetails().get(key);
                                String value;
                                if (object instanceof Exception exception) {
                                    value = ExceptionUtils.getLastExceptionMessage(exception);
                                } else {
                                    value = String.valueOf(object);
                                }
                                return key + "=" + VeracodeMitigationUtils.sanitizeLogValue(value);
                            })
                            .collect(Collectors.joining(", ", "{", "}")));
        return buf.toString();
    }

    @Override
    public String toString() {
        return getMessage();
    }

    @SuppressWarnings("unchecked")
    public <T extends ApimsBaseException> T logError(Logger log) {
        log.error(getMessage());
        return (T) this;
    }
}
