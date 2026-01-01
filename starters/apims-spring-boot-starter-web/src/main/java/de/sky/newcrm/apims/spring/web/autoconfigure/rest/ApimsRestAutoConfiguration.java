/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.autoconfigure.rest;

import de.sky.newcrm.apims.spring.exceptions.ApimsRestClientBusinessExceptionHandler;
import de.sky.newcrm.apims.spring.web.config.ApimsRestConfig;
import de.sky.newcrm.apims.spring.web.core.http.converter.*;
import de.sky.newcrm.apims.spring.web.core.rest.*;
import de.sky.newcrm.apims.spring.web.core.web.client.ApimsHttpComponentsClientHttpRequestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.OrderComparator;
import org.springframework.vault.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "apims.rest", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(ApimsRestConfig.class)
@SuppressWarnings({"java:S6212"})
public class ApimsRestAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ApimsRestAutoConfiguration.class);
    private final ApimsRestConfig apimsRestConfig;

    public ApimsRestAutoConfiguration(ApimsRestConfig apimsRestConfig) {
        log.debug("[APIMS AUTOCONFIG] Rest.");
        this.apimsRestConfig = apimsRestConfig;
    }


    // TODO: Testing
//    @Bean
//    @ConditionalOnMissingBean()
//    public ApimsRestTraceFileStorage apimsRestTraceFileStorage() {
//        return new ApimsRestTraceFileStorage();
//    }

    // TODO: Testing
//    @Bean
//    @ConditionalOnMissingBean()
//    public ApimsRestTraceFileHandler apimsRestTraceFileHandler(
//            ApimsRestTraceFileStorage apimsRestTraceFileStorage,
//            @Value("${apims.trace.force-record-trace-files:false}") boolean forceRecordTraceFiles) {
//        return new ApimsRestTraceFileHandler(forceRecordTraceFiles, apimsRestTraceFileStorage);
//    }

    @Bean
    @ConditionalOnMissingBean(name = "apimsRestFormHttpMessageConverter")
    public ApimsFormHttpMessageConverter apimsRestFormHttpMessageConverter(
            @Value("${apims.rest.form-http-message-converter-enabled:true}") boolean enabled) {
        return new ApimsFormHttpMessageConverter(enabled, false);
    }

    @Bean
    @ConditionalOnMissingBean(name = "apimsRestStringHttpMessageConverter")
    public ApimsStringHttpMessageConverter apimsRestStringHttpMessageConverter(
            @Value("${apims.rest.string-http-message-converter-enabled:true}") boolean enabled) {
        return new ApimsStringHttpMessageConverter(enabled, false);
    }

    @Bean
    @ConditionalOnMissingBean(name = "apimsRestByteArrayHttpMessageConverter")
    public ApimsByteArrayHttpMessageConverter apimsRestByteArrayHttpMessageConverter(
            @Value("${apims.rest.byte-array-http-message-converter-enabled:true}") boolean enabled) {
        return new ApimsByteArrayHttpMessageConverter(enabled, false);
    }

    @Bean
    @ConditionalOnMissingBean(name = "apimsRestMappingJackson2HttpMessageConverter")
    public ApimsMappingJackson2HttpMessageConverter apimsRestMappingJackson2HttpMessageConverter(
            @Qualifier("restObjectMapper") ObjectMapper restObjectMapper,
            @Value("${apims.rest.jackson-http-message-converter-enabled:true}") boolean enabled) {
        return new ApimsMappingJackson2HttpMessageConverter(restObjectMapper, enabled, false);
    }

    @Bean
    @ConditionalOnMissingBean(name = "apimsRestMappingJackson2XmlHttpMessageConverter")
    public ApimsMappingJackson2XmlHttpMessageConverter apimsRestMappingJackson2XmlHttpMessageConverter(
            @Qualifier("restObjectMapperXml") ObjectMapper restObjectMapper,
            @Value("${apims.rest.jackson-xml-http-message-converter-enabled:false}") boolean enabled) {
        return new ApimsMappingJackson2XmlHttpMessageConverter(restObjectMapper, enabled, false);
    }

    @Bean
    @ConditionalOnMissingBean
    public ApimsRestClientHttpRequestHeaderInterceptor apimsRestClientHttpRequestHeaderInterceptor() {
        return new ApimsRestClientHttpRequestHeaderInterceptor(
                apimsRestConfig.getHeaders(),
                apimsRestConfig.getAdditionalHeaders());
    }

    @Bean
    @ConditionalOnMissingBean
    public ApimsRestClientHttpRequestContentInterceptor apimsRestClientHttpRequestContentInterceptor() {
        return new ApimsRestClientHttpRequestContentInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean
    public ApimsRestClientHttpRequestMockInterceptor restClientHttpRequestMockInterceptor(
            List<ApimsRestClientHttpRequestMockHandler> handlers) {
        return new ApimsRestClientHttpRequestMockInterceptor(handlers);
    }

    // TODO: Testing
//    @Bean
//    @ConditionalOnMissingBean
//    public ApimsRestClientHttpRequestTraceInterceptor apimsRestClientHttpRequestTraceInterceptor(
//            @Value("${apims.rest.trace.include-payload:true}") boolean includePayload,
//            ApimsRestTraceFileHandler apimsRestTraceFileHandler) {
//        return new ApimsRestClientHttpRequestTraceInterceptor(includePayload, apimsRestTraceFileHandler);
//    }

    @Bean
    @ConditionalOnMissingBean
    public ApimsRestClientBusinessExceptionHandler apimsRestClientBusinessExceptionHandler() {
        return new ApimsRestClientBusinessExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public ApimsRestResponseErrorHandler apimsResponseErrorHandler(
            ApimsRestClientBusinessExceptionHandler businessExceptionHandler) {
        return new ApimsRestResponseErrorHandler(businessExceptionHandler);
    }

    @Bean
    @ConditionalOnMissingBean
    public ApimsDefaultUriBuilderFactory apimsDefaultUriBuilderFactory(
            @Value("${apims.rest.expand-uri-vars:true}") boolean expandUriVars,
            @Value("${apims.rest.prevent-double-encoding:false}") boolean preventDoubleEncoding) {
        return new ApimsDefaultUriBuilderFactory(expandUriVars, preventDoubleEncoding);
    }

    @Bean
    @ConditionalOnMissingBean
    @Primary
    @SuppressWarnings("java:S107")
    public RestTemplate restTemplate(
            RestTemplateBuilder builder,
            List<ApimsRestClientHttpRequestInterceptor> apimsRestClientHttpRequestInterceptorList,
            List<? extends ApimsHttpMessageConverter<?>> apimsHttpMessageConverterList,
            Optional<ApimsHttpComponentsClientHttpRequestFactory> clientHttpRequestFactory,
            ApimsRestResponseErrorHandler apimsRestResponseErrorHandler,
//            ApimsRestTraceFileHandler apimsRestTraceFileHandler,
            ApimsDefaultUriBuilderFactory apimsDefaultUriBuilderFactory,
            @Value("${apims.app.mocks.resttemplate-mock-enabled:false}") boolean mockEnabled) {
        log.debug("[APIMS AUTOCONFIG] Rest:restTemplate.");
        if (clientHttpRequestFactory.isPresent()) {
            builder = builder.requestFactory(clientHttpRequestFactory::get);
        }

        RestTemplate restTemplate = builder.build();
        // TODO: Testing
//        if (mockEnabled) {
//            restTemplate = ObjectUtils.createInstance(ObjectUtils.CreateInstanceDefinition.builder()
//                    .className("de.sky.newcrm.apims.spring.core.mocks.ApimsMockedRestTemplate")
//                    .constructorTypes(new Class<?>[] {ApimsRestTraceFileHandler.class, List.class})
//                    .constructorArgs(
//                            new Object[] {apimsRestTraceFileHandler, apimsRestClientHttpRequestInterceptorList})
//                    .build());
//        }
        restTemplate.setErrorHandler(apimsRestResponseErrorHandler);
        restTemplate.setUriTemplateHandler(apimsDefaultUriBuilderFactory);
        if (!apimsRestClientHttpRequestInterceptorList.isEmpty()) {
            apimsRestClientHttpRequestInterceptorList.sort(new OrderComparator());
            restTemplate.getInterceptors().addAll(apimsRestClientHttpRequestInterceptorList);
        }

        new ApimsHttpMessageConvertersConfigurer()
                .configureRestTemplateConverters(restTemplate.getMessageConverters(), apimsHttpMessageConverterList);
        return restTemplate;
    }

    @Bean
    @ConditionalOnMissingBean
    ApimsClientRequestObservationConvention apimsApimsClientRequestObservationConvention(
            @Value("${management.metrics.high-cardinality-client-uri:false}") boolean highCardinalityUri) {
        return new ApimsClientRequestObservationConvention(highCardinalityUri);
    }
}
