/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.exceptions;

import de.sky.newcrm.apims.spring.environment.core.ApimsApplicationReadyListener;
import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings({"java:S6212"})
public class ApimsBaseExceptionPreloader implements ApimsApplicationReadyListener {

    @Override
    @ApimsReportGeneratedHint
    public void onApplicationReadyEvent() throws Exception {
        Exception bex = ApimsBaseExceptionResolver.getException(de.sky.newcrm.apims.spring.exceptions.BusinessExceptionErrorCodes.BUSINESS_ERROR);
        if (!(bex instanceof de.sky.newcrm.apims.spring.exceptions.BusinessException)) {
            throw new ApimsBaseExceptionResolver.ApimsBaseExceptionResolverException(
                    "Class with code '" + de.sky.newcrm.apims.spring.exceptions.BusinessExceptionErrorCodes.BUSINESS_ERROR + "' must be instance of "
                            + de.sky.newcrm.apims.spring.exceptions.BusinessException.class.getName());
        }
    }
}
