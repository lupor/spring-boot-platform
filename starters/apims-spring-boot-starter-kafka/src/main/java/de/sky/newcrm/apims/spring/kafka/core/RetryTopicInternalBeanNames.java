/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core;

public abstract class RetryTopicInternalBeanNames {

    private RetryTopicInternalBeanNames() {}

    /**
     * {@link org.springframework.kafka.retrytopic.DestinationTopicProcessor} bean name.
     */
    public static final String DESTINATION_TOPIC_PROCESSOR_NAME = "internalDestinationTopicProcessor";

    /**
     * {@link org.springframework.kafka.retrytopic.DeadLetterPublishingRecovererFactory} bean name.
     */
    public static final String DEAD_LETTER_PUBLISHING_RECOVERER_FACTORY_BEAN_NAME =
            "internalDeadLetterPublishingRecovererProvider";

    /**
     * The {@link java.time.Clock} bean name that will be used for backing off partitions.
     */
    public static final String INTERNAL_BACKOFF_CLOCK_BEAN_NAME = "internalBackOffClock";

    /**
     * Default {@link org.springframework.kafka.core.KafkaTemplate} bean name for publishing to retry topics.
     */
    public static final String DEFAULT_KAFKA_TEMPLATE_BEAN_NAME = "retryTopicDefaultKafkaTemplate";
}
