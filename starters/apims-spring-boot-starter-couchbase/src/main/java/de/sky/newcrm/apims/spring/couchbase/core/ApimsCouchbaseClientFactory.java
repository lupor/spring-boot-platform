/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.couchbase.core;


import com.couchbase.client.core.io.CollectionIdentifier;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import de.sky.newcrm.apims.spring.environment.core.ApimsSpringContext;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.couchbase.CouchbaseClientFactory;
import org.springframework.data.couchbase.core.CouchbaseExceptionTranslator;
import org.springframework.util.StringUtils;

public class ApimsCouchbaseClientFactory implements CouchbaseClientFactory {

    private final ApimsCouchbaseNativeEndpoint apimsCouchbaseNativeEndpoint;
    private final String bucketName;
    private final String scopeName;

    private boolean mocksEnabled;
    private Bucket bucket;
    private Scope scope;
    private PersistenceExceptionTranslator exceptionTranslator;

    public ApimsCouchbaseClientFactory(
            boolean lazyConnect,
            ApimsCouchbaseNativeEndpoint apimsCouchbaseNativeEndpoint,
            String bucketName,
            String scopeName) {
        this.apimsCouchbaseNativeEndpoint = apimsCouchbaseNativeEndpoint;
        this.bucketName = bucketName;
        this.scopeName = scopeName;
        if (!lazyConnect) {
            init();
        }
    }

    protected void init() {
        this.exceptionTranslator = new CouchbaseExceptionTranslator();
        getCluster();
        getBucket();
        getScope();
    }

    public boolean isMocksEnabled() {
        return mocksEnabled;
    }

    public void setMocksEnabled(boolean mocksEnabled) {
        this.mocksEnabled = mocksEnabled;
    }

    @Override
    public CouchbaseClientFactory withScope(String scopeName) {
        return withBucketAndScope(null, scopeName);
    }

    public ApimsCouchbaseClientFactory withBucketAndScope(String bucketName, String scopeName) {

        bucketName = resolveRequiredPlaceholders(bucketName, null);
        scopeName = resolveRequiredPlaceholders(scopeName, null);

        ApimsCouchbaseClientFactory apimsCouchbaseClientFactory = new ApimsCouchbaseClientFactory(
                true,
                apimsCouchbaseNativeEndpoint,
                StringUtils.hasLength(bucketName) ? bucketName : getBucketName(),
                StringUtils.hasLength(scopeName) ? scopeName : getScopeName());
        apimsCouchbaseClientFactory.setMocksEnabled(isMocksEnabled());
        return apimsCouchbaseClientFactory;
    }

    public ApimsCouchbaseNativeEndpoint getApimsCouchbaseNativeEndpoint() {
        return apimsCouchbaseNativeEndpoint;
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getScopeName() {
        return scopeName;
    }

    @Override
    public Cluster getCluster() {
        return apimsCouchbaseNativeEndpoint.getCluster();
    }

    @Override
    public Bucket getBucket() {
        if (bucket == null) {
            bucket = getCluster().bucket(bucketName);
        }
        return bucket;
    }

    @Override
    public Scope getScope() {
        if (scope == null) {
            scope = StringUtils.hasLength(scopeName)
                    ? getBucket().scope(scopeName)
                    : getBucket().defaultScope();
        }
        return scope;
    }

    @Override
    @SuppressWarnings({"java:S1117"})
    public Collection getCollection(final String collectionName) {
        final Scope scope = getScope();
        if (collectionName == null) {
            if (!scope.name().equals(CollectionIdentifier.DEFAULT_SCOPE)) {
                throw new IllegalStateException("A collectionName must be provided if a non-default scope is used!");
            }
            return getBucket().defaultCollection();
        }
        return scope.collection(collectionName);
    }

    @Override
    public Collection getDefaultCollection() {
        return getCollection(null);
    }

    @Override
    public PersistenceExceptionTranslator getExceptionTranslator() {
        return exceptionTranslator;
    }

    @Override
    @SuppressWarnings({"java:S1186"})
    public void close() {}

    protected String resolveRequiredPlaceholders(String value, String defaultValue) {
        return ApimsSpringContext.resolveRequiredPlaceholders(value, defaultValue);
    }
}
