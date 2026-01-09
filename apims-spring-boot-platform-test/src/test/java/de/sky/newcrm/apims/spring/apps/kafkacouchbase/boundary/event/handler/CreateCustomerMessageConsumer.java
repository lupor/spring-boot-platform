/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.apps.kafkacouchbase.boundary.event.handler;

import de.sky.newcrm.apims.spring.apps.kafkacouchbase.control.CustomerService;
import de.sky.newcrm.apims.spring.apps.kafkacouchbase.entity.Customer;
import de.sky.newcrm.apims.spring.examples.boundary.event.model.CreateCustomer;
import de.sky.newcrm.apims.spring.examples.boundary.event.model.Payload;
import de.sky.newcrm.apims.spring.exceptions.InvalidRequestDataBusinessException;
import de.sky.newcrm.apims.spring.kafka.core.processing.ApimsKafkaConsumerHandler;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateCustomerMessageConsumer extends ApimsKafkaConsumerHandler<Payload, Customer> {

    private final DtoMapper dtoMapper = Mappers.getMapper(DtoMapper.class);
    private final CustomerService service;

    @Override
    public boolean supportsInboundData(Payload inboundEntity) {
        return CreateCustomer.class.getSimpleName().equals(inboundEntity.getMessage());
    }

    @Override
    public void validateInboundData(Payload inboundEntity) {
        InvalidRequestDataBusinessException.createValidator()
                .assertPropertyNotEmpty(inboundEntity, "createCustomer.general.customerId")
                // .assertNotEmpty("testProperty", "")
                .throwIfContainsViolations();
    }

    @Override
    public Customer convertInboundData(Payload inboundEntity) {
        return dtoMapper.fromDto(inboundEntity.getCreateCustomer());
    }

    @Override
    public void validateProcessingData(Customer processingEntity) {
        InvalidRequestDataBusinessException.createValidator()
                // .assertNotNull("test", processingEntity.getTest())
                .throwIfContainsViolations();
    }

    @Override
    public void process(Customer processingEntity) {
        service.upsertCustomer(processingEntity);
    }

    @Mapper()
    public interface DtoMapper {

        default Customer fromDto(CreateCustomer customer) {
            return Customer.builder()
                    .customerId(customer.getGeneral().getCustomerId())
                    .name(customer.getName())
                    .email(customer.getEmail())
                    .build();
        }
    }
}
