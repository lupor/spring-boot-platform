/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.web.client;

import org.apache.hc.core5.ssl.TrustStrategy;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class ApimsTrustAllCertificatesStrategy implements TrustStrategy {

    @Override
    public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        return true;
    }
}
