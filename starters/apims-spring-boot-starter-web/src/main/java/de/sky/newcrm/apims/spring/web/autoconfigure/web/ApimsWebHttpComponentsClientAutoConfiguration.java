/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.autoconfigure.web;

import de.sky.newcrm.apims.spring.web.config.ApimsWebConfig;
import de.sky.newcrm.apims.spring.web.core.web.client.*;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultClientConnectionReuseStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.core5.util.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.GeneralSecurityException;
import java.util.concurrent.TimeUnit;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "apims.web", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnExpression("'${apims.web.http-components.enabled:true}'.equals('true')")

@AutoConfiguration(afterName = {
        "org.springframework.boot.webmvc.autoconfigure.WebMvcAutoConfiguration"         // Boot 4
})
@EnableConfigurationProperties(ApimsWebConfig.class)
@SuppressWarnings({"java:S6212"})
public class ApimsWebHttpComponentsClientAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ApimsWebHttpComponentsClientAutoConfiguration.class);
    private final ApimsWebConfig apimsWebConfig;

    public ApimsWebHttpComponentsClientAutoConfiguration(ApimsWebConfig apimsWebConfig) {
        log.debug("[APIMS AUTOCONFIG] Http client.");
        this.apimsWebConfig = apimsWebConfig;
    }

    @Bean
    ApimsPoolingHttpClientConnectionManager poolingConnectionManager() throws GeneralSecurityException {
        ApimsWebConfig.HttpComponents httpComponents =
                apimsWebConfig.getHttpComponents();
        return ApimsPoolingHttpClientConnectionManager.builder()
                .setConnectionTimeToLiveSecs(httpComponents.getConnectionTimeToLiveSecs())
                .setMaxTotalConnections(httpComponents.getMaxTotalConnections())
                .setDefaultMaxConnectionsPerRoute(httpComponents.getDefaultMaxConnectionsPerRoute())
                .setHostnameVerifier(httpComponents.isHostnameVerifierDisabled() ? NoopHostnameVerifier.INSTANCE : null)
                .setSslContextTrustStrategy(
                        httpComponents.isTrustAllCertificates() ? new ApimsTrustAllCertificatesStrategy() : null)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    ApimsConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
        return new ApimsConnectionKeepAliveStrategy(
                apimsWebConfig.getHttpComponents().getDefaultKeepAliveTimeMillis());
    }

    @Bean
    @SuppressWarnings({"java:S1874"})
    RequestConfig requestConfig() {

        ApimsWebConfig.HttpComponents properties = apimsWebConfig.getHttpComponents();
        return RequestConfig.custom()
                .setConnectionRequestTimeout(properties.getRequestTimeout(), TimeUnit.MILLISECONDS)
                .setConnectTimeout(properties.getConnectTimeout(), TimeUnit.MILLISECONDS)
                .setResponseTimeout(properties.getSocketTimeout(), TimeUnit.MILLISECONDS)
                .setProtocolUpgradeEnabled(false)
                .build();
    }

    @Bean(name = "httpClient")
    CloseableHttpClient httpClient(
            ApimsPoolingHttpClientConnectionManager poolingConnectionManager,
            ApimsConnectionKeepAliveStrategy connectionKeepAliveStrategy,
            RequestConfig requestConfig) {

        ApimsWebConfig.HttpComponents properties = apimsWebConfig.getHttpComponents();
        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(poolingConnectionManager)
                .setKeepAliveStrategy(connectionKeepAliveStrategy)
                .setConnectionReuseStrategy(DefaultClientConnectionReuseStrategy.INSTANCE)
                .evictExpiredConnections()
                .evictIdleConnections(TimeValue.ofSeconds(properties.getCloseIdleConnectionWaitTimeSecs()))
                .build();
    }

    @Bean
    public ApimsHttpComponentsClientHttpRequestFactory clientHttpRequestFactory(
            @Qualifier("httpClient") CloseableHttpClient httpClient) {
        return new ApimsHttpComponentsClientHttpRequestFactory(httpClient);
    }

    @Bean
    @ConditionalOnMissingBean
    ApimsIdleConnectionJob apimsIdleConnectionJob(
            ApimsPoolingHttpClientConnectionManager connectionManager,
            @Value("${apims.scheduling.rest-idle-connection-job.enabled:true}") boolean enabled) {
        ApimsWebConfig.HttpComponents httpComponents =
                apimsWebConfig.getHttpComponents();
        return new ApimsIdleConnectionJob(
                connectionManager, httpComponents.getCloseIdleConnectionWaitTimeSecs(), enabled);
    }
}
