/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils.matcher;

import java.util.ArrayList;
import java.util.List;
import org.springframework.util.StringUtils;

@SuppressWarnings({"java:S6212"})
public class DefaultStringIncludeExcludeMatcher implements StringIncludeExcludeMatcher {

    private final List<DefaultStringMatcher> includeMatches = new ArrayList<>();
    private final List<DefaultStringMatcher> excludeMatches = new ArrayList<>();

    public DefaultStringIncludeExcludeMatcher(List<String> includePatterns) {
        this(includePatterns, null);
    }

    public DefaultStringIncludeExcludeMatcher(List<String> includePatterns, List<String> excludePatterns) {
        if (includePatterns != null) {
            for (String pattern : includePatterns) {
                includeMatches.add(new DefaultStringMatcher(pattern, false));
            }
        }
        if (excludePatterns != null) {
            for (String pattern : excludePatterns) {
                excludeMatches.add(new DefaultStringMatcher(pattern, false));
            }
        }
    }

    @Override
    public List<DefaultStringMatcher> getIncludeMatches() {
        return includeMatches;
    }

    @Override
    public List<DefaultStringMatcher> getExcludeMatches() {
        return excludeMatches;
    }

    @Override
    public String getPattern() {
        return getPattern(includeMatches);
    }

    @Override
    public String getExcludePattern() {
        return getPattern(excludeMatches);
    }

    @Override
    public boolean matches(String value) {
        boolean includeFound = includeMatches.isEmpty();
        if (!includeFound) {
            for (DefaultStringMatcher includeMatch : includeMatches) {
                if (includeMatch.matches(value)) {
                    includeFound = true;
                    break;
                }
            }
        }
        if (includeFound) {
            for (DefaultStringMatcher excludeMatch : excludeMatches) {
                if (excludeMatch.matches(value)) {
                    return false;
                }
            }
        }
        return includeFound;
    }

    @Override
    public String toString() {
        return "includeMatches=[" + StringUtils.collectionToDelimitedString(includeMatches, ",") + "], excludeMatches=["
                + StringUtils.collectionToDelimitedString(excludeMatches, ",") + "]";
    }

    protected String getPattern(List<DefaultStringMatcher> list) {
        StringBuilder buf = new StringBuilder();
        for (DefaultStringMatcher includeMatcher : list) {
            if (!buf.isEmpty()) {
                buf.append(", ");
            }
            buf.append(includeMatcher.getPattern());
        }
        return buf.toString();
    }
}
