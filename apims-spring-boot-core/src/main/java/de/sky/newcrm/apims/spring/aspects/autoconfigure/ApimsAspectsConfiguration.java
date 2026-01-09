/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.aspects.autoconfigure;

import de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3.DefaultJacksonObjectFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@Configuration(proxyBeanMethods = false)
@SuppressWarnings({"java:S6212"})
public class ApimsAspectsConfiguration {
    @Bean
    @ConditionalOnMissingBean(name = "aspectsJacksonBuilder")
    public JsonMapper.Builder aspectsJacksonBuilder() {
        return DefaultJacksonObjectFactory.createDefaultJsonMapperBuilder();
    }

    @Bean
    @ConditionalOnMissingBean(name = "aspectsObjectMapper")
    public ObjectMapper aspectsObjectMapper(
            @Qualifier("aspectsJacksonBuilder") JsonMapper.Builder aspectsJacksonBuilder) {
        return aspectsJacksonBuilder.build();
    }
}
