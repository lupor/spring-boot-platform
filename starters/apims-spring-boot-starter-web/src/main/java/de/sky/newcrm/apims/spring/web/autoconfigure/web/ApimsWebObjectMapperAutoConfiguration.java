/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.autoconfigure.web;

import de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3.DefaultJacksonXMLObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jackson.autoconfigure.XmlMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "apims.web", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ApimsWebObjectMapperAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ApimsWebObjectMapperAutoConfiguration.class);

    public ApimsWebObjectMapperAutoConfiguration() {
        log.debug("[APIMS AUTOCONFIG] Web Jackson.");
    }

    @Bean
    public XmlMapperBuilderCustomizer xmlMapperCustomizer() {
        return DefaultJacksonXMLObjectFactory::customizeXmlMapperBuilder; // Example: Enable pretty printing
    }
}
