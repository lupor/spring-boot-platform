/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.couchbase.core;


import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.query.QueryResult;
import de.sky.newcrm.apims.spring.couchbase.core.entity.ApimsCouchbaseMap;
import de.sky.newcrm.apims.spring.utils.AssertUtils;

import java.util.ArrayList;
import java.util.List;

@Deprecated
@SuppressWarnings({"java:S1123", "java:S1133", "java:S1192", "java:S6355"})
public abstract class ApimsNativeCouchbaseMapRepository extends ApimsNativeCouchbaseRepository<ApimsCouchbaseMap> {

    @Override
    public Class<ApimsCouchbaseMap> getDomainType() {
        return ApimsCouchbaseMap.class;
    }

    @Override
    protected void validate(ApimsCouchbaseMap entity) {
        super.validate(entity);
        AssertUtils.notNullCheck(PARAM_NAME_ENTITY + ".document", entity.getDocument());
    }

    @Override
    Object convertRootObjectToWrite(ApimsCouchbaseMap entity) {
        return getApimsCouchbaseMutateInSupport().convertRootObjectToWrite(entity.getId(), entity.getDocument());
    }

    @Override
    List<ApimsCouchbaseMap> translate(QueryResult queryResult) {
        List<JsonObject> querylist = queryResult.rowsAsObject();
        List<ApimsCouchbaseMap> entityList = new ArrayList<>();
        for (JsonObject jsonObject : querylist) {
            entityList.add(ApimsCouchbaseMap.builder()
                    .id((String) jsonObject.get("id"))
                    .document(jsonObject.toMap())
                    .build());
        }
        return entityList;
    }

    @Override
    protected ApimsCouchbaseMap translate(String id, GetResult result) {
        return ApimsCouchbaseMap.builder()
                .id(id)
                .document(result.contentAsObject().toMap())
                .build();
    }
}
