/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.couchbase.core.entity;

import java.util.Map;

import de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3.ObjectMapperUtils;
import de.sky.newcrm.apims.spring.store.core.ApimsEntity;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApimsCouchbaseMap implements ApimsEntity {

    private String id;
    private Map<String, Object> document;

    @Override
    public String toString() {
        return ObjectMapperUtils.writeValueAsString(this);
    }
}
