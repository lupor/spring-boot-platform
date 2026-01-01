/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.couchbase.core;


import com.couchbase.client.java.codec.JsonSerializer;
import com.couchbase.client.java.kv.MutateInSpec;
import com.couchbase.client.java.query.QueryResult;
import com.couchbase.client.java.query.QueryScanConsistency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import de.sky.newcrm.apims.spring.environment.core.ApimsSpringContext;
import lombok.Getter;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.couchbase.core.convert.MappingCouchbaseConverter;
import org.springframework.data.couchbase.core.convert.translation.TranslationService;
import org.springframework.data.couchbase.repository.config.RepositoryOperationsMapping;
import org.springframework.util.StringUtils;

@Getter
public class ApimsCouchbaseContext {

    private final boolean mocksEnabled;
    private final ApimsCouchbaseSerializerFactory apimsCouchbaseSerializerFactory;
    private final RepositoryOperationsMapping repositoryOperationsMapping;
    private final ApimsCouchbaseCollectionNameResolver collectionNameResolver;
    private final TranslationService couchbaseTranslationService;
    private final ApimsCouchbaseMutateInSupport apimsCouchbaseMutateInSupport;
    private final MappingCouchbaseConverter mappingCouchbaseConverter;
    private final CustomConversions customConversions;
    private final QueryScanConsistency queryScanConsistency;
    private final Map<String, ApimsCouchbaseTemplate> apimsCouchbaseTemplates = new HashMap<>();
    private final ApimsCouchbaseTemplate apimsPrimaryCouchbaseTemplate;

    @SuppressWarnings({"java:S107"})
    public ApimsCouchbaseContext(
            boolean mocksEnabled,
            ApimsCouchbaseSerializerFactory apimsCouchbaseSerializerFactory,
            RepositoryOperationsMapping repositoryOperationsMapping,
            ApimsCouchbaseCollectionNameResolver collectionNameResolver,
            ApimsCouchbaseMutateInSupport apimsCouchbaseMutateInSupport,
            TranslationService couchbaseTranslationService,
            MappingCouchbaseConverter mappingCouchbaseConverter,
            CustomConversions customConversions,
            QueryScanConsistency queryScanConsistency,
            List<ApimsCouchbaseTemplate> apimsCouchbaseTemplates) {
        this.mocksEnabled = mocksEnabled;
        this.apimsCouchbaseSerializerFactory = apimsCouchbaseSerializerFactory;
        this.repositoryOperationsMapping = repositoryOperationsMapping;
        this.collectionNameResolver = collectionNameResolver;
        this.apimsCouchbaseMutateInSupport = apimsCouchbaseMutateInSupport;
        this.couchbaseTranslationService = couchbaseTranslationService;
        this.mappingCouchbaseConverter = mappingCouchbaseConverter;
        this.customConversions = customConversions;
        this.queryScanConsistency = queryScanConsistency;
        ApimsCouchbaseTemplate primaryCouchbaseTemplate = null;
        if (apimsCouchbaseTemplates != null) {
            for (ApimsCouchbaseTemplate template : apimsCouchbaseTemplates) {
                if (mocksEnabled) {
                    template.setMocksEnabled(true);
                }
                if (template.isPrimaryTemplate() && primaryCouchbaseTemplate == null) {
                    primaryCouchbaseTemplate = template;
                }
                this.apimsCouchbaseTemplates.put(template.getBucketName(), template);
            }
            if (primaryCouchbaseTemplate == null && !apimsCouchbaseTemplates.isEmpty()) {
                primaryCouchbaseTemplate = apimsCouchbaseTemplates.get(0);
            }
        }
        this.apimsPrimaryCouchbaseTemplate = primaryCouchbaseTemplate;
    }

    protected ApimsCouchbaseNativeEndpoint getApimsCouchbaseNativeEndpoint(String bucketName) {
        return getClientFactory(bucketName).getApimsCouchbaseNativeEndpoint();
    }

    public String getDefaultBucketName() {
        return getApimsPrimaryCouchbaseTemplate().getBucketName();
    }

    public String getDefaultScopeName() {
        return getApimsPrimaryCouchbaseTemplate().getScopeName();
    }

    public ApimsCouchbaseTemplate getApimsCouchbaseTemplate(String bucketName) {
        if (!StringUtils.hasLength(bucketName)) {
            return getApimsPrimaryCouchbaseTemplate();
        }
        bucketName = resolveRequiredPlaceholders(bucketName, bucketName);
        ApimsCouchbaseTemplate template = apimsCouchbaseTemplates.get(bucketName);
        return template == null ? getApimsPrimaryCouchbaseTemplate() : template;
    }

    public JsonSerializer getJsonSerializer() {
        return apimsCouchbaseSerializerFactory.getCouchbaseJsonSerializer();
    }

    public ApimsCouchbaseClientFactory getClientFactory(String bucketName) {
        return getApimsCouchbaseTemplate(bucketName).getApimsCouchbaseClientFactory();
    }

    public ApimsCouchbaseClientFactory getClientFactory(String bucketName, String scopeName) {
        return getClientFactory(bucketName).withBucketAndScope(bucketName, scopeName);
    }

    public void mutateIn(
            String bucketName, String scopeName, String collectionName, String id, List<MutateInSpec> specs) {
        collectionName = resolveRequiredPlaceholders(collectionName, collectionName);
        getApimsCouchbaseNativeEndpoint(bucketName)
                .mutateIn(getClientFactory(bucketName, scopeName), collectionName, id, specs);
    }

    @ApimsReportGeneratedHint
    public List<String> loadDocumentIds(String bucketName, String scopeName, String collectionName) {
        return getApimsCouchbaseNativeEndpoint(bucketName)
                .loadDocumentIds(getClientFactory(bucketName, scopeName), collectionName);
    }

    public QueryResult query(String bucketName, String statement) {
        return getApimsCouchbaseNativeEndpoint(bucketName).query(statement);
    }

    protected String resolveRequiredPlaceholders(String value, String defaultValue) {
        return ApimsSpringContext.resolveRequiredPlaceholders(value, defaultValue);
    }
}
