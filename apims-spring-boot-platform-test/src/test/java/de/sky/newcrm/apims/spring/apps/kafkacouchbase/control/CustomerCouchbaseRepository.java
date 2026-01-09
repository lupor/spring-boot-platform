/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.apps.kafkacouchbase.control;

import de.sky.newcrm.apims.spring.apps.kafkacouchbase.entity.CustomerDocument;
import de.sky.newcrm.apims.spring.couchbase.core.ApimsCouchbaseRepository;
import org.springframework.data.couchbase.repository.Collection;
import org.springframework.stereotype.Repository;

@Repository
@Collection("customer")
public interface CustomerCouchbaseRepository extends ApimsCouchbaseRepository<CustomerDocument, String> {}
