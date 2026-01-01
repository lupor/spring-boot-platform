/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.autoconfigure;

import de.sky.newcrm.apims.spring.environment.config.ApimsAppConfig;import de.sky.newcrm.apims.spring.environment.core.ApimsValueResolver;import de.sky.newcrm.apims.spring.kafka.config.ApimsKafkaConfig;import de.sky.newcrm.apims.spring.kafka.core.*;import de.sky.newcrm.apims.spring.kafka.core.serializers.ApimsKafkaRecordProducer;import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.OrderComparator;
import org.springframework.kafka.retrytopic.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({KafkaProperties.class})
@SuppressWarnings({"java:S6830"})
class ApimsKafkaRetryTopicAutoConfiguration extends RetryTopicConfigurationSupport {

    private RetryTopicComponentFactory componentFactory;

    private final ApimsAppConfig apimsAppConfig;

    private final ApimsKafkaConfig apimsKafkaConfig;

    ApimsRetryTopicConfigurationPropertiesMap apimsRetryTopicConfigurationPropertiesMap;

    @Override
    public DestinationTopicResolver destinationTopicResolver(
            ObjectProvider<RetryTopicComponentFactory> componentFactoryProvider) {
        return new ApimsDefaultDestinationTopicResolver(
                componentFactory.internalRetryTopicClock(), apimsRetryTopicConfigurationPropertiesMap);
    }

    @Override
    protected RetryTopicComponentFactory createComponentFactory() {
        if (componentFactory == null) {
            componentFactory = new ApimsRetryTopicComponentFactory();
        }
        return componentFactory;
    }

    public ApimsKafkaRetryTopicAutoConfiguration(
            @Autowired ApimsAppConfig apimsAppConfig,
            @Autowired ApimsKafkaConfig apimsKafkaConfig
    ) {
        this.apimsAppConfig = apimsAppConfig;
        this.apimsKafkaConfig = apimsKafkaConfig;
        ApimsKafkaConfig.KafkaConsumer apimsConsumerProperties =
                apimsKafkaConfig.getConsumer();
        ApimsKafkaConfig.KafkaConsumer.Dlt apimsDltProperties = apimsConsumerProperties.getDlt();
        Map<String, String> topics = apimsConsumerProperties.getTopics();
        Map<String, ApimsRetryTopicConfigurationProperties> configMap = new HashMap<>();
        for (Map.Entry<String, String> entry : topics.entrySet()) {
            ApimsKafkaConfig.KafkaConsumer.TopicRetryProperties dltProps =
                    apimsConsumerProperties.getRetries().get(entry.getKey());
            configMap.put(
                    entry.getValue(),
                    ApimsRetryTopicConfigurationProperties.builder()
                            .enabled(apimsDltProperties.isEnabled())
                            .resourcePrefix(apimsAppConfig.getResourcePrefix())
                            .groupBasedRetryAndDltTopics(apimsDltProperties.isGroupBasedRetryAndDltTopics())
                            .defaultGroupId(apimsConsumerProperties.getGroupId())
                            .retryTopicSuffix(apimsDltProperties.getRetryTopicSuffix())
                            .dltTopicSuffix(apimsDltProperties.getDltTopicSuffix())
                            .retryAttempts(
                                    dltProps != null && !(dltProps.getRetryAttempts() < 0)
                                            ? dltProps.getRetryAttempts()
                                            : apimsDltProperties.getRetryAttempts())
                            .lastRetryAttempts(
                                    dltProps != null && !(dltProps.getLastRetryAttempts() < 0)
                                            ? dltProps.getLastRetryAttempts()
                                            : apimsDltProperties.getLastRetryAttempts())
                            .delay(
                                    dltProps != null && !(dltProps.getDelay() < 0)
                                            ? dltProps.getDelay()
                                            : apimsDltProperties.getDelay())
                            .multiplier(
                                    dltProps != null && !(dltProps.getMultiplier() < 0)
                                            ? dltProps.getMultiplier()
                                            : apimsDltProperties.getMultiplier())
                            .listenerContainerFactory(apimsDltProperties.getListenerContainerFactory())
                            .autoCreateTopics(apimsDltProperties.isAutoCreateTopics())
                            .autoCreateNumPartitions(apimsDltProperties.getAutoCreateNumPartitions())
                            .autoCreateReplicationFactor(apimsDltProperties.getAutoCreateReplicationFactor())
                            .useDltTopicOnNoRetryableException(apimsDltProperties.isUseDltTopicOnNoRetryableException())
                            .build());
        }
        this.apimsRetryTopicConfigurationPropertiesMap = ApimsRetryTopicConfigurationPropertiesMap.builder()
                .propertiesMap(configMap)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean()
    @SuppressWarnings("unchecked")
    public ApimsRetryTopicConfigurationPropertiesMap apimsRetryTopicConfigurationProperties() {
        return apimsRetryTopicConfigurationPropertiesMap;
    }

    @Bean
    @ConditionalOnMissingBean()
    public ApimsDeadLetterLoggingAction apimsDeadLetterLoggingAction() {
        return new ApimsDeadLetterLoggingAction();
    }

    @Bean
    @ConditionalOnMissingBean()
    @ConditionalOnProperty(
            prefix = "apims.kafka.consumer.dlt",
            name = "central-dlt-topic-enabled",
            havingValue = "true",
            matchIfMissing = false)
    public ApimsDeadLetterCentralTopicSendingAction apimsDeadLetterCentralTopicSendingAction() {
        return new ApimsDeadLetterCentralTopicSendingAction();
    }

    @Bean
    @ConditionalOnMissingBean()
    public ApimsDeadLetterHandler apimsDeadLetterHandler(
            ApimsKafkaRecordProducer kafkaRecordProducer, List<ApimsDeadLetterAction> receivers) {
        receivers.sort(new OrderComparator());
        return new ApimsDeadLetterHandler(kafkaRecordProducer, receivers);
    }

    @Bean
    @ConditionalOnMissingBean()
    public ApimsRetryTopicConfigurationResolver apimsRetryTopicConfigurationResolver(
            ApimsRetryTopicConfigurationPropertiesMap apimsRetryTopicConfigurationProperties,
            ApimsValueResolver apimsValueResolver,
            ApimsDeadLetterHandler apimsDeadLetterHandler) {
        return new ApimsRetryTopicConfigurationResolverDefaultImpl(
                apimsRetryTopicConfigurationProperties, apimsValueResolver, apimsDeadLetterHandler);
    }
}
