/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.autoconfigure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sky.newcrm.apims.spring.core.objectmapper.DefaultJacksonObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "apims.web", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ApimsWebObjectMapperAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ApimsWebObjectMapperAutoConfiguration.class);

    public ApimsWebObjectMapperAutoConfiguration() {
        log.debug("[APIMS AUTOCONFIG] Web Jackson.");
    }

    @Bean(name = "webJacksonBuilder")
    @ConditionalOnMissingBean(name = "webJacksonBuilder")
    public Jackson2ObjectMapperBuilder webJacksonBuilder() {
        log.debug("[APIMS AUTOCONFIG] Web Jackson:webJacksonBuilder.");
        return DefaultJacksonObjectFactory.buildJackson2ObjectMapperBuilder();
    }

    @Bean(name = "webObjectMapper")
    @ConditionalOnMissingBean(name = "webObjectMapper")
    public ObjectMapper webObjectMapper(
            @Qualifier("webJacksonBuilder") Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder) {
        log.debug("[APIMS AUTOCONFIG] Web Jackson:webObjectMapper.");

        return jackson2ObjectMapperBuilder.build();
    }

    @Bean(name = "webJacksonBuilderXml")
    @ConditionalOnMissingBean(name = "webJacksonBuilderXml")
    public Jackson2ObjectMapperBuilder webJacksonBuilderXml() {
        log.debug("[APIMS AUTOCONFIG] Web Jackson:webJacksonBuilderXml.");
        return DefaultJacksonObjectFactory.buildJackson2ObjectXmlMapperBuilder();
    }

    @Bean(name = "webObjectMapperXml")
    @ConditionalOnMissingBean(name = "webObjectMapperXml")
    public ObjectMapper webObjectMapperXml(
            @Qualifier("webJacksonBuilderXml") Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder) {
        log.debug("[APIMS AUTOCONFIG] Web Jackson:webObjectMapperXml.");
        return jackson2ObjectMapperBuilder.build();
    }
}
