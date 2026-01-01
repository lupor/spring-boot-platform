/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.autoconfigure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sky.newcrm.apims.spring.autoconfigure.env.ApimsProperties;
import de.sky.newcrm.apims.spring.core.http.converter.*;
import de.sky.newcrm.apims.spring.core.oauth.builder.ApimsServiceTokenBuilder;
import de.sky.newcrm.apims.spring.core.oauth.handler.ApimsAuthenticationRequestHandler;
import de.sky.newcrm.apims.spring.core.oauth.handler.ApimsAuthenticationRequestJwtHandler;
import de.sky.newcrm.apims.spring.core.oauth.handler.ApimsAuthenticationRequestTrustedServicesHandler;
import de.sky.newcrm.apims.spring.core.oauth.principal.ApimsUserPrincipalManager;
import de.sky.newcrm.apims.spring.core.oauth.validator.*;
import de.sky.newcrm.apims.spring.core.support.exception.ApimsRuntimeException;
import de.sky.newcrm.apims.spring.core.support.report.ApimsReportGeneratedHint;
import de.sky.newcrm.apims.spring.core.support.web.ApimsApiErrorAttributes;
import de.sky.newcrm.apims.spring.core.support.web.ApimsRestControllerExceptionHandler;
import de.sky.newcrm.apims.spring.core.utils.ObjectUtils;
import de.sky.newcrm.apims.spring.core.web.ApimsRequestLoggingFilter;
import de.sky.newcrm.apims.spring.core.web.ApimsRoleStatelessAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.OrderComparator;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.filter.ForwardedHeaderFilter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;

import java.util.*;

@Configuration(proxyBeanMethods = false)
@AutoConfiguration(before = ErrorMvcAutoConfiguration.class)
@EnableConfigurationProperties(ApimsProperties.class)
@ConditionalOnProperty(prefix = "apims.web", name = "enabled", havingValue = "true", matchIfMissing = true)
@SuppressWarnings({"java:S6212"})
public class ApimsWebAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ApimsWebAutoConfiguration.class);
    private final ApimsProperties apimsProperties;

    public ApimsWebAutoConfiguration(ApimsProperties apimsProperties) {
        log.debug("[APIMS AUTOCONFIG] Web.");
        this.apimsProperties = apimsProperties;
    }

    @Bean
    @ConditionalOnMissingBean()
    LocaleResolver localeResolver() {
        return new FixedLocaleResolver(Locale.ENGLISH);
    }

    @Bean
    @ConditionalOnMissingBean()
    @ConditionalOnProperty(
            prefix = "apims.web.global-rest-controller-exception-handler",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true)
    public ApimsRestControllerExceptionHandler apimsRestControllerExceptionHandler() {
        log.debug("[APIMS AUTOCONFIG] Web:apimsRestControllerExceptionHandler.");
        return new ApimsRestControllerExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean()
    public ApimsApiErrorAttributes apimsApiErrorAttributes() {
        log.debug("[APIMS AUTOCONFIG] Web:apimsApiErrorAttributes.");
        return new ApimsApiErrorAttributes();
    }

    @Bean
    @ConditionalOnMissingBean()
    public ForwardedHeaderFilter apimsForwardedHeaderFilter() {
        log.debug("[APIMS AUTOCONFIG] Web:apimsForwardedHeaderFilter.");
        return new ForwardedHeaderFilter();
    }

    @Bean
    @ConditionalOnMissingBean()
    @ConditionalOnProperty(prefix = "apims.web.logging", name = "enabled", havingValue = "true", matchIfMissing = true)
    @ApimsReportGeneratedHint
    public ApimsRequestLoggingFilter apimsRequestLoggingFilter(
            @Value("${apims.web.logging.include-query-string:false}") boolean includeQueryString,
            @Value("${apims.web.logging.include-sky-headers:true}") boolean includeSkyHeaders,
            @Value("${apims.web.logging.include-headers:false}") boolean includeHeaders,
            @Value("${apims.web.logging.include-payload:false}") boolean includePayload,
            @Value("${apims.web.logging.max-payload-length:1000}") int maxPayloadLength,
            @Value("${apims.web.logging.header-predicate:}") String headerPredicate) {

        log.debug("[APIMS AUTOCONFIG] Web:apimsRequestLoggingFilter.");
        ApimsRequestLoggingFilter filter = new ApimsRequestLoggingFilter();
        filter.setIncludeQueryString(includeQueryString);
        filter.setIncludeSkyHeaders(includeSkyHeaders);
        filter.setIncludeHeaders(includeHeaders);
        filter.setIncludePayload(includePayload);
        filter.setMaxPayloadLength(maxPayloadLength);
        filter.setHeaderPredicateHeaders(StringUtils.tokenizeToStringArray(headerPredicate, ",", true, true));
        filter.setBeforeMessageSuffix("");
        filter.setAfterMessageSuffix("");
        filter.setBeforeMessagePrefix("[WEB] _START REQUEST : ");
        filter.setAfterMessagePrefix("[WEB] ___END REQUEST : ");
        return filter;
    }

    @Bean
    @ConditionalOnMissingBean()
    @ConditionalOnProperty(prefix = "apims.web.auth", name = "enabled", havingValue = "true", matchIfMissing = false)
    public ApimsTokenKeySourceLoader tokenKeySourceLoader() {
        return new ApimsTokenKeySourceLoader();
    }

    @Bean
    @ConditionalOnMissingBean()
    @ConditionalOnProperty(prefix = "apims.web.auth", name = "enabled", havingValue = "true", matchIfMissing = false)
    public ApimsUserPrincipalManager apimsUserPrincipalManager(List<ApimsTokenValidator> tokenValidators) {
        log.debug("[APIMS AUTOCONFIG] Web:apimsUserPrincipalManager.");
        ApimsProperties.Web.Auth auth = apimsProperties.getWeb().getAuth();
        tokenValidators.sort(new OrderComparator());
        Map<String, String> roleMapping = new HashMap<>();
        for (Map.Entry<String, String> entry : auth.getRoleMapping().entrySet()) {
            String[] externalRoles = StringUtils.tokenizeToStringArray(entry.getValue(), ",");
            for (String externalRole : externalRoles) {
                if (roleMapping.containsKey(externalRole)) {
                    throw new ApimsRuntimeException("Invalid external role configuration! The role '" + externalRole
                            + "' is defined several times. Please check config section 'apims.web.auth.role-mapping'!");
                }
                roleMapping.put(externalRole, entry.getKey());
            }
        }
        return new ApimsUserPrincipalManager(
                tokenValidators,
                Set.of(StringUtils.tokenizeToStringArray(auth.getDefaultRoles(), ",")),
                roleMapping,
                Set.of(StringUtils.tokenizeToStringArray(auth.getJwtClaimNameRoles(), ",")));
    }

    @Bean(name = "apimsServiceTokenBuilder")
    @ConditionalOnMissingBean(name = "apimsServiceTokenBuilder")
    @ConditionalOnExpression(
            "'${apims.web.auth.trusted-services.enabled:true}'.equals('true') && '${apims.web.auth.enabled:false}'.equals('true')")
    public ApimsServiceTokenBuilder apimsServiceTokenBuilder() {
        log.debug("[APIMS AUTOCONFIG] Web:apimsServiceTokenBuilder.");
        ApimsProperties.Web.Auth.TrustedServices trustedConfig =
                apimsProperties.getWeb().getAuth().getTrustedServices();
        return new ApimsServiceTokenBuilder(
                getTrustedRoles(trustedConfig.getServicesDefaultRoles(), trustedConfig.getServicesRoles()),
                getTrustedRoles(trustedConfig.getDomainsDefaultRoles(), trustedConfig.getDomainsRoles()));
    }

    @Bean(name = "apimsAuthenticationRequestJwtHandler")
    @ConditionalOnMissingBean(name = "apimsAuthenticationRequestJwtHandler")
    @ConditionalOnProperty(prefix = "apims.web.auth", name = "enabled", havingValue = "true", matchIfMissing = false)
    public ApimsAuthenticationRequestJwtHandler apimsAuthenticationRequestJwtHandler(
            ApimsUserPrincipalManager principalManager) {
        log.debug("[APIMS AUTOCONFIG] Web:apimsAuthenticationRequestJwtHandler.");
        return new ApimsAuthenticationRequestJwtHandler(principalManager);
    }

    @Bean(name = "apimsAuthenticationRequestTrustedServicesHandler")
    @ConditionalOnMissingBean(name = "apimsAuthenticationRequestTrustedServicesHandler")
    @ConditionalOnExpression(
            "'${apims.web.auth.trusted-services.trust-by-sky-headers:false}'.equals('true') && '${apims.web.auth.trusted-services.enabled:true}'.equals('true') && '${apims.web.auth.enabled:false}'.equals('true')")
    public ApimsAuthenticationRequestTrustedServicesHandler apimsAuthenticationRequestTrustedServicesHandler(
            ApimsUserPrincipalManager principalManager) {
        log.debug("[APIMS AUTOCONFIG] Web:apimsAuthenticationRequestTrustedServicesHandler.");
        ApimsProperties.Web.Auth.TrustedServices trustedConfig =
                apimsProperties.getWeb().getAuth().getTrustedServices();
        return new ApimsAuthenticationRequestTrustedServicesHandler(
                principalManager,
                getTrustedRoles(trustedConfig.getServicesDefaultRoles(), trustedConfig.getServicesRoles()),
                getTrustedRoles(trustedConfig.getDomainsDefaultRoles(), trustedConfig.getDomainsRoles()));
    }

    @Bean(name = "apimsAuthenticationRequestMockHandler")
    @ConditionalOnMissingBean(name = "apimsAuthenticationRequestMockHandler")
    @ConditionalOnExpression(
            "'${apims.app.mocks.web-auth-mock-enabled:false}'.equals('true') && '${apims.web.auth.enabled:false}'.equals('true')")
    @ApimsReportGeneratedHint
    public ApimsAuthenticationRequestHandler apimsAuthenticationRequestMockHandler(
            ApimsUserPrincipalManager principalManager) {
        log.debug("[APIMS AUTOCONFIG] Web:apimsAuthenticationRequestMockHandler.");
        return ObjectUtils.createInstance(ObjectUtils.CreateInstanceDefinition.builder()
                .className("de.sky.newcrm.apims.spring.core.mocks.ApimsAuthenticationRequestMockHandler")
                .constructorTypes(new Class<?>[] {ApimsUserPrincipalManager.class})
                .constructorArgs(new Object[] {principalManager})
                .build());
    }

    @Bean(name = "apimsAadTokenValidator")
    @ConditionalOnMissingBean(name = "apimsAadTokenValidator")
    @ConditionalOnExpression(
            "'${apims.web.auth.aad-token-validator.enabled:true}'.equals('true') && '${apims.web.auth.enabled:false}'.equals('true')")
    public ApimsAadTokenValidator apimsAadTokenValidator() {
        log.debug("[APIMS AUTOCONFIG] Web:apimsAadTokenValidator.");
        return new ApimsAadTokenValidator();
    }

    @Bean(name = "apimsServiceTokenValidator")
    @ConditionalOnMissingBean(name = "apimsServiceTokenValidator")
    @ConditionalOnExpression(
            "'${apims.web.auth.service-token-validator.enabled:true}'.equals('true') && '${apims.web.auth.enabled:false}'.equals('true')")
    public ApimsServiceTokenValidator apimsServiceTokenValidator() {
        log.debug("[APIMS AUTOCONFIG] Web:apimsServiceTokenValidator.");
        return new ApimsServiceTokenValidator();
    }

    @Bean(name = "apimsAdditionalTokenValidator")
    @ConditionalOnMissingBean(name = "apimsAdditionalTokenValidator")
    @ConditionalOnExpression(
            "'${apims.web.auth.additional-token-validator.enabled:false}'.equals('true') && '${apims.web.auth.enabled:false}'.equals('true')")
    public ApimsAdditionalTokenValidator apimsAdditionalTokenValidator() {
        log.debug("[APIMS AUTOCONFIG] Web:apimsAdditionalTokenValidator.");
        return new ApimsAdditionalTokenValidator();
    }

    @Bean(name = "apimsAdditionalTokenValidator")
    @ConditionalOnMissingBean(name = "apimsAdditionalTokenValidator")
    @ConditionalOnExpression(
            "'${apims.web.auth.test-token-validator.enabled:true}'.equals('true') && '${apims.web.auth.enabled:false}'.equals('true')")
    public ApimsTestTokenValidator apimsTestTokenValidator() {
        log.debug("[APIMS AUTOCONFIG] Web:apimsTestTokenValidator.");
        return new ApimsTestTokenValidator();
    }

    @Bean
    @ConditionalOnMissingBean()
    @ConditionalOnProperty(prefix = "apims.web.auth", name = "enabled", havingValue = "true", matchIfMissing = false)
    @ApimsReportGeneratedHint
    public ApimsRoleStatelessAuthenticationFilter apimsRoleStatelessAuthenticationFilter(
            List<ApimsAuthenticationRequestHandler> authenticationRequestHandlerList) {
        log.debug("[APIMS AUTOCONFIG] Web:apimsRoleStatelessAuthenticationFilter.");
        authenticationRequestHandlerList.sort(new OrderComparator());
        return new ApimsRoleStatelessAuthenticationFilter(authenticationRequestHandlerList);
    }

    @Bean
    @ConditionalOnMissingBean(name = "apimsWebFormHttpMessageConverter")
    public ApimsFormHttpMessageConverter apimsWebFormHttpMessageConverter(
            @Value("${apims.web.form-http-message-converter-enabled:true}") boolean enabled) {
        return new ApimsFormHttpMessageConverter(false, enabled);
    }

    @Bean
    @ConditionalOnMissingBean(name = "apimsWebStringHttpMessageConverter")
    public ApimsStringHttpMessageConverter apimsWebStringHttpMessageConverter(
            @Value("${apims.web.string-http-message-converter-enabled:true}") boolean enabled) {
        return new ApimsStringHttpMessageConverter(false, enabled);
    }

    @Bean
    @ConditionalOnMissingBean(name = "apimsWebByteArrayHttpMessageConverter")
    public ApimsByteArrayHttpMessageConverter apimsWebByteArrayHttpMessageConverter(
            @Value("${apims.web.byte-array-http-message-converter-enabled:true}") boolean enabled) {
        return new ApimsByteArrayHttpMessageConverter(false, enabled);
    }

    @Bean
    @ConditionalOnMissingBean(name = "apimsWebMappingJackson2HttpMessageConverter")
    @SuppressWarnings({"java:S1199"})
    public ApimsMappingJackson2HttpMessageConverter apimsWebMappingJackson2HttpMessageConverter(
            @Qualifier("webObjectMapper") ObjectMapper webObjectMapper,
            @Value("${apims.web.jackson-http-message-converter-enabled:true}") boolean enabled) {
        {
            log.debug("[APIMS AUTOCONFIG] Web:apimsWebMappingJackson2HttpMessageConverter.");
            return new ApimsMappingJackson2HttpMessageConverter(webObjectMapper, false, enabled);
        }
    }

    @Bean
    @ConditionalOnMissingBean(name = "apimsWebMappingJackson2XmlHttpMessageConverter")
    @SuppressWarnings({"java:S1199"})
    public ApimsMappingJackson2XmlHttpMessageConverter apimsWebMappingJackson2XmlHttpMessageConverter(
            @Qualifier("webObjectMapperXml") ObjectMapper webObjectMapper,
            @Value("${apims.web.jackson-xml-http-message-converter-enabled:false}") boolean enabled) {
        {
            log.debug("[APIMS AUTOCONFIG] Web:apimsWebMappingJackson2XmlHttpMessageConverter.");
            return new ApimsMappingJackson2XmlHttpMessageConverter(webObjectMapper, false, enabled);
        }
    }

    @Bean
    @ConditionalOnMissingBean(name = "apimsCorsFilter")
    @ConditionalOnProperty(prefix = "apims.cors.filter", name = "enabled", havingValue = "true", matchIfMissing = true)
    public CorsFilter apimsCorsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedMethods(Collections.singletonList(CorsConfiguration.ALL));
        config.setAllowedOrigins(Collections.singletonList(CorsConfiguration.ALL));
        config.setAllowedHeaders(Collections.singletonList(CorsConfiguration.ALL));
        source.registerCorsConfiguration("/**", config.applyPermitDefaultValues()); // NOSONAR
        return new CorsFilter(source);
    }

    @Configuration(proxyBeanMethods = false)
    public static class ApimsWebMvcAutoConfigurationAdapter implements WebMvcConfigurer {
        @Override
        public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
            new ApimsHttpMessageConvertersConfigurer().configureWebConverters(converters);
        }
    }

    protected Map<String, Set<String>> getTrustedRoles(String defaultRoles, Map<String, String> rolesMapping) {
        Set<String> trustedDefaultRoles = Set.of(StringUtils.tokenizeToStringArray(defaultRoles, ","));
        Map<String, Set<String>> trustedRolesMapping = new HashMap<>();
        for (Map.Entry<String, String> entry : rolesMapping.entrySet()) {
            Set<String> entryRoles = Set.of(StringUtils.tokenizeToStringArray(entry.getValue(), ","));
            trustedRolesMapping.put(
                    entry.getKey(), new HashSet<>(entryRoles.isEmpty() ? trustedDefaultRoles : entryRoles));
        }
        return trustedRolesMapping;
    }
}
