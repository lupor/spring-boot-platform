/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@SuppressWarnings({"java:S1192"})
public abstract class AssertUtils extends Assert {

    private AssertUtils() {}

    public static void hasLengthCheck(String name, String value) {
        state(
                StringUtils.hasLength(value),
                "[Assertion failed] - argument/field '" + name + "' is required; it must not be null or empty");
    }

    public static void notNullCheck(String name, Object value) {
        state(value != null, "[Assertion failed] - argument/field '" + name + "' is required; it must not be null");
    }

    public static void isTrueCheck(String name, boolean value) {
        state(value, "[Assertion failed] - argument/field/expression '" + name + "' is false, it must be true");
    }

    @ApimsReportGeneratedHint
    public static void fileExistsCheck(String name, File value) {
        state(
                value != null && value.exists(),
                "[Assertion failed] - argument/field '" + name
                        + "' is required; it must not be null and must be exists: " + value);
    }

    @ApimsReportGeneratedHint
    public static void fileExistsCheck(String name, Path value) {
        state(
                value != null && Files.exists(value),
                "[Assertion failed] - argument/field '" + name
                        + "' is required; it must not be null and must be exists: " + value);
    }
}
