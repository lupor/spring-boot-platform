/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.context.core;

import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import java.util.Locale;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.util.StringUtils;

public abstract class ApimsSpringApplication {

    public static final String MAIN_NOP_START_ARG = "___main-nop___";

    protected ApimsSpringApplication() {}

    public static void init() {
        initLocale();
        initTimeZone();
    }

    private static void initLocale() {
        String language = System.getProperty("apims.user.language");
        if (!StringUtils.hasLength(language)) {
            language = "en";
        }
        String country = System.getProperty("apims.user.country");
        if (!StringUtils.hasLength(country)) {
            country = "US";
        }
        Locale.setDefault(new Locale(language, country));
    }

    private static void initTimeZone() {
        String value = System.getProperty("apims.user.timezone");
        if (!StringUtils.hasLength(value)) {
            value = "UTC";
        }
        TimeZone.setDefault(TimeZone.getTimeZone(value));
    }

    @ApimsReportGeneratedHint
    public static void run(Class<?> application, String[] args) {
        init();
        if (args != null && args.length == 1 && MAIN_NOP_START_ARG.equals(args[0])) {
            return;
        }
        SpringApplication.run(application, args);
    }
}
