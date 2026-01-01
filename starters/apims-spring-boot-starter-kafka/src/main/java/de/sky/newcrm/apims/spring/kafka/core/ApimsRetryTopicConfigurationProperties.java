/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ApimsRetryTopicConfigurationProperties {
    private boolean enabled;
    private String resourcePrefix;
    private boolean groupBasedRetryAndDltTopics;
    private int retryAttempts;
    private int lastRetryAttempts;
    private long delay;
    private double multiplier;
    private String listenerContainerFactory;
    private boolean autoCreateTopics;
    private int autoCreateNumPartitions;
    private short autoCreateReplicationFactor;
    private String defaultGroupId;
    private String dltTopicSuffix;
    private String retryTopicSuffix;
    private boolean useDltTopicOnNoRetryableException;
}
