/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import java.util.Arrays;
import java.util.Collection;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public interface JSONStringMasker {

    static String mask(String source, String maskValue, String... keys) {
        return mask(source, maskValue, Arrays.asList(keys));
    }

    static String mask(String source, String maskValue, Collection<String> keys) {
        Assert.notNull(keys, "[Assertion failed] - 'keys' is required; it must not be null");
        if (!StringUtils.hasLength(source)) {
            return source;
        }
        if (!StringUtils.hasLength(maskValue)) {
            maskValue = "___masked___";
        }
        boolean cutted = source.endsWith("...");
        if (cutted) {
            source = source + "\"";
        }
        final String checkValue = source.length() > 500 ? source.substring(0, 500) : source;
        if (checkValue.contains("\n")) {
            source = source.replace("\n", " ");
        }
        if (checkValue.contains("\" :")) {
            source = source.replace("\" :", "\":");
        }
        if (checkValue.contains("  ")) {
            source = source.replaceAll("\\s{2,}", " ");
        }
        for (String key : keys) {
            String jsonKey = "\"" + key + "\"";
            if (source.contains(jsonKey)) {
                String jsonRegEx = jsonKey + ": ([\"'])(?:(?=(\\\\?))\\2.)*?\\1";
                String jsonMaskValue = jsonKey + ": \"" + maskValue + "\"";
                source = source.replaceAll(jsonRegEx, jsonMaskValue);
            }
        }
        if (cutted) {
            if (source.endsWith("...\"")) {
                source = source.substring(0, source.length() - 1);
            } else {
                source += "...";
            }
        }
        return source;
    }
}
