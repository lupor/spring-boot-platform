/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.web.client;

import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.TrustSelfSignedStrategy;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.apache.hc.core5.util.TimeValue;

import javax.net.ssl.HostnameVerifier;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.concurrent.TimeUnit;

@Slf4j
@SuppressWarnings("all")
public class ApimsPoolingHttpClientConnectionManager extends PoolingHttpClientConnectionManager {

    public ApimsPoolingHttpClientConnectionManager(
            Registry<ConnectionSocketFactory> socketFactoryRegistry, long timeToLiveSec) {
        super(socketFactoryRegistry, null, TimeValue.of(timeToLiveSec, TimeUnit.SECONDS), null);
    }

    public static ApimsPoolingHttpClientConnectionManagerBuilder builder() {
        return new ApimsPoolingHttpClientConnectionManagerBuilder();
    }

    public static class ApimsPoolingHttpClientConnectionManagerBuilder {

        private SSLConnectionSocketFactory httpsConnectionSocketFactory;
        private ConnectionSocketFactory httpConnectionSocketFactory;
        private SSLContextBuilder sslContextBuilder;
        private KeyStore sslContextTruststore;
        private TrustStrategy sslContextTrustStrategy;
        private HostnameVerifier hostnameVerifier;
        private long connectionTimeToLiveSecs = 600;
        private int maxTotalConnections = 180;
        private int defaultMaxConnectionsPerRoute = 30;

        protected ApimsPoolingHttpClientConnectionManagerBuilder() {}

        @ApimsReportGeneratedHint
        public SSLConnectionSocketFactory getHttpsConnectionSocketFactory() {
            return httpsConnectionSocketFactory;
        }

        @ApimsReportGeneratedHint
        public ApimsPoolingHttpClientConnectionManagerBuilder setHttpsConnectionSocketFactory(
                SSLConnectionSocketFactory httpsConnectionSocketFactory) {
            this.httpsConnectionSocketFactory = httpsConnectionSocketFactory;
            return this;
        }

        @ApimsReportGeneratedHint
        public ConnectionSocketFactory getHttpConnectionSocketFactory() {
            return httpConnectionSocketFactory;
        }

        @ApimsReportGeneratedHint
        public ApimsPoolingHttpClientConnectionManagerBuilder setHttpConnectionSocketFactory(
                ConnectionSocketFactory httpConnectionSocketFactory) {
            this.httpConnectionSocketFactory = httpConnectionSocketFactory;
            return this;
        }

        @ApimsReportGeneratedHint
        public SSLContextBuilder getSslContextBuilder() {
            return sslContextBuilder;
        }

        @ApimsReportGeneratedHint
        public ApimsPoolingHttpClientConnectionManagerBuilder setSslContextBuilder(
                SSLContextBuilder sslContextBuilder) {
            this.sslContextBuilder = sslContextBuilder;
            return this;
        }

        @ApimsReportGeneratedHint
        public KeyStore getSslContextTruststore() {
            return sslContextTruststore;
        }

        @ApimsReportGeneratedHint
        public ApimsPoolingHttpClientConnectionManagerBuilder setSslContextTruststore(KeyStore sslContextTruststore) {
            this.sslContextTruststore = sslContextTruststore;
            return this;
        }

        @ApimsReportGeneratedHint
        public TrustStrategy getSslContextTrustStrategy() {
            return sslContextTrustStrategy;
        }

        @ApimsReportGeneratedHint
        public ApimsPoolingHttpClientConnectionManagerBuilder setSslContextTrustStrategy(
                TrustStrategy sslContextTrustStrategy) {
            this.sslContextTrustStrategy = sslContextTrustStrategy;
            return this;
        }

        @ApimsReportGeneratedHint
        public HostnameVerifier getHostnameVerifier() {
            return hostnameVerifier;
        }

        @ApimsReportGeneratedHint
        public ApimsPoolingHttpClientConnectionManagerBuilder setHostnameVerifier(HostnameVerifier hostnameVerifier) {
            this.hostnameVerifier = hostnameVerifier;
            return this;
        }

        @ApimsReportGeneratedHint
        public long getConnectionTimeToLiveSecs() {
            return connectionTimeToLiveSecs;
        }

        public ApimsPoolingHttpClientConnectionManagerBuilder setConnectionTimeToLiveSecs(
                long connectionTimeToLiveSecs) {
            this.connectionTimeToLiveSecs = connectionTimeToLiveSecs;
            return this;
        }

        @ApimsReportGeneratedHint
        public int getMaxTotalConnections() {
            return maxTotalConnections;
        }

        public ApimsPoolingHttpClientConnectionManagerBuilder setMaxTotalConnections(int maxTotalConnections) {
            this.maxTotalConnections = maxTotalConnections;
            return this;
        }

        public int getDefaultMaxConnectionsPerRoute() {
            return defaultMaxConnectionsPerRoute;
        }

        public ApimsPoolingHttpClientConnectionManagerBuilder setDefaultMaxConnectionsPerRoute(
                int defaultMaxConnectionsPerRoute) {
            this.defaultMaxConnectionsPerRoute = defaultMaxConnectionsPerRoute;
            return this;
        }

        public ApimsPoolingHttpClientConnectionManager build() throws GeneralSecurityException {

            if (httpsConnectionSocketFactory == null) {
                if (sslContextBuilder == null) {
                    sslContextBuilder = new SSLContextBuilder();
                    if (sslContextTrustStrategy == null) {
                        sslContextTrustStrategy = new TrustSelfSignedStrategy();
                    }
                    sslContextBuilder.loadTrustMaterial(sslContextTruststore, sslContextTrustStrategy);
                }
                httpsConnectionSocketFactory = hostnameVerifier == null
                        ? new SSLConnectionSocketFactory(sslContextBuilder.build())
                        : new SSLConnectionSocketFactory(sslContextBuilder.build(), hostnameVerifier);
            }
            if (httpConnectionSocketFactory == null) {
                httpConnectionSocketFactory = new PlainConnectionSocketFactory();
            }
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("https", httpsConnectionSocketFactory)
                    .register("http", httpConnectionSocketFactory)
                    .build();

            ApimsPoolingHttpClientConnectionManager poolingConnectionManager =
                    new ApimsPoolingHttpClientConnectionManager(socketFactoryRegistry, connectionTimeToLiveSecs);
            poolingConnectionManager.setMaxTotal(maxTotalConnections);
            poolingConnectionManager.setDefaultMaxPerRoute(defaultMaxConnectionsPerRoute);
            return poolingConnectionManager;
        }
    }
}
