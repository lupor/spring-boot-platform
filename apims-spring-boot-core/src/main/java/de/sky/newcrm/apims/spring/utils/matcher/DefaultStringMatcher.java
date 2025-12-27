/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils.matcher;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;

@SuppressWarnings({"java:S6212"})
public class DefaultStringMatcher implements StringMatcher {

    private static final String MATCH_ALL = "/**";

    private final Matcher matcher;

    private final String pattern;

    public DefaultStringMatcher(String pattern, boolean caseSensitive) {
        Assert.hasText(pattern, "Pattern cannot be null or empty");
        if (pattern.equals(MATCH_ALL) || pattern.equals("**")) {
            pattern = MATCH_ALL;
            this.matcher = null;
        } else {
            // If the pattern ends with {@code /**} and has no other wildcards or path
            // variables, then optimize to a sub-path match
            if (pattern.endsWith(MATCH_ALL)
                    && (pattern.indexOf('?') == -1 && pattern.indexOf('{') == -1 && pattern.indexOf('}') == -1)
                    && pattern.indexOf("*") == pattern.length() - 2) {
                this.matcher = new SubPathMatcher(pattern.substring(0, pattern.length() - 3), caseSensitive);
            } else {
                this.matcher = new AntRootPathMatcher(pattern, caseSensitive);
            }
        }
        this.pattern = pattern;
    }

    @Override
    public String getPattern() {
        return pattern;
    }

    @Override
    public boolean matches(String value) {
        if (this.pattern.equals(MATCH_ALL)) {
            return true;
        }
        return this.matcher.matches(value);
    }

    private interface Matcher {

        boolean matches(String path);
    }

    private static final class AntRootPathMatcher implements Matcher {

        private final AntPathMatcher antMatcher;

        private final String pattern;

        private AntRootPathMatcher(String pattern, boolean caseSensitive) {
            this.pattern = pattern;
            this.antMatcher = createMatcher(caseSensitive);
        }

        private static AntPathMatcher createMatcher(boolean caseSensitive) {
            AntPathMatcher matcher = new AntPathMatcher();
            matcher.setTrimTokens(false);
            matcher.setCaseSensitive(caseSensitive);
            return matcher;
        }

        @Override
        public boolean matches(String path) {
            return this.antMatcher.match(this.pattern, path);
        }
    }

    /**
     * Optimized matcher for trailing wildcards
     */
    private static final class SubPathMatcher implements Matcher {

        private final String subpath;

        private final int length;

        private final boolean caseSensitive;

        private SubPathMatcher(String subpath, boolean caseSensitive) {
            Assert.isTrue(!subpath.contains("*"), "subpath cannot contain \"*\"");
            this.subpath = caseSensitive ? subpath : subpath.toLowerCase();
            this.length = subpath.length();
            this.caseSensitive = caseSensitive;
        }

        @Override
        public boolean matches(String path) {
            if (!this.caseSensitive) {
                path = path.toLowerCase();
            }
            return path.startsWith(this.subpath) && (path.length() == this.length || path.charAt(this.length) == '/');
        }
    }

    @Override
    public String toString() {
        return pattern;
    }
}
