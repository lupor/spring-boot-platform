/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core.producer;

import de.sky.newcrm.apims.spring.kafka.core.ApimsKafkaMessageHeaderWriter;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.List;

@RequiredArgsConstructor
public class ApimsKafkaProducerHeaderInterceptor implements ApimsKafkaProducerInterceptor {

    private final List<ApimsKafkaMessageHeaderWriter> apimsKafkaMessageHeaderWriter;

    @Override
    public ProducerRecord<Object, Object> onSend(ProducerRecord<Object, Object> producerRecord) {
        apimsKafkaMessageHeaderWriter.forEach(headerWriter -> headerWriter.writeHeaders(producerRecord.headers()));
        return producerRecord;
    }
}
