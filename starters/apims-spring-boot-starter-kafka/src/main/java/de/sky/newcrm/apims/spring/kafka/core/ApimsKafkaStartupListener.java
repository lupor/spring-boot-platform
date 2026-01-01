/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core;

import de.sky.newcrm.apims.spring.environment.core.ApimsApplicationReadyListener;
import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
@SuppressWarnings({"java:S6212"})
public class ApimsKafkaStartupListener implements ApimsApplicationReadyListener {

    private final KafkaListenerEndpointRegistry endpointRegistry;

    @Value("${apims.kafka.consumer.asynchronized-startup:true}")
    private boolean asynchronizedStartup;

    @Value("${apims.kafka.consumer.asynchronized-startup-error-code:1}")
    private int asynchronizedStartupErrorCode;

    @Value("${apims.app.mocks.kafka-mock-enabled:false}")
    private boolean mocksEnabled;

    private boolean applicationReadyEventConsumed = false;

    public ApimsKafkaStartupListener(KafkaListenerEndpointRegistry endpointRegistry) {
        this.endpointRegistry = endpointRegistry;
    }

    @Override
    public synchronized void onApplicationReadyEvent() throws Exception {
        if (mocksEnabled) {
            applicationReadyEventConsumed = true;
        }
        if (applicationReadyEventConsumed) {
            return;
        }
        if (asynchronizedStartup) {
            startupListenerAsync();
        } else {
            startupListener();
        }
        applicationReadyEventConsumed = true;
    }

    private void startupListenerAsync() {
        new Thread(null, null, "KafkaListenerStartupThread", 0) {
            @Override
            public void run() {
                try {
                    startupListener();
                } catch (Exception e) {
                    log.error("startup kafka listener failed!", e);
                    onAsyncStartupError(e);
                }
            }
        }.start();
    }

    @ApimsReportGeneratedHint
    protected void onAsyncStartupError(Exception e) {
        if (asynchronizedStartupErrorCode != 0) {
            System.exit(asynchronizedStartupErrorCode);
        }
    }

    private void startupListener() {
        List<MessageListenerContainer> list = new ArrayList<>(endpointRegistry.getAllListenerContainers());
        list.sort(Comparator.comparing(MessageListenerContainer::getGroupId));
        for (MessageListenerContainer messageListenerContainer : list) {
            if (messageListenerContainer.isAutoStartup()) {
                continue;
            }
            String topics = StringUtils.arrayToCommaDelimitedString(
                    messageListenerContainer.getContainerProperties().getTopics());
            if (!messageListenerContainer.isRunning()) {
                log.info(
                        "Kafka listener {}.[{}] is not running. start()...",
                        messageListenerContainer.getGroupId(),
                        topics);
                messageListenerContainer.start();
            }
        }
    }
}
