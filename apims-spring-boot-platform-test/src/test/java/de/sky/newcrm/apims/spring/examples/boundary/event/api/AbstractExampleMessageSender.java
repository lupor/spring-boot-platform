/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.examples.boundary.event.api;

import de.sky.newcrm.apims.spring.examples.boundary.event.model.CreateCustomer;
import de.sky.newcrm.apims.spring.examples.boundary.event.model.DeleteCustomer;
import de.sky.newcrm.apims.spring.examples.boundary.event.model.Header;
import de.sky.newcrm.apims.spring.examples.boundary.event.model.Payload;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;

public abstract class AbstractExampleMessageSender {
    private final KafkaTemplate<String, Payload> kafkaTemplate;

    public AbstractExampleMessageSender(KafkaTemplate<String, Payload> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    protected abstract String getTopic();

    public void sendMessage(String key, Payload payload, Header header) {
        ProducerRecord<String, Payload> record = new ProducerRecord<>(getTopic(), key, payload);
        kafkaTemplate.send(record);
    }

    public void sendCreateCustomer(String key, CreateCustomer createCustomer, Header header) {
        Payload payload = Payload.newBuilder()
                .setCreateCustomer(createCustomer)
                .setMessage("CreateCustomer")
                .build();
        ProducerRecord<String, Payload> record = new ProducerRecord<>(getTopic(), key, payload);
        kafkaTemplate.send(record);
    }

    public void sendDeleteCustomer(String key, DeleteCustomer deleteCustomer, Header header) {
        Payload payload = Payload.newBuilder()
                .setDeleteCustomer(deleteCustomer)
                .setMessage("DeleteCustomer")
                .build();
        ProducerRecord<String, Payload> record = new ProducerRecord<>(getTopic(), key, payload);
        kafkaTemplate.send(record);
    }
}
