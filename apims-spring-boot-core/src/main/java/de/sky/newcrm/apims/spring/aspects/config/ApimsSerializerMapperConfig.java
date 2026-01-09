/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.aspects.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("apims.object-mapper-config")
@Getter
@Setter
public class ApimsSerializerMapperConfig {
    private boolean dateTimeSerializerWriteIsoDateWithTimezone = false;
}
