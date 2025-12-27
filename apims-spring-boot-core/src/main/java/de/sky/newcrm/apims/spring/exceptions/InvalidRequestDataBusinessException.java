/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.exceptions;

import static de.sky.newcrm.apims.spring.exceptions.BusinessExceptionErrorCodes.INVALID_REQ_DATA;

@ApimsBusinessException(INVALID_REQ_DATA)
public class InvalidRequestDataBusinessException extends ApimsBaseException {

    public static ApimsBeanValidator<InvalidRequestDataBusinessException> createValidator() {
        return new ApimsBeanValidator<>(new InvalidRequestDataBusinessException());
    }
}
