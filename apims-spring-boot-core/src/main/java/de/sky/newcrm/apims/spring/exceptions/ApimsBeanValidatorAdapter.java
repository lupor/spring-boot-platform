/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.exceptions;

import de.sky.newcrm.apims.spring.utils.AnnotationUtils;
import de.sky.newcrm.apims.spring.utils.AssertUtils;
import de.sky.newcrm.apims.spring.utils.ObjectUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.Validator;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorDescriptor;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

@SuppressWarnings({"java:S6212"})
public class ApimsBeanValidatorAdapter extends SpringValidatorAdapter {

    private final ConstraintHelper constraintHelper;

    public ApimsBeanValidatorAdapter(Validator targetValidator) {
        super(targetValidator);
        constraintHelper = ConstraintHelper.forAllBuiltinConstraints();
    }

    public <T, A extends Annotation> boolean isValid(
            T value, Class<A> annotationType, Map<String, Object> annotationValues) {
        return validate(value, annotationType, annotationValues);
    }

    @SuppressWarnings({"java:S3252", "java:S4449", "java:S5669"})
    @SneakyThrows
    protected <T, A extends Annotation> boolean validate(
            T value, Class<A> annotationType, Map<String, Object> annotationValues) {
        ConstraintValidatorDescriptor<A> descriptor = findValidationDescriptor(value, annotationType);
        AssertUtils.state(
                descriptor != null,
                "[Assertion failed] - descriptor for annotation '" + annotationType + "' and value type '"
                        + value.getClass() + "' is required; descriptor not found.");
        Class<? extends ConstraintValidator<A, ?>> validatorClass = descriptor.getValidatorClass();
        ConstraintValidator<A, ?> validator;
        validator = validatorClass.getConstructor().newInstance();
        Annotation annotation = AnnotationUtils.createAnnotation(annotationType, annotationValues);
        Method initializeMethod = ObjectUtils.findMethod(validator.getClass(), "initialize");
        AssertUtils.notNullCheck("validator method initialize", initializeMethod);
        ObjectUtils.invokeMethod(initializeMethod, validator, annotation);

        Method isValidMethod = ObjectUtils.findMethod(validator.getClass(), "isValid");
        AssertUtils.notNullCheck("validator method isValid", isValidMethod);
        return Boolean.TRUE.equals(ObjectUtils.invokeMethod(isValidMethod, validator, value, null));
    }

    @SuppressWarnings({"java:S3252"})
    protected <T, A extends Annotation> ConstraintValidatorDescriptor<A> findValidationDescriptor(
            T value, Class<A> annotationType) {
        List<ConstraintValidatorDescriptor<A>> descriptors =
                constraintHelper.getAllValidatorDescriptors(annotationType);
        AssertUtils.state(
                !descriptors.isEmpty(),
                "[Assertion failed] - descriptor for annotation '" + annotationType
                        + "' is required; descriptor not found.");
        if (descriptors.size() == 1 || value == null) {
            return descriptors.get(0);
        }
        Class<?> valueType = value.getClass();
        for (ConstraintValidatorDescriptor<A> descriptor : descriptors) {
            Type validatorType = descriptor.getValidatedType();
            if (validatorType.equals(valueType)) {
                return descriptor;
            }
            if (validatorType instanceof Class<?> class1 && class1.isAssignableFrom(valueType)) {
                return descriptor;
            }
        }
        return null;
    }
}
