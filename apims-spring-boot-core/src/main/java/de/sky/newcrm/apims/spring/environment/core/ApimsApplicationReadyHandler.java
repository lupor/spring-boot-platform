/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.environment.core;

import de.sky.newcrm.apims.spring.serialization.core.mapper.ObjectMapperUtils;
import de.sky.newcrm.apims.spring.serialization.core.masker.ApimsAroundObjectMasker;
import de.sky.newcrm.apims.spring.telemetry.metrics.aspects.ApimsAroundMetricsListenerSuppress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.sky.newcrm.apims.spring.utils.AssertUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings({"java:S6212"})
public class ApimsApplicationReadyHandler implements BeanFactoryAware {

    private final ApimsSpringEnvironmentInfo environmentInfo;
    private final List<ApimsApplicationReadyListener> applicationReadylisteners;
    private final List<ApimsServiceStartupListener> applicationServiceStartupListeners;
    private boolean applicationReadyEventConsumed = false;
    private BeanFactory beanFactory = null;

    @Value("${apims.app.name:}")
    private String apimsAppName;

    @Value("${apims.app.build-version:}")
    private String apimsAppBuildVersion;

    @Value("${apims.app.type:}")
    private String apimsAppType;

    @Value("${apims.app.service-startup-listener-enabled:true}")
    private boolean serviceStartupListenerEnabled;

    @Override
    @ApimsAroundMetricsListenerSuppress
    public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReadyEvent() throws Exception {

        if (applicationReadyEventConsumed) {
            return;
        }
        applicationReadylisteners.sort(new OrderComparator());
        applicationServiceStartupListeners.sort(new OrderComparator());
        logPostProcessorPreStartupLogMessages();
        logInfos();
        applicationReadyEventConsumed = true;
        SpringApplication springApplication = ApimsSpringContext.getSpringApplication();
        informApplicationReadyListeners();
        AssertUtils.notNullCheck("SHeck if main application class is not null", springApplication.getMainApplicationClass());
        assert springApplication.getMainApplicationClass() != null;
        log.info(
                "SpringApplication {} loaded: {} {} : {} : {}",
                apimsAppName,
                apimsAppBuildVersion,
                apimsAppType,
                springApplication.getMainApplicationClass().getName(),
                LocalDateTime.now());
        informServiceStartupListeners();
    }

    protected void logPostProcessorPreStartupLogMessages() {
        List<ApimsEnvironmentPostProcessor.ShadowStartupLogItem> preStartupLogMessages =
                ApimsEnvironmentPostProcessor.getPreStartupLogMessages();
        for (ApimsEnvironmentPostProcessor.ShadowStartupLogItem preStartupLogMessage : preStartupLogMessages) {
            if (preStartupLogMessage.warn()) {
                log.warn(preStartupLogMessage.message());
            } else {
                log.info(preStartupLogMessage.message());
            }
        }
    }

    @SuppressWarnings({"java:S112", "java:S2629", "java:S3457", "java:S3776"})
    @ApimsReportGeneratedHint
    protected void logInfos() throws Exception {
        if (!log.isTraceEnabled()) {
            return;
        }
        ApimsAroundObjectMasker aroundObjectMasker;
        try {
            aroundObjectMasker = ApimsSpringContext.getApplicationContext().getBean(ApimsAroundObjectMasker.class);
        } catch (BeansException _) {
            return;
        }
        Map<String, ApimsSpringEnvironmentInfo.EnvironmentItem> environmentItemMap =
                environmentInfo.getResolvedEnvironmentInfoMap();
        List<String> maskKeys = new ArrayList<>(aroundObjectMasker.getMaskKeys());
        String maskValue = aroundObjectMasker.getMaskValue();
        if (!maskKeys.contains("password")) {
            maskKeys.add("password");
        }
        for (Map.Entry<String, ApimsSpringEnvironmentInfo.EnvironmentItem> entry : environmentItemMap.entrySet()) {
            for (String maskKey : maskKeys) {
                if (entry.getKey().toLowerCase().contains(maskKey.toLowerCase())) {
                    entry.getValue().setValue(maskValue);
                }
            }
        }
        log.trace("[APIMS CONFIG] - \n{}", ObjectMapperUtils.writeValueAsString(environmentItemMap));
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) beanFactory;
        String[] names = StringUtils.sortStringArray(defaultListableBeanFactory.getBeanDefinitionNames());
        for (String name : names) {
            BeanDefinition beanDefinition = defaultListableBeanFactory.getBeanDefinition(name);
            String clazzName = beanDefinition.getBeanClassName();
            if (clazzName != null && clazzName.startsWith("de.sky.newcrm")) {
                log.trace("[APIMS BEAN] - {} -> {}", name, clazzName);
            }
        }
    }

    protected void informApplicationReadyListeners() throws Exception {
        for (ApimsApplicationReadyListener listener : applicationReadylisteners) {
            listener.onApplicationReadyEvent();
        }
    }

    protected void informServiceStartupListeners() throws Exception {
        if (serviceStartupListenerEnabled) {
            for (ApimsServiceStartupListener listener : applicationServiceStartupListeners) {
                listener.execute();
            }
        }
    }
}
