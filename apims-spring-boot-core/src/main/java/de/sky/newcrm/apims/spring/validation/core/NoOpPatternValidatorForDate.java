/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.validation.core;

import jakarta.validation.constraints.Pattern;

import java.util.Date;

public class NoOpPatternValidatorForDate extends NoOpValidator<Pattern, Date> {}
