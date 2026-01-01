/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.autoconfigure;

import de.sky.newcrm.apims.spring.kafka.core.ApimsKafkaBootstrap;import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(name = "org.springframework.boot.autoconfigure.kafka.KafkaAnnotationDrivenConfiguration")
@ApimsKafkaBootstrap
public class ApimsKafkaBootstrapConfiguration {}
