/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.oauth.validator;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.SecurityContext;
import com.veracode.annotation.FilePathCleanser;
import de.sky.newcrm.apims.spring.utils.AssertUtils;
import de.sky.newcrm.apims.spring.utils.FunctionUtils;
import de.sky.newcrm.apims.spring.web.core.oauth.entity.ApimsConfigKeySourceTypeEnum;
import de.sky.newcrm.apims.spring.web.core.oauth.entity.ApimsKeySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

public class ApimsTokenKeySourceLoader {

    public ApimsKeySource refreshKeySource(ApimsKeySource source) {
        AssertUtils.notNullCheck("source", source);
        ApimsKeySource keySource = getKeySource(source.getKeySourceType(), source.getKeySourceValue());
        if (source.getAdditionalParams() != null) {
            keySource.getAdditionalParams().putAll(source.getAdditionalParams());
        }
        return keySource;
    }

    @FilePathCleanser
    public ApimsKeySource getKeySource(ApimsConfigKeySourceTypeEnum keySourceType, String keySourceValue) {
        AssertUtils.notNullCheck("keySourceType", keySourceType);
        JWKSource<SecurityContext> jwtSource = null;
        if (!ApimsConfigKeySourceTypeEnum.TRUSTED.equals(keySourceType)) {
            AssertUtils.hasLengthCheck("keySourceValue", keySourceValue);
        }
        if (ApimsConfigKeySourceTypeEnum.RESOURCE_LOCATION.equals(keySourceType)) {
            jwtSource = getKeySource(new DefaultResourceLoader().getResource(keySourceValue));
        } else if (ApimsConfigKeySourceTypeEnum.URL_LOCATION.equals(keySourceType)) {
            jwtSource = getKeySource(FunctionUtils.execute(() -> new URI(keySourceValue).toURL()));
        } else if (ApimsConfigKeySourceTypeEnum.FILE_LOCATION.equals(keySourceType)) {
            jwtSource = getKeySource(new File(keySourceValue));
        } else if (ApimsConfigKeySourceTypeEnum.BYTE_SECRET_BASE_64.equals(keySourceType)) {
            jwtSource = getKeySource(Base64.getDecoder().decode(keySourceValue.getBytes(StandardCharsets.UTF_8)));
        } else if (ApimsConfigKeySourceTypeEnum.BYTE_SECRET.equals(keySourceType)) {
            jwtSource = getKeySource(keySourceValue.getBytes(StandardCharsets.UTF_8));
        }
        return ApimsKeySource.builder()
                .keySourceType(keySourceType)
                .keySourceValue(keySourceValue)
                .jwtSource(jwtSource)
                .lastRefresh(new Date())
                .build();
    }

    public JWKSource<SecurityContext> getKeySource(final byte[] secret) {
        return createKeySource(secret);
    }

    public JWKSource<SecurityContext> getKeySource(final Resource keySource) {
        return FunctionUtils.execute(() -> {
            try (InputStream inputStream = keySource.getInputStream()) {
                return getKeySource(inputStream);
            }
        });
    }

    public JWKSource<SecurityContext> getKeySource(final InputStream inputStream) {
        return createKeySource(FunctionUtils.execute(() -> JWKSet.load(inputStream)));
    }

    public JWKSource<SecurityContext> getKeySource(final URL url) {
        return createKeySource(url);
    }

    public JWKSource<SecurityContext> getKeySource(final File file) {
        return createKeySource(FunctionUtils.execute(() -> JWKSet.load(file)));
    }

    protected JWKSource<SecurityContext> createKeySource(JWKSet jwkSet) {
        return new ImmutableJWKSet<>(jwkSet);
    }

    protected JWKSource<SecurityContext> createKeySource(final byte[] secret) {
        return new ImmutableSecret<>(secret);
    }

    protected JWKSource<SecurityContext> createKeySource(final URL url) {
        return JWKSourceBuilder.create(url).build();
    }
}
