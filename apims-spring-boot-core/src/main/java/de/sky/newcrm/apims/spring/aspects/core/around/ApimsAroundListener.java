/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.aspects.core.around;

import org.springframework.core.Ordered;

public interface ApimsAroundListener extends Ordered {

    default void beforeAroundMethod(ApimsAroundContext context) {}

    default void afterAroundMethod(ApimsAroundContext context, Object result, Exception resultError) {}
}
