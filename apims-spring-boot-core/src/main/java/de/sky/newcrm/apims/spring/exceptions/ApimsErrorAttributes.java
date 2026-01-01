/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.exceptions;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3.ObjectMapperUtils;
import de.sky.newcrm.apims.spring.utils.ExceptionUtils;
import de.sky.newcrm.apims.spring.utils.VeracodeMitigationUtils;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.io.Serializable;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@SuppressWarnings({"java:S1141", "java:S3776", "java:S6201", "java:S6212"})
public class ApimsErrorAttributes {

    private static final String[] SILENT_HEADER_NAME_PARTS = new String[] {"akamai"};
    private static final String EXCEPTION_CLASS_KEY = "exception";
    private static final String EXCEPTION_MESSAGE_KEY = "message";
    private static final String EXCEPTION_GENERIC_MESSAGE_VALUE = "GENERIC_ERROR";
    private static final String EXCEPTION_PATH_KEY = "path";
    private static final String EXCEPTION_TIMESTAMP_KEY = "timestamp";
    private static final String EXCEPTION_STATUS_KEY = "status";
    private static final String EXCEPTION_ERROR_KEY = "error";
    protected static final String BUSINESS_EXCEPTION_MESSAGE_KEY = "message";
    protected static final String BUSINESS_EXCEPTION_DETAILS_KEY = "details";
    private static final String BUSINESS_EXCEPTION_CLASS_KEY = "apimsBusinessExceptionClass";
    private static final String BUSINESS_EXCEPTION_HIERARCHY_KEY = "apimsBusinessExceptionHierarchy";
    private static final String BUSINESS_EXCEPTION_IDENTIFIES_KEY = "isApimsBusinessException";
    protected static final String BUSINESS_EXCEPTION_ERROR_CODE_KEY = "code";
    public static final String BUSINESS_EXCEPTION_ERRORS_KEY = "errors";
    public static final String VALIDATION_ERROR_FIELD_KEY = "field";
    public static final String VALIDATION_ERROR_CODE_KEY = "requires";
    public static final String VALIDATION_ERROR_CODE_VALUE_NOT_NULL = "NotNull";

    public boolean resolveErrorAttributes(Throwable exception, Map<String, Object> errorAttributes) {
        return resolveErrorAttributes(null, null, true, exception, errorAttributes);
    }

    public boolean resolveErrorAttributes(
            HttpServletRequest request,
            HttpStatus httpStatus,
            Throwable exception,
            Map<String, Object> errorAttributes) {
        return resolveErrorAttributes(request, httpStatus, true, exception, errorAttributes);
    }

    public Map<String, Object> resolveErrorAttributes(
            HttpServletRequest request, HttpStatus httpStatus, Exception e, CreateErrorAttributesCallback consumer) {
        boolean logTheError = consumer.isLogError();
        Map<String, Object> errorAttributes = new TreeMap<>();
        resolveErrorAttributes(request, httpStatus, logTheError, e, errorAttributes);
        errorAttributes = consumer.create(e, errorAttributes);
        if (logTheError && log.isWarnEnabled()) {
            log.warn(
                    "[WEB] ERROR RESPONSE : PATCHED -> {}",
                    VeracodeMitigationUtils.sanitizeLogValue(ObjectMapperUtils.writeValueAsString(errorAttributes)));
        }
        return errorAttributes;
    }

    @SuppressWarnings({"java:S108", "java:S1871"})
    public boolean resolveErrorAttributes(
            HttpServletRequest request,
            HttpStatus httpStatus,
            boolean logTheError,
            Throwable exception,
            Map<String, Object> errorAttributes) {

        exception = ExceptionUtils.resolveUndeclaredThrowable(exception);
        boolean logStacktrace = false;
        if (exception instanceof InvalidResponseDataBusinessException) {
            processCommonErrorAttributes(request, httpStatus, exception, errorAttributes);
            logStacktrace = true;
        } else if (exception instanceof ApimsBaseException) {
            processCommonErrorAttributes(request, httpStatus, exception, errorAttributes);
        } else if (exception instanceof ApimsNotAuthorizedException) {
            processCommonErrorAttributes(request, httpStatus, exception, errorAttributes);
        } else if (exception instanceof HttpStatusCodeException) {
            processCommonErrorAttributes(request, httpStatus, exception, errorAttributes);
            logStacktrace = true;
        } else if (exception instanceof MethodArgumentNotValidException validationException) {
            processCommonErrorAttributes(
                    request, httpStatus, new InvalidRequestDataBusinessException(), errorAttributes);
            saveErrorDetails(errorAttributes, createFieldValidationErrors(validationException));
        } else if (exception instanceof MethodArgumentTypeMismatchException validationException) {
            processCommonErrorAttributes(
                    request, httpStatus, new InvalidRequestDataBusinessException(), errorAttributes);
            saveErrorDetails(errorAttributes, createFieldValidationErrors(validationException));
        } else if (exception instanceof ConstraintViolationException validationException) {
            processCommonErrorAttributes(
                    request, httpStatus, new InvalidRequestDataBusinessException(), errorAttributes);
            saveErrorDetails(errorAttributes, createFieldValidationErrors(validationException));
        } else if (exception instanceof MissingMatrixVariableException validationException) {
            processCommonErrorAttributes(
                    request, httpStatus, new InvalidRequestDataBusinessException(), errorAttributes);
            saveErrorDetails(
                    errorAttributes,
                    createSingleFieldValidationErrors(
                            "matrix-variable",
                            validationException.getVariableName(),
                            VALIDATION_ERROR_CODE_VALUE_NOT_NULL));
        } else if (exception instanceof MissingPathVariableException validationException) {
            processCommonErrorAttributes(
                    request, httpStatus, new InvalidRequestDataBusinessException(), errorAttributes);
            saveErrorDetails(
                    errorAttributes,
                    createSingleFieldValidationErrors(
                            "path-variable",
                            validationException.getVariableName(),
                            VALIDATION_ERROR_CODE_VALUE_NOT_NULL));
        } else if (exception instanceof MissingRequestCookieException validationException) {
            processCommonErrorAttributes(
                    request, httpStatus, new InvalidRequestDataBusinessException(), errorAttributes);
            saveErrorDetails(
                    errorAttributes,
                    createSingleFieldValidationErrors(
                            "request-cookie",
                            validationException.getCookieName(),
                            VALIDATION_ERROR_CODE_VALUE_NOT_NULL));
        } else if (exception instanceof MissingRequestHeaderException validationException) {
            processCommonErrorAttributes(
                    request, httpStatus, new InvalidRequestDataBusinessException(), errorAttributes);
            saveErrorDetails(
                    errorAttributes,
                    createSingleFieldValidationErrors(
                            "request-header",
                            validationException.getHeaderName(),
                            VALIDATION_ERROR_CODE_VALUE_NOT_NULL));
        } else if (exception instanceof MissingServletRequestParameterException validationException) {
            processCommonErrorAttributes(
                    request, httpStatus, new InvalidRequestDataBusinessException(), errorAttributes);
            saveErrorDetails(
                    errorAttributes,
                    createSingleFieldValidationErrors(
                            "request-parameter",
                            validationException.getParameterName(),
                            VALIDATION_ERROR_CODE_VALUE_NOT_NULL));
        } else if (exception instanceof HttpMessageNotReadableException validationException) {
            processCommonErrorAttributes(
                    request, httpStatus, new InvalidRequestDataBusinessException(), errorAttributes);
            saveErrorDetails(errorAttributes, createSingleFieldValidationErrors(validationException));
            logStacktrace = true;
        } else if (exception instanceof CallNotPermittedException) {
            processCommonErrorAttributes(request, HttpStatus.SERVICE_UNAVAILABLE, exception, errorAttributes);
        } else {
            logStacktrace = true;
            processCommonErrorAttributes(request, httpStatus, exception, errorAttributes);
        }

        if (logTheError) {
            String code = (String) errorAttributes.get(BUSINESS_EXCEPTION_ERROR_CODE_KEY);
            if (!StringUtils.hasLength(code)) {
                code = EXCEPTION_GENERIC_MESSAGE_VALUE;
            }
            if (logStacktrace && exception != null) {
                log.error(
                        "[WEB] ERROR RESPONSE : %s -> %s"
                                .formatted(
                                        code,
                                        VeracodeMitigationUtils.sanitizeLogValue(
                                                ObjectMapperUtils.writeValueAsString(errorAttributes))),
                        exception);
            } else {
                log.warn(
                        "[WEB] ERROR RESPONSE : {} -> {}",
                        code,
                        VeracodeMitigationUtils.sanitizeLogValue(
                                ObjectMapperUtils.writeValueAsString(errorAttributes)));
            }
        }
        return true;
    }

    @ApimsReportGeneratedHint
    protected void saveErrorDetails(Map<String, Object> errorAttributes, List<Map<String, String>> errors) {
        if (errors != null) {
            errors.sort(Comparator.comparing(o -> String.valueOf(o.get(VALIDATION_ERROR_FIELD_KEY))));
            Map<String, List<Map<String, String>>> errorDetails = new LinkedHashMap<>();
            errorDetails.put(BUSINESS_EXCEPTION_ERRORS_KEY, errors);
            errorAttributes.put(BUSINESS_EXCEPTION_DETAILS_KEY, errorDetails);
        }
    }

    @ApimsReportGeneratedHint
    @SuppressWarnings("java:S6541")
    protected void processCommonErrorAttributes(
            HttpServletRequest request,
            HttpStatus httpStatus,
            Throwable exception,
            Map<String, Object> errorAttributes) {
        ApimsBaseException bex = exception instanceof ApimsBaseException abe ? abe : null;
        String errorCode = bex == null ? null : bex.getErrorCode();
        ApimsDetailsAwareException dae = exception instanceof ApimsDetailsAwareException adae ? adae : null;
        if (!StringUtils.hasLength(errorCode)) {
            errorCode = BusinessExceptionErrorCodes.calculateErrorCode(exception, false);
        }
        boolean silentRequest = isSilentRequest(request);
        if (silentRequest) {
            errorAttributes.remove(EXCEPTION_PATH_KEY);
        } else if (request != null) {
            errorAttributes.put(EXCEPTION_PATH_KEY, request.getRequestURI());
        }
        errorAttributes.put(EXCEPTION_TIMESTAMP_KEY, new Date());
        errorAttributes.remove(EXCEPTION_STATUS_KEY);
        errorAttributes.remove(EXCEPTION_ERROR_KEY);
        if (httpStatus != null && !silentRequest && log.isTraceEnabled()) {
            errorAttributes.put(EXCEPTION_STATUS_KEY, httpStatus.value());
            errorAttributes.put(EXCEPTION_ERROR_KEY, httpStatus.getReasonPhrase());
        }
        if (StringUtils.hasLength(errorCode)) {
            errorAttributes.put(BUSINESS_EXCEPTION_ERROR_CODE_KEY, errorCode);
            if (!silentRequest && log.isTraceEnabled()) {
                errorAttributes.put(
                        BUSINESS_EXCEPTION_HIERARCHY_KEY, createBusinessExceptionHierarchy(exception.getClass()));
                errorAttributes.put(BUSINESS_EXCEPTION_IDENTIFIES_KEY, String.valueOf(true));
                errorAttributes.put(
                        BUSINESS_EXCEPTION_CLASS_KEY, exception.getClass().getName());
            } else {
                errorAttributes.remove(BUSINESS_EXCEPTION_MESSAGE_KEY);
                errorAttributes.remove(BUSINESS_EXCEPTION_HIERARCHY_KEY);
                errorAttributes.remove(BUSINESS_EXCEPTION_IDENTIFIES_KEY);
                errorAttributes.remove(BUSINESS_EXCEPTION_CLASS_KEY);
            }
            if (dae != null) {
                Map<String, Serializable> details = dae.getDetails();
                if (details != null && !details.isEmpty()) {
                    if (bex != null) {
                        details.remove(
                                BUSINESS_EXCEPTION_ERROR_CODE_KEY); // the overwritten one in case of business-exception
                    }
                    errorAttributes.put(BUSINESS_EXCEPTION_DETAILS_KEY, details);
                }
            }
        } else {
            if (silentRequest) {
                errorAttributes.put(EXCEPTION_MESSAGE_KEY, EXCEPTION_GENERIC_MESSAGE_VALUE);
            } else if (exception != null) {
                String msg;
                if (exception instanceof HttpStatusCodeException codeException) {
                    msg = codeException.getResponseBodyAsString();
                    if (errorAttributes.containsKey(EXCEPTION_MESSAGE_KEY)) {
                        msg = msg + " -> " + errorAttributes.get(EXCEPTION_MESSAGE_KEY);
                    }
                } else {
                    msg = exception.getMessage();
                }
                if (StringUtils.hasLength(msg) && msg.contains("\n")) {
                    msg = msg.replace("\n", "");
                }
                if (log.isTraceEnabled()) {
                    errorAttributes.put(
                            EXCEPTION_CLASS_KEY, exception.getClass().getName());
                    errorAttributes.put(EXCEPTION_MESSAGE_KEY, msg);
                } else {
                    errorAttributes.put(EXCEPTION_MESSAGE_KEY, msg);
                }
            }
        }
    }

    protected boolean isSilentRequest(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        Enumeration<String> enumeration = request.getHeaderNames();
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                String headerName = enumeration.nextElement();
                for (String part : SILENT_HEADER_NAME_PARTS) {
                    if (headerName.toLowerCase().contains(part.toLowerCase())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @SuppressWarnings("java:S1872")
    protected List<String> createBusinessExceptionHierarchy(Class<?> clazz) {
        List<String> list = new ArrayList<>();
        Class<?> searchType = clazz;
        while (Object.class != searchType && searchType != null) {
            list.add(searchType.getName());
            if (searchType.getName().equals(BusinessException.class.getName())) {
                break;
            }
            searchType = searchType.getSuperclass();
        }
        return list;
    }

    protected List<Map<String, String>> createFieldValidationErrors(MethodArgumentNotValidException m) {
        List<Map<String, String>> errors = new ArrayList<>();

        m.getBindingResult().getFieldErrors().forEach(error -> {
            Map<String, String> item = new LinkedHashMap<>();
            String field = error.getField();
            String message = error.getCode();
            item.put(VALIDATION_ERROR_FIELD_KEY, field);
            item.put(VALIDATION_ERROR_CODE_KEY, message);
            errors.add(item);
        });
        return errors;
    }

    protected List<Map<String, String>> createFieldValidationErrors(MethodArgumentTypeMismatchException m) {
        List<Map<String, String>> errors = new ArrayList<>();
        Map<String, String> item = new LinkedHashMap<>();
        String message = ExceptionUtils.getLastExceptionMessage(m);
        item.put(VALIDATION_ERROR_FIELD_KEY, message);
        item.put(VALIDATION_ERROR_CODE_KEY, "ValidType");
        errors.add(item);
        return errors;
    }

    protected List<Map<String, String>> createFieldValidationErrors(ConstraintViolationException m) {
        List<Map<String, String>> errors = new ArrayList<>();
        m.getConstraintViolations().forEach(error -> {
            Map<String, String> item = new LinkedHashMap<>();
            String field = String.valueOf(error.getPropertyPath());
            String message = error.getMessage();
            String template = error.getMessageTemplate();
            if (template.startsWith("jakarta.validation.constraints.")) {
                message = StringUtils.tokenizeToStringArray(template, ".", true, true)[3];
            }
            item.put(VALIDATION_ERROR_FIELD_KEY, field);
            item.put(VALIDATION_ERROR_CODE_KEY, message);
            errors.add(item);
        });
        return errors;
    }

    @SuppressWarnings("java:S135")
    protected List<Map<String, String>> createSingleFieldValidationErrors(HttpMessageNotReadableException exception) {
        String fieldName = "requestBody";
        String fieldValue = "body";
        String codeValue = ExceptionUtils.getLastExceptionMessage(exception);
        Throwable e = exception;
        while (e != null) {
            if (e instanceof JsonParseException) {
                String msg = e.getMessage();
                if (msg.startsWith("Illegal unquoted character")) {
                    fieldValue = "\"";
                    codeValue = "QuotedCharacter";
                } else if (msg.startsWith("Unexpected character")) {
                    String[] items = StringUtils.tokenizeToStringArray(msg, "'");
                    if (items.length == 2) {
                        fieldValue = "'";
                        codeValue = "ExpectedCharacter";
                    } else if (items.length > 2) {
                        fieldValue = items[1];
                        codeValue = "ExpectedCharacter";
                    }
                }
                break;
            } else if (e instanceof ValueInstantiationException) {
                String msg = e.getMessage();
                if (msg.startsWith("Cannot construct instance of") && msg.contains("problem: Unexpected value")) {
                    String[] items = StringUtils.tokenizeToStringArray(msg, "`$'");
                    if (items.length > 4) {
                        fieldName = items[2];
                        if (!fieldName.equals("Enum") && fieldName.endsWith("Enum")) {
                            fieldName = fieldName.substring(0, fieldName.length() - 4);
                        }
                        fieldName = fieldName.length() > 1
                                ? fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1)
                                : fieldName.toLowerCase();
                        fieldValue = items[4];
                        codeValue = "Enum";
                    }
                }
                break;
            }
            e = e.getCause();
        }
        return createSingleFieldValidationErrors(fieldName, fieldValue, VALIDATION_ERROR_CODE_KEY, codeValue);
    }

    @ApimsReportGeneratedHint
    protected List<Map<String, String>> createSingleFieldValidationErrors(
            String fieldName, String fieldValue, String codeValue) {
        return createSingleFieldValidationErrors(fieldName, fieldValue, VALIDATION_ERROR_CODE_KEY, codeValue);
    }

    @ApimsReportGeneratedHint
    protected List<Map<String, String>> createSingleFieldValidationErrors(
            String fieldName, String fieldValue, String codeName, String codeValue) {
        List<Map<String, String>> errors = new ArrayList<>();
        Map<String, String> item = new LinkedHashMap<>();
        item.put(fieldName, fieldValue);
        item.put(codeName, codeValue);
        errors.add(item);
        return errors;
    }

    public interface CreateErrorAttributesCallback {
        default boolean isLogError() {
            return true;
        }

        Map<String, Object> create(Exception e, Map<String, Object> errorAttributes);
    }
}
