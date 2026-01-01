/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.couchbase.core.converter;

import org.springframework.core.convert.converter.Converter;

public interface ApimsCouchbaseConverter<S, T> extends Converter<S, T> {}
