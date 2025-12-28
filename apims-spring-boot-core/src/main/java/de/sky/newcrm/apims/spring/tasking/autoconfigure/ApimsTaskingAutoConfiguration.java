/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.tasking.autoconfigure;

import de.sky.newcrm.apims.spring.async.autoconfigure.ApimsAsyncAutoConfigurationHelper;
import de.sky.newcrm.apims.spring.tasking.config.ApimsTaskingConfig;
import de.sky.newcrm.apims.spring.tasking.core.ApimsExecutor;
import de.sky.newcrm.apims.spring.tasking.core.ApimsRunnableWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableAsync;

@ConditionalOnProperty(prefix = "apims.tasking", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableAsync
@Configuration
@EnableConfigurationProperties(ApimsTaskingConfig.class)
public class ApimsTaskingAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ApimsTaskingAutoConfiguration.class);

    private final ApimsTaskingConfig apimsTaskingConfig;

    public ApimsTaskingAutoConfiguration(ApimsTaskingConfig apimsTaskingConfig) {
        log.debug("[APIMS AUTOCONFIG] Tasking.");
        this.apimsTaskingConfig = apimsTaskingConfig;
    }

    @Bean
    @ConditionalOnMissingBean
    public ApimsExecutor apimsTaskExecuter(
            @Value("${apims.app.mocks.tasking-mock-enabled:false}") boolean mockEnabled) {
        return ApimsAsyncAutoConfigurationHelper.createExecuter(apimsTaskingConfig, mockEnabled);
    }

    @Bean()
    @ConditionalOnMissingBean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ApimsRunnableWrapper apimsRunnableWrapper() {
        return new ApimsRunnableWrapper();
    }
}
