/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core.producer;

import de.sky.newcrm.apims.spring.core.env.ApimsSpringContext;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.core.OrderComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("java:S1186")
public class ApimsProducerInterceptor implements ProducerInterceptor<Object, Object> {

    private List<ApimsKafkaProducerInterceptor> delegates = null;

    synchronized List<ApimsKafkaProducerInterceptor> getDelegates() {
        if (delegates == null) {
            Map<String, ApimsKafkaProducerInterceptor> beanMap =
                    ApimsSpringContext.getApplicationContext().getBeansOfType(ApimsKafkaProducerInterceptor.class);
            List<ApimsKafkaProducerInterceptor> instances = new ArrayList<>(beanMap.values());
            instances.sort(new OrderComparator());
            delegates = instances;
        }
        return delegates;
    }

    @Override
    public ProducerRecord<Object, Object> onSend(ProducerRecord<Object, Object> producerRecord) {
        ProducerRecord<Object, Object> currentRecord = producerRecord;
        for (ApimsKafkaProducerInterceptor delegate : getDelegates()) {
            currentRecord = delegate.onSend(currentRecord);
        }
        return currentRecord;
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {}

    @Override
    public void close() {}

    @Override
    public void configure(Map<String, ?> configs) {}
}
