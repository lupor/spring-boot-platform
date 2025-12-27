/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.aspects.core.around;

public class ApimsAroundInterceptorResult {

    private final Object result;

    public ApimsAroundInterceptorResult(Object result) {
        this.result = result;
    }

    public Object getResult() {
        return result;
    }
}
