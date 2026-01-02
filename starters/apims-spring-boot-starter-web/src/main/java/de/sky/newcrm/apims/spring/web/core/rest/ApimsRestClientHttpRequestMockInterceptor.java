///*
// * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
// * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
// */
//package de.sky.newcrm.apims.spring.web.core.rest;
//
//import org.springframework.http.HttpRequest;
//import org.springframework.http.client.ClientHttpRequestExecution;
//import org.springframework.http.client.ClientHttpResponse;
//
//import java.io.IOException;
//import java.util.List;
//
//// TODO: Resting related
//@SuppressWarnings({"java:S6201"})
//public class ApimsRestClientHttpRequestMockInterceptor implements ApimsRestClientHttpRequestInterceptor {
//
//    private final List<ApimsRestClientHttpRequestMockHandler> handlers;
//
//    public ApimsRestClientHttpRequestMockInterceptor(List<ApimsRestClientHttpRequestMockHandler> handlers) {
//        this.handlers = handlers;
//    }
//
//    @Override
//    public int getOrder() {
//        return LOWEST_PRECEDENCE - 30;
//    }
//
//    @Override
//    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
//            throws IOException {
//        ClientHttpResponse response = null;
//        ApimsRestClientHttpRequestMockHandler handler = findFirstHandler(request);
//        if (handler != null) {
//            response = handler.intercept(request, body);
//        }
//        if (response == null) {
//            response = execution.execute(request, body);
//        }
//        return response;
//    }
//
//    protected ApimsRestClientHttpRequestMockHandler findFirstHandler(HttpRequest request) {
//        if (!handlers.isEmpty()) {
//            for (ApimsRestClientHttpRequestMockHandler handler : handlers) {
//                if (handler.canHandle(request)) {
//                    return handler;
//                }
//            }
//        }
//        return null;
//    }
//}
