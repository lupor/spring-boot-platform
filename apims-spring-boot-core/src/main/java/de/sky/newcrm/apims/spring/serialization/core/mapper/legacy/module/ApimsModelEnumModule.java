/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core.mapper.legacy.module;

import com.fasterxml.jackson.databind.module.SimpleModule;

public class ApimsModelEnumModule extends SimpleModule {

    public ApimsModelEnumModule() {
        registerDeserializerModifier();
    }

    protected void registerDeserializerModifier() {
        setDeserializerModifier(new ApimsModelEnumDeserializerModifier());
    }
}
