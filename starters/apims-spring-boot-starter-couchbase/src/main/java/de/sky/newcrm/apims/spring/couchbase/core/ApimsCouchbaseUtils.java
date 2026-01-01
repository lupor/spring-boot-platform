/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.couchbase.core;


import de.sky.newcrm.apims.spring.couchbase.core.entity.ApimsMutateInSpec;
import de.sky.newcrm.apims.spring.couchbase.core.entity.ApimsMutateInType;
import de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3.ObjectMapperUtils;

import java.util.List;
import java.util.Map;

@SuppressWarnings({"java:S1123", "java:S1133", "java:S6355"})
@Deprecated
public class ApimsCouchbaseUtils {

    private ApimsCouchbaseUtils() {}

    @SuppressWarnings("unchecked")
    public static <T> T updateEntityBySpecs(Object entity, ApimsMutateInSpec... specs) {
        if (entity == null) {
            return null;
        }
        if (specs.length == 0) {
            return (T) entity;
        }
        Map<String, Object> map = ObjectMapperUtils.getValueAsMap(entity);
        for (ApimsMutateInSpec spec : specs) {
            if (ApimsMutateInType.REMOVE.equals(spec.getType())) {
                ObjectMapperUtils.removeNode(map, spec.getPath());
            } else if (ApimsMutateInType.ARRAY_APPEND.equals(spec.getType())) {
                ObjectMapperUtils.appendList(map, spec.getPath(), (List<Object>) spec.getValue());
            } else if (ApimsMutateInType.UPSERT.equals(spec.getType())) {
                ObjectMapperUtils.upsertNode(map, spec.getPath(), spec.getValue());
            }
        }
        return (T) ObjectMapperUtils.readValue(map, entity.getClass());
    }

    public static boolean isLike(String value, String expr) {
        if (value == null) {
            return false;
        }
        expr = expr.replace(".", "\\.").replace("_", ".").replace("%", ".*");
        return value.matches(expr);
    }
}
