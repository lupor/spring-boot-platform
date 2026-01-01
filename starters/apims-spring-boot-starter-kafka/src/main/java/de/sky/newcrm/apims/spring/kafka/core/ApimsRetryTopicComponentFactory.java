/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core;

import org.springframework.kafka.retrytopic.DeadLetterPublishingRecovererFactory;
import org.springframework.kafka.retrytopic.DestinationTopicResolver;
import org.springframework.kafka.retrytopic.RetryTopicComponentFactory;

public class ApimsRetryTopicComponentFactory extends RetryTopicComponentFactory {

    @Override
    public DeadLetterPublishingRecovererFactory deadLetterPublishingRecovererFactory(
            DestinationTopicResolver destinationTopicResolver) {
        DeadLetterPublishingRecovererFactory factory =
                super.deadLetterPublishingRecovererFactory(destinationTopicResolver);
        factory.neverLogListenerException();
        return new ApimsDeadLetterPublishingRecovererFactoryHeadersFunction(destinationTopicResolver)
                .attachFactory(factory);
    }
}
