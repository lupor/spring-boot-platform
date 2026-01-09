/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.apps.kafkacouchbase.boundary.http;

import de.sky.newcrm.apims.spring.apps.kafkacouchbase.control.CustomerProxyService;
import de.sky.newcrm.apims.spring.apps.kafkacouchbase.control.CustomerService;
import de.sky.newcrm.apims.spring.apps.kafkacouchbase.generated.openapi.api.DefaultApi;
import de.sky.newcrm.apims.spring.apps.kafkacouchbase.generated.openapi.model.Customer;
import de.sky.newcrm.apims.spring.web.core.rest.ApimsRestController;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class CustomerController extends ApimsRestController implements DefaultApi {

    private final CustomerProxyService proxyService;
    private final CustomerService service;
    private final DtoMapper dtoMapper;

    public CustomerController(CustomerProxyService proxyService, CustomerService service) {
        this.proxyService = proxyService;
        this.service = service;
        dtoMapper = Mappers.getMapper(DtoMapper.class);
    }

    @Override
    public ResponseEntity<Void> createCustomer(
            @Valid Customer customer,
            String customerId) {
        proxyService.onCreateCustomer(dtoMapper.fromDto(customer, customerId));
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deleteCustomerById(String customerId) {
        proxyService.onDeleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Customer>
            getCustomerById(String customerId) {
        Customer responseBody =
                dtoMapper.toDto(service.getCustomerById(customerId));
        return ResponseEntity.ok(responseBody);
    }

    @Mapper()
    public interface DtoMapper {

        de.sky.newcrm.apims.spring.apps.kafkacouchbase.entity.Customer fromDto(
                Customer customer,
                String customerId);

        Customer toDto(
                de.sky.newcrm.apims.spring.apps.kafkacouchbase.entity.Customer customer);
    }
}
