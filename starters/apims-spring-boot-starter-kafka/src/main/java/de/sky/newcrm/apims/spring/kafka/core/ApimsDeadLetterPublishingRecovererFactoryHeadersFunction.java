/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core;

import de.sky.newcrm.apims.spring.environment.core.ApimsSpringContext;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.kafka.retrytopic.DeadLetterPublishingRecovererFactory;
import org.springframework.kafka.retrytopic.DestinationTopic;
import org.springframework.kafka.retrytopic.DestinationTopicResolver;
import org.springframework.kafka.support.KafkaHeaders;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ApimsDeadLetterPublishingRecovererFactoryHeadersFunction {

    private final DestinationTopicResolver destinationTopicResolver;
    private List<ApimsKafkaMessageHeaderWriter> apimsKafkaMessageHeaderWriterList = null;

    public ApimsDeadLetterPublishingRecovererFactoryHeadersFunction(DestinationTopicResolver destinationTopicResolver) {
        this.destinationTopicResolver = destinationTopicResolver;
    }

    public DeadLetterPublishingRecovererFactory attachFactory(DeadLetterPublishingRecovererFactory factory) {
        factory.setHeadersFunction(this::headersFunction);
        return factory;
    }

    protected List<ApimsKafkaMessageHeaderWriter> getApimsKafkaMessageHeaderWriterList() {
        if (apimsKafkaMessageHeaderWriterList == null) {
            apimsKafkaMessageHeaderWriterList = new ArrayList<>(ApimsSpringContext.getApplicationContext()
                    .getBeansOfType(ApimsKafkaMessageHeaderWriter.class)
                    .values());
        }
        return apimsKafkaMessageHeaderWriterList;
    }

    protected Headers headersFunction(ConsumerRecord<?, ?> consumerRecord, Exception e) {
        Headers headers = new RecordHeaders();
        // save the first configured retry topic
        if (destinationTopicResolver instanceof ApimsDefaultDestinationTopicResolver resolver) {
            List<DestinationTopic> destinationTopics = resolver.getDestinationTopics(consumerRecord.topic());
            Optional<DestinationTopic> retryDestinationTopic = destinationTopics.stream()
                    .filter(dt -> !dt.isMainTopic() && !dt.isDltTopic() && !dt.isNoOpsTopic())
                    .findFirst();
            retryDestinationTopic.ifPresent(destinationTopic -> headers.add(
                    KafkaHeaders.PREFIX + "retry-topic",
                    destinationTopic.getDestinationName().getBytes(StandardCharsets.UTF_8)));
        }
        // Retry fallback function (set right B3 Headers for example)
        // see: ApimsMessageListenerMethodInterceptor, ApimsKafkaTemplateAspect
        getApimsKafkaMessageHeaderWriterList().forEach(headerWriter -> headerWriter.writeHeaders(headers));
        return headers;
    }
}
