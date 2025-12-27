/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.exceptions;

import de.sky.newcrm.apims.spring.utils.ExceptionUtils;
import de.sky.newcrm.apims.spring.utils.VeracodeMitigationUtils;
import org.slf4j.Logger;

public class ApimsRuntimeException extends RuntimeException {

    public ApimsRuntimeException() {}

    public ApimsRuntimeException(String message) {
        super(message);
    }

    public ApimsRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApimsRuntimeException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    public ApimsRuntimeException(
            String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @SuppressWarnings("unchecked")
    public <T extends ApimsRuntimeException> T logError(Logger log) {
        log.error(
                "{}; details: \\n\\n{}",
                VeracodeMitigationUtils.sanitizeLogValues(getMessage(), ExceptionUtils.getExceptionInfo(getCause())));
        return (T) this;
    }
}
