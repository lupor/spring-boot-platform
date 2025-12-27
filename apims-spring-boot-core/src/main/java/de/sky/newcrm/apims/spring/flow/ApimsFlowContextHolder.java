/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.flow;

public class ApimsFlowContextHolder {

    private static final ThreadLocal<ApimsFlowContextImpl> threadLocalContext = new ThreadLocal<>();

    private ApimsFlowContextHolder() {}

    public static void resetFlowContext() {
        threadLocalContext.remove();
    }

    public static ApimsFlowContext getFlowContext() {
        ApimsFlowContextImpl ctx = threadLocalContext.get();
        if (ctx == null) {
            ctx = new ApimsFlowContextImpl();
            threadLocalContext.set(ctx);
        }
        return ctx;
    }

    public static void pushFlowMethodReference(ApimsFlowMethodReference reference) {
        getFlowContextImpl().pushFlowMethodReference(reference);
    }

    public static void popFlowMethodReference() {
        getFlowContextImpl().popFlowMethodReference();
    }

    private static ApimsFlowContextImpl getFlowContextImpl() {
        return (ApimsFlowContextImpl) getFlowContext();
    }
}
