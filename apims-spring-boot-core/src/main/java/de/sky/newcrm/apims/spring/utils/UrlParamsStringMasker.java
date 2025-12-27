/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import java.util.Arrays;
import java.util.Collection;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public interface UrlParamsStringMasker {

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
        for (String key : keys) {
            if (source.contains(key + "=")) {
                String regEx = "([?&]" + key + ")=[^?&]+";
                String replaceMaskValue = "$1=" + maskValue;
                source = source.replaceAll(regEx, replaceMaskValue);
            }
        }
        return source;
    }
}
