/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.apps.kafkacouchbase.control;

import de.sky.newcrm.apims.spring.apps.kafkacouchbase.entity.Customer;
import de.sky.newcrm.apims.spring.apps.kafkacouchbase.entity.CustomerDocument;
import de.sky.newcrm.apims.spring.apps.kafkacouchbase.entity.CustomerTypeEnum;
import de.sky.newcrm.apims.spring.apps.kafkacouchbase.entity.SourceSystemTypeEnum;
import de.sky.newcrm.apims.spring.exceptions.InvalidRequestDataBusinessException;
import de.sky.newcrm.apims.spring.utils.FunctionUtils;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static de.sky.newcrm.apims.spring.couchbase.core.entity.ApimsMutateInSpec.arrayAppend;
import static de.sky.newcrm.apims.spring.couchbase.core.entity.ApimsMutateInSpec.upsert;


@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerCouchbaseRepository repository;

    public Customer upsertCustomer(@NotNull Customer customer) {
        InvalidRequestDataBusinessException.createValidator()
                .validateAnnotations(customer)
                .throwIfContainsViolations();
        final String id = CustomerDocument.createDocumentId(customer.getCustomerId());
        CustomerDocument customerDocument = FunctionUtils.apply(
                repository.findById(id),
                document -> {
                    updateCustomer(document, customer.getEmail(), customer.getName());
                    return document;
                },
                () -> {
                    CustomerDocument document = repository.save(CustomerDocument.builder()
                            .id(id)
                            .customerId(customer.getCustomerId())
                            .email(customer.getEmail())
                            .name(customer.getName())
                            .lastModification(new Date())
                            .changeHistory(List.of(new Date()))
                            .build());
                    return document;
                });
        return Customer.builder()
                .customerId(customerDocument.getCustomerId())
                .name(customerDocument.getName())
                .email(customerDocument.getEmail())
                .build();
    }

    public Customer getCustomerById(String customerId) {
        InvalidRequestDataBusinessException.createValidator()
                .assertNotEmpty("customerId", customerId)
                .throwIfContainsViolations();

        final String id = CustomerDocument.createDocumentId(customerId);
        return FunctionUtils.applyIfPresent(
                repository.findById(id), CustomerNotFoundException.class, document -> Customer.builder()
                        .customerId(document.getCustomerId())
                        .name(document.getName())
                        .email(document.getEmail())
                        .build());
    }

    public void deleteCustomer(String customerId) {
        InvalidRequestDataBusinessException.createValidator()
                .assertNotEmpty("customerId", customerId)
                .throwIfContainsViolations();
        FunctionUtils.applyIfPresent(repository.findById(CustomerDocument.createDocumentId(customerId)), document -> {
            repository.delete(document);
            return document;
        });
    }

    protected void updateCustomer(CustomerDocument document, String email, String name) {
        Date now = new Date();
        CustomerDocument savedDocument = repository.mutateInAndGet(
                document.getId(),
                upsert("email", email),
                upsert("name", name),
                upsert("customerTypeEnum", CustomerTypeEnum.Customer),
                upsert("sourceSystemTypeEnum", SourceSystemTypeEnum.SALESFORCE),
                upsert("lastModification", now),
                arrayAppend("changeHistory", now));
        document.setEmail(savedDocument.getEmail());
        document.setName(savedDocument.getName());
        document.setLastModification(savedDocument.getLastModification());
        document.getChangeHistory().add(now);
    }
}
