/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.core.Ordered;

public interface ApimsKafkaConsumerInterceptor extends Ordered {

    @Override
    default int getOrder() {
        return 0;
    }

    ConsumerRecords<Object, Object> onConsume(ConsumerRecords<Object, Object> consumerRecords);
}
