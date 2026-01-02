/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.oauth.validator;

import de.sky.newcrm.apims.spring.environment.core.ApimsSpringContext;
import de.sky.newcrm.apims.spring.web.core.oauth.entity.ApimsConfigKeySourceTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import java.util.Set;

@RequiredArgsConstructor
public abstract class ApimsConfigTokenValidator extends ApimsAbstractTokenValidator implements InitializingBean {

    protected final String rootConfigLocation;

    @Override
    public void afterPropertiesSet() throws Exception {
        String issuer = getConfiguredIssuer();
        ApimsConfigKeySourceTypeEnum keySourceType = getConfiguredKeySourceType();
        String keySourceValue = getConfiguredKeySourceValue();
        boolean explicitAudienceCheck = getConfiguredExplicitAudienceCheck();
        boolean validateExpirationTime = getConfiguredValidateExpirationTime();
        String validAudiences = getConfiguredValidAudiences();
        setKeySource(getTokenKeySourceLoader().getKeySource(keySourceType, keySourceValue));
        getIssuers().addAll(Set.of(StringUtils.tokenizeToStringArray(issuer, ",")));
        setExplicitAudienceCheck(explicitAudienceCheck);
        getValidAudiences().addAll(Set.of(StringUtils.tokenizeToStringArray(validAudiences, ",")));
        setValidateExpirationTime(validateExpirationTime);
    }

    protected String getConfiguredIssuer() {
        return getConfigurationValue("issuer", "");
    }

    protected ApimsConfigKeySourceTypeEnum getConfiguredKeySourceType() {
        return ApimsConfigKeySourceTypeEnum.valueOf(
                getConfigurationValue("key-source-type", ApimsConfigKeySourceTypeEnum.RESOURCE_LOCATION.name()));
    }

    protected String getConfiguredKeySourceValue() {
        return getConfigurationValue("key-source-value", "");
    }

    protected boolean getConfiguredExplicitAudienceCheck() {
        return Boolean.parseBoolean(getConfigurationValue("explicit-audience-check", "false"));
    }

    protected boolean getConfiguredValidateExpirationTime() {
        return Boolean.parseBoolean(getConfigurationValue("validate-expiration-time", "false"));
    }

    protected String getConfiguredValidAudiences() {
        return getConfigurationValue("valid-audiences", "");
    }

    protected String getConfigurationValue(String key, String defaultValue) {
        return ApimsSpringContext.getProperty(rootConfigLocation + "." + key, defaultValue);
    }
}
