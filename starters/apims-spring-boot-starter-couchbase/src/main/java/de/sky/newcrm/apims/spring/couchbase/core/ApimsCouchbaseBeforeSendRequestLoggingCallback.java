/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.couchbase.core;



import java.util.Map;
import java.util.TreeMap;

import com.couchbase.client.core.logging.RedactableArgument;
import com.couchbase.client.core.msg.analytics.AnalyticsRequest;
import com.couchbase.client.core.msg.kv.*;
import com.couchbase.client.core.msg.query.QueryRequest;
import com.couchbase.client.core.msg.search.ServerSearchRequest;
import com.couchbase.client.core.msg.view.ViewRequest;
import de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3.ObjectMapperUtils;
import lombok.extern.slf4j.Slf4j;
import com.couchbase.client.core.msg.Request;

@Slf4j
public class ApimsCouchbaseBeforeSendRequestLoggingCallback extends ApimsCouchbaseBeforeSendRequestCallback {

    @Override
    protected boolean isEnabled(Request<?> request) {
        return log.isTraceEnabled();
    }

    @Override
    protected void before(AppendRequest request) {
        logRequest(request);
    }

    @Override
    protected void before(DecrementRequest request) {
        logRequest(request);
    }

    @Override
    protected void before(GetAndLockRequest request) {
        logRequest(request);
    }

    @Override
    protected void before(GetAndTouchRequest request) {
        logRequest(request);
    }

    @Override
    protected void before(GetCollectionIdRequest request) {
        logRequest(request);
    }

    @Override
    protected void before(GetCollectionManifestRequest request) {
        logRequest(request);
    }

    @Override
    protected void before(GetMetaRequest request) {
        logRequest(request);
    }

    @Override
    protected void before(GetRequest request) {
        logRequest(request);
    }

    @Override
    protected void before(IncrementRequest request) {
        logRequest(request);
    }

    @Override
    protected void before(InsertRequest request) {
        logRequest(request);
    }

    @Override
    protected void before(KvPingRequest request) {
        logRequest(request);
    }

    @Override
    protected void before(MultiObserveViaCasRequest request) {
        logRequest(request);
    }

    @Override
    protected void before(NoopRequest request) {
        logRequest(request);
    }

    @Override
    protected void before(ObserveViaCasRequest request) {
        logRequest(request);
    }

    @Override
    protected void before(ObserveViaSeqnoRequest request) {
        logRequest(request);
    }

    @Override
    protected void before(PrependRequest request) {
        logRequest(request);
    }

    @Override
    protected void before(RemoveRequest request) {
        logRequest(request);
    }

    @Override
    protected void before(ReplaceRequest request) {
        logRequest(request);
    }

    @Override
    protected void before(ReplicaGetRequest request) {
        logRequest(request);
    }

    @Override
    protected void before(SubdocGetRequest request) {
        logRequest(request);
    }

    @Override
    protected void before(SubdocMutateRequest request) {
        logRequest(request);
    }

    @Override
    protected void before(TouchRequest request) {
        logRequest(request);
    }

    @Override
    protected void before(UnlockRequest request) {
        logRequest(request);
    }

    @Override
    protected void before(UpsertRequest request) {
        logRequest(request);
    }

    @Override
    protected void before(QueryRequest request) {
        logRequest(request);
    }

    @Override
    protected void before(ServerSearchRequest request) {
        logRequest(request);
    }

    @Override
    protected void before(ViewRequest request) {
        logRequest(request);
    }

    @Override
    protected void before(AnalyticsRequest request) {
        logRequest(request);
    }

    protected String getServiceContext(Request<?> request) {
        final Map<String, Object> originalData = request.serviceContext();
        final Map<String, Object> data = new TreeMap<>();
        if (originalData == null) {
            return null;
        }
        for (Map.Entry<String, Object> entry : originalData.entrySet()) {
            Object value = entry.getValue();
            if (entry.getValue() instanceof RedactableArgument redactableArgument) {
                value = redactableArgument.message();
            }
            data.put(entry.getKey(), value);
        }
        return ObjectMapperUtils.writeValueAsString(data);
    }

    protected void logRequest(Request<?> request) {
        logRequest(request, getServiceContext(request));
    }

    protected void logRequest(Request<?> request, String message) {
        if (message != null) {
            log.trace("|[COUCHBASE REQUEST] : {} : {}", request.name(), message);
        }
    }
}
