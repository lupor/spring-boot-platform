/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.store.core;

import com.veracode.annotation.FilePathCleanser;
import de.sky.newcrm.apims.spring.exceptions.ApimsRuntimeException;
import de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3.ObjectMapperUtils;
import de.sky.newcrm.apims.spring.utils.FunctionUtils;
import de.sky.newcrm.apims.spring.utils.ObjectUtils;
import de.sky.newcrm.apims.spring.utils.collections.ThreadSaveArrayList;
import de.sky.newcrm.apims.spring.utils.collections.ThreadSaveList;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public abstract class ApimsEntityInMemoryStore<T> implements ApimsEntityStore<T> {

    protected final Class<T> entityClass;
    protected final ObjectMapper objectMapper;
    private final ThreadSaveList<T> backend;

    protected ApimsEntityInMemoryStore(Class<?> entityClass) {
        this(entityClass, null);
    }

    @SuppressWarnings("unchecked")
    protected ApimsEntityInMemoryStore(Class<?> entityClass, ThreadSaveList<T> backend) {
        this.entityClass = (Class<T>) entityClass;
        this.backend = backend == null ? new ThreadSaveArrayList<>() : backend;
        this.objectMapper = ObjectMapperUtils.getApimsObjectMapperJson().unwrap();
    }

    @Override
    public Class<T> getEntityClass() {
        return entityClass;
    }

    @Override
    public T cloneEntity(T entity) {
        return cloneEntity(entity, entityClass);
    }

    @SuppressWarnings("java:S1488")
    protected <E> E cloneEntity(E entity, Class<E> type) {
        return FunctionUtils.execute(
                () -> objectMapper.readValue(objectMapper.writeValueAsBytes(entity), type),
                ApimsRuntimeException.class);
    }

    @Override
    public List<T> cloneEntities(Iterable<T> entities) {
        List<T> list = new ArrayList<>();
        if (entities != null) {
            for (T entity : entities) {
                list.add(cloneEntity(entity));
            }
        }
        // unmodifiableList
        return list.stream().toList();
    }

    @Override
    public List<T> cloneEntities(Predicate<T> predicate) {
        return cloneEntities(backend.filter(predicate));
    }

    @Override
    public boolean isEntityEquals(T entity, T other) {
        return ObjectUtils.isEquals(entity, other, false);
    }

    @Override
    public List<T> getEntities() {
        return backend.getAll();
    }

    @Override
    public void setEntities(final List<T> entities) {
        this.backend.accept(ts -> {
            ts.clear();
            ts.addAll(entities);
        });
    }

    @Override
    @FilePathCleanser
    public void loadEntitiesByJsonFile(String resourceName) {
        loadEntitiesByJsonFile(
                new File(Objects.requireNonNull(this.getClass().getResource(resourceName)).getFile()));
    }

    @Override
    public void loadEntitiesByJsonFile(File file) {
        FunctionUtils.execute(
                () -> backend.addAll(objectMapper.readValue(
                        file, objectMapper.getTypeFactory().constructCollectionType(List.class, entityClass))),
                ApimsRuntimeException.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S extends T> S save(S entity) {
        S clonedEntity = (S) cloneEntity(entity);
        delete(entity);
        backend.add(clonedEntity);
        return entity;
    }

    @Override
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        List<S> list = new ArrayList<>();
        for (S entity : entities) {
            list.add(save(entity));
        }
        return list;
    }

    @Override
    public List<T> findAll() {
        // unmodifiableList
        return cloneEntities(getEntities());
    }

    @Override
    public long count() {
        return backend.size();
    }

    @Override
    public void delete(final T entity) {
        backend.removeIf(other -> isEntityEquals(entity, other));
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        for (T entity : entities) {
            delete(entity);
        }
    }

    @Override
    public void deleteAll() {
        backend.clear();
    }
}
