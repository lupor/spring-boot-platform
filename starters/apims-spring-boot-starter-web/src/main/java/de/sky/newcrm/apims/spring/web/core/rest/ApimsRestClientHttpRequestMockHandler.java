///*
// * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
// * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
// */
//package de.sky.newcrm.apims.spring.web.core.rest;
//
//import de.sky.newcrm.apims.spring.utils.matcher.DefaultStringIncludeExcludeMatcher;
//import de.sky.newcrm.apims.spring.utils.matcher.StringIncludeExcludeMatcher;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpRequest;
//import org.springframework.http.client.ClientHttpResponse;
//import org.springframework.util.StringUtils;
//
//import java.io.IOException;
//import java.util.Arrays;
//
//public abstract class ApimsRestClientHttpRequestMockHandler implements InitializingBean {
//
//    protected StringIncludeExcludeMatcher urlMatcher;
//
//    protected abstract String getIncludeUrlPattern();
//
//    public abstract ClientHttpResponse intercept(HttpRequest request, byte[] body) throws IOException;
//
//    @Value("${apims.api.mock-implementations-allowed:false}")
//    boolean mockImplementationsAllowed;
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        if (!mockImplementationsAllowed) {
//            throw new IllegalStateException(
//                    "Configuration of mock bean '" + this.getClass().getName() + "' not allowed in this environment!");
//        }
//        this.urlMatcher = new DefaultStringIncludeExcludeMatcher(
//                Arrays.stream(StringUtils.tokenizeToStringArray(getIncludeUrlPattern(), ",", true, true))
//                        .toList(),
//                Arrays.stream(StringUtils.tokenizeToStringArray(getExcludeUrlPattern(), ",", true, true))
//                        .toList());
//    }
//
//    @SuppressWarnings("java:S3400")
//    protected String getExcludeUrlPattern() {
//        return "";
//    }
//
//    protected StringIncludeExcludeMatcher getUrlMatcher() {
//        return urlMatcher;
//    }
//
//    protected String getMatcherUrl(HttpRequest request) {
//        return request.getURI().toString();
//    }
//
//    public boolean canHandle(HttpRequest request) {
//        StringIncludeExcludeMatcher matcher = getUrlMatcher();
//        String matcherUrl = getMatcherUrl(request);
//        return matcher.matches(matcherUrl);
//    }
//}
