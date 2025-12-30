/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.store.core;

import de.sky.newcrm.apims.spring.utils.AssertUtils;
import de.sky.newcrm.apims.spring.utils.ObjectUtils;
import de.sky.newcrm.apims.spring.utils.collections.ThreadSaveList;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings({"java:S119"})
public abstract class ApimsEntityIdInMemoryStore<ID, T> extends ApimsEntityInMemoryStore<T>
        implements ApimsEntityIdStore<ID, T> {

    private final String[] entityIdAnnotationClassNames =
            new String[] {"org.springframework.data.annotation.Id", "jakarta.persistence.Id"};
    protected final Class<ID> keyClass;
    private Field idField = null;

    protected ApimsEntityIdInMemoryStore(Class<?> keyClass, Class<?> entityClass) {
        this(keyClass, entityClass, null);
    }

    @SuppressWarnings("unchecked")
    protected ApimsEntityIdInMemoryStore(Class<?> keyClass, Class<?> entityClass, ThreadSaveList<T> backend) {
        super(entityClass, backend);
        this.keyClass = (Class<ID>) keyClass;
    }

    @Override
    public Class<ID> getKeyClass() {
        return keyClass;
    }

    @Override
    public boolean isIdEquals(ID id, ID other) {
        return ObjectUtils.isEquals(id, other);
    }

    @Override
    public boolean isEntityIdEquals(T entity, ID other) {
        return ObjectUtils.isEquals(getId(entity), other);
    }

    @Override
    public boolean isEntityEquals(T entity, T other) {
        return isEntityIdEquals(entity, getId(other));
    }

    @Override
    public Optional<T> findById(ID id) {
        for (T entity : getEntities()) {
            if (isEntityIdEquals(entity, id)) {
                return Optional.of(cloneEntity(entity));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<T> findAllById(Iterable<ID> ids) {
        List<T> list = new ArrayList<>();
        for (ID id : ids) {
            Optional<T> o = findById(id);
            o.ifPresent(list::add);
        }
        return list;
    }

    @Override
    public boolean existsById(ID id) {
        return findById(id).isPresent();
    }

    @Override
    public void deleteById(ID id) {
        findById(id).ifPresent(super::delete);
    }

    @Override
    public void deleteAllById(Iterable<? extends ID> ids) {
        for (T entity : getEntities()) {
            for (ID id : ids) {
                if (isEntityIdEquals(entity, id)) {
                    super.delete(entity);
                }
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public ID getId(T entity) {
        if (ApimsEntity.class.isAssignableFrom(entity.getClass())) {
            return (ID) ((ApimsEntity) entity).getId();
        }
        return ObjectUtils.getField(getIdField(), entity);
    }

    @SuppressWarnings("unchecked")
    protected synchronized Field getIdField() {
        if (idField == null) {
            for (String entityIdAnnotationClassName : entityIdAnnotationClassNames) {
                Class<? extends Annotation> clazz =
                        (Class<? extends Annotation>) ObjectUtils.getClass(entityIdAnnotationClassName, true);
                if (clazz != null) {
                    idField = ObjectUtils.findAnnotatedField(getEntityClass(), clazz);
                    if (idField != null) {
                        return idField;
                    }
                }
            }
            idField = ObjectUtils.findField(getEntityClass(), "id");
            if (idField == null) {
                idField = ObjectUtils.findField(getEntityClass(), "_id");
            }
            AssertUtils.notNullCheck(
                    "Field with @Id annotation (" + getEntityClass().getName() + ".@Id)", idField);
        }
        return idField;
    }
}
