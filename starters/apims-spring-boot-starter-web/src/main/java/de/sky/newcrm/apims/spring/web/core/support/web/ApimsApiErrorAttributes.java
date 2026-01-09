/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.support.web;

import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import de.sky.newcrm.apims.spring.web.core.exceptions.ApimsErrorAttributes;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.error.DefaultErrorAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

public class ApimsApiErrorAttributes extends DefaultErrorAttributes {

    private final ApimsErrorAttributes apimsErrorAttributes = new ApimsErrorAttributes();

    @Override
    @SuppressWarnings({"java:S1192", "java:S6201"})
    @ApimsReportGeneratedHint
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {

        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);
        Throwable t = getError(webRequest);
        apimsErrorAttributes.resolveErrorAttributes(t, errorAttributes);
        return errorAttributes;
    }
}
