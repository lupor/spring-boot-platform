/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core;

import de.sky.newcrm.apims.spring.utils.MapUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

public interface KafkaUtils {

    @SafeVarargs
    static <K, V> Map<K, V> mapOfEntries(Map.Entry<? extends K, ? extends V>... entries) {
        return MapUtils.ofTreeMapEntries(entries);
    }

    static String format(ConsumerRecord<?, ?> consumerRecord) {
        return org.springframework.kafka.support.KafkaUtils.format(consumerRecord);
    }

    static Map<String, String> getHeaderValues(Headers kafkaHeaders) {
        Map<String, String> headers = new TreeMap<>();
        for (Header header : kafkaHeaders.toArray()) {
            headers.put(header.key(), new String(header.value(), StandardCharsets.UTF_8));
        }
        return headers;
    }
}
