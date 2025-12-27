/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.environment.core;

import de.sky.newcrm.apims.spring.exceptions.ApimsRuntimeException;
import de.sky.newcrm.apims.spring.utils.AssertUtils;
import de.sky.newcrm.apims.spring.utils.FunctionUtils;
import de.sky.newcrm.apims.spring.utils.IdUtils;
import de.sky.newcrm.apims.spring.utils.ObjectUtils;
import de.sky.newcrm.apims.spring.utils.scanner.ApimsClassScanner;
import java.net.InetAddress;
import java.util.*;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@SuppressWarnings({"java:S1075", "java:S1192", "java:S2629", "java:S6212"})
public class ApimsEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final ShadowStartupLog SHADOW_STARTUP_LOG = new ShadowStartupLog();
    private final YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
    private final Map<String, Resource> resourceMap = new HashMap<>();
    private final Map<String, Object> forcedProperties = ApimsSpringContext.getForcedProperties();
    private final MapPropertySource forcedPropertiesSource =
            new MapPropertySource(ApimsSpringContext.APIMS_FORCED_PROPERTIES, forcedProperties);

    static List<ShadowStartupLogItem> getPreStartupLogMessages() {
        return SHADOW_STARTUP_LOG.getMessages();
    }

    @Override
    @SuppressWarnings({"java:S2259"})
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

        getPreStartupLogMessages().clear();
        forcedProperties.clear();
        forcedProperties.put("apims.app.parent-version", ApimsVersion.getParentVersion());
        forcedProperties.put("apims.app.build-version", ApimsVersion.getAppBuildVersion());

        String[] springProfiles = environment.getActiveProfiles();
        // check application.yml exists
        PropertySource<?> applicationPropertySource = findYamlFilePropertySourceByProfile(environment, "");
        AssertUtils.notNullCheck("application.yml", applicationPropertySource);
        // find first application*.yml
        PropertySource<?> firstApplicationPropertySource = findFirstYamlFilePropertySource(environment);
        environment.getPropertySources().addBefore(firstApplicationPropertySource.getName(), forcedPropertiesSource);

        for (int i = springProfiles.length - 1; i >= 0; i--) {
            String springProfile = springProfiles[i];
            // add profile default file after last yml - application-[profile].yml, if not exists: before initial first
            // application*.yml
            loadAndAddPropertySourceByProfile(environment, resolveAppConfigProfile(springProfile), null, false);
        }

        String appConfigProfile = getAppConfigProfile(environment);
        if (StringUtils.hasLength(appConfigProfile)) {
            // add env profile default file after last yml
            loadAndAddPropertySourceByProfile(environment, appConfigProfile, null, false);
        }
        // add default file after last yml
        loadAndAddPropertySourceByProfile(environment, "default", null, true);

        validateProperties(environment);

        for (Map.Entry<String, Object> entry : forcedProperties.entrySet()) {
            SHADOW_STARTUP_LOG.info(
                    "property '%s' overwritten with value '%s'".formatted(entry.getKey(), entry.getValue()));
        }

        ApimsSpringContext.setEnvironment(environment);
        ApimsSpringContext.setSpringApplication(application);
    }

    @SuppressWarnings("java:S1452")
    protected PropertySource<?> findFirstYamlFilePropertySource(ConfigurableEnvironment environment) {
        for (PropertySource<?> prpSource : environment.getPropertySources()) {
            if (isYamlFilePropertySource(prpSource.getName())) {
                return prpSource;
            }
        }
        return null;
    }

    private PropertySource<?> findLastYamlFilePropertySource(ConfigurableEnvironment environment) {
        PropertySource<?> propertySource = null;
        for (PropertySource<?> prpSource : environment.getPropertySources()) {
            if (isYamlFilePropertySource(prpSource.getName())) {
                propertySource = prpSource;
            }
        }
        return propertySource;
    }

    private boolean isYamlFilePropertySource(String name) {
        return name.startsWith("Config resource 'class path resource [")
                || name.startsWith("configsets/")
                || name.equals("application.yml");
    }

    private boolean isUnitTestProfile(String name) {
        return "unit-test".equalsIgnoreCase(name);
    }

    private PropertySource<?> findYamlFilePropertySourceByProfile(
            ConfigurableEnvironment environment, String profileName) {
        String profileFileName = StringUtils.hasLength(profileName) ? ("-" + profileName) : "";
        final String applicationSourceNameTemplate =
                "Config resource 'class path resource [application%s.yml]' via location 'optional:classpath:/'";
        String applicationSourceName = applicationSourceNameTemplate.formatted(profileFileName);
        PropertySource<?> propertySource = environment.getPropertySources().get(applicationSourceName);
        if (propertySource != null) {
            return propertySource;
        }
        String lookupContains = "application%s.yml".formatted(profileFileName);
        for (PropertySource<?> prpSource : environment.getPropertySources()) {
            String name = prpSource.getName();
            if (name.contains(lookupContains)) {
                return prpSource;
            }
        }
        return null;
    }

    protected void loadAndAddPropertySourceByProfile(
            ConfigurableEnvironment environment, String profile, String appendAfter, boolean mandatory) {
        loadAndAddPropertySourceByProfile(environment, profile, appendAfter, null, mandatory);
    }

    protected void loadAndAddPropertySourceByProfile(
            ConfigurableEnvironment environment,
            String profile,
            String addAfterProfile,
            String addBeforePropertySourceName,
            boolean mandatory) {
        String configSet = getAppConfigSet(environment);
        String configPath = "configsets/" + configSet;
        profile = profile.toLowerCase();
        String fileName = "default".equalsIgnoreCase(profile)
                ? "apims-spring-default.yml"
                : "apims-spring-default___" + profile + ".yml";
        String configPathValue = configPath + "/" + fileName;
        loadAndAddPropertySource(
                environment, profile, configPathValue, addAfterProfile, addBeforePropertySourceName, mandatory);
    }

    @SuppressWarnings({"java:S2259"})
    private void loadAndAddPropertySource(
            ConfigurableEnvironment environment,
            String profile,
            String path,
            String addAfterProfile,
            String addBeforePropertySourceName,
            boolean mandatory) {
        PropertySource<?> propertySource = loadYaml(path, mandatory);
        if (propertySource != null) {
            if (isUnitTestProfile(profile)) {
                PropertySource<?> prpSource = findYamlFilePropertySourceByProfile(environment, profile);
                if (prpSource == null) {
                    prpSource = findFirstYamlFilePropertySource(environment);
                    environment.getPropertySources().addBefore(prpSource.getName(), propertySource);
                } else {
                    environment.getPropertySources().addAfter(prpSource.getName(), propertySource);
                }
            } else {
                PropertySource<?> prpSource = findLastYamlFilePropertySource(environment);
                environment.getPropertySources().addAfter(prpSource.getName(), propertySource);
            }
        }
    }

    private String getOverwrittenAppConfigProfile(ConfigurableEnvironment environment) {
        return environment.getProperty("apims.app.config-profile");
    }

    private String getOverwrittenAppConfigSet(ConfigurableEnvironment environment) {
        return environment.getProperty("apims.app.config-set");
    }

    private String getAppConfigProfile(ConfigurableEnvironment environment) {
        List<String> appConfigProfileKeyList = new ArrayList<>();
        // overwritten by service/stream application*.yml?
        String configEnvName = getOverwrittenAppConfigProfile(environment);
        if (StringUtils.hasLength(configEnvName)) {
            return configEnvName;
        }
        appConfigProfileKeyList.add("NAMESPACE");
        appConfigProfileKeyList.add("ENV_NAME");
        for (String profileKey : appConfigProfileKeyList) {
            String appConfigProfile = getEnv(profileKey, null);
            if (StringUtils.hasLength(appConfigProfile)) {
                return resolveAppConfigProfile(appConfigProfile);
            }
        }
        return null;
    }

    protected String resolveAppConfigProfile(String appConfigProfile) {
        return StringUtils.hasLength(appConfigProfile)
                        && appConfigProfile.startsWith("dev-")
                        && !appConfigProfile.equalsIgnoreCase("dev-int")
                ? "dev-domain"
                : appConfigProfile;
    }

    private String getAppConfigSet(ConfigurableEnvironment environment) {
        String overwrittenConfigSet = getOverwrittenAppConfigSet(environment);
        return StringUtils.hasLength(overwrittenConfigSet) ? overwrittenConfigSet : getEnv("CONFIG_SET", "v1.0.0");
    }

    protected String getEnv(String name, String defaultValue) {
        String v = System.getProperty(name, null);
        if (StringUtils.hasLength(v)) {
            return v;
        }
        v = System.getenv(name);
        return StringUtils.hasLength(v) ? v : defaultValue;
    }

    @SuppressWarnings("java:S1452")
    protected PropertySource<?> loadYaml(String name, boolean mandatory) {
        return loadYaml(name, name, mandatory);
    }

    @SuppressWarnings("java:S1452")
    protected PropertySource<?> loadYaml(String name, String pathName, boolean mandatory) {
        if (resourceMap.containsKey(name)) {
            return null;
        }
        Resource path = new ClassPathResource(pathName);
        if (!path.exists()) {
            resourceMap.put(name, path);
            String msg = "spring default configuration '%s' not exists.".formatted(pathName);
            if (mandatory) {
                throw new IllegalStateException(msg);
            }
            return null;
        }
        PropertySource<?> propertySource =
                FunctionUtils.execute(() -> this.loader.load(name, path).get(0), ApimsRuntimeException.class);
        resourceMap.put(name, path);
        SHADOW_STARTUP_LOG.info("spring default configuration '%s' registered.".formatted(pathName));
        return propertySource;
    }

    protected void validateProperties(ConfigurableEnvironment environment) {
        validateApimsAppName(environment);
        validateApimsAppDomainName(environment);
        validateApimsAppTeamName(environment);
        validateApimsAppType(environment);
        validateApimsAppHostname(environment);
        validateIncidentMgmtServiceCi(environment);
        validateApimsInstanceId(environment);
        validateVaultInjectedValues(environment);
        validateCouchbaseConfiguration(environment);
        validateHealthReadinessGroup(environment);
        validateForbiddenProperties(environment);
    }

    protected void validateApimsAppDomainName(ConfigurableEnvironment environment) {
        String envAppDomain = environment.getProperty("APP_DOMAIN");
        String apimsAppDomain = environment.getProperty("apims.app.domain");
        String checkAppDomain = apimsAppDomain;
        if (StringUtils.hasLength(envAppDomain) && !envAppDomain.equals(apimsAppDomain)) {
            checkAppDomain = envAppDomain;
        }
        ApimsAppDomainEnum domainEnum = ApimsAppDomainEnum.fromValue(checkAppDomain);
        // cicd use "default" for "no domain"
        if (ApimsAppDomainEnum.DEFAULT.equals(domainEnum)) {
            domainEnum = ApimsAppDomainEnum.NO_DOMAIN;
        }
        if (ApimsAppDomainEnum.UNKNOWN.equals(domainEnum)) {
            String msg =
                    "'apims.app.domain' = '%s' is invalid. Check the env value of 'APP_DOMAIN' and the application value of 'apims.app.domain'!"
                            .formatted(checkAppDomain);
            SHADOW_STARTUP_LOG.warn(msg);
            if (Boolean.TRUE.equals(Boolean.parseBoolean(
                    environment.getProperty("apims.startup-check.throw-exception-on-invalid-domain-enum", "true")))) {
                throw new IllegalStateException("[Assertion failed] - %s".formatted(msg));
            }
        } else if (!domainEnum.getValue().equals(apimsAppDomain)) {
            forcedProperties.put("apims.app.domain", domainEnum.getValue());
        }
    }

    protected void validateApimsAppTeamName(ConfigurableEnvironment environment) {
        String envAppTeam = environment.getProperty("APP_TEAM");
        String apimsAppTeam = environment.getProperty("apims.app.team");
        String checkAppTeam = apimsAppTeam;
        if (StringUtils.hasLength(envAppTeam) && !envAppTeam.equals(apimsAppTeam)) {
            checkAppTeam = envAppTeam;
        }
        ApimsAppTeamEnum teamEnum = ApimsAppTeamEnum.fromValue(checkAppTeam);
        if (ApimsAppTeamEnum.UNKNOWN.equals(teamEnum)) {
            teamEnum = ApimsAppTeamEnum.findFirstByDomain(
                    ApimsAppDomainEnum.fromValue(environment.getProperty("apims.app.domain")));
        }
        if (ApimsAppTeamEnum.UNKNOWN.equals(teamEnum)) {
            String msg =
                    "'apims.app.team' = '%s' is invalid. Check the env value of 'APP_TEAM' and the application value of 'apims.app.team'!"
                            .formatted(checkAppTeam);
            SHADOW_STARTUP_LOG.warn(msg);
            if (Boolean.TRUE.equals(Boolean.parseBoolean(
                    environment.getProperty("apims.startup-check.throw-exception-on-invalid-team-enum", "true")))) {
                throw new IllegalStateException("[Assertion failed] - %s".formatted(msg));
            }
        } else if (!teamEnum.name().equals(apimsAppTeam)) {
            forcedProperties.put("apims.app.team", teamEnum.name());
        }
    }

    protected void validateApimsAppType(ConfigurableEnvironment environment) {
        String appType = environment.getProperty("apims.app.type");
        ApimsAppTypeEnum typeEnum = ApimsAppTypeEnum.fromValue(appType);
        if (ApimsAppTypeEnum.STREAM.equals(typeEnum) || ApimsAppTypeEnum.CLI.equals(typeEnum)) {
            String serverPort = environment.getProperty("server.port");
            if (!"-1".equals(serverPort)) {
                forcedProperties.put("server.port", "-1");
            }
        }
        if (ApimsAppTypeEnum.CLI.equals(typeEnum)) {
            String cliEnabled = environment.getProperty("apims.cli.enabled");
            if (!"true".equalsIgnoreCase(cliEnabled)) {
                forcedProperties.put("apims.cli.enabled", "true");
            }
        }
        if (!typeEnum.name().equals(appType)) {
            forcedProperties.put("apims.app.type", typeEnum.name());
        }
    }

    protected void validateApimsAppName(ConfigurableEnvironment environment) {
        String apimsAppName = environment.getProperty("apims.app.name");
        Assert.state(StringUtils.hasLength(apimsAppName), "[Assertion failed] - apims.app.name is not set!");
        Assert.state(
                !apimsAppName.toLowerCase().contains("unknown"),
                "[Assertion failed] - apims.app.name is not configured!");
        forcedProperties.put("apims.app.resource-name", apimsAppName.replace("-", "_"));
    }

    @ApimsReportGeneratedHint
    protected void validateApimsAppHostname(ConfigurableEnvironment environment) {
        String apimsAppHostname = environment.getProperty("apims.app.host");
        if (!StringUtils.hasLength(apimsAppHostname)
                || "localhost".equalsIgnoreCase(apimsAppHostname)
                || "127.0.0.1".equals(apimsAppHostname)) {
            try {
                apimsAppHostname = InetAddress.getLocalHost().getHostName();
            } catch (Exception ignore) {
                // ignore
            }
            if (!StringUtils.hasLength(apimsAppHostname)) {
                apimsAppHostname = "localhost";
            }
            forcedProperties.put("apims.app.host", apimsAppHostname);
        }
    }

    @ApimsReportGeneratedHint
    protected void validateApimsInstanceId(ConfigurableEnvironment environment) {
        String apimsAppInstanceId = environment.getProperty("apims.app.instance-id");
        if (!StringUtils.hasLength(apimsAppInstanceId)) {
            String apimsAppHost = environment.getProperty("apims.app.host");
            String apimsAppName = environment.getProperty("apims.app.name");
            if (StringUtils.hasLength(apimsAppHost)
                    && StringUtils.hasLength(apimsAppName)
                    && apimsAppHost.length() > apimsAppName.length()
                    && apimsAppHost.contains(apimsAppName)) {
                apimsAppInstanceId = apimsAppHost.substring(apimsAppHost.indexOf(apimsAppName) + apimsAppName.length());
                if (apimsAppInstanceId.startsWith("-")) {
                    apimsAppInstanceId = apimsAppInstanceId.substring(1);
                }
            }
            if (!StringUtils.hasLength(apimsAppInstanceId)) {
                apimsAppInstanceId = IdUtils.nextId();
            }
            forcedProperties.put("apims.app.instance-id", apimsAppInstanceId);
        }
    }

    protected void validateVaultInjectedValues(ConfigurableEnvironment environment) {
        if ("false".equals(environment.getProperty("apims.startup-check.check-vault-values", "true"))) {
            return;
        }
        Map<String, ApimsSpringEnvironmentInfo.EnvironmentItem> map =
                new ApimsSpringEnvironmentInfo(environment).getResolvedEnvironmentInfoMap();
        List<String> invalidProperties = new ArrayList<>();
        for (Map.Entry<String, ApimsSpringEnvironmentInfo.EnvironmentItem> entry : map.entrySet()) {
            FunctionUtils.INSTANCE.acceptIfCondition(
                    "<no value>".equals(entry.getValue().getValue()), true, entry.getKey(), invalidProperties::add);
        }
        Assert.state(
                invalidProperties.isEmpty(),
                "[Assertion failed] - invalid property values ('<no value>') found! Please check your vault configuration (deployment.yaml) : "
                        + StringUtils.collectionToCommaDelimitedString(invalidProperties));
    }

    @ApimsReportGeneratedHint
    protected void validateCouchbaseConfiguration(ConfigurableEnvironment environment) {
        boolean autoResolveEnabledFlag =
                Boolean.parseBoolean(environment.getProperty("apims.couchbase.auto-resolve-enabled-flag"));
        if (!autoResolveEnabledFlag) {
            return;
        }
        boolean couchbaseEnabled = Boolean.parseBoolean(environment.getProperty("apims.couchbase.enabled"));
        boolean couchbaseCachingEnabled =
                Boolean.parseBoolean(environment.getProperty("apims.couchbase.caching.enabled"));
        String couchbaseAuthMethod = environment.getProperty("spring.couchbase.env.security.auth-method");
        boolean couchbaseAuthMethodKeystore = "keystore".equals(couchbaseAuthMethod);
        boolean couchbasePasswordConfigured =
                StringUtils.hasLength(environment.getProperty("spring.couchbase.password"));
        boolean couchbaseNeeded =
                couchbaseCachingEnabled || !couchbaseAuthMethodKeystore && couchbasePasswordConfigured;
        if (!couchbaseNeeded && couchbaseAuthMethodKeystore) {
            String basePackage = environment.getProperty("spring.couchbase.env.repositories-base-package");
            if (!StringUtils.hasLength(basePackage)) {
                basePackage = "de.sky";
            }
            Set<Class<?>> repositoryClasses = getCouchbaseRepositoryClasses(basePackage);
            couchbaseNeeded = !repositoryClasses.isEmpty();
            SHADOW_STARTUP_LOG.info(
                    "Property spring.couchbase.env.security.auth-method is 'keystore'. auto detected repository classes: %s"
                            .formatted(repositoryClasses.size()));
        }
        if (couchbaseNeeded != couchbaseEnabled) {
            forcedProperties.put("apims.couchbase.enabled", String.valueOf(couchbaseNeeded));
        }
    }

    @ApimsReportGeneratedHint
    protected void validateHealthReadinessGroup(ConfigurableEnvironment environment) {
        boolean couchbaseEnabled = Boolean.parseBoolean(environment.getProperty("apims.couchbase.enabled"));
        boolean couchbaseMockEnabled =
                Boolean.parseBoolean(environment.getProperty("apims.app.mocks.couchbase-mock-enabled"));
        if (couchbaseEnabled && !couchbaseMockEnabled) {
            String groupReadinessInclude =
                    environment.getProperty("management.endpoint.health.group.readiness.include");
            String changedGroupReadinessInclude = groupReadinessInclude;
            if (!StringUtils.hasLength(changedGroupReadinessInclude)) {
                changedGroupReadinessInclude = "livenessProbe";
            }
            if (!changedGroupReadinessInclude.contains("couchbase")) {
                changedGroupReadinessInclude += ",couchbase";
            }
            if (!changedGroupReadinessInclude.equals(groupReadinessInclude)) {
                forcedProperties.put(
                        "management.endpoint.health.group.readiness.include", changedGroupReadinessInclude);
            }
        }
    }

    @ApimsReportGeneratedHint
    protected void validateForbiddenProperties(ConfigurableEnvironment environment) {
        if ("false"
                .equals(environment.getProperty(
                        "apims.startup-check.check-forbidden-application-properties", "true"))) {
            return;
        }
        List<String> invalidProperties =
                findForbiddenProperties(environment, findYamlFilePropertySourceByProfile(environment, ""));
        if (!invalidProperties.isEmpty()) {
            throw new IllegalStateException("Forbidden properties found in application.yml:\n"
                    + StringUtils.collectionToDelimitedString(invalidProperties, "\n"));
        }
    }

    @ApimsReportGeneratedHint
    @SuppressWarnings({"java:S3776"})
    protected List<String> findForbiddenProperties(
            ConfigurableEnvironment environment, PropertySource<?> applicationYamlPropertySource) {
        List<String> invalidProperties = new ArrayList<>();
        if (applicationYamlPropertySource != null) {
            String[] checkProperties = StringUtils.tokenizeToStringArray(
                    environment.getProperty("apims.startup-check.forbidden-application-properties", ""), ",");
            for (String checkProperty : checkProperties) {
                if (checkProperty.endsWith(".*")) {
                    checkProperty = checkProperty.substring(0, checkProperty.length() - 1);
                    if (applicationYamlPropertySource instanceof EnumerablePropertySource<?> source) {
                        for (String propertyName : source.getPropertyNames()) {
                            if (propertyName.startsWith(checkProperty)) {
                                invalidProperties.add(propertyName + ": \""
                                        + applicationYamlPropertySource.getProperty(propertyName) + "\"");
                            }
                        }
                    }
                } else {
                    Object object = applicationYamlPropertySource.getProperty(checkProperty);
                    if (object != null) {
                        invalidProperties.add(checkProperty + ": \"" + object + "\"");
                    }
                }
            }
        }
        return invalidProperties;
    }

    @ApimsReportGeneratedHint
    protected void validateIncidentMgmtServiceCi(ConfigurableEnvironment environment) {
        if (environment.getProperty("apims.app.incident-mgmt.service-ci") == null) {
            Stream.of(environment.getProperty("apims.app.domain"))
                    .map(domain -> "apims.app.incident-mgmt.default-cis.domains." + domain)
                    .map(environment::getProperty)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .ifPresent(defaultServiceCi ->
                            forcedProperties.put("apims.app.incident-mgmt.service-ci", defaultServiceCi));
        }
    }

    @SuppressWarnings({"java:S4449"})
    @ApimsReportGeneratedHint
    protected Set<Class<?>> getCouchbaseRepositoryClasses(String basePackage) {
        List<Class<?>> classList = ApimsClassScanner.findClasses(
                org.springframework.data.couchbase.repository.Collection.class, true, basePackage);
        classList.removeIf(c -> ObjectUtils.findClassAnnotation(c, NoRepositoryBean.class, true) != null);
        return new HashSet<>(classList);
    }

    static class ShadowStartupLog {

        final List<ShadowStartupLogItem> messages = new ArrayList<>();

        public List<ShadowStartupLogItem> getMessages() {
            return messages;
        }

        void info(String message) {
            messages.add(new ShadowStartupLogItem(false, message));
        }

        void warn(String message) {
            messages.add(new ShadowStartupLogItem(true, message));
        }
    }

    @Getter
    @RequiredArgsConstructor
    static class ShadowStartupLogItem {

        private final boolean warn;
        private final String message;
    }
}
