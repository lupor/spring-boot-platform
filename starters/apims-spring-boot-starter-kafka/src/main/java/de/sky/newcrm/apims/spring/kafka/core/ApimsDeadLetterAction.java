/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core;

import de.sky.newcrm.apims.spring.core.kafka.entity.ApimsKafkaRecord;
import org.springframework.core.Ordered;

public interface ApimsDeadLetterAction extends Ordered {

    void handleDltRecord(ApimsKafkaRecord apimsKafkaRecord);

    boolean isMandatory();
}
