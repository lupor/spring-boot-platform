/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.couchbase.core;


import com.couchbase.client.java.query.QueryScanConsistency;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.data.couchbase.core.convert.CouchbaseConverter;
import org.springframework.data.couchbase.core.convert.translation.TranslationService;

public class ApimsCouchbaseTemplate extends CouchbaseTemplate {

    private final boolean primaryTemplate;
    private boolean mocksEnabled;

    public ApimsCouchbaseTemplate(
            ApimsCouchbaseClientFactory clientFactory,
            CouchbaseConverter converter,
            TranslationService translationService,
            QueryScanConsistency scanConsistency) {
        this(false, clientFactory, converter, translationService, scanConsistency);
    }

    public ApimsCouchbaseTemplate(
            boolean primaryTemplate,
            ApimsCouchbaseClientFactory clientFactory,
            CouchbaseConverter converter,
            TranslationService translationService,
            QueryScanConsistency scanConsistency) {
        super(clientFactory, converter, translationService, scanConsistency);
        this.primaryTemplate = primaryTemplate;
    }

    public ApimsCouchbaseClientFactory getApimsCouchbaseClientFactory() {
        return (ApimsCouchbaseClientFactory) getCouchbaseClientFactory();
    }

    public boolean isPrimaryTemplate() {
        return primaryTemplate;
    }

    public boolean isMocksEnabled() {
        return mocksEnabled;
    }

    public void setMocksEnabled(boolean mocksEnabled) {
        this.mocksEnabled = mocksEnabled;
    }

    @Override
    public String getBucketName() {
        return getApimsCouchbaseClientFactory().getBucketName();
    }

    @Override
    public String getScopeName() {
        return getApimsCouchbaseClientFactory().getScopeName();
    }
}
