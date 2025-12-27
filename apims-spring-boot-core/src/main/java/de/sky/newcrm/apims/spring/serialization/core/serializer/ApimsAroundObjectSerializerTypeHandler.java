/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core.serializer;

public interface ApimsAroundObjectSerializerTypeHandler {

    boolean canHandle(Object object);

    String serialize(ApimsAroundObjectSerializer parent, Object object, int maxLength);
}
