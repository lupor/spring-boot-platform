/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.apps.kafkacouchbase.control;

import de.sky.newcrm.apims.spring.examples.boundary.event.model.CreateCustomer;
import de.sky.newcrm.apims.spring.examples.boundary.event.model.DeleteCustomer;
import de.sky.newcrm.apims.spring.examples.boundary.event.model.Payload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerMessageSender {

    private final KafkaTemplate<String, Payload> kafkaTemplate;

    @Value("${apims.kafka.producer.topics.customer-message}")
    String topic;

    public void sendMessage(String key, CreateCustomer payload) {
        kafkaTemplate.send(
                topic,
                key,
                Payload.newBuilder()
                        .setCreateCustomer(payload)
                        .setMessage("CreateCustomer")
                        .build());
    }

    public void sendMessage(String key, DeleteCustomer payload) {
        kafkaTemplate.send(
                topic,
                key,
                Payload.newBuilder()
                        .setDeleteCustomer(payload)
                        .setMessage("DeleteCustomer")
                        .build());
    }
}
