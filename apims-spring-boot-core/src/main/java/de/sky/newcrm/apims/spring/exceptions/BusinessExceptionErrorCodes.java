/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.exceptions;

import static de.sky.newcrm.apims.spring.exceptions.ApimsBaseException.DETAILS_KEY_ERROR_CODE;

import de.sky.newcrm.apims.spring.utils.AssertUtils;
import org.springframework.util.StringUtils;

@SuppressWarnings({"java:S1214", "java:S6201"})
public abstract class BusinessExceptionErrorCodes {

    public static final String BUSINESS_ERROR = "BUSINESS_ERROR";
    public static final String INVALID_REQ_DATA = "INVALID_REQ_DATA";
    public static final String INVALID_RESPONSE_DATA = "INVALID_RESPONSE_DATA";

    private BusinessExceptionErrorCodes() {}

    public static String calculateErrorCode(Throwable exception, boolean annotationMandatory) {
        ApimsDetailsAwareException dae = exception instanceof ApimsDetailsAwareException adae ? adae : null;
        String errorCode = dae == null || dae.getDetails() == null
                ? null
                : (String) dae.getDetails().get(DETAILS_KEY_ERROR_CODE);
        if (!StringUtils.hasLength(errorCode)) {
            errorCode = calculateErrorCode(exception == null ? null : exception.getClass(), annotationMandatory);
        }
        return errorCode;
    }

    public static String calculateErrorCode(Class<? extends Throwable> clazz, boolean annotationMandatory) {
        if (!annotationMandatory && clazz == null) {
            return null;
        }
        AssertUtils.notNullCheck("clazz", clazz);
        ApimsBusinessException annotation = clazz.getAnnotation(ApimsBusinessException.class);
        if (!annotationMandatory && annotation == null) {
            return null;
        }
        AssertUtils.notNullCheck("@ApimsBusinessException", annotation);
        String errorCode = annotation.value();
        if (!StringUtils.hasLength(errorCode)) {
            errorCode = clazz.getSimpleName();
        }
        return errorCode.toUpperCase();
    }
}
