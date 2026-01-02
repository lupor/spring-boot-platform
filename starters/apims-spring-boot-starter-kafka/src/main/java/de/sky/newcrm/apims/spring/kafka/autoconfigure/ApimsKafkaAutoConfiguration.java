/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.autoconfigure;

import de.sky.newcrm.apims.spring.kafka.config.ApimsKafkaConfig;
import de.sky.newcrm.apims.spring.kafka.core.*;
import de.sky.newcrm.apims.spring.kafka.core.producer.ApimsKafkaProducerHeaderInterceptor;
import de.sky.newcrm.apims.spring.kafka.core.sender.ApimsKafkaGenericMessageSender;
import de.sky.newcrm.apims.spring.kafka.core.sender.ApimsKafkaGenericMessageToRetryTopicSender;
import de.sky.newcrm.apims.spring.kafka.core.serializers.ApimsAvroRecordProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.kafka.autoconfigure.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.kafka.autoconfigure.KafkaAutoConfiguration;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.converter.RecordMessageConverter;

import java.util.List;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(KafkaTemplate.class)
@AutoConfigureBefore({
        KafkaAutoConfiguration.class,
        ApimsKafkaRecordProducerAutoConfiguration.class,
        ApimsKafkaRetryTopicAutoConfiguration.class
})
@Import({ApimsKafkaBootstrapConfiguration.class})
@EnableKafka
@EnableConfigurationProperties({KafkaProperties.class, ApimsKafkaConfig.class})
@SuppressWarnings({"java:S6212"})
class ApimsKafkaAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ApimsKafkaAutoConfiguration.class);
    private final ApimsKafkaConfig apimsKafkaConfig;
    private final KafkaProperties kafkaProperties;

    public ApimsKafkaAutoConfiguration(ApimsKafkaConfig apimsKafkaConfig, KafkaProperties kafkaProperties) {
        log.debug("[APIMS AUTOCONFIG] Kafka.");
        this.apimsKafkaConfig = apimsKafkaConfig;
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    @ConditionalOnMissingBean()
    ApimsConcurrentKafkaListenerContainerFactoryConfigurer apimsConcurrentKafkaListenerContainerFactoryConfigurer() {
        return new ApimsConcurrentKafkaListenerContainerFactoryConfigurer();
    }

    @Bean
    @ConditionalOnMissingBean(name = "kafkaListenerContainerFactory")
    ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ApimsConcurrentKafkaListenerContainerFactoryConfigurer apimsConfigurer,
            ObjectProvider<ConsumerFactory<Object, Object>> kafkaConsumerFactory,
            @Value("${apims.kafka.consumer.dlt.dlt-topic-suffix:-dlt}") String dltTopixSuffix,
            @Value("${apims.kafka.consumer.dlt.retry-topic-suffix:-dlt}") String retryTopixSuffix) {
        ApimsConcurrentKafkaListenerContainerFactory<Object, Object> factory =
                new ApimsConcurrentKafkaListenerContainerFactory<>(dltTopixSuffix, retryTopixSuffix);
        ConsumerFactory<Object, Object> consumerFactory = kafkaConsumerFactory.getIfAvailable(
                () -> new DefaultKafkaConsumerFactory<>(this.kafkaProperties.buildConsumerProperties()));
        apimsConfigurer.beforeConfigurer(factory, consumerFactory);
        configurer.configure(factory, consumerFactory);
        apimsConfigurer.afterConfigurer(factory, consumerFactory);
        return factory;
    }

    @Bean
    @ConditionalOnMissingBean()
    public ApimsKafkaStartupListener apimsKafkaStartupListener(KafkaListenerEndpointRegistry endpointRegistry) {
        return new ApimsKafkaStartupListener(endpointRegistry);
    }

    @Bean
    @ConditionalOnMissingBean()
    public ApimsDefaultKafkaMessageHeaderWriter apimsDefaultKafkaMessageHeaderWriter() {
        return new ApimsDefaultKafkaMessageHeaderWriter(
                apimsKafkaConfig.getProducer().getHeaders(),
                apimsKafkaConfig.getProducer().getAdditionalHeaders());
    }

    @Bean
    @ConditionalOnMissingBean()
    public ApimsKafkaProducerHeaderInterceptor apimsKafkaProducerHeaderInterceptor(
            List<ApimsKafkaMessageHeaderWriter> apimsKafkaMessageHeaderWriter) {
        return new ApimsKafkaProducerHeaderInterceptor(apimsKafkaMessageHeaderWriter);
    }

    @Bean
    public ApimsKafkaDefaultErrorHandler apimsKafkaDefaultErrorHandler(
            @Value("${apims.kafka.consumer.default-error-handler-interval:5000}") long interval,
            @Value("${apims.kafka.consumer.default-error-handler-max-failures:10}") long maxFailures) {
        return new ApimsKafkaDefaultErrorHandler(interval, maxFailures);
    }

    @Bean
    @ConditionalOnMissingBean()
    public ApimsKafkaGenericMessageSender apimsKafkaGenericMessageSender(ApimsAvroRecordProducer avroRecordProducer) {
        return new ApimsKafkaGenericMessageSender(avroRecordProducer);
    }

    @Bean
    @ConditionalOnMissingBean()
    public ApimsKafkaGenericMessageToRetryTopicSender apimsKafkaGenericMessageToRetryTopicSender(
            ApimsAvroRecordProducer avroRecordProducer) {
        return new ApimsKafkaGenericMessageToRetryTopicSender(avroRecordProducer);
    }

    @Bean
    @ConditionalOnMissingBean(KafkaTemplate.class)
    @SuppressWarnings("java:S1452")
    public KafkaTemplate<?, ?> kafkaTemplate(
            ProducerFactory<Object, Object> kafkaProducerFactory,
            ProducerListener<Object, Object> kafkaProducerListener,
            ObjectProvider<RecordMessageConverter> messageConverter,
            @Value("${spring.kafka.template.auto-flush:false}") boolean autoFlush) {
        PropertyMapper map = PropertyMapper.get();
        KafkaTemplate<Object, Object> kafkaTemplate = new KafkaTemplate<>(kafkaProducerFactory, autoFlush);
        messageConverter.ifUnique(kafkaTemplate::setMessageConverter);
        map.from(kafkaProducerListener).to(kafkaTemplate::setProducerListener);
        map.from(this.kafkaProperties.getTemplate().getDefaultTopic()).to(kafkaTemplate::setDefaultTopic);
        map.from(this.kafkaProperties.getTemplate().getTransactionIdPrefix()).to(kafkaTemplate::setTransactionIdPrefix);
        return kafkaTemplate;
    }

    @Bean
    @ConditionalOnMissingBean()
    public ApimsKafkaTemplateConfigurer apimsKafkaTemplateConfigurer() {
        return new ApimsKafkaTemplateConfigurer();
    }
}
