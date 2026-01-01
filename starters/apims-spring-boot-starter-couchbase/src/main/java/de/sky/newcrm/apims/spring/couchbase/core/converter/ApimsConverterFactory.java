/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.couchbase.core.converter;

import org.springframework.core.convert.converter.ConverterFactory;

/**
 * @author Steven Baumberger - NTT DATA
 * @version 1.0
 * @since Version 1.0 - 22.09.23
 */
public interface ApimsConverterFactory<S, R> extends ConverterFactory<S, R> {}
