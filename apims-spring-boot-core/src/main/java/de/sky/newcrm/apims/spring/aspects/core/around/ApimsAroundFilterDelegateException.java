/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.aspects.core.around;

import de.sky.newcrm.apims.spring.exceptions.ApimsRuntimeException;

public final class ApimsAroundFilterDelegateException extends ApimsRuntimeException {

    public ApimsAroundFilterDelegateException(RuntimeException cause) {
        super(cause);
    }

    @Override
    public synchronized RuntimeException getCause() {
        return (RuntimeException) super.getCause();
    }
}
