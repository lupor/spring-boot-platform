/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.apps.kafkacouchbase.boundary.event.handler;

import de.sky.newcrm.apims.spring.apps.kafkacouchbase.control.CustomerService;
import de.sky.newcrm.apims.spring.examples.boundary.event.model.DeleteCustomer;
import de.sky.newcrm.apims.spring.examples.boundary.event.model.Payload;
import de.sky.newcrm.apims.spring.exceptions.InvalidRequestDataBusinessException;
import de.sky.newcrm.apims.spring.kafka.core.processing.ApimsKafkaConsumerHandler;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@SuppressWarnings("java:S1874")
public class DeleteCustomerMessageConsumer extends ApimsKafkaConsumerHandler<Payload, String> {

    private final CustomerService service;

    @Override
    public boolean supportsInboundData(Payload inboundEntity) {
        return DeleteCustomer.class.getSimpleName().equals(inboundEntity.getMessage());
    }

    @Override
    public void validateInboundData(Payload inboundEntity) {
        // example of 3 additional common property methods
        final String propertyPath = "deleteCustomer.general.customerId";
        InvalidRequestDataBusinessException.createValidator()
                .assertPropertyNotBlank(inboundEntity, propertyPath)
                .assertPropertyNotEmpty(inboundEntity, propertyPath)
                .assertPropertyNotNull(inboundEntity, propertyPath)
                .throwIfContainsViolations();
    }

    @Override
    @NotNull
    public String convertInboundData(Payload inboundEntity) {
        return inboundEntity.getDeleteCustomer().getGeneral().getCustomerId();
    }

    @Override
    public void process(String customerId) {
        service.deleteCustomer(customerId);
    }
}
