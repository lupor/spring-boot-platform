/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.couchbase.core;

import de.sky.newcrm.apims.spring.couchbase.core.entity.ApimsMutateInSpec;
import de.sky.newcrm.apims.spring.store.core.ApimsEntity;

import java.util.List;
import java.util.Optional;

@Deprecated
@SuppressWarnings({"java:S1123", "java:S1133", "java:S6355"})
public interface ApimsCouchbaseNativeRepository<T extends ApimsEntity> {

    Class<T> getDomainType();

    Optional<T> findById(String id);

    boolean exists(String id);

    void insert(T entity);

    void replace(T entity);

    void upsert(T entity);

    void mutateIn(String id, ApimsMutateInSpec... specs);

    void delete(T entity);

    void deleteById(String id);

    List<T> findAll(String where, Object... arguments);

    List<T> findAllById(List<String> ids);

    List<String> findAllIds();

    List<String> findIds(String where, Object... arguments);
}
