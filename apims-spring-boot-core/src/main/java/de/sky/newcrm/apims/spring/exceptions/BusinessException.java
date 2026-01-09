/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.exceptions;

import de.sky.newcrm.apims.spring.utils.AssertUtils;

@ApimsBusinessException(BusinessExceptionErrorCodes.BUSINESS_ERROR)
public class BusinessException extends ApimsBaseException {

    protected BusinessException() {}

    static BusinessException build() {
        return new BusinessException();
    }

    // @Deprecated
    public static BusinessException build(String errorCode) {
        AssertUtils.hasLengthCheck("errorCode", errorCode);
        BusinessException e = new BusinessException();
        e.setDetail(ApimsBaseException.DETAILS_KEY_ERROR_CODE, errorCode);
        return e;
    }
}
