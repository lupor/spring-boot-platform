/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.apps.kafkacouchbase.control;

import de.sky.newcrm.apims.spring.exceptions.ApimsBusinessException;
import de.sky.newcrm.apims.spring.exceptions.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ApimsBusinessException("CUSTOMER_NOT_FOUND")
@ResponseStatus(HttpStatus.NOT_FOUND) // not a good idea, demo only
public class CustomerNotFoundException extends BusinessException {}
