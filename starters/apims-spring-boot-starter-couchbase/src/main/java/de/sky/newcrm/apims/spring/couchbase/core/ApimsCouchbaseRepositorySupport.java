/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.couchbase.core;

import java.util.List;

import de.sky.newcrm.apims.spring.couchbase.core.entity.ApimsMutateInSpec;
import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import de.sky.newcrm.apims.spring.environment.core.ApimsSpringContext;
import org.springframework.data.repository.CrudRepository;

public class ApimsCouchbaseRepositorySupport {

    private ApimsCouchbaseRepositorySupport() {}

    public static <T> T mutateInByCrudOperations(
            CrudRepository<?, ?> repository, Object id, ApimsMutateInSpec... specs) {

        return ApimsSpringContext.getApplicationContext()
                .getBean(ApimsCouchbaseContext.class)
                .getApimsCouchbaseMutateInSupport()
                .mutateInByCrudOperations(repository, id, specs);
    }

    @ApimsReportGeneratedHint
    public static void mutateInByNativeOperations(Class<?> repository, String id, ApimsMutateInSpec... specs) {

        ApimsSpringContext.getApplicationContext()
                .getBean(ApimsCouchbaseNativeSupport.class)
                .mutateIn(repository, id, specs);
    }

    @ApimsReportGeneratedHint
    public static List<String> findAllDocumentIds(Class<?> repository) {

        return ApimsSpringContext.getApplicationContext()
                .getBean(ApimsCouchbaseNativeSupport.class)
                .loadDocumentIds(repository);
    }
}
