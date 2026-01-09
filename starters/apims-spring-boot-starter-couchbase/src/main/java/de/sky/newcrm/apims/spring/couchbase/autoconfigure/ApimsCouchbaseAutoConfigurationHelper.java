/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.couchbase.autoconfigure;

import com.couchbase.client.core.env.SecurityConfig;
import com.couchbase.client.java.query.QueryScanConsistency;
import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import de.sky.newcrm.apims.spring.couchbase.core.tls.TlsFactory;

import javax.net.ssl.TrustManagerFactory;

public class ApimsCouchbaseAutoConfigurationHelper {

    private ApimsCouchbaseAutoConfigurationHelper() {}

    static QueryScanConsistency getDefaultQueryScanConsistency() {
        return null;
    }

    @ApimsReportGeneratedHint
    static void applySecurityConfig(
            SecurityConfig.Builder config,
            boolean tlsEnabled,
            boolean tlsHostnameVerificationEnabled,
            String tlsTrustStoreType,
            String tlsTrustStoreLocation,
            String tlsTrustStorePassword) {
        if (!tlsEnabled) {
            config.enableTls(false);
        } else {
            TrustManagerFactory trustManagerFactory = TlsFactory.createTrustManagerFactory(
                    tlsTrustStoreType, tlsTrustStoreLocation, tlsTrustStorePassword);
            config.enableTls(true)
                    .enableHostnameVerification(tlsHostnameVerificationEnabled)
                    .trustManagerFactory(trustManagerFactory);
        }
    }
}
