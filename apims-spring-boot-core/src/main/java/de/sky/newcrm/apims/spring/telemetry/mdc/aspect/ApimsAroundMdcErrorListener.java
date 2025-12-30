/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.telemetry.mdc.aspect;

import de.sky.newcrm.apims.spring.aspects.core.around.ApimsAroundContext;
import de.sky.newcrm.apims.spring.aspects.core.around.ApimsAroundListener;
import de.sky.newcrm.apims.spring.aspects.core.around.ApimsAspectType;
import de.sky.newcrm.apims.spring.context.core.ApimsMdc;
import de.sky.newcrm.apims.spring.environment.core.IncidentManagement;
import de.sky.newcrm.apims.spring.exceptions.ApimsBusinessException;
import de.sky.newcrm.apims.spring.utils.VeracodeMitigationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Store exception and call context information in MDC to simplify Splunk monitoring.
 */
@Slf4j
@SuppressWarnings("java:S6813")
public class ApimsAroundMdcErrorListener implements ApimsAroundListener {
    private static final String[] PUBLIC_HTTP_HEADER_NAME_PARTS = new String[]{"akamai"};
    private static final String MDC_PREFIX_INCIDENT_MGMT = "apims.incidentMgmt";
    private static final Map<String, String> originMap = Map.of("-exec-", "http", "schedul", "scheduler");

    @Autowired
    private ApimsMdc mdc;

    private final IncidentManagement incidentMgmt;

    @Value("${apims.app.domain:}")
    private String domain;

    public ApimsAroundMdcErrorListener(IncidentManagement incidentMgmt) {
        this.incidentMgmt = incidentMgmt;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 10;
    }

    @Override
    public void afterAroundMethod(ApimsAroundContext context, Object result, Exception resultError) {
        if (resultError != null) {
            String exceptionHash = String.valueOf(resultError.hashCode());
            // handle error only once (on the lowest level)
            if (mdc.hasError(exceptionHash)) {
                return;
            } else {
                // clear previous error information
                mdc.clearError();
            }
            mdc.putErrorInfo("hash", exceptionHash);
            mdc.putErrorInfo("componentType", context.getType().name());
            mdc.putErrorInfo("componentClass", context.getDeclaringType().getName());
            mdc.putErrorInfo("componentMethod", context.getSignature().replaceAll(" .*\\.", " "));
            final String threadName = Thread.currentThread().getName();
            mdc.putErrorInfo(
                    "invocationOrigin",
                    originMap.entrySet().stream()
                            .filter(entry -> threadName.contains(entry.getKey()))
                            .map(Map.Entry::getValue)
                            .findFirst()
                            .orElse("message"));
            // TODO: Kafka starter
            //            ConsumerRecord<?, ?> consumerRecord =
            //                    ApimsFlowContextHolder.getFlowContext().getInboundConsumerRecord();
            //            if (consumerRecord != null) {
            //                mdc.putErrorInfo("messageRetry", consumerRecord.topic().contains("-retry-") ? "true" : "false");
            //            }

            handleAspectType(context);
            handleException(resultError);
            handleIncidentManagement(resultError);
        } else {
            mdc.clearError();
        }
    }

    private void handleException(Exception resultError) {
        // generic information
        mdc.putErrorInfo("exceptionClass", resultError.getClass().getName());
        mdc.putErrorInfo("exceptionText", (String) VeracodeMitigationUtils.sanitizeLogValue(resultError.getMessage()));
        // filter out internal JDK stuff
        StackTraceElement[] stacktrace = resultError.getStackTrace();
        if (stacktrace == null || stacktrace.length == 0) {
            Optional<Throwable> suppressed =
                    Arrays.stream(resultError.getSuppressed()).findFirst();
            if (suppressed.isPresent()) {
                stacktrace = suppressed.get().getStackTrace();
            }
        }
        if (stacktrace != null) {
            Optional<StackTraceElement> topStackElement = Arrays.stream(stacktrace)
                    .filter(e -> !e.isNativeMethod())
                    .filter(e -> !e.getClassName().contains(".reflect."))
                    .filter(e -> !e.getClassName().contains("reactor.core."))
                    .findFirst();
            mdc.putErrorInfo(
                    "exceptionLocation",
                    topStackElement.map(StackTraceElement::toString).orElse("n/a"));
        }
        ApimsBusinessException apimsBusinessExceptionAnnotation =
                resultError.getClass().getAnnotation(ApimsBusinessException.class);
        boolean apimsBusinessException = apimsBusinessExceptionAnnotation != null;
        mdc.putErrorInfo("businessError", String.valueOf(apimsBusinessException));
        // TODO: Relegate to rest
//        mdc.putErrorInfo("publicApiImpact", String.valueOf(isPublicHttpRequest()));
        if (apimsBusinessException) {
            mdc.putErrorInfo("businessErrorCode", apimsBusinessExceptionAnnotation.value());
        }

        // exception specific information
        if (resultError instanceof RestClientException restClientException) {
            handleRestClientException(restClientException);
        }
    }

    private void handleAspectType(ApimsAroundContext context) {
        String firstArg =
                String.valueOf(Arrays.stream(context.getProceedingJoinPoint().getArgs())
                        .findFirst()
                        .orElse("n/a"));
        if (context.getType().equals(ApimsAspectType.RESTCLIENT)) {
            mdc.putErrorInfo("restclient.endpoint", firstArg);
            String secondArg = context.getProceedingJoinPoint().getArgs()[1].toString();
            mdc.putErrorInfo("restclient.method", secondArg);
        } else if (context.getType().equals(ApimsAspectType.SOAPCLIENT)) {
            mdc.putErrorInfo("soapclient.endpoint", firstArg);
        } else if (context.getType().equals(ApimsAspectType.KAFKA)) {
            mdc.putErrorInfo("kafka.topic", firstArg);
        } else if (context.getType().equals(ApimsAspectType.REPOSITORY)) {
            mdc.putErrorInfo("repository.documentId", firstArg);
        }
    }

    private void handleRestClientException(RestClientException restClientException) {
        if (restClientException instanceof HttpStatusCodeException httpStatusCodeException) {
            mdc.putErrorInfo(
                    "restclient.clientError",
                    String.valueOf(httpStatusCodeException.getStatusCode().is4xxClientError()));
            mdc.putErrorInfo(
                    "restclient.serverError",
                    String.valueOf(httpStatusCodeException.getStatusCode().is5xxServerError()));
            mdc.putErrorInfo(
                    "restclient.statusCode",
                    String.valueOf(httpStatusCodeException.getStatusCode().value()));
            mdc.putErrorInfo("restclient.responseText", (String)
                    VeracodeMitigationUtils.sanitizeLogValue(httpStatusCodeException.getResponseBodyAsString()));
        } else if (restClientException instanceof ResourceAccessException) {
            mdc.putErrorInfo("restclient.statusCode", "n/a");
            mdc.putErrorInfo("restclient.responseText", (String)
                    VeracodeMitigationUtils.sanitizeLogValue(restClientException.getMessage()));
        }
    }

    private void handleIncidentManagement(Exception resultError) {
        String cisId;
        if (resultError instanceof RestClientException) {
            cisId = Optional.ofNullable(incidentMgmt.getRestEndpointCis())
                    .flatMap(restEndpointCis -> restEndpointCis.stream()
                            .filter(restEndpointCi ->
                                    getSafeErrorInfo("restclient.endpoint").startsWith(restEndpointCi.getUrl()))
                            .map(IncidentManagement.RestEndpointCI::getId)
                            .findFirst())
                    .orElse(null);
        } else {
            cisId = Optional.ofNullable(incidentMgmt.getExceptionCis())
                    .flatMap(exceptionCis -> exceptionCis.stream()
                            .filter(exceptionCi ->
                                    getSafeErrorInfo("exceptionClass").startsWith(exceptionCi.getPackageInfo()))
                            .map(IncidentManagement.ExceptionCI::getId)
                            .findFirst())
                    .orElse(null);
        }

        mdc.put(MDC_PREFIX_INCIDENT_MGMT + ".remoteCi", cisId);
        mdc.put(
                MDC_PREFIX_INCIDENT_MGMT + ".businessCis",
                Optional.ofNullable(incidentMgmt.getBusinessCis())
                        .map(businessCis -> businessCis.stream()
                                .map(IncidentManagement.BusinessCI::getId)
                                .collect(Collectors.joining(",")))
                        .orElse(null));
        mdc.put(MDC_PREFIX_INCIDENT_MGMT + ".serviceCi", incidentMgmt.getServiceCi());
    }

    private String getSafeErrorInfo(String value) {
        return Objects.requireNonNullElse(mdc.getErrorInfo(value), "");
    }

//    boolean isPublicHttpRequest() {
//        HttpServletRequest request = ApimsFlowContextHolder.getFlowContext().getInboundHttpServletRequest();
//        if (request == null) {
//            return false;
//        }
//        Enumeration<String> enumeration = request.getHeaderNames();
//        if (enumeration != null) {
//            while (enumeration.hasMoreElements()) {
//                String headerName = enumeration.nextElement();
//                for (String part : PUBLIC_HTTP_HEADER_NAME_PARTS) {
//                    if (headerName.toLowerCase().contains(part.toLowerCase())) {
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
//    }
}
