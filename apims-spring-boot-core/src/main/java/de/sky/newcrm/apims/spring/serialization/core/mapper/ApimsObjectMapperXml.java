/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings({"java:S112"})
public class ApimsObjectMapperXml extends ApimsObjectMapper {

    public ApimsObjectMapperXml() {
        this(DEFAULT_OBJECT_MAPPER_XML);
    }

    public ApimsObjectMapperXml(ObjectMapper objectMapper) {
        super(objectMapper);
    }
}
