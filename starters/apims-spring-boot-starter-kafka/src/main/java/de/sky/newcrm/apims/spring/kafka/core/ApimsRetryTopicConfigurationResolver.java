/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.retrytopic.RetryTopicConfiguration;

import java.lang.reflect.Method;

public interface ApimsRetryTopicConfigurationResolver {

    RetryTopicConfiguration createConfiguration(
            ApimsRetryAndDeadletterTopic annotation,
            KafkaListener kafkaListener,
            KafkaOperations<?, ?> kafkaTemplate,
            Method method,
            Object bean,
            String beanName,
            String topic);
}
