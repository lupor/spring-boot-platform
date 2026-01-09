/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.support.web;

import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import de.sky.newcrm.apims.spring.exceptions.*;
import de.sky.newcrm.apims.spring.exceptions.ApimsBaseExceptionResolver;
import de.sky.newcrm.apims.spring.web.core.exceptions.ApimsErrorAttributes;
import de.sky.newcrm.apims.spring.exceptions.BusinessExceptionErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;
import java.util.TreeMap;

@RestControllerAdvice
@Slf4j
public class ApimsRestControllerExceptionHandler {

    private final ApimsErrorAttributes apimsErrorAttributes = new ApimsErrorAttributes();

    @Value("${apims.rest.report-not-handled-http-errors-as-status-code:-1}")
    private int reportNotHandledHttpErrorsAsStatusCode;

    @ExceptionHandler({
        ConstraintViolationException.class,
        HttpMessageNotReadableException.class,
        InvalidRequestDataBusinessException.class,
        MethodArgumentNotValidException.class,
        MethodArgumentTypeMismatchException.class,
        MissingRequestValueException.class
    })
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody Map<String, Object> handleInvalidRequestDataException(
            HttpServletRequest request, Exception e) {
        return resolveErrorAttributes(request, HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler({NoResourceFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public @ResponseBody Map<String, Object> handleNoResourceFoundException(HttpServletRequest request, Exception e) {
        return resolveErrorAttributes(request, HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(ApimsNotAuthorizedException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public @ResponseBody Map<String, Object> handleApimsNotAuthorizedException(
            HttpServletRequest request, Exception e) {
        return resolveErrorAttributes(request, HttpStatus.UNAUTHORIZED, e);
    }

    @ExceptionHandler(ApimsNotInRoleException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public @ResponseBody Map<String, Object> handleApimsNotInRoleException(HttpServletRequest request, Exception e) {
        return resolveErrorAttributes(request, HttpStatus.FORBIDDEN, e);
    }

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<Map<String, Object>> handleHttpStatusCodeException(
            HttpServletRequest request, HttpStatusCodeException e) {
        Map<String, Object> errorAttributes =
                new TreeMap<>(ApimsBaseExceptionResolver.parseBodyAsMap(e.getResponseBodyAsString()));
        HttpStatus httpStatus = reportNotHandledHttpErrorsAsStatusCode > 0
                ? HttpStatus.valueOf(reportNotHandledHttpErrorsAsStatusCode)
                : HttpStatus.valueOf(e.getStatusCode().value());
        boolean logTheError = !(e instanceof HttpClientErrorException.NotFound);
        apimsErrorAttributes.resolveErrorAttributes(request, httpStatus, logTheError, e, errorAttributes);
        return ResponseEntity.status(httpStatus).body(errorAttributes);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(HttpServletRequest request, Exception e) {
        String errorCode = BusinessExceptionErrorCodes.calculateErrorCode(e, false);
        HttpStatus httpStatus =
                StringUtils.hasLength(errorCode) ? HttpStatus.UNPROCESSABLE_ENTITY : HttpStatus.INTERNAL_SERVER_ERROR;
        ResponseStatus annotation = e.getClass().getAnnotation(ResponseStatus.class);
        if (annotation != null) {
            httpStatus = annotation.value();
        }
        return ResponseEntity.status(httpStatus).body(resolveErrorAttributes(request, httpStatus, e));
    }

    @ExceptionHandler(io.github.resilience4j.circuitbreaker.CallNotPermittedException.class)
    public ResponseEntity<Map<String, Object>> handleCallNotPermittedException(
            HttpServletRequest request, io.github.resilience4j.circuitbreaker.CallNotPermittedException e) {
        Map<String, Object> errorAttributes = resolveErrorAttributes(request, HttpStatus.SERVICE_UNAVAILABLE, e);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorAttributes);
    }

    @ApimsReportGeneratedHint
    public Map<String, Object> resolveErrorAttributes(HttpServletRequest request, HttpStatus httpStatus, Exception e) {
        Map<String, Object> errorAttributes = new TreeMap<>();
        apimsErrorAttributes.resolveErrorAttributes(request, httpStatus, true, e, errorAttributes);
        return errorAttributes;
    }
}
