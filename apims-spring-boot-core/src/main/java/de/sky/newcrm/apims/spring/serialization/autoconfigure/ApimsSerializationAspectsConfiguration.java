/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.autoconfigure;

import de.sky.newcrm.apims.spring.environment.config.ApimsCoreProperties;
import de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3.DefaultJacksonObjectFactory;
import de.sky.newcrm.apims.spring.serialization.core.masker.ApimsAroundObjectMasker;
import de.sky.newcrm.apims.spring.serialization.core.masker.ApimsAroundObjectMaskerDefaultImpl;
import de.sky.newcrm.apims.spring.serialization.core.serializer.ApimsAroundObjectSerializer;
import de.sky.newcrm.apims.spring.serialization.core.serializer.ApimsAroundObjectSerializerDefaultImpl;
import de.sky.newcrm.apims.spring.serialization.core.serializer.ApimsAroundObjectSerializerTypeHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ApimsCoreProperties.class)
@SuppressWarnings({"java:S6212"})
public class ApimsSerializationAspectsConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ApimsSerializationAspectsConfiguration.class);

    public ApimsSerializationAspectsConfiguration(ApimsCoreProperties apimsCoreProperties) {
        log.debug("[APIMS AUTOCONFIG] Serialization Aspects.");
    }

    @Bean
    @ConditionalOnMissingBean(name = "aspectsJacksonBuilder")
    public JsonMapper.Builder aspectsJacksonBuilder() {
        return DefaultJacksonObjectFactory.createDefaultJsonMapperBuilder();
    }

    @Bean
    @ConditionalOnMissingBean(name = "aspectsObjectMapper")
    public ObjectMapper aspectsObjectMapper(
            @Qualifier("aspectsJacksonBuilder") JsonMapper.Builder jacksonObjectMapperBuilder) {
        return jacksonObjectMapperBuilder.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public ApimsAroundObjectMasker apimsAroundObjectMasker(
            @Value(
                            "${apims.aspects.serializer.mask-keys:password, newPassword, oldPassword, new-password, old-password, pin, newPin, oldPin}")
                    String maskKeysCSV,
            @Value("${apims.aspects.serializer.additional-mask-keys:}") String additionalMaskKeysCSV,
            @Value("${apims.aspects.serializer.mask-value:___masked___}") String maskValue) {
        List<String> maskKeys =
                new ArrayList<>(Arrays.asList(StringUtils.tokenizeToStringArray(maskKeysCSV, ",", true, true)));
        maskKeys.addAll(Arrays.asList(StringUtils.tokenizeToStringArray(additionalMaskKeysCSV, ",", true, true)));
        return new ApimsAroundObjectMaskerDefaultImpl(maskKeys, maskValue);
    }

    @Bean
    @ConditionalOnMissingBean
    public ApimsAroundObjectSerializer apimsAroundObjectSerializer(
            @Qualifier("aspectsObjectMapper") ObjectMapper aspectsObjectMapper,
            ApimsAroundObjectMasker masker,
            List<ApimsAroundObjectSerializerTypeHandler> typeHandlers,
            @Value("${apims.aspects.serializer.default-max-characters:15000}") int defaultMaxCharacters) {

        return new ApimsAroundObjectSerializerDefaultImpl(
                aspectsObjectMapper, masker, typeHandlers, defaultMaxCharacters);
    }
}
