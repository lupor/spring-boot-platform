/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.couchbase.core;


import static com.couchbase.client.java.ClusterOptions.clusterOptions;

import com.couchbase.client.core.Core;
import com.couchbase.client.core.endpoint.http.CoreCommonOptions;
import com.couchbase.client.core.endpoint.http.CoreHttpClient;
import com.couchbase.client.core.endpoint.http.CoreHttpPath;
import com.couchbase.client.core.env.Authenticator;
import com.couchbase.client.core.msg.RequestTarget;
import com.couchbase.client.core.util.CoreAsyncUtils;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;
import com.couchbase.client.java.env.ClusterEnvironment;
import com.couchbase.client.java.kv.MutateInOptions;
import com.couchbase.client.java.kv.MutateInSpec;
import com.couchbase.client.java.query.QueryResult;

import java.nio.charset.StandardCharsets;
import java.util.*;

import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3.ObjectMapperUtils;
import de.sky.newcrm.apims.spring.telemetry.logging.core.ApimsAroundLoggingListenerSuppress;
import org.springframework.data.couchbase.CouchbaseClientFactory;

public class ApimsCouchbaseNativeEndpoint {

    private final String connectionString;
    private final Authenticator authenticator;
    private final ClusterEnvironment couchbaseClusterEnvironment;
    private final Object clusterLock = new Object();
    private boolean mocksEnabled;
    private Cluster cluster = null;

    public ApimsCouchbaseNativeEndpoint(
            String connectionString, Authenticator authenticator, ClusterEnvironment couchbaseClusterEnvironment) {
        this.connectionString = connectionString;
        this.authenticator = authenticator;
        this.couchbaseClusterEnvironment = couchbaseClusterEnvironment;
    }

    @ApimsAroundLoggingListenerSuppress(suppressMethodCall = true)
    public void disconnect() {
        if (cluster != null) {
            cluster.disconnect();
        }
    }

    @ApimsAroundLoggingListenerSuppress(suppressMethodCall = true)
    public ClusterEnvironment getCouchbaseClusterEnvironment() {
        return couchbaseClusterEnvironment;
    }

    public boolean isMocksEnabled() {
        return mocksEnabled;
    }

    public void setMocksEnabled(boolean mocksEnabled) {
        this.mocksEnabled = mocksEnabled;
    }

    @ApimsAroundLoggingListenerSuppress(suppressMethodCall = true)
    @SuppressWarnings({"java:S2168"})
    public Cluster getCluster() {
        if (cluster == null) {
            synchronized (clusterLock) {
                if (cluster == null) {
                    cluster = connect(
                            connectionString, clusterOptions(authenticator).environment(couchbaseClusterEnvironment));
                }
            }
        }
        return cluster;
    }

    @ApimsAroundLoggingListenerSuppress(suppressMethodCall = true)
    public void mutateIn(
            CouchbaseClientFactory couchbaseClientFactory, String collectionName, String id, List<MutateInSpec> specs) {
        couchbaseClientFactory
                .getCollection(collectionName)
                .mutateIn(id, specs, MutateInOptions.mutateInOptions().preserveExpiry(true));
    }

    public QueryResult query(String statement) {
        return getCluster().query(statement);
    }

    @ApimsAroundLoggingListenerSuppress(suppressMethodCall = true)
    @SuppressWarnings({"unchecked", "java:S135"})
    @ApimsReportGeneratedHint
    public List<String> loadDocumentIds(CouchbaseClientFactory couchbaseClientFactory, String collectionName) {
        final long limit = 10000L;
        CoreCommonOptions options = CoreCommonOptions.DEFAULT;
        String template =
                "/pools/default/buckets/" + couchbaseClientFactory.getBucket().name()
                        + "/scopes/" + couchbaseClientFactory.getScope().name()
                        + "/collections/" + collectionName
                        + "/docs?include_docs=false&limit=" + limit;
        Set<String> docIds = new HashSet<>();
        long skip = 0;
        while (true) {
            String url = template + "&skip=" + skip;
            CoreHttpPath coreHttpPath = CoreHttpPath.path(url);
            Core core = getCluster().async().core();
            CoreHttpClient httpClient = core.httpClient(RequestTarget.manager());
            String payload = CoreAsyncUtils.block(httpClient
                    .get(coreHttpPath, options)
                    .build()
                    .exec(core)
                    .thenApply(response -> new String(response.content(), StandardCharsets.UTF_8)));
            Map<String, Object> response = ObjectMapperUtils.readMap(payload);
            List<Map<String, Object>> rows = (List<Map<String, Object>>) response.get("rows");
            if (rows == null || rows.isEmpty()) {
                break;
            } else {
                for (Map<String, Object> row : rows) {
                    Object id = row.get("id");
                    if (id != null) {
                        docIds.add(String.valueOf(id));
                    }
                }
                if (rows.size() < limit) {
                    break;
                }
                skip += limit;
            }
        }
        List<String> idList = new ArrayList<>(docIds);
        idList.sort(String::compareTo);
        return idList;
    }

    @ApimsReportGeneratedHint
    protected Cluster connect(final String connectionString, final ClusterOptions options) {
        return Cluster.connect(connectionString, options);
    }
}
