/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.store.core;

import java.io.File;
import java.util.List;
import java.util.function.Predicate;

public interface ApimsEntityStore<T> {

    Class<T> getEntityClass();

    T cloneEntity(T entity);

    List<T> cloneEntities(Iterable<T> entities);

    List<T> cloneEntities(Predicate<T> predicate);

    boolean isEntityEquals(T entity, T other);

    List<T> getEntities();

    void setEntities(List<T> entities);

    void loadEntitiesByJsonFile(String resourceName);

    void loadEntitiesByJsonFile(File file);

    <S extends T> S save(S entity);

    <S extends T> List<S> saveAll(Iterable<S> entities);

    List<T> findAll();

    long count();

    void delete(T entity);

    void deleteAll(Iterable<? extends T> entities);

    void deleteAll();
}
