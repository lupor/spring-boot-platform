/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.telemetry.metrics.core.aspects;

import de.sky.newcrm.apims.spring.aspects.core.around.ApimsAroundContext;
import de.sky.newcrm.apims.spring.aspects.core.around.ApimsAroundListener;
import de.sky.newcrm.apims.spring.context.core.ApimsMdc;
import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import de.sky.newcrm.apims.spring.exceptions.ApimsBusinessException;
import de.sky.newcrm.apims.spring.exceptions.BusinessExceptionErrorCodes;
import de.sky.newcrm.apims.spring.flow.ApimsFlowContext;
import de.sky.newcrm.apims.spring.telemetry.metrics.core.ApimsExecutionMetric;
import de.sky.newcrm.apims.spring.telemetry.metrics.core.ApimsMeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

@Slf4j
@SuppressWarnings({"java:S1854", "java:S3776", "java:S6212"})
public class ApimsAroundMetricsListener implements ApimsAroundListener, InitializingBean {

    private static final String MDC_METRIC_TYPE_KEY = "metric.type";
    private static final String MDC_METRIC_COMPONENT_TYPE_KEY = "metric.component.type";
    private static final double NANOS_TO_SECOND_SCALE = 1000D * 1000D * 1000D;
    private static final String WEB_SERVICE_TEMPLATE_CLASS = "org.springframework.ws.client.core.WebServiceTemplate";
    private static final String MOCKED_WEB_SERVICE_TEMPLATE_CLASS =
            "de.sky.newcrm.apims.spring.core.mocks.ApimsMockedWebServiceTemplate";
    private static final String CONTEXT_DATA_TIMER_IDENTIFIER = "timer";
    private static final String METRIC_TYPE_INBOUND = "INBOUND";
    private static final String METRIC_TYPE_EXECUTION = "EXECUTION";

    @Value("${apims.app.env:}")
    private String appEnv;

    @Value("${apims.app.domain:}")
    private String appDomain;

    @Value("${apims.app.name:}")
    private String appName;

    @Value("${apims.app.instance-id:}")
    private String appInstanceId;

    @Value("${apims.aspects.listeners.metrics.outbound-rest-enabled:true}")
    private boolean outboundRestEnabled;

    @Value("${apims.aspects.listeners.metrics.outbound-couchbase-enabled:true}")
    private boolean outboundCouchbaseEnabled;

    @Value("${apims.aspects.listeners.metrics.outbound-spring-repository-enabled:true}")
    private boolean outboundSpringRepositoryEnabled;

    @Value("${apims.aspects.listeners.metrics.outbound-webservice-enabled:true}")
    private boolean outboundWebServiceEnabled;

    @Value("${apims.aspects.listeners.metrics.outbound-kafka-enabled:false}")
    private boolean outboundKafkaEnabled;

    @Value("${apims.aspects.listeners.metrics.outbound-pubsub-enabled:false}")
    private boolean outboundPubsubEnabled;

    @Value("${spring.couchbase.bucket:}")
    private String couchbaseBucketName;

    @Value("${spring.couchbase.scopename:}")
    private String couchbaseScopeName;

    @Value("${spring.couchbase.second-bucket:}")
    private String couchbaseSecondBucketName;

    @Value("${spring.couchbase.second-scopename:}")
    private String couchbaseSecondScopeName;

    private final Set<String> ignoredComponents;
    private final ApimsMeterRegistry meterRegistry;

    @Autowired
    private ApimsMdc mdc;

    //    @Autowired(required = false)
    //    private ApimsCouchbaseCollectionNameResolver apimsCouchbaseCollectionNameResolver;

    //    private Class<?> springRepositoryClass;

    public ApimsAroundMetricsListener(ApimsMeterRegistry meterRegistry, Set<String> ignoredComponents) {
        this.meterRegistry = meterRegistry;
        this.ignoredComponents = ignoredComponents;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //        TODO: move to relevant starters or refactor
        //        springRepositoryClass = ObjectUtils.getClass("org.springframework.data.repository.Repository", true);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 110;
    }

    @Override
    public void beforeAroundMethod(ApimsAroundContext context) {
        if (isMetricMethod(context)) {
            context.getData().put(calculateContextDataTimerKey(context), meterRegistry.createTimerSample());
        }
    }

    @Override
    public void afterAroundMethod(ApimsAroundContext context, Object result, Exception resultError) {
        if (!isMetricMethod(context)) {
            return;
        }
        boolean executionMetricMethod = isExecutionMetricMethod(context);
        Timer.Sample sample = (Timer.Sample) context.getData().remove(calculateContextDataTimerKey(context));
        ApimsExecutionMetric.ApimsExecutionMetricBuilder builder = executionMetricMethod
                ? calculateExecutionMetric(context, result, resultError)
                : calculateOutboundMetric(context, result, resultError);
        ApimsExecutionMetric metric = builder.build();
        long durationNs = 0L;
        if (sample != null) {
            durationNs = meterRegistry.timer(
                    sample,
                    executionMetricMethod
                            ? ApimsMeterRegistry.APIMS_EXECUTION_TIMER_NAME
                            : ApimsMeterRegistry.APIMS_EXECUTION_OUTBOUND_TIMER_NAME,
                    metric.calculateTags());
        }
        meterRegistry
                .counter(
                        executionMetricMethod
                                ? ApimsMeterRegistry.APIMS_EXECUTION_COUNTER_NAME
                                : ApimsMeterRegistry.APIMS_EXECUTION_OUTBOUND_COUNTER_NAME,
                        metric.calculateTags())
                .increment();
        if (resultError != null && metric.isResultFailed()) {
            meterRegistry
                    .counter(
                            executionMetricMethod
                                    ? ApimsMeterRegistry.APIMS_EXECUTION_ERROR_COUNTER_NAME
                                    : ApimsMeterRegistry.APIMS_EXECUTION_OUTBOUND_ERROR_COUNTER_NAME)
                    .increment();
        }
        logMetric(metric, durationNs);
    }

    protected void logMetric(ApimsExecutionMetric metric, long durationNs) {
        if (metric == null || !log.isDebugEnabled()) {
            return;
        }
        StringBuilder buf = new StringBuilder(100);
        String durationSecondsValue = new DecimalFormat("0.000000").format(durationNs / NANOS_TO_SECOND_SCALE);
        String threadName = Thread.currentThread().getName();
        if (threadName.length() > 12) {
            threadName = threadName.substring(threadName.length() - 12);
        }
        buf.append(durationSecondsValue)
                .append(" | ")
                .append(appEnv)
                .append(" | ")
                .append(appDomain)
                .append(" | ")
                .append(appName)
                .append(" | ")
                .append(appInstanceId)
                .append(" | ")
                .append(threadName)
                .append(" | ")
                .append(metric.getMetricType())
                .append(" | ")
                .append(metric.getComponentType())
                .append(" | ")
                .append((metric.isResultFailed() ? "ERROR" : "OK"))
                .append(" | ")
                .append(metric.getComponmentMethod())
                .append(" | ")
                .append(metric.getRequestTag())
                .append(" | ")
                .append(metric.getResultTag());
        Map<String, String> mdcData = Map.of(
                ApimsMdc.MDC_LOG_TYPE_KEY,
                ApimsMdc.MDC_LOG_TYPE_METRIC,
                MDC_METRIC_TYPE_KEY,
                metric.getMetricType(),
                MDC_METRIC_COMPONENT_TYPE_KEY,
                metric.getComponentType());
        mdc.putAll(mdcData);
        log.debug("|------ [____METRIC] : {}", buf);
        mdc.removeAll(mdcData);
    }

    protected String calculateContextDataTimerKey(ApimsAroundContext context) {
        return this.getClass().getSimpleName() + "." + context.getShortSignature() + "."
                + CONTEXT_DATA_TIMER_IDENTIFIER;
    }

    protected boolean isMetricMethod(ApimsAroundContext context) {
        return isExecutionMetricMethod(context) || isOutboundMetricMethod(context);
    }

    protected boolean isExecutionMetricMethod(ApimsAroundContext context) {
        return context.getActiveCallsCount() == 1 && isMetricsActivated(context);
    }

    protected boolean isOutboundMetricMethod(ApimsAroundContext context) {
        //        TODO: relocate to relevant starters or refactor
        //        Class<?> declaringType = context.getDeclaringType();
        //        boolean flag = (outboundRestEnabled && RestTemplate.class.isAssignableFrom(declaringType))
        //                || (outboundCouchbaseEnabled &&
        // ApimsCouchbaseRepository.class.isAssignableFrom(declaringType))
        //                || (outboundSpringRepositoryEnabled
        //                        && springRepositoryClass != null
        //                        && springRepositoryClass.isAssignableFrom(declaringType))
        //                || (outboundKafkaEnabled && KafkaTemplate.class.isAssignableFrom(declaringType))
        //                || (outboundPubsubEnabled && PubSubTemplate.class.isAssignableFrom(declaringType))
        //                || (outboundWebServiceEnabled && isWebServiceTemplate(declaringType));
        //        return flag && isMetricsActivated(context);
        return isMetricsActivated(context);
    }

    @SuppressWarnings({"java:S1481", "java:S1854"})
    protected ApimsExecutionMetric.ApimsExecutionMetricBuilder calculateExecutionMetric(
            ApimsAroundContext context, Object result, Exception resultError) {
        ApimsExecutionMetric.ApimsExecutionMetricBuilder builder = ApimsExecutionMetric.builder();
        builder.metricType(METRIC_TYPE_EXECUTION);
        resolveDefaultMetric(builder, context, result, resultError);
        //        TODO: What is this for? Can this be relocated to the given starters?
        //        boolean flag = resolveControllerInboundMetric(builder, context, result)
        //                || resolveKafkaInboundMetric(builder, context, result)
        //                || resolvePubsubInboundMetric(builder, context, result)
        //                || resolveSftpInboundMetric(builder, context, result);
        return builder;
    }

    @SuppressWarnings("java:1172")
    protected void resolveDefaultMetric(
            ApimsExecutionMetric.ApimsExecutionMetricBuilder builder,
            ApimsAroundContext context,
            Object result,
            Exception resultError) {
        builder.componentType(context.getType().name())
                .componentFullName(context.getDeclaringType().getName())
                .componentName(context.getDeclaringType().getSimpleName())
                .componmentMethod(context.getShortSignature());
        if (resultError == null) {
            builder.resultOK(true).resultFailed(false);
        }
        if (resultError != null) {
            String resultTag = BusinessExceptionErrorCodes.calculateErrorCode(resultError, false);
            if (!StringUtils.hasLength(resultTag)) {
                resultTag = resultError.getClass().getSimpleName();
            }

            ApimsBusinessException annotation = resultError.getClass().getAnnotation(ApimsBusinessException.class);
            boolean reportAsError = annotation == null || annotation.logAsError();
            builder.resultOK(!reportAsError).resultFailed(reportAsError).resultTag(resultTag);
        }
    }

    //    @SuppressWarnings("java:1172")
    //    protected boolean resolveControllerInboundMetric(
    //            ApimsExecutionMetric.ApimsExecutionMetricBuilder builder, ApimsAroundContext context, Object result) {
    //        HttpServletRequest request = ApimsFlowContext.get().getInboundHttpServletRequest();
    //        if (request == null) {
    //            return false;
    //        }
    //        builder.metricType(METRIC_TYPE_INBOUND);
    //        builder.requestTag(request.getMethod());
    //        if (result instanceof ResponseEntity<?> responseEntity) {
    //            resolveResult(builder, responseEntity);
    //        }
    //        return true;
    //    }
    //
    //    @SuppressWarnings("java:1172")
    //    @ApimsReportGeneratedHint
    //    protected boolean resolveKafkaInboundMetric(
    //            ApimsExecutionMetric.ApimsExecutionMetricBuilder builder, ApimsAroundContext context, Object result) {
    //        ConsumerRecord<?, ?> consumerRecord = ApimsFlowContext.get().getInboundConsumerRecord();
    //        if (consumerRecord == null) {
    //            return false;
    //        }
    //        builder.metricType(METRIC_TYPE_INBOUND);
    //        String topic = consumerRecord.topic();
    //        if (topic.contains("-retry-")) {
    //            topic = "[RETRY TOPIC] " + topic;
    //        } else if (topic.contains("-dlt")) {
    //            topic = "[DEADLETTER TOPIC] " + topic;
    //        } else if (topic.contains("-posion")) {
    //            topic = "[POISON TOPIC] " + topic;
    //        } else {
    //            topic = "[MAIN TOPIC] " + topic;
    //        }
    //        builder.componentType(ApimsAspectType.KAFKA.name()).requestTag(topic);
    //        return true;
    //    }
    //
    //    @SuppressWarnings("java:1172")
    //    protected boolean resolvePubsubInboundMetric(
    //            ApimsExecutionMetric.ApimsExecutionMetricBuilder builder, ApimsAroundContext context, Object result) {
    //        PubsubMessage pubsubMessage = ApimsFlowContext.get().getInboundPubsubMessage();
    //        if (pubsubMessage == null) {
    //            return false;
    //        }
    //        builder.metricType(METRIC_TYPE_INBOUND);
    //        builder.componentType(ApimsAspectType.PUBSUB.name());
    //        return true;
    //    }
    //
    //    @SuppressWarnings("java:1172")
    //    protected boolean resolveSftpInboundMetric(
    //            ApimsExecutionMetric.ApimsExecutionMetricBuilder builder, ApimsAroundContext context, Object result) {
    //        ApimsSftpDownloadedFile sftpDownloadedFile = ApimsFlowContext.get().getInboundSftpDownloadedFile();
    //        if (sftpDownloadedFile == null) {
    //            return false;
    //        }
    //        builder.metricType(METRIC_TYPE_INBOUND);
    //        builder.componentType(ApimsAspectType.SFTP.name())
    //                .requestTag(sftpDownloadedFile.getRemoteServerHost() + ":" +
    // sftpDownloadedFile.getRemoteServerPort()
    //                        + "/" + sftpDownloadedFile.getRemoteDirectoryName() + "/"
    //                        + sftpDownloadedFile.getRemoteFileName());
    //        return true;
    //    }

    @SuppressWarnings({"java:S1481", "java:S1854"})
    protected ApimsExecutionMetric.ApimsExecutionMetricBuilder calculateOutboundMetric(
            ApimsAroundContext context, Object result, Exception resultError) {
        ApimsExecutionMetric.ApimsExecutionMetricBuilder builder = ApimsExecutionMetric.builder();
        builder.metricType("OUTBOUND");
        resolveDefaultMetric(builder, context, result, resultError);
        //        TODO: What is this for? Can this be relocated to the given starters?
        //        boolean flag = resolveRestTemplateOutboundMetric(builder, context, result)
        //                || resolveCouchbaseOutboundMetric(builder, context, result)
        //                || resolveSpringRepositoryOutboundMetric(builder, context, result)
        //                || resolveKafkaTemplateOutboundMetric(builder, context, result)
        //                || resolvePubsubTemplateOutboundMetric(builder, context, result)
        //                || resolveWebServiceTemplateOutboundMetric(builder, context, result);
        return builder;
    }

    @SuppressWarnings("java:1172")
    //    protected boolean resolveRestTemplateOutboundMetric(
    //            ApimsExecutionMetric.ApimsExecutionMetricBuilder builder, ApimsAroundContext context, Object result) {
    //        if (!RestTemplate.class.isAssignableFrom(context.getDeclaringType())) {
    //            return false;
    //        }
    //        URI uri = ApimsAroundHelper.getURI(context);
    //        HttpMethod httpMethod = ApimsAroundHelper.getHttpMethod(context);
    //        String requestTag = httpMethod == null ? "" : (httpMethod.name() + " ");
    //        if (uri != null) {
    //            requestTag += (uri.getScheme() + "://" + uri.getHost());
    //            if (uri.getPort() > 0) {
    //                requestTag += (":" + uri.getPort());
    //            }
    //        }
    //        builder.requestTag(requestTag);
    //        if (result instanceof ResponseEntity<?> responseEntity) {
    //            resolveResult(builder, responseEntity);
    //        }
    //        return true;
    //    }
    //
    //    @SuppressWarnings("java:1172")
    //    protected boolean resolveWebServiceTemplateOutboundMetric(
    //            ApimsExecutionMetric.ApimsExecutionMetricBuilder builder, ApimsAroundContext context, Object result) {
    //        if (!isWebServiceTemplate(context.getDeclaringType())) {
    //            return false;
    //        }
    //        URI uri = ApimsAroundHelper.getURI(context);
    //        HttpMethod httpMethod = HttpMethod.POST;
    //        String requestTag = httpMethod.name() + " ";
    //        if (uri != null) {
    //            requestTag += (uri.getScheme() + "://" + uri.getHost());
    //            if (uri.getPort() > 0) {
    //                requestTag += (":" + uri.getPort());
    //            }
    //        }
    //        builder.requestTag(requestTag);
    //        return true;
    //    }
    //
    //    @SuppressWarnings("java:1172")
    //    @ApimsReportGeneratedHint
    //    protected boolean resolveKafkaTemplateOutboundMetric(
    //            ApimsExecutionMetric.ApimsExecutionMetricBuilder builder, ApimsAroundContext context, Object result) {
    //        if (!KafkaTemplate.class.isAssignableFrom(context.getDeclaringType())) {
    //            return false;
    //        }
    //        ProducerRecord<?, ?> producerRecord = ApimsAroundHelper.getProducerRecord(context);
    //        if (producerRecord != null) {
    //            builder.requestTag("[TOPIC] " + producerRecord.topic());
    //        } else if (context.getProceedingJoinPoint().getArgs().length != 0
    //                && context.getProceedingJoinPoint().getArgs()[0] instanceof String) {
    //            builder.requestTag("[TOPIC] " + context.getProceedingJoinPoint().getArgs()[0]);
    //        }
    //        return true;
    //    }
    //
    //    @ApimsReportGeneratedHint
    //    protected boolean resolveCouchbaseOutboundMetric(
    //            ApimsExecutionMetric.ApimsExecutionMetricBuilder builder, ApimsAroundContext context, Object result) {
    //
    //        if (!ApimsCouchbaseRepository.class.isAssignableFrom(context.getDeclaringType())) {
    //            return false;
    //        }
    //        if (apimsCouchbaseCollectionNameResolver != null) {
    //            final Class<?> declaringType = context.getDeclaringType();
    //            String bucketName = apimsCouchbaseCollectionNameResolver.getBucketName(declaringType);
    //            String scopeName = apimsCouchbaseCollectionNameResolver.getScopeName(declaringType);
    //            String collectionName = apimsCouchbaseCollectionNameResolver.getCollectionName(declaringType);
    //            if (bucketName == null) {
    //                bucketName = couchbaseBucketName;
    //            }
    //            if (scopeName == null) {
    //                scopeName = bucketName.equals(couchbaseBucketName) ? couchbaseScopeName :
    // couchbaseSecondScopeName;
    //            }
    //            builder.requestTag("[COLLECTION] " + bucketName + "." + scopeName + "." + collectionName);
    //        }
    //        return true;
    //    }
    //
    //    @ApimsReportGeneratedHint
    //    protected boolean resolveSpringRepositoryOutboundMetric(
    //            ApimsExecutionMetric.ApimsExecutionMetricBuilder builder, ApimsAroundContext context, Object result) {
    //
    //        return springRepositoryClass != null &&
    // springRepositoryClass.isAssignableFrom(context.getDeclaringType());
    //    }
    //
    //    @SuppressWarnings("java:1172")
    //    @ApimsReportGeneratedHint
    //    protected boolean resolvePubsubTemplateOutboundMetric(
    //            ApimsExecutionMetric.ApimsExecutionMetricBuilder builder, ApimsAroundContext context, Object result) {
    //        if (!PubSubTemplate.class.isAssignableFrom(context.getDeclaringType())) {
    //            return false;
    //        }
    //        if (context.getProceedingJoinPoint().getArgs().length != 0) {
    //            builder.requestTag(String.valueOf(context.getProceedingJoinPoint().getArgs()[0]));
    //        }
    //        return true;
    //    }

    @ApimsReportGeneratedHint
    protected void resolveResult(
            ApimsExecutionMetric.ApimsExecutionMetricBuilder builder, ResponseEntity<?> responseEntity) {
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            builder.resultOK(true)
                    .resultFailed(false)
                    .resultTag(responseEntity.getStatusCode().toString());
        } else {
            builder.resultOK(false)
                    .resultFailed(true)
                    .resultTag(responseEntity.getStatusCode().toString());
        }
    }

    protected boolean isMetricsActivated(ApimsAroundContext context) {
        return !ignoredComponents.contains(context.getDeclaringType().getSimpleName())
                && findApimsAroundMetricsListenerSuppressAnnotation() == null;
    }

    protected ApimsAroundMetricsListenerSuppress findApimsAroundMetricsListenerSuppressAnnotation() {
        return ApimsFlowContext.get()
                .findCurrentMethodOrClassAnnotation(ApimsAroundMetricsListenerSuppress.class, false);
    }

    protected boolean isWebServiceTemplate(Class<?> declaringType) {
        return WEB_SERVICE_TEMPLATE_CLASS.equals(declaringType.getName())
                || MOCKED_WEB_SERVICE_TEMPLATE_CLASS.equals(declaringType.getName());
    }
}
