/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.aspects.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sky.newcrm.apims.spring.serialization.core.mapper.JacksonObjectMapperBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "apims.aspects", name = "enabled", havingValue = "true", matchIfMissing = true)
@SuppressWarnings({"java:S6212"})
public class ApimsAspectsConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "aspectsJacksonBuilder")
    public JacksonObjectMapperBuilder aspectsJacksonBuilder() {
        return de.sky.newcrm.apims.spring.serialization.core.mapper.DefaultJacksonObjectFactory
                .buildJacksonObjectMapperBuilder();
    }

    @Bean
    @ConditionalOnMissingBean(name = "aspectsObjectMapper")
    public ObjectMapper aspectsObjectMapper(
            @Qualifier("aspectsJacksonBuilder") JacksonObjectMapperBuilder jacksonObjectMapperBuilder) {
        return jacksonObjectMapperBuilder.build();
    }
}
