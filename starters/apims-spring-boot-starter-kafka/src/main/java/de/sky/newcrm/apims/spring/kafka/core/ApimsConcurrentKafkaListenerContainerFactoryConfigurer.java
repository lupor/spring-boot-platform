/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

import java.time.Duration;

@SuppressWarnings({"java:S1172"})
public class ApimsConcurrentKafkaListenerContainerFactoryConfigurer {

    @Value("${spring.kafka.listener.auto-startup:true}")
    boolean autoStartup;

    @Value("${apims.app.mocks.kafka-mock-enabled:false}")
    private boolean mocksEnabled;

    public void beforeConfigurer(
            ConcurrentKafkaListenerContainerFactory<Object, Object> listenerContainerFactory,
            ConsumerFactory<Object, Object> consumerFactory) {
        listenerContainerFactory.setAutoStartup(autoStartup && !mocksEnabled);
    }

    public void afterConfigurer(
            ConcurrentKafkaListenerContainerFactory<Object, Object> listenerContainerFactory,
            ConsumerFactory<Object, Object> consumerFactory) {
        long authExceptionRetryIntervalMs = Long.parseLong(String.valueOf(
                consumerFactory.getConfigurationProperties().getOrDefault("auth.exception.retry.interval.ms", "-1")));
        if (authExceptionRetryIntervalMs > 0) {
            long maxPollIntervalMs = Long.parseLong(String.valueOf(
                    consumerFactory.getConfigurationProperties().getOrDefault("max.poll.interval.ms", "-1")));
            if (maxPollIntervalMs > 0) {
                authExceptionRetryIntervalMs = Math.min(authExceptionRetryIntervalMs, maxPollIntervalMs);
            }
            listenerContainerFactory
                    .getContainerProperties()
                    .setAuthExceptionRetryInterval(Duration.ofMillis(authExceptionRetryIntervalMs));
        }
        listenerContainerFactory.getContainerProperties().setObservationEnabled(true);
    }
}
