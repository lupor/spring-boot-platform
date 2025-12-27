/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.exceptions;

import static de.sky.newcrm.apims.spring.exceptions.ApimsErrorAttributes.*;

import de.sky.newcrm.apims.spring.utils.ObjectUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.*;
import java.lang.annotation.Annotation;
import java.util.*;
import org.springframework.util.StringUtils;

@SuppressWarnings({"java:S1123", "java:S1133", "java:S6355"})
public class ApimsBeanValidator<T extends ApimsBaseException> {

    public static final String MESSAGE_CODE_ENUM = "Enum";
    public static final String MESSAGE_CODE_NOTENUM = "NotEnum";
    public static final String MESSAGE_CODE_NOTBLANK = NotBlank.class.getSimpleName();
    public static final String MESSAGE_CODE_NOTEMPTY = NotEmpty.class.getSimpleName();
    public static final String MESSAGE_CODE_NOTNULL = NotNull.class.getSimpleName();

    private final ApimsBeanValidatorAdapter validator;
    private final T exception;
    private final ArrayList<Map<String, String>> errors = new ArrayList<>();

    public ApimsBeanValidator(T exception) {
        this.exception = exception;
        validator = ApimsBeanValidatorFactory.getInstance().getValidator();
    }

    public ApimsBeanValidator<T> registerError(String propertyName, ConstraintViolation<?> violation) {
        return registerError(propertyName, String.valueOf(violation.getPropertyPath()), getMessageCode(violation));
    }

    public String getMessageCode(ConstraintViolation<?> violation) {
        String template = violation.getMessageTemplate();
        if (template.startsWith("{jakarta.validation.constraints.")) {
            return StringUtils.tokenizeToStringArray(template, ".", true, true)[3];
        }
        return violation.getMessage();
    }

    public ApimsBeanValidator<T> registerError(String rootPropertyName, String propertyName, String messageCode) {
        if (propertyName == null) {
            propertyName = "unknown";
        }
        String name = rootPropertyName == null ? "" : (rootPropertyName + ".");
        name += propertyName;
        registerError(name, messageCode);
        return this;
    }

    public ApimsBeanValidator<T> registerError(String propertyName, String messageCode) {
        Map<String, String> item = new LinkedHashMap<>();
        item.put(VALIDATION_ERROR_FIELD_KEY, propertyName == null ? "unknown" : propertyName);
        item.put(VALIDATION_ERROR_CODE_KEY, messageCode);
        errors.add(item);
        return this;
    }

    public List<Map<String, String>> getErrors() {
        sortErrors();
        return errors;
    }

    public boolean containsErrors() {
        return !errors.isEmpty();
    }

    @Deprecated
    public ApimsBeanValidator<T> validate(Object object, Class<?>... groups) {
        return validate(false, null, object, groups);
    }

    @Deprecated
    public ApimsBeanValidator<T> validate(String propertyName, Object object, Class<?>... groups) {
        return validate(false, propertyName, object, groups);
    }

    @Deprecated
    public ApimsBeanValidator<T> validate(
            boolean throwExceptionImmediately, String propertyName, Object object, Class<?>... groups) {
        return validateAnnotations(throwExceptionImmediately, propertyName, object, groups);
    }

    public ApimsBeanValidator<T> validateAnnotations(Object object, Class<?>... groups) {
        return validateAnnotations(false, null, object, groups);
    }

    public ApimsBeanValidator<T> validateAnnotations(String propertyName, Object object, Class<?>... groups) {
        return validateAnnotations(false, propertyName, object, groups);
    }

    public ApimsBeanValidator<T> validateAnnotations(
            boolean throwExceptionImmediately, String propertyName, Object object, Class<?>... groups) {
        assertNotNull(propertyName, object);
        if (object != null) {
            register(propertyName, validator.validate(object, groups));
        }
        if (throwExceptionImmediately) {
            throwIfContainsViolations();
        }
        return this;
    }

    public ApimsBeanValidator<T> register(String propertyName, ConstraintViolationException e) {
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            registerError(propertyName, violation);
        }
        return this;
    }

    public ApimsBeanValidator<T> register(String propertyName, Set<ConstraintViolation<Object>> violations) {
        if (violations != null && !violations.isEmpty()) {
            for (ConstraintViolation<?> violation : violations) {
                registerError(propertyName, violation);
            }
        }
        return this;
    }

    public ApimsBeanValidator<T> assertEnum(String propertyName, String value, String[] validValues) {
        return assertEnum(true, propertyName, value, validValues);
    }

    public ApimsBeanValidator<T> assertEnum(
            boolean condition, String propertyName, String value, String[] validValues) {
        boolean state = value != null;
        if (state) {
            state = false;
            for (String validValue : validValues) {
                if (value.equals(validValue)) {
                    state = true;
                    break;
                }
            }
        }
        return assertState(condition, state, propertyName, MESSAGE_CODE_ENUM);
    }

    public ApimsBeanValidator<T> assertNotEnum(String propertyName, String value, String[] validValues) {
        return assertNotEnum(true, propertyName, value, validValues);
    }

    public ApimsBeanValidator<T> assertNotEnum(
            boolean condition, String propertyName, String value, String[] validValues) {
        boolean state = value != null;
        if (state) {
            for (String validValue : validValues) {
                if (value.equals(validValue)) {
                    state = false;
                    break;
                }
            }
        }
        return assertState(condition, state, propertyName, MESSAGE_CODE_NOTENUM);
    }

    public ApimsBeanValidator<T> assertPattern(String propertyName, String value, String regEx) {
        return assertPattern(true, propertyName, value, regEx);
    }

    public ApimsBeanValidator<T> assertPattern(boolean condition, String propertyName, String value, String regEx) {
        return assertIsValid(condition, propertyName, value, Pattern.class, Map.of("regexp", regEx));
    }

    public ApimsBeanValidator<T> assertEmail(String propertyName, String value) {
        return assertEmail(true, propertyName, value);
    }

    public ApimsBeanValidator<T> assertEmail(boolean condition, String propertyName, String value) {
        return assertIsValid(condition, propertyName, value, Email.class);
    }

    public ApimsBeanValidator<T> assertMax(String propertyName, Object value, Long maxValue) {
        return assertMax(true, propertyName, value, maxValue);
    }

    public ApimsBeanValidator<T> assertMax(boolean condition, String propertyName, Object value, Long maxValue) {
        return assertIsValid(condition, propertyName, value, Max.class, Map.of("value", maxValue));
    }

    public ApimsBeanValidator<T> assertDecimalMax(String propertyName, Object value, String maxBigDecimalValue) {
        return assertDecimalMax(true, propertyName, value, maxBigDecimalValue);
    }

    public ApimsBeanValidator<T> assertDecimalMax(
            boolean condition, String propertyName, Object value, String maxBigDecimalValue) {
        return assertIsValid(condition, propertyName, value, DecimalMax.class, Map.of("value", maxBigDecimalValue));
    }

    public ApimsBeanValidator<T> assertPropertyNotNull(Object rootObject, String propertyPath) {
        Object value = ObjectUtils.getPropertyValue(rootObject, propertyPath);
        return assertNotNull(true, propertyPath, value);
    }

    public ApimsBeanValidator<T> assertNotNull(String propertyName, Object value) {
        return assertNotNull(true, propertyName, value);
    }

    public ApimsBeanValidator<T> assertNotNull(boolean condition, String propertyName, Object value) {
        return assertState(condition, value != null, propertyName, MESSAGE_CODE_NOTNULL);
    }

    public ApimsBeanValidator<T> assertPropertyNotEmpty(Object rootObject, String propertyPath) {
        String value = ObjectUtils.getPropertyValue(rootObject, propertyPath);
        return assertNotEmpty(true, propertyPath, value);
    }

    public ApimsBeanValidator<T> assertNotEmpty(String propertyName, String value) {
        return assertNotEmpty(true, propertyName, value);
    }

    public ApimsBeanValidator<T> assertNotEmpty(boolean condition, String propertyName, String value) {
        return assertState(condition, StringUtils.hasLength(value), propertyName, MESSAGE_CODE_NOTEMPTY);
    }

    public ApimsBeanValidator<T> assertPropertyNotBlank(Object rootObject, String propertyPath) {
        String value = ObjectUtils.getPropertyValue(rootObject, propertyPath);
        return assertNotBlank(true, propertyPath, value);
    }

    public ApimsBeanValidator<T> assertNotBlank(String propertyName, String value) {
        return assertNotBlank(true, propertyName, value);
    }

    public ApimsBeanValidator<T> assertNotBlank(boolean condition, String propertyName, String value) {
        return assertState(condition, StringUtils.hasText(value), propertyName, MESSAGE_CODE_NOTBLANK);
    }

    public ApimsBeanValidator<T> assertState(boolean state, String propertyName, String messageCode) {
        return assertState(true, state, propertyName, messageCode);
    }

    public ApimsBeanValidator<T> assertState(
            boolean condition, boolean state, String propertyName, String messageCode) {
        if (condition && !state) {
            registerError(propertyName, messageCode);
        }
        return this;
    }

    public <A extends Annotation> ApimsBeanValidator<T> assertIsValid(
            String propertyName, Object value, Class<A> annotationType) {
        return assertIsValid(propertyName, value, annotationType, Collections.emptyMap());
    }

    public <A extends Annotation> ApimsBeanValidator<T> assertIsValid(
            String propertyName, Object value, Class<A> annotationType, Map<String, Object> annotationValues) {
        return assertIsValid(true, propertyName, value, annotationType, annotationValues);
    }

    public <A extends Annotation> ApimsBeanValidator<T> assertIsValid(
            boolean condition, String propertyName, Object value, Class<A> annotationType) {
        return assertIsValid(condition, propertyName, value, annotationType, Collections.emptyMap());
    }

    public <A extends Annotation> ApimsBeanValidator<T> assertIsValid(
            boolean condition,
            String propertyName,
            Object value,
            Class<A> annotationType,
            Map<String, Object> annotationValues) {
        if (condition) {
            boolean isValid = validator.isValid(value, annotationType, annotationValues);
            if (!isValid) {
                registerError(propertyName, annotationType.getSimpleName());
            }
        }
        return this;
    }

    public ApimsBeanValidator<T> throwIfContainsViolations() throws T {
        if (containsErrors()) {
            sortErrors();
            exception.getDetails().put(BUSINESS_EXCEPTION_ERRORS_KEY, errors);
            throw exception;
        }
        return this;
    }

    protected void sortErrors() {
        errors.sort(Comparator.comparing(o -> String.valueOf(o.get(VALIDATION_ERROR_FIELD_KEY))));
    }
}
