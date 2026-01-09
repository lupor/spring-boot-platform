/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.rest;

import de.sky.newcrm.apims.spring.exceptions.ApimsBeanValidator;
import de.sky.newcrm.apims.spring.web.core.exceptions.ApimsErrorAttributes;
import de.sky.newcrm.apims.spring.exceptions.InvalidRequestDataBusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

@RestController
public class ApimsRestController {

    protected final ApimsErrorAttributes apimsErrorAttributes;

    public ApimsRestController() {
        this.apimsErrorAttributes = new ApimsErrorAttributes();
    }

    protected Map<String, Object> resolveErrorAttributes(
            HttpServletRequest request,
            HttpStatus httpStatus,
            Exception e,
            ApimsErrorAttributes.CreateErrorAttributesCallback callback) {
        return apimsErrorAttributes.resolveErrorAttributes(request, httpStatus, e, callback);
    }

    protected void validateRequest(Object request) throws InvalidRequestDataBusinessException {
        ApimsBeanValidator<InvalidRequestDataBusinessException> validator = createValidator();
        validateRequest(validator, request);
        validator.throwIfContainsViolations();
    }

    protected void validateRequest(ApimsBeanValidator<InvalidRequestDataBusinessException> validator, Object request) {
        validator.assertNotNull("request", request).throwIfContainsViolations().validateAnnotations(request);
    }

    protected ApimsBeanValidator<InvalidRequestDataBusinessException> createValidator() {
        return InvalidRequestDataBusinessException.createValidator();
    }

    protected String getHttpServletRequestHeaderValue(String headerName) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }

        HttpServletRequest httpServletRequest = attributes.getRequest();

        return httpServletRequest.getHeader(headerName);
    }
}
