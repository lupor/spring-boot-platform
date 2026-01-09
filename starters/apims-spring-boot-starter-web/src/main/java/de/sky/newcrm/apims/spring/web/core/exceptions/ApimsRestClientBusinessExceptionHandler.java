/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings({"java:S1141", "java:S6201"})
public class ApimsRestClientBusinessExceptionHandler {

    public Exception parseException(String body) {
        return de.sky.newcrm.apims.spring.exceptions.ApimsBaseExceptionResolver.parseException(body);
    }
}
