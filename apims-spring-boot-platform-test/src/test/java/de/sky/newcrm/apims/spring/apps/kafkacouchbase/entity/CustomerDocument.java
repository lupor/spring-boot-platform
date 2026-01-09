/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.apps.kafkacouchbase.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
/**
 * Example of a couchbase document definition
 */
@Document(expiryExpression = "${customer-document.expiry:86400}")
public class CustomerDocument {
    @Id
    private String id;

    private String customerId;
    private String name;
    private String email;

    @Builder.Default
    private CustomerTypeEnum customerTypeEnum = CustomerTypeEnum.Customer;

    @Builder.Default
    private SourceSystemTypeEnum sourceSystemTypeEnum = SourceSystemTypeEnum.SALESFORCE;

    @Builder.Default
    private List<Date> changeHistory = new ArrayList<>();

    private Date lastModification;

    public static String createDocumentId(String id) {
        return "CustomerData::" + id;
    }
}
