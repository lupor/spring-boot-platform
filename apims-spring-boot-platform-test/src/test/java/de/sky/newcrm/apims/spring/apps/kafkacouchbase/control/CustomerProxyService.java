/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.apps.kafkacouchbase.control;

import de.sky.newcrm.apims.spring.apps.kafkacouchbase.entity.Customer;
import de.sky.newcrm.apims.spring.examples.boundary.event.model.CreateCustomer;
import de.sky.newcrm.apims.spring.examples.boundary.event.model.DeleteCustomer;
import de.sky.newcrm.apims.spring.examples.boundary.event.model.General;
import de.sky.newcrm.apims.spring.exceptions.InvalidRequestDataBusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CustomerProxyService {

    private final CustomerService service;
    private final CustomerMessageSender messageSender;

    public void onCreateCustomer(Customer customer) {
        InvalidRequestDataBusinessException.createValidator()
                .validateAnnotations(customer)
                .throwIfContainsViolations();
        CreateCustomer cc = CreateCustomer.newBuilder()
                .setGeneral(General.newBuilder()
                        .setCustomerId(customer.getCustomerId())
                        .build())
                .setName(customer.getName())
                .setEmail(customer.getEmail())
                .build();
        messageSender.sendMessage(customer.getCustomerId(), cc);
    }

    public void onDeleteCustomer(String customerId) {
        Customer customer = service.getCustomerById(customerId);
        DeleteCustomer cc = DeleteCustomer.newBuilder()
                .setGeneral(General.newBuilder()
                        .setCustomerId(customer.getCustomerId())
                        .build())
                .setName(customer.getName())
                .setEmail(customer.getEmail())
                .build();
        messageSender.sendMessage(customerId, cc);
    }
}
