/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.validation.core;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.annotation.Annotation;

public abstract class NoOpValidator<A extends Annotation, T> implements ConstraintValidator<A, T> {

    @Override
    public boolean isValid(T value, ConstraintValidatorContext context) {
        return true;
    }
}
