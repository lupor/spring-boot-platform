/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.autoconfigure.rest;

import de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3.DefaultJacksonObjectFactory;
import de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3.DefaultJacksonXMLObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.boot.jackson.autoconfigure.XmlMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "apims.server.rest", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ApimsRestObjectMapperAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ApimsRestObjectMapperAutoConfiguration.class);

    public ApimsRestObjectMapperAutoConfiguration() {
        log.debug("[APIMS AUTOCONFIG] Rest Jackson.");
    }


    @Bean
    public JsonMapperBuilderCustomizer jsonMapperCustomizer() {
        return DefaultJacksonObjectFactory::customizeJsonMapperBuilder; // Example: Enable pretty printing
    }

    @Bean
    public XmlMapperBuilderCustomizer xmlMapperCustomizer() {
        return DefaultJacksonXMLObjectFactory::customizeXmlMapperBuilder; // Example: Enable pretty printing
    }
}
