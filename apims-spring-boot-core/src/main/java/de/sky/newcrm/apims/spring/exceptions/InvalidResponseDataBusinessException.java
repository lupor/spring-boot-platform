/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.exceptions;

import static de.sky.newcrm.apims.spring.exceptions.BusinessExceptionErrorCodes.INVALID_RESPONSE_DATA;

@ApimsBusinessException(value = INVALID_RESPONSE_DATA, logAsError = true)
public class InvalidResponseDataBusinessException extends ApimsBaseException {

    public static ApimsBeanValidator<InvalidResponseDataBusinessException> createValidator() {
        return new ApimsBeanValidator<>(new InvalidResponseDataBusinessException());
    }
}
