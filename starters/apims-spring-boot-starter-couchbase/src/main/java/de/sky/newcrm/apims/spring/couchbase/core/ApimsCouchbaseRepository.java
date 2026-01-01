/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.couchbase.core;


import java.util.List;

import de.sky.newcrm.apims.spring.couchbase.core.entity.ApimsMutateInSpec;
import org.springframework.data.couchbase.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@SuppressWarnings({"java:S119"})
@NoRepositoryBean
public interface ApimsCouchbaseRepository<T, ID> extends CrudRepository<T, ID> {

    void mutateIn(ID id, ApimsMutateInSpec... specs);

    <S extends T> S mutateInAndGet(ID id, ApimsMutateInSpec... specs);

    @Query("#{#n1ql.selectEntity} WHERE META().`id` is not null")
    Iterable<T> findAll();

    List<ID> findAllDocumentIds();
}
