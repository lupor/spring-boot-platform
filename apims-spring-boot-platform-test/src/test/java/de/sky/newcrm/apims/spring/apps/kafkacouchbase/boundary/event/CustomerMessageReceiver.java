/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.apps.kafkacouchbase.boundary.event;

import de.sky.newcrm.apims.spring.examples.boundary.event.model.Payload;
import de.sky.newcrm.apims.spring.kafka.core.ApimsKafkaMessageReceiver;
import de.sky.newcrm.apims.spring.kafka.core.ApimsRetryAndDeadletterTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * This is the example of a kafka listener which implements the methods of the interface and uses avro
 * generated from using asyncapi generator on the example-event.yaml which can be found in the api-specs/examples.
 */
@Component
public class CustomerMessageReceiver extends ApimsKafkaMessageReceiver<Payload> {

    @ApimsRetryAndDeadletterTopic(groupBasedRetryAndDltTopics = "true")
    @KafkaListener(
            topics = {"${apims.kafka.consumer.topics.customer-message}"},
            groupId = "${apims.kafka.consumer.group-id}-consumer")
    @Override
    public void onEvent(ConsumerRecord<String, Payload> consumerRecord) {
        process(consumerRecord);
    }
}
