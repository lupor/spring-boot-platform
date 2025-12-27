/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.flow;

import java.lang.reflect.Method;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApimsFlowMethodReference {

    private Method method;
    private Object[] args;
}
