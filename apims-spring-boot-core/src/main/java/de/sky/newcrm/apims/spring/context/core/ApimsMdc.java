/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.context.core;

import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

@Slf4j
@SuppressWarnings("java:S6857")
public class ApimsMdc implements InitializingBean {

    public static final String MDC_LOG_TYPE_KEY = "log.type";
    public static final String MDC_LOG_TYPE_METRIC = "metric";
    public static final String MDC_LOG_TYPE_HEALTH = "health";
    private static final String ERROR_KEY_PREFIX = "apims.error.";

    private final Map<String, String> globalFields = new HashMap<>();

    @Value("${apims.mdc.prefix:apims.}")
    private String mdcPrefix;

    @Value("${apims.mdc.global-fields-prefix:}")
    private String globalFieldsMdcPrefix;

    public ApimsMdc(Map<String, String> globalFields) {
        if (globalFields != null) {
            this.globalFields.putAll(globalFields);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        globalFields.forEach((key, value) -> logInfo("[MDC] global field -> '{}': '{}'", key, value));
    }

    public void putGlobalFields() {
        globalFields.forEach((key, value) -> MDC.put(calculateApimsMdcKey(key, true), value));
    }

    public void putAll(Map<String, String> data) {
        data.forEach(this::put);
    }

    public void removeAllApimsValues() {
        removeAll(getApimsMap());
    }

    public void removeAll(Map<String, String> data) {
        removeAll(data.keySet().toArray());
    }

    public boolean hasError() {
        return StringUtils.hasLength(MDC.get(ERROR_KEY_PREFIX + "hash"));
    }

    public boolean hasError(String hash) {
        return Objects.equals(hash, MDC.get(ERROR_KEY_PREFIX + "hash"));
    }

    public void putErrorInfo(String key, String value) {
        put(ERROR_KEY_PREFIX + key, value);
    }

    public String getErrorInfo(String key) {
        return get(ERROR_KEY_PREFIX + key);
    }

    public void clearError() {
        if (hasError()) {
            getCopyOfContextMap().keySet().stream()
                    .filter(key -> key.startsWith(ERROR_KEY_PREFIX))
                    .forEach(this::remove);
        }
    }

    public void removeAll(Object... keys) {
        for (Object key : keys) {
            remove(String.valueOf(key));
        }
    }

    public void put(String key, String value) {
        final String apimsKey = calculateApimsMdcKey(key, false);
        logInfo("[MDC] put({}, {})", apimsKey, value);
        MDC.put(apimsKey, value);
    }

    public void remove(String key) {
        final String apimsKey = calculateApimsMdcKey(key, false);
        logInfo("[MDC] remove({})", apimsKey);
        MDC.remove(apimsKey);
    }

    protected String get(String key) {
        final String apimsKey = calculateApimsMdcKey(key, false);
        final String value = MDC.get(apimsKey);
        logInfo("[MDC] get({}) = {}", apimsKey, value);
        return value;
    }

    protected Map<String, String> getCopyOfContextMap() {
        Map<String, String> map = MDC.getCopyOfContextMap();
        return map == null ? new HashMap<>() : map;
    }

    protected Map<String, String> getApimsMap() {
        Map<String, String> copy = new HashMap<>();
        if (StringUtils.hasLength(mdcPrefix)) {
            for (Map.Entry<String, String> entry : getCopyOfContextMap().entrySet()) {
                if (isApimsMdcKey(entry.getKey())) {
                    copy.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return copy;
    }

    protected boolean isApimsMdcKey(String key) {
        return StringUtils.hasLength(mdcPrefix) && key.startsWith(mdcPrefix);
    }

    protected String calculateApimsMdcKey(String key, boolean globalField) {
        String prefix = globalField ? globalFieldsMdcPrefix : mdcPrefix;
        return !StringUtils.hasLength(prefix) || key.startsWith(prefix) ? key : (prefix + key);
    }

    @ApimsReportGeneratedHint
    protected void logInfo(String message, Object... args) {
        if (log.isTraceEnabled()) {
            log.trace(message, args);
        }
    }
}
