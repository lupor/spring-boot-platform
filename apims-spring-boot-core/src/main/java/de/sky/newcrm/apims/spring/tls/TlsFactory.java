/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.tls;

import com.couchbase.client.core.deps.io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import de.sky.newcrm.apims.spring.exceptions.ApimsRuntimeException;
import org.springframework.util.StringUtils;

import javax.net.ssl.*;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;

@SuppressWarnings("java:S1192")
public class TlsFactory {

    private TlsFactory() {}

    @ApimsReportGeneratedHint
    public static SSLContext createTlsSSLContext(
            String trustStoreType,
            String trustStoreLocation,
            String trustStorePassword,
            String keyStoreType,
            String keyStoreLocation,
            String keyStorePassword) {
        TrustManagerFactory trustManagerFactory =
                createTrustManagerFactory(trustStoreType, trustStoreLocation, trustStorePassword);
        KeyManagerFactory keyManagerFactory = createKeyManagerFactory(keyStoreType, keyStoreLocation, keyStorePassword);
        TrustManager[] tm = trustManagerFactory.getTrustManagers();
        KeyManager[] km = keyManagerFactory == null ? null : keyManagerFactory.getKeyManagers();
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(km, tm, null);
            return context;
        } catch (Exception e) {
            throw new ApimsRuntimeException(
                    "Failed to create TLS ssl context " + trustStoreLocation + " of type " + trustStoreType, e);
        }
    }

    @ApimsReportGeneratedHint
    public static TrustManagerFactory createTrustManagerFactory(
            String trustStoreType, String trustStoreLocation, String trustStorePassword) {
        TrustManagerFactory trustManagerFactory;
        KeyStore keyStore = loadKeyStore(trustStoreType, trustStoreLocation, trustStorePassword);
        if (keyStore != null) {
            try {
                trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(keyStore);
            } catch (Exception e) {
                throw new ApimsRuntimeException(
                        "Failed to create TrustManagerFactory. trust store " + trustStoreLocation + " of type "
                                + trustStoreType,
                        e);
            }
        } else {
            trustManagerFactory = InsecureTrustManagerFactory.INSTANCE;
        }
        return trustManagerFactory;
    }

    @ApimsReportGeneratedHint
    public static KeyManagerFactory createKeyManagerFactory(
            String keyStoreType, String keyStoreLocation, String keyStorePassword) {

        KeyManagerFactory keyManagerFactory = null;
        KeyStore keyStore = loadKeyStore(keyStoreType, keyStoreLocation, keyStorePassword);
        if (keyStore != null) {
            try {
                keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                keyManagerFactory.init(keyStore, keyStorePassword != null ? keyStorePassword.toCharArray() : null);
            } catch (Exception e) {
                throw new ApimsRuntimeException(
                        "Failed to create KeyManagerFactory. key store " + keyStoreLocation + " of type "
                                + keyStoreType,
                        e);
            }
        }
        return keyManagerFactory;
    }

    @ApimsReportGeneratedHint
    public static KeyStore loadKeyStore(String keyStoreType, String keyStoreLocation, String keyStorePassword) {
        if (keyStoreLocation != null && keyStoreLocation.startsWith("file://")) {
            keyStoreLocation = keyStoreLocation.substring(7);
        }
        Path keyStoreLocationPath = StringUtils.hasLength(keyStoreLocation) ? Path.of(keyStoreLocation) : null;
        KeyStore keyStore = null;
        if (keyStoreLocationPath != null && Files.exists(keyStoreLocationPath)) {
            try (InputStream in = Files.newInputStream(keyStoreLocationPath)) {
                keyStore = KeyStore.getInstance(keyStoreType);
                char[] passwordChars = keyStorePassword != null ? keyStorePassword.toCharArray() : null;
                keyStore.load(in, passwordChars);
            } catch (Exception e) {
                throw new ApimsRuntimeException(
                        "Failed to load key store " + keyStoreLocation + " of type " + keyStoreType, e);
            }
        }
        return keyStore;
    }
}
