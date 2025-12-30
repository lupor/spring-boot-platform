/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.store.core;

import java.util.Optional;

@SuppressWarnings({"java:S119"})
public interface ApimsEntityIdStore<ID, T> extends ApimsEntityStore<T> {

    Class<ID> getKeyClass();

    boolean isIdEquals(ID id, ID other);

    boolean isEntityIdEquals(T entity, ID other);

    Optional<T> findById(ID id);

    Iterable<T> findAllById(Iterable<ID> ids);

    boolean existsById(ID id);

    void deleteById(ID id);

    void deleteAllById(Iterable<? extends ID> ids);

    ID getId(T entity);
}
