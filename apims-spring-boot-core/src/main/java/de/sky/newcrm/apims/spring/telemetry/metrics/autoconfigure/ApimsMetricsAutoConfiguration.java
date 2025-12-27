/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.telemetry.metrics.autoconfigure;

import de.sky.newcrm.apims.spring.environment.config.ApimsCoreProperties;
import de.sky.newcrm.apims.spring.telemetry.metrics.aspects.ApimsAroundMetricsListener;
import de.sky.newcrm.apims.spring.telemetry.metrics.core.ApimsMeterRegistry;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ApimsCoreProperties.class)
@SuppressWarnings({"java:S6212"})
public class ApimsMetricsAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ApimsMetricsAutoConfiguration.class);
    //    private final ApimsCoreProperties apimsProperties;

    public ApimsMetricsAutoConfiguration(
            //            ApimsCoreProperties apimsProperties
            ) {
        log.debug("[APIMS AUTOCONFIG] Aspects.");
        //        this.apimsProperties = apimsProperties;
    }

    //    @Bean
    //    @ConditionalOnMissingBean(name = "aspectsJacksonBuilder")
    //    public Jackson2ObjectMapperBuilder aspectsJacksonBuilder() {
    //        return DefaultJacksonObjectFactory.buildJackson2ObjectMapperBuilder();
    //    }

    //    @Bean
    //    @ConditionalOnMissingBean(name = "aspectsObjectMapper")
    //    public ObjectMapper aspectsObjectMapper(
    //            @Qualifier("aspectsJacksonBuilder") Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder) {
    //        return jackson2ObjectMapperBuilder.build();
    //    }

    //    @Bean
    //    @ConditionalOnMissingBean
    //    public ApimsAroundObjectMasker apimsAroundObjectMasker(
    //            @Value(
    //                            "${apims.aspects.serializer.mask-keys:password, newPassword, oldPassword,
    // new-password, old-password, pin, newPin, oldPin}")
    //                    String maskKeysCSV,
    //            @Value("${apims.aspects.serializer.additional-mask-keys:}") String additionalMaskKeysCSV,
    //            @Value("${apims.aspects.serializer.mask-value:___masked___}") String maskValue) {
    //        List<String> maskKeys =
    //                new ArrayList<>(Arrays.asList(StringUtils.tokenizeToStringArray(maskKeysCSV, ",", true, true)));
    //        maskKeys.addAll(Arrays.asList(StringUtils.tokenizeToStringArray(additionalMaskKeysCSV, ",", true, true)));
    //        return new ApimsAroundObjectMaskerDefaultImpl(maskKeys, maskValue);
    //    }

    //    @Bean
    //    @ConditionalOnMissingBean
    //    public ApimsAroundObjectSerializer apimsAroundObjectSerializer(
    //            @Qualifier("aspectsObjectMapper") ObjectMapper aspectsObjectMapper,
    //            ApimsAroundObjectMasker masker,
    //            List<ApimsAroundObjectSerializerTypeHandler> typeHandlers,
    //            @Value("${apims.aspects.serializer.default-max-characters:15000}") int defaultMaxCharacters) {
    //
    //        return new ApimsAroundObjectSerializerDefaultImpl(
    //                aspectsObjectMapper, masker, typeHandlers, defaultMaxCharacters);
    //    }

    //    @Bean
    //    @ConditionalOnMissingBean
    //    public ApimsAroundFlowContextListener apimsAroundFlowContextListener() {
    //        return new ApimsAroundFlowContextListener();
    //    }

    //    @Bean
    //    @ConditionalOnMissingBean
    //    @ConditionalOnProperty(
    //            prefix = "apims.aspects.listeners.kafka-template-synchronizer",
    //            name = "enabled",
    //            havingValue = "true",
    //            matchIfMissing = true)
    //    public ApimsAroundKafkaTemplateSynchronizer aroundKafkaTemplateSynchronizer() {
    //        return new ApimsAroundKafkaTemplateSynchronizer();
    //    }

    //    @Bean
    //    @ConditionalOnMissingBean
    //    @ConditionalOnProperty(
    //            prefix = "apims.aspects.listeners.logging",
    //            name = "enabled",
    //            havingValue = "true",
    //            matchIfMissing = true)
    //    public ApimsAroundLoggingListener apimsAroundLoggingListener(
    //            ApimsAroundObjectSerializer apimsAroundObjectSerializer,
    //            @Autowired(required = false)
    //                    ApimsRetryTopicConfigurationPropertiesMap apimsRetryTopicConfigurationPropertiesMap,
    //            @Value("${apims.aspects.listeners.logging.save-log-lines-as-span-tag:all}") String
    // saveLogLinesAsSpanTag) {
    //        return new ApimsAroundLoggingListener(apimsAroundObjectSerializer,
    // apimsRetryTopicConfigurationPropertiesMap);
    //    }

    //    @Bean
    //    @ConditionalOnMissingBean
    //    @ConditionalOnProperty(
    //            prefix = "apims.aspects.listeners.auth",
    //            name = "enabled",
    //            havingValue = "true",
    //            matchIfMissing = true)
    //    public ApimsAroundSecuredListener apimsAroundSecuredListener() {
    //        return new ApimsAroundSecuredListener();
    //    }

    //    @Bean
    //    @ConditionalOnMissingBean
    //    @ConditionalOnProperty(
    //            prefix = "apims.aspects.listeners.mdc",
    //            name = "enabled",
    //            havingValue = "true",
    //            matchIfMissing = true)
    //    public ApimsAroundMdcListener apimsAroundMdcListener() {
    //        return new ApimsAroundMdcListener(
    //                apimsProperties.getAspects().getListeners().getMdc().getRemoteFields());
    //    }

    //    @Bean
    //    @ConditionalOnMissingBean
    //    @ConditionalOnProperty(
    //            prefix = "apims.aspects.listeners.mdc",
    //            name = "enabled",
    //            havingValue = "true",
    //            matchIfMissing = true)
    //    public ApimsAroundMdcErrorListener apimsAroundMdcErrorListener() {
    //        return new ApimsAroundMdcErrorListener(apimsProperties.getApp().getIncidentMgmt());
    //    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            prefix = "apims.aspects.listeners.metrics",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true)
    @ConditionalOnExpression("'${apims.metrics.enabled:true}'.equals('true')")
    public ApimsAroundMetricsListener apimsAroundMetricsListener(
            ApimsMeterRegistry apimsMeterRegistry,
            @Value("${apims.aspects.listeners.metrics.ignored-components:}") String ignoredComponents) {
        Set<String> ignoredSet =
                new HashSet<>(Arrays.asList(StringUtils.tokenizeToStringArray(ignoredComponents, ",", true, true)));
        return new ApimsAroundMetricsListener(apimsMeterRegistry, ignoredSet);
    }
}
