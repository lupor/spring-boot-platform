/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.couchbase.core;


import java.nio.charset.StandardCharsets;

import com.couchbase.client.core.callbacks.BeforeSendRequestCallback;
import com.couchbase.client.core.msg.analytics.AnalyticsRequest;
import com.couchbase.client.core.msg.kv.*;
import com.couchbase.client.core.msg.query.QueryRequest;
import com.couchbase.client.core.msg.search.ServerSearchRequest;
import com.couchbase.client.core.msg.view.ViewRequest;
import lombok.extern.slf4j.Slf4j;
import com.couchbase.client.core.msg.Request;

@Slf4j
@SuppressWarnings({"java:S1172", "java:S1186", "java:S1871", "java:S3776"})
public abstract class ApimsCouchbaseBeforeSendRequestCallback implements BeforeSendRequestCallback {

    @Override
    public void beforeSend(Request<?> request) {
        try {
            beforeSendInternal(request);
        } catch (Exception e) {
            log.warn("beforeSend failed: ", e);
        }
    }

    protected void beforeSendInternal(Request<?> request) {
        if (!isEnabled(request)) {
            return;
        }
        if (request instanceof AppendRequest instance) {
            before(instance);
        } else if (request instanceof CarrierBucketConfigRequest instance) {
            before(instance);
        } else if (request instanceof CarrierGlobalConfigRequest instance) {
            before(instance);
        } else if (request instanceof DecrementRequest instance) {
            before(instance);
        } else if (request instanceof GetAndLockRequest instance) {
            before(instance);
        } else if (request instanceof GetAndTouchRequest instance) {
            before(instance);
        } else if (request instanceof GetCollectionIdRequest instance) {
            before(instance);
        } else if (request instanceof GetCollectionManifestRequest instance) {
            before(instance);
        } else if (request instanceof GetMetaRequest instance) {
            before(instance);
        } else if (request instanceof GetRequest instance && !(request instanceof ReplicaGetRequest)) {
            before(instance);
        } else if (request instanceof IncrementRequest instance) {
            before(instance);
        } else if (request instanceof InsertRequest instance) {
            before(instance);
        } else if (request instanceof KvPingRequest instance) {
            before(instance);
        } else if (request instanceof MultiObserveViaCasRequest instance) {
            before(instance);
        } else if (request instanceof NoopRequest instance) {
            before(instance);
        } else if (request instanceof ObserveViaCasRequest instance) {
            before(instance);
        } else if (request instanceof ObserveViaSeqnoRequest instance) {
            before(instance);
        } else if (request instanceof PrependRequest instance) {
            before(instance);
        } else if (request instanceof RemoveRequest instance) {
            before(instance);
        } else if (request instanceof ReplaceRequest instance) {
            before(instance);
        } else if (request instanceof ReplicaGetRequest instance) {
            before(instance);
        } else if (request instanceof SubdocGetRequest instance) {
            before(instance);
        } else if (request instanceof SubdocMutateRequest instance) {
            before(instance);
        } else if (request instanceof TouchRequest instance) {
            before(instance);
        } else if (request instanceof UnlockRequest instance) {
            before(instance);
        } else if (request instanceof UpsertRequest instance) {
            before(instance);
        } else if (request instanceof QueryRequest instance) {
            before(instance);
        } else if (request instanceof ServerSearchRequest instance) {
            before(instance);
        } else if (request instanceof ViewRequest instance) {
            before(instance);
        } else if (request instanceof AnalyticsRequest instance) {
            before(instance);
        }
    }

    protected boolean isEnabled(Request<?> request) {
        return true;
    }

    protected void before(AppendRequest request) {}

    protected void before(CarrierBucketConfigRequest request) {}

    protected void before(CarrierGlobalConfigRequest request) {}

    protected void before(DecrementRequest request) {}

    protected void before(GetAndLockRequest request) {}

    protected void before(GetAndTouchRequest request) {}

    protected void before(GetCollectionIdRequest request) {}

    protected void before(GetCollectionManifestRequest request) {}

    protected void before(GetMetaRequest request) {}

    protected void before(GetRequest request) {}

    protected void before(IncrementRequest request) {}

    protected void before(InsertRequest request) {}

    protected void before(KvPingRequest request) {}

    protected void before(MultiObserveViaCasRequest request) {}

    protected void before(NoopRequest request) {}

    protected void before(ObserveViaCasRequest request) {}

    protected void before(ObserveViaSeqnoRequest request) {}

    protected void before(PrependRequest request) {}

    protected void before(RemoveRequest request) {}

    protected void before(ReplaceRequest request) {}

    protected void before(ReplicaGetRequest request) {}

    protected void before(SubdocGetRequest request) {}

    protected void before(SubdocMutateRequest request) {}

    protected void before(TouchRequest request) {}

    protected void before(UnlockRequest request) {}

    protected void before(UpsertRequest request) {}

    protected void before(QueryRequest request) {}

    protected void before(ServerSearchRequest request) {}

    protected void before(ViewRequest request) {}

    protected void before(AnalyticsRequest request) {}

    protected String getKey(BaseKeyValueRequest<?> request) {
        return request.key().length == 0 ? null : new String(request.key(), StandardCharsets.UTF_8);
    }
}
