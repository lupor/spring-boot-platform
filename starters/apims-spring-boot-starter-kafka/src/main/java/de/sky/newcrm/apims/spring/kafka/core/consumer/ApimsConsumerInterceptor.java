/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core.consumer;

import de.sky.newcrm.apims.spring.environment.core.ApimsSpringContext;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.core.OrderComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("java:S1186")
public class ApimsConsumerInterceptor implements ConsumerInterceptor<Object, Object> {

    private List<ApimsKafkaConsumerInterceptor> delegates = null;

    synchronized List<ApimsKafkaConsumerInterceptor> getDelegates() {
        if (delegates == null) {
            Map<String, ApimsKafkaConsumerInterceptor> beanMap =
                    ApimsSpringContext.getApplicationContext().getBeansOfType(ApimsKafkaConsumerInterceptor.class);
            List<ApimsKafkaConsumerInterceptor> instances = new ArrayList<>(beanMap.values());
            instances.sort(new OrderComparator());
            delegates = instances;
        }
        return delegates;
    }

    @Override
    public ConsumerRecords<Object, Object> onConsume(ConsumerRecords<Object, Object> consumerRecords) {
        ConsumerRecords<Object, Object> currentRecords = consumerRecords;
        for (ApimsKafkaConsumerInterceptor delegate : getDelegates()) {
            currentRecords = delegate.onConsume(currentRecords);
        }
        return currentRecords;
    }

    @Override
    public void onCommit(Map<TopicPartition, OffsetAndMetadata> map) {}

    @Override
    public void close() {}

    @Override
    public void configure(Map<String, ?> map) {}
}
