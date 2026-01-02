/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.oauth.validator;

import com.nimbusds.jose.Header;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.*;
import de.sky.newcrm.apims.spring.utils.DateTimeUtc;
import de.sky.newcrm.apims.spring.web.core.oauth.entity.ApimsConfigKeySourceTypeEnum;
import de.sky.newcrm.apims.spring.web.core.oauth.entity.ApimsKeySource;
import de.sky.newcrm.apims.spring.web.core.oauth.exception.ApimsOAuthTokenNotValidException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public abstract class ApimsAbstractTokenValidator implements ApimsTokenValidator {

    private static final long DEFAULT_KEY_SOURCE_INVALIDATION_TIMEOUT_SECONDS = 60L * 60L * 12L;
    private final Set<String> issuers = new HashSet<>();
    private ApimsKeySource keySource;
    private boolean explicitAudienceCheck;
    private final Set<String> validAudiences = new HashSet<>();
    private boolean validateExpirationTime;

    @Autowired
    @Getter
    private ApimsTokenKeySourceLoader tokenKeySourceLoader;

    protected ApimsAbstractTokenValidator() {
        this(new HashSet<>(), null);
    }

    protected ApimsAbstractTokenValidator(Set<String> issuers) {
        this(issuers, null);
    }

    protected ApimsAbstractTokenValidator(Set<String> issuers, ApimsKeySource keySource) {
        this.issuers.addAll(issuers);
        this.keySource = keySource;
        this.explicitAudienceCheck = false;
    }

    protected Set<String> getIssuers() {
        return issuers;
    }

    protected ApimsKeySource getKeySource() {
        if (keySource != null && !isKeySourceValid(keySource)) {
            refreshKeySource();
        }
        return keySource;
    }

    protected synchronized void refreshKeySource() {
        if (keySource != null && !isKeySourceValid(keySource)) {
            this.keySource = getTokenKeySourceLoader().refreshKeySource(keySource);
        }
    }

    protected void setKeySource(ApimsKeySource keySource) {
        this.keySource = keySource;
    }

    protected boolean isExplicitAudienceCheck() {
        return explicitAudienceCheck;
    }

    protected void setExplicitAudienceCheck(boolean explicitAudienceCheck) {
        this.explicitAudienceCheck = explicitAudienceCheck;
    }

    protected Set<String> getValidAudiences() {
        return validAudiences;
    }

    protected boolean isValidateExpirationTime() {
        return validateExpirationTime;
    }

    protected void setValidateExpirationTime(boolean validateExpirationTime) {
        this.validateExpirationTime = validateExpirationTime;
    }

    protected String getIssuer(JWT token) {
        if (token == null) {
            return null;
        }
        try {
            return token.getJWTClaimsSet().getIssuer();
        } catch (ParseException ignore) {
            // ignore
        }
        return null;
    }

    @Override
    public boolean canValidate(JWT token) {
        if (token == null) {
            return false;
        }
        final String issuer = getIssuer(token);
        boolean flag = issuers.contains(issuer);
        if (!flag && issuer != null) {
            Optional<String> parentIssuer =
                    issuers.stream().filter(issuer::startsWith).findFirst();
            if (parentIssuer.isPresent()) {
                flag = true;
                issuers.add(issuer);
            }
        }
        return flag;
    }

    @Override
    public JWTClaimsSet validate(JWT token, final Set<String> runtimeAudiences)
            throws ApimsOAuthTokenNotValidException {
        if (token == null) {
            return null;
        }
        JWTClaimsSet jwtClaimsSet = null;
        try {
            jwtClaimsSet = token.getJWTClaimsSet();
            validateExpirationTime(jwtClaimsSet);
            final ConfigurableJWTProcessor<SecurityContext> validator = createProzessor(token, runtimeAudiences);
            if (validator != null) {
                jwtClaimsSet = validator.process(token, null);
                validator.getJWTClaimsSetVerifier().verify(jwtClaimsSet, null);
            }
        } catch (ParseException | BadJOSEException | JOSEException e) {
            throw new ApimsOAuthTokenNotValidException(e);
        }
        return jwtClaimsSet;
    }

    protected void validateExpirationTime(JWTClaimsSet jwtClaimsSet) throws ApimsOAuthTokenNotValidException {
        if (validateExpirationTime) {
            Date expirationTime = jwtClaimsSet.getExpirationTime();
            if (expirationTime != null && expirationTime.before(new Date())) {
                throw new ApimsOAuthTokenNotValidException("Token is expired! " + DateTimeUtc.format(expirationTime));
            }
        }
    }

    protected ConfigurableJWTProcessor<SecurityContext> createProzessor(JWT token, final Set<String> runtimeAudiences) {
        final JWSKeySelector<SecurityContext> keySelector = createKeySelector(token);
        if (keySelector == null) {
            return null;
        }
        final JWTClaimsSetVerifier<SecurityContext> claimVerifier = createClaimVerifier(token, runtimeAudiences);
        final ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        jwtProcessor.setJWSKeySelector(keySelector);
        jwtProcessor.setJWTClaimsSetVerifier(claimVerifier);
        return jwtProcessor;
    }

    protected boolean isKeySourceValid(ApimsKeySource keySource) {
        return isKeySourceValid(keySource, DEFAULT_KEY_SOURCE_INVALIDATION_TIMEOUT_SECONDS);
    }

    protected boolean isKeySourceValid(ApimsKeySource keySource, long invalidationTimeoutSeconds) {
        if (keySource == null) {
            return false;
        } else if (ApimsConfigKeySourceTypeEnum.TRUSTED.equals(keySource.getKeySourceType())) {
            return true;
        } else if (keySource.getJwtSource() == null) {
            return false;
        } else if (ApimsConfigKeySourceTypeEnum.URL_LOCATION.equals(keySource.getKeySourceType())) {
            return keySource.getLastRefresh() != null
                    && (keySource.getLastRefresh().getTime() + (invalidationTimeoutSeconds * 1000L))
                            > System.currentTimeMillis();
        }
        return true;
    }

    protected JWSKeySelector<SecurityContext> createKeySelector(JWT token) {
        ApimsKeySource ks = getKeySource();
        if (ks == null || ks.getJwtSource() == null) {
            return null;
        }
        JWSAlgorithm jwsAlgorithm = (JWSAlgorithm) token.getHeader().getAlgorithm();
        return new JWSVerificationKeySelector<>(jwsAlgorithm, ks.getJwtSource());
    }

    @SuppressWarnings("java:S1172")
    protected JWTClaimsSetVerifier<SecurityContext> createClaimVerifier(JWT token, final Set<String> runtimeAudiences) {
        return new DefaultJWTClaimsVerifier<>(null, null) {
            @Override
            public void verify(JWTClaimsSet claimsSet, SecurityContext ctx) throws BadJWTException {
                super.verify(claimsSet, ctx);
                verifyAudience(claimsSet, runtimeAudiences, ctx);
            }
        };
    }

    @SuppressWarnings("java:S1172")
    protected void verifyAudience(JWTClaimsSet claimsSet, final Set<String> runtimeAudiences, SecurityContext ctx)
            throws BadJWTException {
        if (explicitAudienceCheck) {
            final Set<String> valid = new HashSet<>(getValidAudiences());
            if (runtimeAudiences != null) {
                valid.addAll(runtimeAudiences);
            }
            Optional<String> matchedAudience =
                    claimsSet.getAudience().stream().filter(valid::contains).findFirst();
            if (matchedAudience.isEmpty()) {
                throw new BadJWTException("Invalid token audience. Provided value " + claimsSet.getAudience()
                        + " does not match neither client-id nor AppIdUri.");
            }
        }
    }

    protected String getHeaderKeyId(Header header) {
        if (header instanceof JWSHeader jwsHeader) {
            return jwsHeader.getKeyID();
        }
        return null;
    }
}
