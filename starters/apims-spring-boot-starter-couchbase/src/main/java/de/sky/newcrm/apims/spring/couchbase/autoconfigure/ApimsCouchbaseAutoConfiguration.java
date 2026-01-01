/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.couchbase.autoconfigure;

import com.couchbase.client.core.deps.com.fasterxml.jackson.databind.DeserializationFeature;
import com.couchbase.client.core.encryption.CryptoManager;
import com.couchbase.client.core.env.*;
import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.core.retry.BestEffortRetryStrategy;
import com.couchbase.client.core.retry.RetryStrategy;
import com.couchbase.client.java.codec.JacksonJsonSerializer;
import com.couchbase.client.java.encryption.annotation.Encrypted;
import com.couchbase.client.java.env.ClusterEnvironment;
import com.couchbase.client.java.json.JacksonTransformers;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.sky.newcrm.apims.spring.couchbase.config.ApimsCouchbaseCachingConfig;
import de.sky.newcrm.apims.spring.couchbase.config.ApimsCouchbaseConfig;
import de.sky.newcrm.apims.spring.couchbase.config.ApimsCouchbaseEncryptionConfig;
import de.sky.newcrm.apims.spring.couchbase.config.ApimsCouchbaseReEncryptionConfig;
import de.sky.newcrm.apims.spring.couchbase.core.*;
import de.sky.newcrm.apims.spring.couchbase.core.converter.ApimsConverterFactory;
import de.sky.newcrm.apims.spring.couchbase.core.converter.ApimsCouchbaseConverter;
import de.sky.newcrm.apims.spring.couchbase.core.converter.ApimsDateToStringCouchbaseConverter;
import de.sky.newcrm.apims.spring.couchbase.core.converter.ApimsStringToDateCouchbaseConverter;
import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import de.sky.newcrm.apims.spring.serialization.core.mapper.legacy.ObjectMapperUtils;
import de.sky.newcrm.apims.spring.utils.AssertUtils;
import de.sky.newcrm.apims.spring.utils.ObjectUtils;
import de.sky.newcrm.apims.spring.utils.scanner.ApimsClassScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.convert.PropertyValueConverterRegistrar;
import org.springframework.data.convert.SimplePropertyValueConversions;
import org.springframework.data.couchbase.cache.CouchbaseCacheConfiguration;
import org.springframework.data.couchbase.cache.CouchbaseCacheManager;
import org.springframework.data.couchbase.config.BeanNames;
import org.springframework.data.couchbase.core.ReactiveCouchbaseTemplate;
import org.springframework.data.couchbase.core.convert.*;
import org.springframework.data.couchbase.core.convert.translation.JacksonTranslationService;
import org.springframework.data.couchbase.core.convert.translation.TranslationService;
import org.springframework.data.couchbase.core.mapping.CouchbaseMappingContext;
import org.springframework.data.couchbase.repository.auditing.EnableCouchbaseAuditing;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;
import org.springframework.data.couchbase.repository.config.ReactiveRepositoryOperationsMapping;
import org.springframework.data.couchbase.repository.config.RepositoryOperationsMapping;
import org.springframework.data.mapping.model.CamelCaseAbbreviatingFieldNamingStrategy;
import org.springframework.data.mapping.model.FieldNamingStrategy;
import org.springframework.data.mapping.model.PropertyNameFieldNamingStrategy;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.util.StringUtils;
import org.springframework.vault.core.VaultTemplate;

import java.lang.annotation.Annotation;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;

@Configuration()
@AutoConfiguration(
        beforeName = {
            "org.springframework.boot.autoconfigure.couchbase.CouchbaseAutoConfiguration",
            "org.springframework.boot.actuate.autoconfigure.health.HealthEndpointConfiguration"
        })
@EnableCouchbaseRepositories(basePackages = "de.sky", repositoryBaseClass = ApimsSimpleCouchbaseRepository.class)
@ConditionalOnProperty(prefix = "apims.couchbase", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableCouchbaseAuditing
@SuppressWarnings({"java:S6212", "java:S6857"})
public class ApimsCouchbaseAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ApimsCouchbaseAutoConfiguration.class);

    @Value("${apims.app.name}")
    protected String appName;

    @Value("${spring.couchbase.username}")
    protected String username;

    @Value("${spring.couchbase.password}")
    protected String password;

    @Value("${spring.couchbase.bucket}")
    protected String bucketName;

    @Value("${spring.couchbase.scopename:}")
    protected String scopeName;

    @Value("${spring.couchbase.second-username:}")
    protected String secondUsername;

    @Value("${spring.couchbase.second-password:}")
    protected String secondPassword;

    @Value("${spring.couchbase.second-bucket:}")
    protected String secondBucketName;

    @Value("${spring.couchbase.second-scopename:}")
    protected String secondScopeName;

    @Value("${spring.couchbase.hostname}")
    protected String hostname;

    @Value("${spring.couchbase.env.repositories-base-package:de.sky}")
    protected String repositoriesBasePackage;

    @Value("${spring.couchbase.env.auto-index-creation:false}")
    protected boolean autoIndexCreation;

    @Value("${spring.couchbase.env.collection-mapping-type-key:}")
    protected String collectionMappingTypeKey;

    @Value("${spring.couchbase.env.collection-mapping-type-value:}")
    protected String collectionMappingTypeValue;

    @Value("${spring.couchbase.env.retry-strategy.first-backoff-millis:1}")
    protected long retryStrategyFirstBackoffMillis;

    @Value("${spring.couchbase.env.retry-strategy.max-backoff-millis:1000}")
    protected long retryStrategyMaxBackoffMillis;

    @Value("${spring.couchbase.env.retry-strategy.backoff-factor:2}")
    protected int retryStrategyBackoffFactor;

    @Value("${spring.couchbase.env.timeouts.analytics:75000}")
    protected Long analyticsTimeout;

    @Value("${spring.couchbase.env.timeouts.connect:25000}")
    protected Long connectTimeout;

    @Value("${spring.couchbase.env.timeouts.disconnect:25000}")
    protected Long disconnectTimeout;

    @Value("${spring.couchbase.env.timeouts.eventing:75000}")
    protected Long eventingTimeout;

    @Value("${spring.couchbase.env.timeouts.key-value:2500}")
    protected Long keyValueTimeout;

    @Value("${spring.couchbase.env.timeouts.key-value-durable:2500}")
    protected Long keyValueDurableTimeout;

    @Value("${spring.couchbase.env.timeouts.management:75000}")
    protected Long managementTimeout;

    @Value("${spring.couchbase.env.timeouts.query:75000}")
    protected Long queryTimeout;

    @Value("${spring.couchbase.env.timeouts.search:75000}")
    protected Long searchTimeout;

    @Value("${spring.couchbase.env.timeouts.view:75000}")
    protected Long viewTimeout;

    @Value("${spring.couchbase.env.security.auth-method:password}")
    protected String authMethod;

    @Value("${spring.couchbase.env.security.auth-key-store-type:JKS}")
    protected String authKeyStoreType;

    @Value("${spring.couchbase.env.security.auth-key-store-location:}")
    protected String authKeyStoreLocation;

    @Value("${spring.couchbase.env.security.auth-key-store-password:}")
    protected String authKeyStorePassword;

    @Value("${spring.couchbase.env.security.second-auth-key-store-type:JKS}")
    protected String secondAuthKeyStoreType;

    @Value("${spring.couchbase.env.security.second-auth-key-store-location:}")
    protected String secondAuthKeyStoreLocation;

    @Value("${spring.couchbase.env.security.second-auth-key-store-password:}")
    protected String secondAuthKeyStorePassword;

    @Value("${spring.couchbase.env.security.tls-enabled:true}")
    protected boolean tlsEnabled;

    @Value("${spring.couchbase.env.security.tls-hostname-verification-enable:true}")
    protected boolean tlsHostnameVerificationEnabled;

    @Value("${spring.couchbase.env.security.tls-trust-store-type:JKS}")
    protected String tlsTrustStoreType;

    @Value("${spring.couchbase.env.security.tls-trust-store-location:}")
    protected String tlsTrustStoreLocation;

    @Value("${spring.couchbase.env.security.tls-trust-store-password:}")
    protected String tlsTrustStorePassword;

    @Value("${spring.couchbase.env.io.networkResolution:auto}")
    protected String networkResolution;

    @Autowired
    protected ApimsCouchbaseConfig apimsCouchbaseConfig;

    @Autowired
    protected ApimsCouchbaseCachingConfig apimsCouchbaseCachingConfig;

    @Autowired
    protected ApimsCouchbaseEncryptionConfig apimsCouchbaseEncryptionConfig;

    @Autowired
    protected ApimsCouchbaseReEncryptionConfig apimsCouchbaseReEncryptionConfig;

    public ApimsCouchbaseAutoConfiguration() {
        log.debug("[APIMS AUTOCONFIG] Couchbase.");
    }

    @ApimsReportGeneratedHint
    public String getConnectionString() {
        return hostname;
    }

    @ApimsReportGeneratedHint
    public String getUserName() {
        return username;
    }

    @ApimsReportGeneratedHint
    public String getPassword() {
        return password;
    }

    @ApimsReportGeneratedHint
    public String getAuthKeyStoreLocation() {
        return authKeyStoreLocation;
    }

    @ApimsReportGeneratedHint
    public String getAuthKeyStorePassword() {
        return authKeyStorePassword;
    }

    @ApimsReportGeneratedHint
    public String getAuthKeyStoreType() {
        return authKeyStoreType;
    }

    @ApimsReportGeneratedHint
    public String getBucketName() {
        return bucketName;
    }

    @ApimsReportGeneratedHint
    protected String getScopeName() {
        return StringUtils.hasLength(scopeName) ? scopeName : null;
    }

    @ApimsReportGeneratedHint
    public String getSecondUsername() {
        return secondUsername;
    }

    @ApimsReportGeneratedHint
    public String getSecondPassword() {
        return secondPassword;
    }

    @ApimsReportGeneratedHint
    public String getSecondAuthKeyStoreLocation() {
        return secondAuthKeyStoreLocation;
    }

    @ApimsReportGeneratedHint
    public String getSecondAuthKeyStorePassword() {
        return secondAuthKeyStorePassword;
    }

    @ApimsReportGeneratedHint
    public String getSecondAuthKeyStoreType() {
        return secondAuthKeyStoreType;
    }

    @ApimsReportGeneratedHint
    public String getSecondBucketName() {
        return secondBucketName;
    }

    @ApimsReportGeneratedHint
    public String getSecondScopeName() {
        return StringUtils.hasLength(secondScopeName) ? secondScopeName : null;
    }

    @ApimsReportGeneratedHint
    protected boolean isSecondCouchbaseContextNeeded() {
        return StringUtils.hasLength(getSecondBucketName())
                && StringUtils.hasLength(getSecondUsername())
                && !getUserName().equals(getSecondUsername());
    }

    @ApimsReportGeneratedHint
    protected Authenticator authenticator() {
        return authenticator(
                getUserName(),
                getPassword(),
                getAuthKeyStoreLocation(),
                getAuthKeyStorePassword(),
                getAuthKeyStoreType());
    }

    @ApimsReportGeneratedHint
    protected Authenticator secondAuthenticator() {
        return authenticator(
                getSecondUsername(),
                getSecondPassword(),
                getSecondAuthKeyStoreLocation(),
                getSecondAuthKeyStorePassword(),
                getSecondAuthKeyStoreType());
    }

    @ApimsReportGeneratedHint
    protected Authenticator authenticator(
            String username,
            String password,
            String authKeyStoreLocation,
            String authKeyStorePassword,
            String authKeyStoreType) {
        if ("keystore".equalsIgnoreCase(authMethod)) {
            return CertificateAuthenticator.fromKeyStore(
                    Path.of(authKeyStoreLocation), authKeyStorePassword, Optional.of(authKeyStoreType));
        } else {
            return PasswordAuthenticator.create(username, password);
        }
    }

    @ApimsReportGeneratedHint
    protected boolean nonShadowedJacksonPresent() {
        try {
            JacksonJsonSerializer.preflightCheck();
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    protected boolean isAutoIndexCreation() {
        return autoIndexCreation;
    }

    @SuppressWarnings("java:S1874")
    protected void configureEnvironment(
            ClusterEnvironment.Builder builder, List<ApimsCouchbaseBeforeSendRequestCallback> sendRequestCallbacks) {
        builder.timeoutConfig(this::applyTimeoutConfig)
                .securityConfig(this::applySecurityConfig)
                .retryStrategy(getRetryStrategy())
                .ioConfig(this::applyIoConfig);
        sendRequestCallbacks.forEach(builder::addRequestCallback);
    }

    protected void configureRepositoryOperationsMapping(RepositoryOperationsMapping mapping) {
        // NO_OP
    }

    protected void configureReactiveRepositoryOperationsMapping(ReactiveRepositoryOperationsMapping mapping) {
        // NO_OP
    }

    protected Set<Class<?>> getInitialEntitySet() {
        return new HashSet<>();
    }

    @SuppressWarnings({"java:S4449"})
    protected Set<Class<?>> getInitialRepositoriesSet() {
        List<Class<?>> classList = ApimsClassScanner.findClasses(
                org.springframework.data.couchbase.repository.Collection.class, true, getRepositoriesBasePackage());
        classList.removeIf(c -> ObjectUtils.findClassAnnotation(c, NoRepositoryBean.class, true) != null);
        return new HashSet<>(classList);
    }

    protected String getMappingTypeKey() {
        return collectionMappingTypeKey == null || "NULL".equals(collectionMappingTypeKey)
                ? null
                : collectionMappingTypeKey;
    }

    protected String getMappingTypeValue() {
        return collectionMappingTypeValue == null || "NULL".equals(collectionMappingTypeValue)
                ? null
                : collectionMappingTypeValue;
    }

    @Bean(name = "apimsCouchbaseNativeEndpoint", destroyMethod = "disconnect")
    @ConditionalOnMissingBean(name = "apimsCouchbaseNativeEndpoint")
    @Primary
    @ApimsReportGeneratedHint
    ApimsCouchbaseNativeEndpoint apimsCouchbaseNativeEndpoint(
            ClusterEnvironment couchbaseClusterEnvironment,
            @Value("${apims.app.mocks.couchbase-mock-enabled:false}") boolean mockEnabled) {
        ApimsCouchbaseNativeEndpoint apimsCouchbaseNativeEndpoint =
                new ApimsCouchbaseNativeEndpoint(getConnectionString(), authenticator(), couchbaseClusterEnvironment);
        if (mockEnabled) {
            apimsCouchbaseNativeEndpoint.setMocksEnabled(true);
        }
        return apimsCouchbaseNativeEndpoint;
    }

    @Bean(name = "apimsCouchbaseCacheManager")
    @ConditionalOnMissingBean(name = "apimsCouchbaseCacheManager")
    @ConditionalOnProperty(
            prefix = "apims.couchbase.caching",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = false)
    @ApimsReportGeneratedHint
    public CacheManager apimsCouchbaseCacheManager(ApimsCouchbaseClientFactory apimsCouchbaseClientFactory) {
        final Map<String, CouchbaseCacheConfiguration> cacheConfigurations = new HashMap<>();
        final Long defaultExpirySeconds = apimsCouchbaseCachingConfig.getExpirySeconds();
        if (apimsCouchbaseCachingConfig.getCaches() != null
                && !apimsCouchbaseCachingConfig.getCaches().isEmpty()) {
            apimsCouchbaseCachingConfig.getCaches().forEach(cacheName -> {
                Long expirySeconds =
                        cacheName.getExpirySeconds() != null ? cacheName.getExpirySeconds() : defaultExpirySeconds;
                cacheConfigurations.put(
                        cacheName.getName(),
                        CouchbaseCacheConfiguration.defaultCacheConfig()
                                .entryExpiry(Duration.ofSeconds(expirySeconds))
                                .disableCachingNullValues()
                                .prefixCacheNameWith(apimsCouchbaseCachingConfig.getCachePrefix())
                                .collection(apimsCouchbaseCachingConfig.getCollection()));
            });
            return CouchbaseCacheManager.builder(apimsCouchbaseClientFactory)
                    .withInitialCacheConfigurations(cacheConfigurations)
                    .build();
        } else {
            return CouchbaseCacheManager.builder(apimsCouchbaseClientFactory)
                    .cacheDefaults(CouchbaseCacheConfiguration.defaultCacheConfig()
                            .entryExpiry(Duration.ofSeconds(defaultExpirySeconds))
                            .disableCachingNullValues()
                            .prefixCacheNameWith(apimsCouchbaseCachingConfig.getCachePrefix())
                            .collection(apimsCouchbaseCachingConfig.getCollection()))
                    .build();
        }
    }

    @Bean(name = "secondApimsCouchbaseNativeEndpoint", destroyMethod = "disconnect")
    @ConditionalOnMissingBean(name = "secondApimsCouchbaseNativeEndpoint")
    @ConditionalOnExpression("!'${spring.couchbase.second-bucket:}'.equals('')")
    @ApimsReportGeneratedHint
    ApimsCouchbaseNativeEndpoint secondApimsCouchbaseNativeEndpoint(
            @Qualifier("apimsCouchbaseNativeEndpoint") ApimsCouchbaseNativeEndpoint apimsCouchbaseNativeEndpoint,
            ClusterEnvironment couchbaseClusterEnvironment,
            @Value("${apims.app.mocks.couchbase-mock-enabled:false}") boolean mockEnabled) {
        if (isSecondCouchbaseContextNeeded()) {
            ApimsCouchbaseNativeEndpoint secondApimsCouchbaseNativeEndpoint = new ApimsCouchbaseNativeEndpoint(
                    getConnectionString(), secondAuthenticator(), couchbaseClusterEnvironment);
            if (mockEnabled) {
                secondApimsCouchbaseNativeEndpoint.setMocksEnabled(true);
            }
            return secondApimsCouchbaseNativeEndpoint;
        } else {
            return apimsCouchbaseNativeEndpoint;
        }
    }

    @Bean
    @ConditionalOnMissingBean()
    ApimsCouchbaseSerializerFactory apimsCouchbaseSerializerFactory(Optional<CryptoManager> cryptoManager) {
        return new ApimsCouchbaseSerializerFactory(cryptoManager.orElse(null));
    }

    @Bean(name = "couchbaseClientFactory")
    @ConditionalOnMissingBean(name = "couchbaseClientFactory")
    @Primary
    @ApimsReportGeneratedHint
    public ApimsCouchbaseClientFactory couchbaseClientFactory(
            @Qualifier("apimsCouchbaseNativeEndpoint") ApimsCouchbaseNativeEndpoint apimsCouchbaseNativeEndpoint,
            @Value("${apims.app.mocks.couchbase-mock-enabled:false}") boolean mockEnabled) {
        ApimsCouchbaseClientFactory apimsCouchbaseClientFactory = new ApimsCouchbaseClientFactory(
                mockEnabled, apimsCouchbaseNativeEndpoint, getBucketName(), getScopeName());
        if (mockEnabled) {
            apimsCouchbaseClientFactory.setMocksEnabled(true);
        }
        return apimsCouchbaseClientFactory;
    }

    @Bean(name = "couchbaseHealthIndicator")
    @ConditionalOnMissingBean(name = "couchbaseHealthIndicator")
    @ConditionalOnProperty(
            prefix = "apims.app.mocks",
            name = "couchbase-mock-enabled",
            havingValue = "false",
            matchIfMissing = true)
    public ApimsCouchbaseHealthIndicator couchbaseHealthIndicator(ApimsCouchbaseClientFactory couchbaseClientFactory) {
        return new ApimsCouchbaseHealthIndicator(couchbaseClientFactory.getCluster());
    }

    @Bean
    @ConditionalOnMissingBean()
    public ApimsCouchbaseStartupListener apimsCouchbaseStartupListener(
            Optional<ApimsCouchbaseHealthIndicator> apimsCouchbaseHealthIndicator) {
        return new ApimsCouchbaseStartupListener(apimsCouchbaseHealthIndicator.orElse(null));
    }

    @Bean(name = "secondCouchbaseClientFactory")
    @ConditionalOnMissingBean(name = "secondCouchbaseClientFactory")
    @ConditionalOnExpression("!'${spring.couchbase.second-bucket:}'.equals('')")
    @ApimsReportGeneratedHint
    public ApimsCouchbaseClientFactory secondCouchbaseClientFactory(
            @Qualifier("secondApimsCouchbaseNativeEndpoint") ApimsCouchbaseNativeEndpoint apimsCouchbaseNativeEndpoint,
            @Value("${apims.app.mocks.couchbase-mock-enabled:false}") boolean mockEnabled) {
        ApimsCouchbaseClientFactory apimsCouchbaseClientFactory = new ApimsCouchbaseClientFactory(
                mockEnabled, apimsCouchbaseNativeEndpoint, getSecondBucketName(), getSecondScopeName());
        if (mockEnabled) {
            apimsCouchbaseClientFactory.setMocksEnabled(true);
        }
        return apimsCouchbaseClientFactory;
    }

    @Bean(destroyMethod = "shutdown")
    @ApimsReportGeneratedHint
    public ClusterEnvironment couchbaseClusterEnvironment(
            ApimsCouchbaseSerializerFactory apimsCouchbaseSerializerFactory) {
        ClusterEnvironment.Builder builder = ClusterEnvironment.builder();
        if (!nonShadowedJacksonPresent()) {
            throw new CouchbaseException("non-shadowed Jackson not present");
        }
        builder.jsonSerializer(apimsCouchbaseSerializerFactory.getCouchbaseJsonSerializer());
        builder.cryptoManager(apimsCouchbaseSerializerFactory.getCryptoManager());
        configureEnvironment(builder, List.of(new ApimsCouchbaseBeforeSendRequestLoggingCallback()));
        return builder.build();
    }

    @Bean(name = BeanNames.COUCHBASE_TEMPLATE)
    @Primary
    public ApimsCouchbaseTemplate couchbaseTemplate(
            ApimsCouchbaseClientFactory couchbaseClientFactory,
            MappingCouchbaseConverter mappingCouchbaseConverter,
            TranslationService couchbaseTranslationService) {
        return new ApimsCouchbaseTemplate(
                true,
                couchbaseClientFactory,
                mappingCouchbaseConverter,
                couchbaseTranslationService,
                ApimsCouchbaseAutoConfigurationHelper.getDefaultQueryScanConsistency());
    }

    @Bean(name = "secondCouchbaseTemplate")
    @ConditionalOnMissingBean(name = "secondCouchbaseTemplate")
    @ConditionalOnExpression("!'${spring.couchbase.second-bucket:}'.equals('')")
    @ApimsReportGeneratedHint
    public ApimsCouchbaseTemplate secondCouchbaseTemplate(
            @Qualifier("secondCouchbaseClientFactory") ApimsCouchbaseClientFactory couchbaseClientFactory,
            MappingCouchbaseConverter mappingCouchbaseConverter,
            TranslationService couchbaseTranslationService) {

        return new ApimsCouchbaseTemplate(
                false,
                couchbaseClientFactory,
                mappingCouchbaseConverter,
                couchbaseTranslationService,
                ApimsCouchbaseAutoConfigurationHelper.getDefaultQueryScanConsistency());
    }

    @Bean
    @ConditionalOnMissingBean
    public ApimsCouchbaseCollectionNameResolver apimsCouchbaseCollectionNameResolver() {
        return new ApimsCouchbaseCollectionNameResolver();
    }

    @Bean(name = BeanNames.COUCHBASE_OPERATIONS_MAPPING)
    @ConditionalOnMissingBean(name = BeanNames.COUCHBASE_OPERATIONS_MAPPING)
    @Primary
    @SuppressWarnings({"java:S2259"})
    @ApimsReportGeneratedHint
    public RepositoryOperationsMapping couchbaseRepositoryOperationsMapping(
            List<ApimsCouchbaseTemplate> apimsCouchbaseTemplates,
            ApimsCouchbaseCollectionNameResolver apimsCouchbaseCollectionNameResolver) {
        ApimsCouchbaseTemplate primaryTemplate = apimsCouchbaseTemplates.stream()
                .filter(ApimsCouchbaseTemplate::isPrimaryTemplate)
                .findFirst()
                .orElse(null);
        AssertUtils.notNullCheck(BeanNames.COUCHBASE_TEMPLATE, primaryTemplate);
        String primaryScopeName = primaryTemplate == null ? null : primaryTemplate.getScopeName();
        String primaryBucketName = primaryTemplate == null ? null : primaryTemplate.getBucketName();
        String primaryTemplateKey = primaryScopeName + "/" + primaryBucketName;
        RepositoryOperationsMapping baseMapping = new RepositoryOperationsMapping(primaryTemplate);
        Map<String, ApimsCouchbaseTemplate> templates = new HashMap<>();
        for (ApimsCouchbaseTemplate template : apimsCouchbaseTemplates) {
            templates.put(template.getScopeName() + "/" + template.getBucketName(), template);
        }
        Set<Class<?>> repositories = getInitialRepositoriesSet();
        for (Class<?> repository : repositories) {
            apimsCouchbaseCollectionNameResolver.registerRepository(repository);
            String repositoryScopeName = apimsCouchbaseCollectionNameResolver.getScopeName(repository);
            String repositoryBucketName = apimsCouchbaseCollectionNameResolver.getBucketName(repository);
            String repositoryTemplateKey = repositoryScopeName + "/" + repositoryBucketName;
            if (StringUtils.hasLength(repositoryScopeName)
                    && StringUtils.hasLength(repositoryBucketName)
                    && !repositoryTemplateKey.equals(primaryTemplateKey)) {
                ApimsCouchbaseTemplate targetTemplate = templates.get(repositoryTemplateKey);
                if (targetTemplate != null) {
                    baseMapping.map(repository, targetTemplate);
                }
            }
        }
        configureRepositoryOperationsMapping(baseMapping);
        return baseMapping;
    }

    @Bean(name = BeanNames.REACTIVE_COUCHBASE_OPERATIONS_MAPPING)
    public ReactiveRepositoryOperationsMapping reactiveCouchbaseRepositoryOperationsMapping(
            ReactiveCouchbaseTemplate reactiveCouchbaseTemplate) {
        ReactiveRepositoryOperationsMapping baseMapping =
                new ReactiveRepositoryOperationsMapping(reactiveCouchbaseTemplate);
        configureReactiveRepositoryOperationsMapping(baseMapping);
        return baseMapping;
    }

    /**
     * Creates a {@link MappingCouchbaseConverter} using the configured {@link #couchbaseMappingContext}.
     */
    @Bean
    public MappingCouchbaseConverter mappingCouchbaseConverter(
            CouchbaseMappingContext couchbaseMappingContext, CouchbaseCustomConversions couchbaseCustomConversions) {
        MappingCouchbaseConverter converter =
                new ApimsMappingCouchbaseConverter(couchbaseMappingContext, getMappingTypeKey(), getMappingTypeValue());
        converter.setCustomConversions(couchbaseCustomConversions);
        return converter;
    }

    /**
     * Creates a {@link TranslationService}.
     *
     * @return TranslationService, defaulting to JacksonTranslationService.
     */
    @Bean
    public TranslationService couchbaseTranslationService() {
        final JacksonTranslationService jacksonTranslationService = new JacksonTranslationService();
        jacksonTranslationService.afterPropertiesSet();
        // for sdk3, we need to ask the mapper _it_ uses to ignore extra fields...
        JacksonTransformers.MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return jacksonTranslationService;
    }

    @Bean
    @ConditionalOnMissingBean
    public CouchbaseMappingContext couchbaseMappingContext(CustomConversions customConversions) {
        CouchbaseMappingContext mappingContext = new CouchbaseMappingContext();
        mappingContext.setInitialEntitySet(getInitialEntitySet());
        mappingContext.setSimpleTypeHolder(customConversions.getSimpleTypeHolder());
        mappingContext.setFieldNamingStrategy(fieldNamingStrategy());
        mappingContext.setAutoIndexCreation(isAutoIndexCreation());
        return mappingContext;
    }

    @Bean
    @ConditionalOnMissingBean
    @SuppressWarnings({"java:S107"})
    public ApimsCouchbaseContext apimsCouchbaseContext(
            @Value("${apims.app.mocks.couchbase-mock-enabled:false}") boolean mockEnabled,
            ApimsCouchbaseSerializerFactory apimsCouchbaseSerializerFactory,
            RepositoryOperationsMapping repositoryOperationsMapping,
            ApimsCouchbaseCollectionNameResolver apimsCouchbaseCollectionNameResolver,
            ApimsCouchbaseMutateInSupport apimsCouchbaseMutateInSupport,
            TranslationService couchbaseTranslationService,
            MappingCouchbaseConverter mappingCouchbaseConverter,
            CustomConversions customConversions,
            List<ApimsCouchbaseTemplate> apimsCouchbaseTemplates) {

        return new ApimsCouchbaseContext(
                mockEnabled,
                apimsCouchbaseSerializerFactory,
                repositoryOperationsMapping,
                apimsCouchbaseCollectionNameResolver,
                apimsCouchbaseMutateInSupport,
                couchbaseTranslationService,
                mappingCouchbaseConverter,
                customConversions,
                ApimsCouchbaseAutoConfigurationHelper.getDefaultQueryScanConsistency(),
                apimsCouchbaseTemplates);
    }

    @Bean
    @ConditionalOnMissingBean
    public ApimsCouchbaseNativeSupport apimsCouchbaseNativeSupport(ApimsCouchbaseContext apimsCouchbaseContext) {
        return new ApimsCouchbaseNativeSupport(apimsCouchbaseContext);
    }

    protected String getMappingBasePackage() {
        return getClass().getPackage().getName();
    }

    public String getRepositoriesBasePackage() {
        return repositoriesBasePackage;
    }

    /**
     * Set to true if field names should be abbreviated with the {@link CamelCaseAbbreviatingFieldNamingStrategy}.
     *
     * @return true if field names should be abbreviated, default is false.
     */
    protected boolean abbreviateFieldNames() {
        return false;
    }

    /**
     * Configures a {@link FieldNamingStrategy} on the {@link CouchbaseMappingContext} instance created.
     *
     * @return the naming strategy.
     */
    protected FieldNamingStrategy fieldNamingStrategy() {
        return abbreviateFieldNames()
                ? new CamelCaseAbbreviatingFieldNamingStrategy()
                : PropertyNameFieldNamingStrategy.INSTANCE;
    }

    // this creates the auditor aware bean that will feed the annotations
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            prefix = "spring.couchbase.env",
            name = "auditor-aware-enabled",
            havingValue = "true",
            matchIfMissing = true)
    public ApimsCouchbaseAuditorAware apimsCouchbaseAuditorAware() {
        log.debug("[APIMS AUTOCONFIG] Couchbase:apimsCouchbaseAuditorAware.");
        ApimsCouchbaseAuditorAware apimsCouchbaseAuditorAware = new ApimsCouchbaseAuditorAware();
        apimsCouchbaseAuditorAware.setAuditor(appName);
        return apimsCouchbaseAuditorAware;
    }

    @Bean()
    @ConditionalOnMissingBean()
    public ApimsDateToStringCouchbaseConverter apimsDateToStringCouchbaseConverter() {
        return new ApimsDateToStringCouchbaseConverter();
    }

    @Bean()
    @ConditionalOnMissingBean()
    public ApimsStringToDateCouchbaseConverter apimsStringToDateCouchbaseConverter() {
        return new ApimsStringToDateCouchbaseConverter();
    }

    @Bean(name = BeanNames.COUCHBASE_CUSTOM_CONVERSIONS)
    @ConditionalOnMissingBean(name = BeanNames.COUCHBASE_CUSTOM_CONVERSIONS)
    @Primary
    public CustomConversions customConversions(
            ApimsCouchbaseSerializerFactory apimsCouchbaseSerializerFactory,
            List<ApimsCouchbaseConverter<?, ?>> converters,
            List<ApimsConverterFactory<?, ?>> converterFactories) {

        final ObjectMapper objectMapper =
                ObjectMapperUtils.getApimsObjectMapperJson().unwrap();
        List<Object> couchbaseConverters = new ArrayList<>(converters);
        couchbaseConverters.addAll(converterFactories);
        couchbaseConverters.add(new OtherConverters.EnumToObject(objectMapper));
        couchbaseConverters.add(new IntegerToEnumConverterFactory(objectMapper));
        couchbaseConverters.add(new StringToEnumConverterFactory(objectMapper));
        couchbaseConverters.add(new BooleanToEnumConverterFactory(objectMapper));
        return CouchbaseCustomConversions.create(configurationAdapter -> {
            SimplePropertyValueConversions valueConversions = new SimplePropertyValueConversions();
            valueConversions.setConverterFactory(new CouchbasePropertyValueConverterFactory(
                    apimsCouchbaseSerializerFactory.getCryptoManager(),
                    apimsCouchbaseSerializerFactory.getCryptoManager() != null ? annotationToConverterMap() : Map.of(),
                    objectMapper));
            valueConversions.setValueConverterRegistry(new PropertyValueConverterRegistrar<>().buildRegistry());
            valueConversions
                    .afterPropertiesSet(); // wraps the CouchbasePropertyValueConverterFactory with CachingPVCFactory
            configurationAdapter.setPropertyValueConversions(valueConversions);
            configurationAdapter.registerConverters(couchbaseConverters);
        });
    }

    private Map<Class<? extends Annotation>, Class<?>> annotationToConverterMap() {
        Map<Class<? extends Annotation>, Class<?>> map = new HashMap<>();
        map.put(Encrypted.class, CryptoConverter.class);
        map.put(JsonValue.class, JsonValueConverter.class);
        return map;
    }

    @Bean
    @ConditionalOnMissingBean
    public ApimsCouchbaseMutateInSupport apimsCouchbaseMutateInSupport(
            @Value("${spring.couchbase.env.mutate-in-custom-conversions.enabled:true}")
                    boolean mutateInCustomConversionsEnabled,
            MappingCouchbaseConverter mappingCouchbaseConverter,
            @Qualifier(BeanNames.COUCHBASE_CUSTOM_CONVERSIONS) CustomConversions customConversions,
            ClusterEnvironment clusterEnvironment) {
        log.debug("[APIMS AUTOCONFIG] Couchbase:apimsCouchbaseMutateInSupport.");
        return new ApimsCouchbaseMutateInSupport(
                mutateInCustomConversionsEnabled,
                mappingCouchbaseConverter,
                customConversions,
                clusterEnvironment.jsonSerializer());
    }

    protected void applyTimeoutConfig(TimeoutConfig.Builder config) {
        config.analyticsTimeout(Duration.ofMillis(analyticsTimeout))
                .connectTimeout(Duration.ofMillis(connectTimeout))
                .disconnectTimeout(Duration.ofMillis(disconnectTimeout))
                .eventingTimeout(Duration.ofMillis(eventingTimeout))
                .kvTimeout(Duration.ofMillis(keyValueTimeout))
                .kvDurableTimeout(Duration.ofMillis(keyValueDurableTimeout))
                .managementTimeout(Duration.ofMillis(managementTimeout))
                .queryTimeout(Duration.ofMillis(queryTimeout))
                .searchTimeout(Duration.ofMillis(searchTimeout))
                .viewTimeout(Duration.ofMillis(viewTimeout));
    }

    protected void applySecurityConfig(SecurityConfig.Builder config) {
        ApimsCouchbaseAutoConfigurationHelper.applySecurityConfig(
                config,
                tlsEnabled,
                tlsHostnameVerificationEnabled,
                tlsTrustStoreType,
                tlsTrustStoreLocation,
                tlsTrustStorePassword);
    }

    protected void applyIoConfig(IoConfig.Builder config) {
        config.networkResolution(NetworkResolution.valueOf(networkResolution));
    }

    protected RetryStrategy getRetryStrategy() {

        return BestEffortRetryStrategy.withExponentialBackoff(
                Duration.ofMillis(retryStrategyFirstBackoffMillis),
                Duration.ofMillis(retryStrategyMaxBackoffMillis),
                retryStrategyBackoffFactor);
    }

    @Primary
    @Bean
    @ConditionalOnProperty(
            name = {"spring.cloud.vault.enabled", "apims.couchbase.enabled", "apims.couchbase.encryption.enabled"},
            havingValue = "true")
    CryptoManager vaultCryptoManager(VaultTemplate vaultTemplate) {
        return new ApimsCouchbaseVaultCryptoManager(vaultTemplate);
    }

    @Bean
    @ConditionalOnProperty(
            name = {"apims.couchbase.enabled", "apims.couchbase.encryption.enabled"},
            havingValue = "true")
    CryptoManager plainCryptoManager() {
        return new ApimsCouchbasePlainCryptoManager();
    }
}
