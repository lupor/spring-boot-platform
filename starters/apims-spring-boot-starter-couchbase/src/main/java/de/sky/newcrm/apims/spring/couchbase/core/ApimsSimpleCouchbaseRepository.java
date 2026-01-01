/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.couchbase.core;

import java.util.List;

import de.sky.newcrm.apims.spring.couchbase.core.entity.ApimsMutateInSpec;
import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import de.sky.newcrm.apims.spring.environment.core.ApimsSpringContext;
import de.sky.newcrm.apims.spring.utils.AssertUtils;
import de.sky.newcrm.apims.spring.utils.FunctionUtils;
import org.springframework.data.couchbase.core.CouchbaseOperations;
import org.springframework.data.couchbase.repository.query.CouchbaseEntityInformation;
import org.springframework.data.couchbase.repository.support.SimpleCouchbaseRepository;

@SuppressWarnings({"java:S119"})
public class ApimsSimpleCouchbaseRepository<T, ID> extends SimpleCouchbaseRepository<T, ID> {

    private ApimsCouchbaseCollectionNameResolver apimsCouchbaseCollectionNameResolver = null;
    private final Class<?> repositoryInterface;

    public ApimsSimpleCouchbaseRepository(
            CouchbaseEntityInformation<T, String> entityInformation,
            CouchbaseOperations couchbaseOperations,
            Class<?> repositoryInterface) {
        super(entityInformation, couchbaseOperations, repositoryInterface);
        this.repositoryInterface = repositoryInterface;
    }

    @ApimsReportGeneratedHint
    @SuppressWarnings("unchecked")
    public <S extends T> S mutateInAndGet(ID id, ApimsMutateInSpec... specs) {
        mutateIn(id, specs);
        return (S) findById(id).orElse(null);
    }

    @ApimsReportGeneratedHint
    public void mutateIn(ID id, ApimsMutateInSpec... specs) {
        AssertUtils.hasLengthCheck("id", id == null ? null : String.valueOf(id));
        if (specs.length != 0) {
            ApimsCouchbaseRepositorySupport.mutateInByNativeOperations(repositoryInterface, String.valueOf(id), specs);
        }
    }

    @ApimsReportGeneratedHint
    public List<String> findAllDocumentIds() {
        return ApimsCouchbaseRepositorySupport.findAllDocumentIds(repositoryInterface);
    }

    public String getBucketName() {
        return getApimsCouchbaseCollectionNameResolver().getBucketName(repositoryInterface);
    }

    public String getScopeName() {
        return getApimsCouchbaseCollectionNameResolver().getScopeName(repositoryInterface);
    }

    public String getCollectionName() {
        return getApimsCouchbaseCollectionNameResolver().getCollectionName(repositoryInterface);
    }

    @Override
    protected String getScope() {
        return getScopeName();
    }

    @Override
    protected String getCollection() {
        return getCollectionName();
    }

    protected ApimsCouchbaseCollectionNameResolver getApimsCouchbaseCollectionNameResolver() {
        return FunctionUtils.executeIfNull(
                apimsCouchbaseCollectionNameResolver, this::loadApimsCouchbaseCollectionNameResolver);
    }

    protected ApimsCouchbaseCollectionNameResolver loadApimsCouchbaseCollectionNameResolver() {
        apimsCouchbaseCollectionNameResolver =
                ApimsSpringContext.getApplicationContext().getBean(ApimsCouchbaseCollectionNameResolver.class);
        return apimsCouchbaseCollectionNameResolver;
    }
}
