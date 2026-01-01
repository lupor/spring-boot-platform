/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.couchbase.core;


import com.couchbase.client.java.codec.JsonSerializer;
import com.couchbase.client.java.kv.MutateInSpec;
import com.couchbase.client.java.query.QueryResult;
import java.text.MessageFormat;
import java.util.List;

import de.sky.newcrm.apims.spring.couchbase.core.entity.ApimsMutateInSpec;
import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import de.sky.newcrm.apims.spring.environment.core.ApimsSpringContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.Repository;
import org.springframework.util.StringUtils;

@Slf4j
public class ApimsCouchbaseNativeSupport {

    public static final String QUERY_PLACEHOLDER_IDENTIFIER = "#n1ql.";
    public static final String QUERY_PLACEHOLDER_PARAM_SUFFIX = "#n1ql.$";
    public static final String QUERY_PLACEHOLDER_NATIVE_QUALIFIED_SIMPLE_NAME =
            QUERY_PLACEHOLDER_IDENTIFIER + "collection_name";
    public static final String QUERY_PLACEHOLDER_NATIVE_QUALIFIED_NAME =
            QUERY_PLACEHOLDER_IDENTIFIER + "collection_full_name";
    public static final String DEFAULT_VALUE = "_default";
    public static final String DEFAULT_BUCKET_NAME = null;
    public static final String DEFAULT_SCOPE_NAME = null;
    public static final String DEFAULT_COLLECTION_NAME = DEFAULT_VALUE;
    public static final String NATIVE_QUALIFIED_NAME_PATTERN = "`{0}`.`{1}`.`{2}`";
    private ApimsCouchbaseContext apimsCouchbaseContext;

    public ApimsCouchbaseNativeSupport(ApimsCouchbaseContext apimsCouchbaseContext) {
        this.apimsCouchbaseContext = apimsCouchbaseContext;
    }

    public ApimsCouchbaseContext getApimsCouchbaseContext() {
        if (apimsCouchbaseContext == null) {
            apimsCouchbaseContext = ApimsSpringContext.getApplicationContext().getBean(ApimsCouchbaseContext.class);
        }
        return apimsCouchbaseContext;
    }

    public String getDefaultBucketName() {
        return getApimsCouchbaseContext().getDefaultBucketName();
    }

    public String getDefaultScopeName() {
        return getApimsCouchbaseContext().getDefaultScopeName();
    }

    public JsonSerializer getJsonSerializer() {
        return getApimsCouchbaseContext().getJsonSerializer();
    }

    protected String nativeQualifiedName(String bucketName, String scopeName, String collectionName) {
        if (!StringUtils.hasLength(bucketName) || DEFAULT_VALUE.equals(bucketName)) {
            bucketName = getDefaultBucketName();
        }
        if (!StringUtils.hasLength(scopeName)) {
            scopeName = getDefaultScopeName();
        }
        return MessageFormat.format(NATIVE_QUALIFIED_NAME_PATTERN, bucketName, scopeName, collectionName);
    }

    public String getBucketName(Class<?> repository) {
        return getApimsCouchbaseContext().getCollectionNameResolver().getBucketName(repository);
    }

    public String getScopeName(Class<?> repository) {
        return getApimsCouchbaseContext().getCollectionNameResolver().getScopeName(repository);
    }

    public String getCollectionName(Class<?> repository) {
        return getApimsCouchbaseContext().getCollectionNameResolver().getCollectionName(repository);
    }

    public ApimsCouchbaseTemplate getApimsCouchbaseTemplate(Class<?> repository) {
        return getApimsCouchbaseTemplate(getBucketName(repository));
    }

    public ApimsCouchbaseTemplate getApimsCouchbaseTemplate(String bucketName) {
        return getApimsCouchbaseContext().getApimsCouchbaseTemplate(bucketName);
    }

    public void mutateIn(Class<?> repository, String id, ApimsMutateInSpec... specs) {
        mutateIn(getBucketName(repository), getScopeName(repository), getCollectionName(repository), id, specs);
    }

    public void mutateIn(String bucket, String scope, String collection, String id, ApimsMutateInSpec... specs) {
        getApimsCouchbaseContext().mutateIn(bucket, scope, collection, id, translate(specs));
    }

    @ApimsReportGeneratedHint
    public List<String> loadDocumentIds(Repository<?, ?> repository) {
        return loadDocumentIds(repository.getClass());
    }

    @ApimsReportGeneratedHint
    public List<String> loadDocumentIds(Class<?> repository) {
        return loadDocumentIds(getBucketName(repository), getScopeName(repository), getCollectionName(repository));
    }

    @ApimsReportGeneratedHint
    public List<String> loadDocumentIds(String bucket, String scope, String collection) {
        return getApimsCouchbaseContext().loadDocumentIds(bucket, scope, collection);
    }

    public QueryResult query(String statement, Repository<?, ?> repository, Object... arguments) {
        return query(statement, repository.getClass(), arguments);
    }

    public QueryResult query(String statement, Class<?> repository, Object... arguments) {
        return query(
                statement,
                getBucketName(repository),
                getScopeName(repository),
                getCollectionName(repository),
                arguments);
    }

    public QueryResult query(
            String statement, String bucketName, String scopeName, String collectionName, Object... arguments) {
        if (statement.contains(QUERY_PLACEHOLDER_NATIVE_QUALIFIED_NAME)) {
            statement = statement.replace(
                    QUERY_PLACEHOLDER_NATIVE_QUALIFIED_NAME,
                    nativeQualifiedName(bucketName, scopeName, collectionName));
        }
        if (statement.contains(QUERY_PLACEHOLDER_NATIVE_QUALIFIED_SIMPLE_NAME)) {
            statement = statement.replace(QUERY_PLACEHOLDER_NATIVE_QUALIFIED_SIMPLE_NAME, collectionName);
        }
        if (arguments.length != 0) {
            int index = 0;
            for (Object argument : arguments) {
                String key = QUERY_PLACEHOLDER_PARAM_SUFFIX + index;
                statement = statement.replace(key, String.valueOf(argument));
                index++;
            }
        }
        return getApimsCouchbaseContext().query(bucketName, statement);
    }

    public String toString(ApimsMutateInSpec... specs) {
        return getApimsCouchbaseContext().getApimsCouchbaseMutateInSupport().toString(specs);
    }

    protected List<MutateInSpec> translate(ApimsMutateInSpec... specs) {
        return getApimsCouchbaseContext().getApimsCouchbaseMutateInSupport().translate(specs);
    }
}
