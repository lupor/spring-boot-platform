/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.oauth.builder;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3.ObjectMapperUtils;
import de.sky.newcrm.apims.spring.utils.AssertUtils;
import de.sky.newcrm.apims.spring.web.core.entities.SecretEntity;
import de.sky.newcrm.apims.spring.web.core.oauth.entity.ApimsJwsAlgorithmEnum;
import de.sky.newcrm.apims.spring.web.core.oauth.entity.Model;
import de.sky.newcrm.apims.spring.web.core.oauth.exception.ApimsOAuthCreateTokenException;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;
import java.util.Set;

public class ApimsJwtBuilder {

    public static final ApimsJwsAlgorithmEnum DEFAULT_JWS_ALGORITHM = ApimsJwsAlgorithmEnum.HS256;
    public static final String DEFAULT_AUDIENCE = "ApimsService";
    public static final long DEFAULT_EXPIRATION_OFFSET_MILLIS = 1000L * 60L * 60L * 12L;
    public static final String DEFAULT_ISSUER = "de.sky.apims";
    public static final String DEFAULT_NAME = ApimsJwtBuilder.class.getSimpleName();
    public static final String DEFAULT_SUBJECT = "ApimsService";

    public String createToken(SecretEntity secret, Map<String, Object> data) throws ApimsOAuthCreateTokenException {
        return createToken(secret, resolveDefaultJwsAlgorithmEnum(secret), data);
    }

    public String createToken(SecretEntity secret, ApimsJwsAlgorithmEnum algorithmEnum, Map<String, Object> data)
            throws ApimsOAuthCreateTokenException {
        AssertUtils.notNullCheck("algorithm", algorithmEnum);
        JWSAlgorithm jwsAlgorithm = JWSAlgorithm.parse(algorithmEnum.name());
        AssertUtils.notNullCheck("jwsAlgorithm", jwsAlgorithm);
        JWSHeader header =
                new JWSHeader.Builder(jwsAlgorithm).type(JOSEObjectType.JWT).build();
        return createToken(secret, header, data);
    }

    public String createToken(SecretEntity secret, JWSHeader header, Map<String, Object> data)
            throws ApimsOAuthCreateTokenException {

        JWSSigner signer = createSigner(secret);
        AssertUtils.notNullCheck("header", header);
        AssertUtils.notNullCheck("header.algorithm", header.getAlgorithm());
        AssertUtils.notNullCheck("header.type", header.getType());
        resolveDefaultData(data);

        String token;
        JWSObject jwsObject = new JWSObject(header, new Payload(ObjectMapperUtils.writeValueAsString(data)));
        try {
            JWSAlgorithm algorithm = header.getAlgorithm();
            Set<JWSAlgorithm> compatibleAlgorithms = signer.supportedJWSAlgorithms();
            if (!compatibleAlgorithms.contains(algorithm)) {
                throw new ApimsOAuthCreateTokenException(
                        "[Assertion failed] - The %s algorithm is not supported by the %s. Please check your secret length! Supported algorithms: [%s]"
                                .formatted(
                                        algorithm.getName(),
                                        signer.getClass().getSimpleName(),
                                        StringUtils.collectionToCommaDelimitedString(compatibleAlgorithms)));
            }

            jwsObject.sign(signer);
            token = jwsObject.serialize();
        } catch (JOSEException e) {
            throw new ApimsOAuthCreateTokenException(e);
        }
        return token;
    }

    protected ApimsJwsAlgorithmEnum resolveDefaultJwsAlgorithmEnum(SecretEntity secret) {
        JWSSigner signer = createSigner(secret);
        Set<JWSAlgorithm> compatibleAlgorithms = signer.supportedJWSAlgorithms();
        if (compatibleAlgorithms.contains(JWSAlgorithm.HS512)) {
            return ApimsJwsAlgorithmEnum.HS512;
        } else if (compatibleAlgorithms.contains(JWSAlgorithm.HS384)) {
            return ApimsJwsAlgorithmEnum.HS384;
        } else if (compatibleAlgorithms.contains(JWSAlgorithm.HS256)) {
            return ApimsJwsAlgorithmEnum.HS256;
        }
        return DEFAULT_JWS_ALGORITHM;
    }

    protected JWSSigner createSigner(SecretEntity secret) throws ApimsOAuthCreateTokenException {
        AssertUtils.notNullCheck("secret", secret);
        AssertUtils.notNullCheck("secret.value", secret.getSecureValue());
        try {
            return new MACSigner(secret.getSecureValue());
        } catch (KeyLengthException e) {
            throw new ApimsOAuthCreateTokenException(e);
        }
    }

    protected void resolveDefaultData(Map<String, Object> data) {

        AssertUtils.notNullCheck("data", data);
        Date issuedAt = new Date();
        Date expirationDate = new Date(issuedAt.getTime() + DEFAULT_EXPIRATION_OFFSET_MILLIS);

        data.putIfAbsent(Model.JwtClaimNames.AUDIENCE, DEFAULT_AUDIENCE);
        data.putIfAbsent(Model.JwtClaimNames.EXPIRATION_TIME, expirationDate.getTime());
        data.putIfAbsent(Model.JwtClaimNames.ISSUER, DEFAULT_ISSUER);
        data.putIfAbsent(Model.JwtClaimNames.ISSUED_AT, issuedAt.getTime());
        data.putIfAbsent(Model.JwtClaimNames.NAME, DEFAULT_NAME);
        data.putIfAbsent(Model.JwtClaimNames.SUBJECT, DEFAULT_SUBJECT);
    }
}
