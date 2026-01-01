/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core;

import de.sky.newcrm.apims.spring.kafka.core.entity.ApimsKafkaRecord;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApimsDeadLetterLoggingAction implements ApimsDeadLetterAction {

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    @Override
    public boolean isMandatory() {
        return false;
    }

    @Override
    public void handleDltRecord(ApimsKafkaRecord apimsKafkaRecord) {
        logTheError(apimsKafkaRecord, "Received message in dlt listener: " + getRecordErrorMessage(apimsKafkaRecord));
    }

    protected String getRecordErrorMessage(ApimsKafkaRecord apimsKafkaRecord) {
        return "Record: " + getRecordInfo(apimsKafkaRecord) + " threw an error at topic " + apimsKafkaRecord.getTopic();
    }

    protected String getRecordInfo(ApimsKafkaRecord apimsKafkaRecord) {
        String exceptionMessage = apimsKafkaRecord.getExceptionMessage();
        String exceptionStacktrace = apimsKafkaRecord.getExceptionStacktrace();
        String retryTopic = apimsKafkaRecord.getRetryTopic();
        return "main topic = %s, retry-topic = %s, partition = %s, offset = %s, message = %s%s"
                .formatted(
                        apimsKafkaRecord.getOriginalTopic(),
                        retryTopic == null ? "" : retryTopic,
                        apimsKafkaRecord.getPartition(),
                        apimsKafkaRecord.getOffset(),
                        exceptionMessage == null ? "" : exceptionMessage,
                        exceptionStacktrace == null ? "" : ("\n" + exceptionStacktrace));
    }

    @SuppressWarnings("java:S1172")
    protected void logTheError(ApimsKafkaRecord apimsKafkaRecord, String message) {
        log.warn(message);
    }
}
