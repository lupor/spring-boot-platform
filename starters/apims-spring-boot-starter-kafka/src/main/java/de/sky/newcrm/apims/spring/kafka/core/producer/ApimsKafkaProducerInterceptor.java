/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core.producer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.core.Ordered;

@SuppressWarnings("java:S6213")
public interface ApimsKafkaProducerInterceptor extends Ordered {

    @Override
    default int getOrder() {
        return 0;
    }

    ProducerRecord<Object, Object> onSend(ProducerRecord<Object, Object> record);
}
