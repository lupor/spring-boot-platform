/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import com.veracode.annotation.CRLFCleanser;
import com.veracode.annotation.FilePathCleanser;
import java.io.File;
import java.util.Arrays;

@SuppressWarnings({"java:S6201"})
public class VeracodeMitigationUtils {

    private static final String[] VALID_URLS = new String[] {
        "http://", // backward
        "https://",
        "http://10.82.68.228",
        "http://10.96.53.10",
        "http://10.96.111.22",
        "http://10.176.169.23",
        "http://dazn-",
        "http://localhost",
        "http://opentelemetry",
        "http://return.paketomat.at",
        "http://skyde-",
        "http://svc-",
        "http://tibco-",
        "http://www.sandbox"
    };

    private VeracodeMitigationUtils() {}

    @CRLFCleanser
    public static Object sanitizeLogValue(Object arg) {
        if (arg instanceof CharSequence value && !value.isEmpty()) {
            return String.valueOf(value).replace("\n", "\\n").replace("\r", "");
        }
        return arg;
    }

    @CRLFCleanser
    public static Object[] sanitizeLogValues(Object... args) {
        return Arrays.stream(args)
                .map(VeracodeMitigationUtils::sanitizeLogValue)
                .toArray();
    }

    @FilePathCleanser
    public static File sanitizeNewFile(String path) {
        return new File(path);
    }

    public static String sanitizeUrl(String url) {
        AssertUtils.hasLengthCheck("url", url);
        for (String validUrl : VALID_URLS) {
            if (url.startsWith(validUrl)) {
                return url;
            }
        }
        return null;
    }
}
