/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.retrytopic.SameIntervalTopicReuseStrategy;

import java.lang.annotation.*;

/**
 * Annotation to create the retry and dlt topics for a {@link KafkaListener} annotated
 * listener.
 * All String properties can be resolved from property placeholders
 * {@code ${...}} or SpEL expressions {@code #{...}}.
 *
 * @see de.sky.newcrm.apims.spring.core.kafka.ApimsRetryTopicConfigurationResolver
 * @see de.sky.newcrm.apims.spring.core.kafka.ApimsRetryTopicConfigurationResolverDefaultImpl
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApimsRetryAndDeadletterTopic {

    /**
     * Whether or not the retry and dead letter topics should be included the groupId (so there are several consumers for the same topic).
     * Expressions must resolve to a boolean or a String that can be parsed as such.
     * Default 'null' -> apims.kafka.consumer.dlt.group-based-retry-and-dlt-topics
     *
     * @return the configuration.
     */
    String groupBasedRetryAndDltTopics() default "null";

    /**
     * The number of attempts made before the message is sent to the DLT. Expressions must
     * resolve to an integer or a string that can be parsed as such.
     * Default 'null' -> apims.kafka.consumer.dlt.retry-attempts
     *
     * @return the number of retryAttempts.
     */
    String retryAttempts() default "null";

    /**
     * A canonical backoff period. Used as an initial value in the exponential case, and
     * as a minimum value in the uniform case.
     * Default 'null' -> apims.kafka.consumer.dlt.delay
     *
     * @return the initial or canonical backoff period in milliseconds
     */
    String delay() default "null";

    /**
     * If positive, then used as a multiplier for generating the next delay for backoff.
     * Default 'null' -> apims.kafka.consumer.dlt.multiplier
     *
     * @return a multiplier to use to calculate the next backoff delay
     */
    String multiplier() default "null";

    /**
     * The suffix that will be appended to the main topic in order to generate the dlt
     * topic.
     * Default 'null' -> apims.kafka.consumer.dlt.dltTopicSuffix ('-dlt')
     *
     * @return the dlt suffix.
     */
    String dltTopicSuffix() default "null";

    /**
     * Whether or not create a DLT, and redeliver to the DLT if delivery fails or just give up.
     *
     * @return the dlt strategy.
     */
    DltStrategy dltStrategy() default DltStrategy.FAIL_ON_ERROR;

    /**
     * Topic reuse strategy for sequential attempts made with a same backoff interval.
     *
     * <p>Note: for fixed backoffs, when this is configured as
     * {@link SameIntervalTopicReuseStrategy#SINGLE_TOPIC}, it has precedence over
     * the configuration in {@link #fixedDelayTopicStrategy()}.
     *
     * @return the strategy.
     * @since 3.0.4
     */
    SameIntervalTopicReuseStrategy sameIntervalTopicReuseStrategy() default
            SameIntervalTopicReuseStrategy.MULTIPLE_TOPICS;
}
