/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.telemetry.mdc.aspect;

import de.sky.newcrm.apims.spring.aspects.core.around.ApimsAroundContext;
import de.sky.newcrm.apims.spring.aspects.core.around.ApimsAroundListener;
import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import de.sky.newcrm.apims.spring.telemetry.mdc.core.ApimsMdc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@SuppressWarnings("java:S6813")
public class ApimsAroundMdcListener implements ApimsAroundListener, InitializingBean {

    @Value("${apims.aspects.listeners.enable-sky-headers:true}")
    private boolean mdcSkyHeadersEnabled;

    private static final String SKY_HEADERS_PREFIX = "x-sky-client";
    private static final String KAFKA_LAG_MILLIS_FIELD = "currentProcessingLagMillis";

    @Autowired
    private ApimsMdc mdc;

    private final Map<String, String> remoteFields = new HashMap<>();

    @SuppressWarnings("java:S2259")
    public ApimsAroundMdcListener(Map<String, String> remoteFields) {
        if (remoteFields != null) {
            // remote-fields configuration-> mdc-key=field-key or key=mdc-key:field-key
            // internal usage-> field-key=mdc-key
            for (Map.Entry<String, String> entry : remoteFields.entrySet()) {
                String mdcKey = entry.getKey().toLowerCase();
                String fieldKey = entry.getValue().toLowerCase();
                if (fieldKey.contains(":")) {
                    String[] pair = StringUtils.split(fieldKey, ":");
                    mdcKey = pair[0].trim();
                    fieldKey = pair[1].trim();
                }
                this.remoteFields.put(fieldKey, mdcKey);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (log.isTraceEnabled()) {
            for (Map.Entry<String, String> entry : remoteFields.entrySet()) {
                log.trace("[MDC] remote field -> '{}': '{}'", entry.getValue(), entry.getKey());
            }
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 140;
    }

    @Override
    @SuppressWarnings({"java:S1481", "java:S1854"})
    public void beforeAroundMethod(ApimsAroundContext context) {
        if (context.getActiveCallsCount() == 1) {
//            boolean flag = !remoteFields.isEmpty()
//                    && (resolveControllerInboundMdc(context) || resolveKafkaInboundMdc(context));
        }
    }

//    @ApimsReportGeneratedHint
//    protected boolean resolveKafkaInboundMdc(ApimsAroundContext context) {
//        ConsumerRecord<?, ?> consumerRecord = ApimsFlowContext.get().getInboundConsumerRecord();
//        if (consumerRecord == null) {
//            return false;
//        }
//        Map<String, String> headersMap = new HashMap<>();
//        if (mdcSkyHeadersEnabled) {
//            Optional.ofNullable(consumerRecord.headers())
//                    .ifPresent(headers -> StreamSupport.stream(headers.spliterator(), false)
//                            .filter(header -> header.value() != null)
//                            .forEach(header ->
//                                    headersMap.put(header.key(), new String(header.value(), StandardCharsets.UTF_8))));
//        }
//        if (Stream.of("-retry", "-dlt").noneMatch(consumerRecord.topic()::contains)) {
//            headersMap.put(KAFKA_LAG_MILLIS_FIELD, getTimeDifferenceMillis(consumerRecord.timestamp()));
//        }
//        addHeadersToMdc(headersMap);
//        if (!remoteFields.isEmpty()) {
//            Object recordValue = consumerRecord.value();
//            if (recordValue != null) {
//                String payload = String.valueOf(recordValue);
//                try {
//                    saveMdc(ObjectMapperUtils.toFlattenMap(ObjectMapperUtils.readMap(payload)));
//                } catch (Exception ignore) {
//                    // ignore
//                }
//            }
//        }
//        return true;
//    }

//    @ApimsReportGeneratedHint
//    @SuppressWarnings("java:S3776")
//    protected boolean resolveControllerInboundMdc(ApimsAroundContext context) {
//        HttpServletRequest request = ApimsFlowContext.get().getInboundHttpServletRequest();
//        if (request == null) {
//            return false;
//        }
//        if (mdcSkyHeadersEnabled) {
//            addHeadersToMdc(HttpRequestUtils.getHttpRequestValues(request));
//        }
//        if (!remoteFields.isEmpty()) {
//            Map<String, Object> map = new TreeMap<>();
//            Object[] args = context.getProceedingJoinPoint().getArgs();
//            for (int i = 0; i < args.length; i++) {
//                PathVariable pathVariable =
//                        ApimsFlowContext.get().findCurrentMethodParamAnnotation(PathVariable.class, i);
//                if (pathVariable == null) {
//                    try {
//                        saveMdc(ObjectMapperUtils.toFlattenMap(ObjectMapperUtils.getValueAsMap(args[i])));
//                    } catch (Exception ignore) {
//                        // ignore
//                    }
//                } else {
//                    String name =
//                            StringUtils.hasLength(pathVariable.name()) ? pathVariable.name() : pathVariable.value();
//                    if (StringUtils.hasLength(name)) {
//                        map.put(name, args[i]);
//                    }
//                }
//            }
//            if (!map.isEmpty()) {
//                saveMdc(map);
//            }
//        }
//        return true;
//    }

    @ApimsReportGeneratedHint
    public void addHeadersToMdc(Map<String, String> headers) {
        Map<String, String> skyHeaders = headers.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(SKY_HEADERS_PREFIX)
                        || entry.getKey().equals(KAFKA_LAG_MILLIS_FIELD))
                .collect(Collectors.toMap(entry -> entry.getKey().toLowerCase(), Map.Entry::getValue));
        if (!CollectionUtils.isEmpty(skyHeaders)) {
            mdc.putAll(skyHeaders);
        }
    }

    public void saveMdc(Map<String, Object> flattenMap) {
        if (!remoteFields.isEmpty()) {
            saveMdc(flattenMap, remoteFields);
        }
    }

    public void saveMdc(Map<String, Object> flattenMap, Map<String, String> configuredFields) {
        if (!flattenMap.isEmpty() && !configuredFields.isEmpty()) {
            Map<String, String> mdcData = new HashMap<>();
            for (Map.Entry<String, Object> entry : flattenMap.entrySet()) {
                String fieldKey = entry.getKey().toLowerCase();
                if (fieldKey.contains(".")) {
                    fieldKey = fieldKey.substring(fieldKey.lastIndexOf(".") + 1);
                }
                String mdcKey = configuredFields.get(fieldKey);
                if (mdcKey != null) {
                    mdcData.putIfAbsent(mdcKey, String.valueOf(entry.getValue()));
                }
            }
            if (!mdcData.isEmpty()) {
                mdc.putAll(mdcData);
            }
        }
    }

    private String getTimeDifferenceMillis(long timestampMillis) {
        return String.valueOf(Duration.ofMillis(Instant.now().toEpochMilli()).toMillis() - timestampMillis);
    }
}
