/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.exceptions;

import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import jakarta.validation.*;
import java.util.Locale;

@SuppressWarnings({"java:S2168", "java:S6212"})
public class ApimsBeanValidatorFactory {

    private static final Object LOCK = new Object();
    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
    private static ApimsBeanValidatorFactory instance;
    private final ApimsBeanValidatorAdapter validator;

    private ApimsBeanValidatorFactory() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            MessageInterpolator messageInterpolator = factory.getMessageInterpolator();
            ValidatorContext validatorContext = factory.usingContext();
            validatorContext.messageInterpolator(new LocaleMessageInterpolator(messageInterpolator, DEFAULT_LOCALE));
            Validator v = validatorContext.getValidator();
            validator = new ApimsBeanValidatorAdapter(v);
        }
    }

    public static ApimsBeanValidatorFactory getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new ApimsBeanValidatorFactory();
                }
            }
        }
        return instance;
    }

    public ApimsBeanValidatorAdapter getValidator() {
        return validator;
    }

    public static class LocaleMessageInterpolator implements MessageInterpolator {

        private final MessageInterpolator targetInterpolator;
        private final Locale locale;

        public LocaleMessageInterpolator(MessageInterpolator targetInterpolator, Locale locale) {
            this.targetInterpolator = targetInterpolator;
            this.locale = locale;
        }

        @Override
        @ApimsReportGeneratedHint
        public String interpolate(String messageTemplate, Context context) {
            return this.targetInterpolator.interpolate(messageTemplate, context, locale);
        }

        @Override
        @ApimsReportGeneratedHint
        public String interpolate(String messageTemplate, Context context, Locale locale) {
            return this.targetInterpolator.interpolate(messageTemplate, context, locale);
        }
    }
}
