/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.couchbase.core.entity;

import java.util.Arrays;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ApimsMutateInSpec {
    private ApimsMutateInType type;
    private String path;
    private Object value;
    private boolean convertedForWrite;

    public static ApimsMutateInSpec upsert(String path, Object value) {
        return ApimsMutateInSpec.builder()
                .type(ApimsMutateInType.UPSERT)
                .path(path)
                .value(value)
                .build();
    }

    public static ApimsMutateInSpec arrayAppend(String path, Object... value) {
        return ApimsMutateInSpec.builder()
                .type(ApimsMutateInType.ARRAY_APPEND)
                .path(path)
                .value(Arrays.stream(value).toList())
                .build();
    }

    public static ApimsMutateInSpec remove(String path) {
        return ApimsMutateInSpec.builder()
                .type(ApimsMutateInType.REMOVE)
                .path(path)
                .build();
    }
}
