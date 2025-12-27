/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.exceptions;

import static de.sky.newcrm.apims.spring.exceptions.BusinessExceptionErrorCodes.BUSINESS_ERROR;

import de.sky.newcrm.apims.spring.utils.AssertUtils;

@ApimsBusinessException(BUSINESS_ERROR)
public class BusinessException extends ApimsBaseException {

    protected BusinessException() {}

    static BusinessException build() {
        return new BusinessException();
    }

    // @Deprecated
    public static BusinessException build(String errorCode) {
        AssertUtils.hasLengthCheck("errorCode", errorCode);
        BusinessException e = new BusinessException();
        e.setDetail(DETAILS_KEY_ERROR_CODE, errorCode);
        return e;
    }
}
