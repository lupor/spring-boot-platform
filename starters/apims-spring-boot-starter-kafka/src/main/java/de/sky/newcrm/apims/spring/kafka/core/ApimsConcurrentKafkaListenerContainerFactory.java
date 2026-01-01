/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpoint;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

@RequiredArgsConstructor
public class ApimsConcurrentKafkaListenerContainerFactory<K, V> extends ConcurrentKafkaListenerContainerFactory<K, V> {

    private final String dltTopicSuffix;
    private final String retryTopicSuffix;

    @Override
    protected void initializeContainer(
            ConcurrentMessageListenerContainer<K, V> instance, KafkaListenerEndpoint endpoint) {
        super.initializeContainer(instance, endpoint);
        if (!isMainEndpoint(endpoint)) {
            instance.setConcurrency(1);
        }
    }

    @SuppressWarnings({"java:S1126"})
    protected boolean isMainEndpoint(KafkaListenerEndpoint endpoint) {
        String groupId = endpoint.getGroupId();
        if (groupId == null) {
            return false;
        } else if (groupId.endsWith(dltTopicSuffix)) {
            return false;
        } else if (groupId.contains(retryTopicSuffix)) {
            return false;
        } else {
            return true;
        }
    }
}
