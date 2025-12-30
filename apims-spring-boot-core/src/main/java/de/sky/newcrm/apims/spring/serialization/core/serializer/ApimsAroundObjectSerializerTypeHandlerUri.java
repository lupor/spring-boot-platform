/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core.serializer;

import java.net.URI;
import java.net.URL;

public class ApimsAroundObjectSerializerTypeHandlerUri implements ApimsAroundObjectSerializerTypeHandler {

    @Override
    public boolean canHandle(Object object) {
        return object instanceof URI || object instanceof URL;
    }

    @Override
    @SuppressWarnings("java:S1192")
    public String serialize(ApimsAroundObjectSerializer parent, Object object, int maxLength) {
        return parent.serialize(String.valueOf(object), maxLength);
    }
}
