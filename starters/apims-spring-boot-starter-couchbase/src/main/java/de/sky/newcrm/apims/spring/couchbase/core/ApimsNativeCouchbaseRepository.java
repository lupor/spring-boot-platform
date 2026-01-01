/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.couchbase.core;


import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.core.msg.kv.SubdocMutateRequest;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.*;
import com.couchbase.client.java.query.QueryResult;
import java.lang.annotation.Annotation;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.sky.newcrm.apims.spring.couchbase.core.entity.ApimsMutateInSpec;
import de.sky.newcrm.apims.spring.environment.core.ApimsSpringContext;
import de.sky.newcrm.apims.spring.store.core.ApimsEntity;
import de.sky.newcrm.apims.spring.utils.AssertUtils;
import de.sky.newcrm.apims.spring.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.mapping.Expiry;
import org.springframework.data.couchbase.repository.Scope;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Slf4j
@Deprecated
@SuppressWarnings({"java:S1123", "java:S1133", "java:S6355"})
public abstract class ApimsNativeCouchbaseRepository<T extends ApimsEntity>
        implements ApimsCouchbaseNativeRepository<T> {

    protected static final int MUTATEIN_MAX_SPECS = SubdocMutateRequest.SUBDOC_MAX_FIELDS;
    protected static final String QUERY_PLACEHOLDER_QUALIFIED_NAME =
            ApimsCouchbaseNativeSupport.QUERY_PLACEHOLDER_NATIVE_QUALIFIED_NAME;
    protected static final String QUERY_PLACEHOLDER_PARAM_SUFFIX =
            ApimsCouchbaseNativeSupport.QUERY_PLACEHOLDER_PARAM_SUFFIX;
    protected static final String QUERY_PLACEHOLDER_PARAM_0 = QUERY_PLACEHOLDER_PARAM_SUFFIX + "0";
    protected static final String QUERY_PLACEHOLDER_PARAM_1 = QUERY_PLACEHOLDER_PARAM_SUFFIX + "1";
    protected static final String QUERY_PLACEHOLDER_PARAM_2 = QUERY_PLACEHOLDER_PARAM_SUFFIX + "2";
    protected static final String QUERY_PLACEHOLDER_PARAM_3 = QUERY_PLACEHOLDER_PARAM_SUFFIX + "3";
    protected static final String QUERY_PLACEHOLDER_PARAM_4 = QUERY_PLACEHOLDER_PARAM_SUFFIX + "4";
    protected static final String QUERY_PLACEHOLDER_PARAM_5 = QUERY_PLACEHOLDER_PARAM_SUFFIX + "5";
    protected static final String PARAM_NAME_ENTITY = "entity";
    protected static final String PARAM_NAME_ID = "id";

    @Autowired
    private ApimsCouchbaseNativeSupport nativeSupport;

    @Override
    public Optional<T> findById(String id) {
        AssertUtils.hasLengthCheck(PARAM_NAME_ID, id);
        return Optional.ofNullable(findInternalById(id));
    }

    @Override
    public boolean exists(String id) {
        AssertUtils.hasLengthCheck(PARAM_NAME_ID, id);
        return existsInternal(id);
    }

    @Override
    public void insert(T entity) {
        validate(entity);
        String id = entity.getId();
        Object value = convertRootObjectToWrite(entity);
        insertInternal(id, value);
    }

    @Override
    public void replace(T entity) {
        validate(entity);
        String id = entity.getId();
        Object value = convertRootObjectToWrite(entity);
        replaceInternal(id, value);
    }

    @Override
    public void upsert(T entity) {
        validate(entity);
        String id = entity.getId();
        Object value = convertRootObjectToWrite(entity);
        upsertInternal(id, value);
    }

    @Override
    public void mutateIn(String id, ApimsMutateInSpec... specs) {
        validateMutateIn(id, specs);
        mutateInInternal(id, specs);
    }

    @Override
    public void delete(T entity) {
        validate(entity);
        deleteById(entity.getId());
    }

    @Override
    public void deleteById(String id) {
        AssertUtils.hasLengthCheck(PARAM_NAME_ID, id);
        deleteInternalById(id);
    }

    @Override
    public List<T> findAll(String where, Object... arguments) {
        String stmt = appendWhere(
                "SELECT meta().id id, entity.* FROM " + QUERY_PLACEHOLDER_QUALIFIED_NAME + " entity", where);
        QueryResult queryResult = query(stmt, arguments);
        return translate(queryResult);
    }

    @Override
    public List<T> findAllById(List<String> ids) {
        AssertUtils.notNullCheck("ids", ids);
        List<T> list = new ArrayList<>();
        for (String id : ids) {
            T entity = findInternalById(id);
            if (entity != null) {
                list.add(entity);
            }
        }
        return list;
    }

    @Override
    public List<String> findAllIds() {
        return findIds(null);
    }

    @Override
    public List<String> findIds(String where, Object... arguments) {
        List<String> ids = new ArrayList<>();
        String stmt = appendWhere("SELECT meta().id id FROM " + QUERY_PLACEHOLDER_QUALIFIED_NAME, where);
        List<JsonObject> list = query(stmt, arguments).rowsAsObject();
        list.forEach(o -> ids.add((String) o.get("id")));
        return ids;
    }

    protected void validate(T entity) {
        AssertUtils.notNullCheck(PARAM_NAME_ENTITY, entity);
        AssertUtils.hasLengthCheck(PARAM_NAME_ENTITY + ".id", entity.getId());
    }

    protected void validateMutateIn(String id, ApimsMutateInSpec... specs) {
        AssertUtils.hasLengthCheck(PARAM_NAME_ID, id);
        Assert.state(specs.length != 0, "[Assertion failed] - 'specs' are required; it must not be empty");
        Assert.state(
                specs.length <= MUTATEIN_MAX_SPECS,
                "[Assertion failed] - a maximum of " + MUTATEIN_MAX_SPECS + " specs can be provided");
    }

    Object convertRootObjectToWrite(T entity) {
        return getApimsCouchbaseMutateInSupport().convertRootObjectToWrite(entity.getId(), entity);
    }

    List<MutateInSpec> convertSpecsToWrite(ApimsMutateInSpec... specs) {
        return getApimsCouchbaseMutateInSupport().translate(specs);
    }

    T findInternalById(String id) {
        try {
            GetResult result = getNativeCollection().get(id);
            if (result == null || result.contentAsBytes() == null || result.contentAsBytes().length == 0) {
                return null;
            }
            return translate(id, result);
        } catch (DocumentNotFoundException e) {
            log.trace(e.getMessage());
            return null;
        }
    }

    boolean existsInternal(String id) {
        return getNativeCollection().exists(id).exists();
    }

    void insertInternal(String id, Object value) {
        InsertOptions insertOptions = buildInsertOptions(InsertOptions.insertOptions());
        getNativeCollection().insert(id, value, insertOptions);
    }

    protected InsertOptions buildInsertOptions(InsertOptions insertOptions) {
        Duration duration = getInsertExpiryDuration();
        insertOptions.expiry(duration);
        return insertOptions;
    }

    protected Duration getInsertExpiryDuration() {
        return getExpiryDuration();
    }

    void replaceInternal(String id, Object value) {
        ReplaceOptions replaceOptions = buildReplaceOptions(ReplaceOptions.replaceOptions());
        getNativeCollection().replace(id, value, replaceOptions);
    }

    protected ReplaceOptions buildReplaceOptions(ReplaceOptions replaceOptions) {
        Duration duration = getReplaceExpiryDuration();
        if (duration.isZero()) {
            replaceOptions.preserveExpiry(true);
        } else {
            replaceOptions.expiry(duration);
        }
        return replaceOptions;
    }

    protected Duration getReplaceExpiryDuration() {
        return getExpiryDuration();
    }

    void upsertInternal(String id, Object value) {
        UpsertOptions replaceOptions = buildUpsertOptions(UpsertOptions.upsertOptions());
        getNativeCollection().upsert(id, value, replaceOptions);
    }

    protected UpsertOptions buildUpsertOptions(UpsertOptions replaceOptions) {
        Duration duration = getUpsertExpiryDuration();
        replaceOptions.expiry(duration);
        return replaceOptions;
    }

    protected Duration getUpsertExpiryDuration() {
        return getExpiryDuration();
    }

    void mutateInInternal(String id, ApimsMutateInSpec... specs) {
        List<MutateInSpec> list = convertSpecsToWrite(specs);
        MutateInOptions mutateInOptions = buildMutateInOptions(MutateInOptions.mutateInOptions());
        getNativeCollection().mutateIn(id, list, mutateInOptions);
    }

    protected MutateInOptions buildMutateInOptions(MutateInOptions mutateInOptions) {
        Duration duration = getMutateInExpiryDuration();
        if (duration.isZero()) {
            mutateInOptions.preserveExpiry(true);
        } else {
            mutateInOptions.expiry(duration);
        }
        return mutateInOptions;
    }

    protected Duration getMutateInExpiryDuration() {
        return getExpiryDuration();
    }

    void deleteInternalById(String id) {
        try {
            getNativeCollection().remove(id);
        } catch (DocumentNotFoundException e) {
            log.trace(e.getMessage());
        }
    }

    String appendWhere(String stmt, String where) {
        if (StringUtils.hasLength(where)) {
            String whereCheck = where.trim().toLowerCase();
            if (!whereCheck.startsWith("limit ") && !whereCheck.startsWith("where ")) {
                where = "WHERE " + where;
            }
            stmt = stmt + " " + where;
        }
        return stmt;
    }

    QueryResult query(String statement, Object... arguments) {
        return getNativeSupport().query(statement, getBucketName(), getScopeName(), getCollectionName(), arguments);
    }

    List<T> translate(QueryResult queryResult) {
        Class<T> type = getDomainType();
        return queryResult.rowsAs(type);
    }

    T translate(String id, GetResult result) {
        Class<T> type = getDomainType();
        T entity = result.contentAs(type);
        entity.setId(id);
        return entity;
    }

    ApimsCouchbaseNativeSupport getNativeSupport() {
        if (nativeSupport == null) {
            nativeSupport = ApimsSpringContext.getApplicationContext().getBean(ApimsCouchbaseNativeSupport.class);
        }
        return nativeSupport;
    }

    ApimsCouchbaseContext getApimsCouchbaseContext() {
        return getNativeSupport().getApimsCouchbaseContext();
    }

    ApimsCouchbaseMutateInSupport getApimsCouchbaseMutateInSupport() {
        return getApimsCouchbaseContext().getApimsCouchbaseMutateInSupport();
    }

    Collection getNativeCollection() {
        return getApimsCouchbaseContext()
                .getClientFactory(getBucketName(), getScopeName())
                .getCollection(getCollectionName());
    }

    protected Duration getExpiryDuration() {
        Expiry annotation = ObjectUtils.findClassAnnotation(getDomainType(), Expiry.class, false); // see @Document
        if (annotation == null) {
            annotation = getAnntotation(Expiry.class);
        }
        if (annotation == null) {
            return Duration.ZERO;
        }
        int expiryValue = annotation.expiry();
        String expiryExpressionString = annotation.expiryExpression();
        if (StringUtils.hasLength(expiryExpressionString)) {
            String expiryWithReplacedPlaceholders =
                    ApimsSpringContext.resolveRequiredPlaceholders(expiryExpressionString, String.valueOf(expiryValue));
            try {
                expiryValue = Integer.parseInt(expiryWithReplacedPlaceholders);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "Invalid Integer value for expiry expression: " + expiryWithReplacedPlaceholders);
            }
        }
        long secondsShift = annotation.expiryUnit().toSeconds(expiryValue);
        return Duration.ofSeconds(secondsShift);
    }

    protected String getBucketName() {
        Bucket annotation = getAnntotation(Bucket.class);
        return annotation == null || !StringUtils.hasLength(annotation.value())
                ? getNativeSupport().getDefaultBucketName()
                : annotation.value();
    }

    protected String getScopeName() {
        Scope annotation = getAnntotation(Scope.class);
        return annotation == null || !StringUtils.hasLength(annotation.value())
                ? getNativeSupport().getDefaultScopeName()
                : annotation.value();
    }

    protected String getCollectionName() {
        org.springframework.data.couchbase.repository.Collection annotation =
                getAnntotation(org.springframework.data.couchbase.repository.Collection.class);
        return annotation == null || !StringUtils.hasLength(annotation.value()) ? "_default" : annotation.value();
    }

    protected <A extends Annotation> A getAnntotation(Class<A> annotationClazz) {
        return ObjectUtils.findClassAnnotation(this.getClass(), annotationClazz);
    }
}
