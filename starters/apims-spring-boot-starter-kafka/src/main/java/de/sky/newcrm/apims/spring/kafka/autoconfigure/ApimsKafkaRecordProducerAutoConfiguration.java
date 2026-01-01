/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.autoconfigure;

import de.sky.newcrm.apims.spring.kafka.core.integration.ApimsKafkaSchemaClient;
import de.sky.newcrm.apims.spring.kafka.core.serializers.ApimsAvroRecordProducer;import de.sky.newcrm.apims.spring.kafka.core.serializers.ApimsKafkaRecordProducer;import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "apims.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ApimsKafkaRecordProducerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean()
    public ApimsAvroRecordProducer apimsAvroRecordProducer(Optional<ApimsKafkaSchemaClient> apimsKafkaSchemaClient) {
        return new ApimsAvroRecordProducer(apimsKafkaSchemaClient.orElse(null));
    }

    @Bean
    @ConditionalOnMissingBean()
    public ApimsKafkaRecordProducer apimsKafkaRecordProducer() {
        return new ApimsKafkaRecordProducer();
    }
}
