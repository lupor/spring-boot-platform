/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.kafka.support.KafkaHeaders;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
public class ApimsKafkaRecord {

    private String key;

    private String appName;

    private String appEnv;

    private String appDomain;

    private List<Header> headers;

    private String bodyType;

    private String body;

    private String topic;

    private String originalTopic;

    private String retryTopic;

    private int partition;

    private long offset;

    private long timestamp;

    private String timestampType;

    private int serializedKeySize;

    private int serializedValueSize;

    private Integer leaderEpoch;

    @SuppressWarnings({"java:S1612", "java:S3958"})
    public List<Header> getHeader(String key) {
        return headers.stream().filter(h -> h.getKey().equals(key)).toList();
    }

    public Header getFirstHeader(String key) {
        List<Header> list = getHeader(key);
        return list.isEmpty() ? null : list.get(0);
    }

    public String getFirstHeaderValue(String key) {
        return getHeaderValue(getFirstHeader(key));
    }

    public Header getLastHeader(String key) {
        List<Header> list = getHeader(key);
        return list.isEmpty() ? null : list.get(list.size() - 1);
    }

    public String getLastHeaderValue(String key) {
        return getHeaderValue(getLastHeader(key));
    }

    public int getLastHeaderValue(String key, int defaultValue) {
        String value = getHeaderValue(getLastHeader(key));
        return value == null || value.length() == 0 ? defaultValue : Integer.parseInt(value);
    }

    public long getLastHeaderValue(String key, long defaultValue) {
        String value = getHeaderValue(getLastHeader(key));
        return value == null || value.length() == 0 ? defaultValue : Long.parseLong(value);
    }

    public String getHeaderValue(Header header) {
        return header == null ? null : header.getValue();
    }

    public String getExceptionMessage() {
        return getLastHeaderValue(KafkaHeaders.EXCEPTION_MESSAGE);
    }

    public String getExceptionStacktrace() {
        return getLastHeaderValue(KafkaHeaders.EXCEPTION_STACKTRACE);
    }

    public Header setHeaderValue(String key, String value) {
        return setHeader(Header.builder().key(key).value(value).build());
    }

    public Header setHeader(Header header) {
        removeHeader(header.getKey());
        getHeaders().add(header);
        return header;
    }

    public void removeHeader(String key) {
        headers.removeIf(h -> h.getKey().equals(key));
    }

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonAutoDetect(
            fieldVisibility = JsonAutoDetect.Visibility.ANY,
            getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE,
            setterVisibility = JsonAutoDetect.Visibility.NONE,
            creatorVisibility = JsonAutoDetect.Visibility.NONE)
    public static class Header {

        private String key;

        private String value;
    }
}
