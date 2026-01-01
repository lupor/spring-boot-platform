///*
// * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
// * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
// */
//package de.sky.newcrm.apims.spring.aspects;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import de.sky.newcrm.apims.spring.aspects.core.around.ApimsAspect;
//import de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3.DefaultJacksonObjectFactory;
//import de.sky.newcrm.apims.spring.serialization.core.masker.ApimsAroundObjectMasker;
//import de.sky.newcrm.apims.spring.serialization.core.masker.ApimsAroundObjectMaskerDefaultImpl;
//import de.sky.newcrm.apims.spring.serialization.core.serializer.ApimsAroundObjectSerializer;
//import de.sky.newcrm.apims.spring.serialization.core.serializer.ApimsAroundObjectSerializerDefaultImpl;
//import de.sky.newcrm.apims.spring.telemetry.mdc.core.ApimsMdc;
//import de.sky.newcrm.apims.spring.utils.ObjectUtils;
//import io.micrometer.tracing.Span;
//import io.micrometer.tracing.otel.bridge.OtelTracer;
//import jakarta.servlet.http.HttpServletRequest;
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Method;
//import java.util.*;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.Signature;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Pointcut;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.core.Ordered;
//import org.springframework.data.repository.Repository;
//import org.springframework.http.HttpHeaders;
//import org.springframework.util.ReflectionUtils;
//import org.springframework.util.StringUtils;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//@ExtendWith(MockitoExtension.class)
//@Slf4j
//class AspectsTest {
//
//    private static final String MASK_KEYS_CSV =
//            "password, newPassword, oldPassword, new-password, old-password, pin, newPin, oldPin, codepin, role-id, secret-id, token, Token, BearerToken, client_secret, client_token, access_token, role_id, secret_id, ciphertext, signed_string, plaintext, authorization, x-forwarded-client-cert, x-envoy-peer-metadata";
//    private static final String MASK_VALUE = "___masked___";
//
//    @Mock
//    OtelTracer tracer;
//
//    @Mock
//    ApimsMdc apimsMdc;
//
//    ObjectMapper aspectsObjectMapper;
//    ApimsAroundObjectMasker apimsAroundObjectMasker;
//
//    ApimsAroundObjectSerializerTypeHandlerCouchbaseUpsert apimsAroundObjectSerializerTypeHandlerCouchbaseUpsert;
//    ApimsAroundObjectSerializerTypeHandlerCouchbaseSubDocumentField
//            apimsAroundObjectSerializerTypeHandlerCouchbaseSubDocumentField;
//    ApimsAroundObjectSerializerTypeHandlerKafkaProducerRecord apimsAroundObjectSerializerTypeHandlerKafkaProducerRecord;
//    ApimsAroundObjectSerializerTypeHandlerKafkaConsumerRecord apimsAroundObjectSerializerTypeHandlerKafkaConsumerRecord;
//    ApimsAroundObjectSerializerTypeHandlerSecuredEntity apimsAroundObjectSerializerTypeHandlerSecuredEntity;
//    ApimsRetryTopicConfigurationPropertiesMap apimsRetryTopicConfigurationPropertiesMap;
//
//    List<ApimsAroundObjectSerializerTypeHandler> apimsAroundObjectSerializerTypeHandlers;
//
//    ApimsAroundObjectSerializer apimsAroundObjectSerializer;
//    ApimsAroundLoggingListener apimsAroundLoggingListener;
//    List<ApimsAroundListener> apimsAroundListeners;
//
//    ApimsTestAspectAroundHandlerDefaultImpl apimsAspectAroundHandler;
//
//    ApimsRestControllerAspect apimsRestControllerAspect;
//    ApimsKafkaListenerContainerFactoryAspect apimsKafkaListenerContainerFactoryAspect;
//    ApimsKafkaTemplateAspect apimsKafkaTemplateAspect;
//
//    ApimsComponentAspect apimsComponentAspect;
//    ApimsControllerAspect apimsControllerAspect;
//    ApimsCouchbaseNativeEndpointAspect apimsCouchbaseNativeEndpointAspect;
//    ApimsPubSubTemplateAspect apimsPubSubTemplateAspect;
//    ApimsIntegrationMessageHandlerAspect apimsIntegrationMessageHandlerAspect;
//    ApimsRepositoryAspect apimsRepositoryAspect;
//    ApimsCouchbaseNativeRepositoryAspect apimsCouchbaseNativeRepositoryAspect;
//    ApimsRestTemplateAspect apimsRestTemplateAspect;
//    ApimsServiceAspect apimsServiceAspect;
//    ApimsStorageTemplateAspect apimsStorageTemplateAspect;
//    ApimsAroundNewTraceIdAspect apimsAroundNewTraceIdAspect;
//    List<ApimsAspect> apimsAspects;
//
//    @Mock
//    HttpServletRequest request;
//
//    @BeforeEach
//    void setUp() {
//        setUpFields();
//    }
//
//    void setUpFields() {
//        aspectsObjectMapper =
//                DefaultJacksonObjectFactory.buildJacksonObjectMapperBuilder().build();
//        apimsAroundObjectMasker = new ApimsAroundObjectMaskerDefaultImpl(
//                Arrays.asList(StringUtils.tokenizeToStringArray(MASK_KEYS_CSV, ",", true, true)), MASK_VALUE);
//        apimsAroundObjectSerializer = new ApimsAroundObjectSerializerDefaultImpl(
//                aspectsObjectMapper, apimsAroundObjectMasker, apimsAroundObjectSerializerTypeHandlers, 4000);
//        apimsRetryTopicConfigurationPropertiesMap =
//                ApimsRetryTopicConfigurationPropertiesMap.builder().build();
//        apimsAroundLoggingListener =
//                new ApimsAroundLoggingListener(apimsAroundObjectSerializer, apimsRetryTopicConfigurationPropertiesMap);
//        apimsAroundListeners = new ArrayList<>();
//        apimsAroundListeners.add(apimsAroundLoggingListener);
//        apimsAspectAroundHandler =
//                new ApimsTestAspectAroundHandlerDefaultImpl(false, apimsAroundListeners, new ArrayList<>());
//
//        apimsKafkaListenerContainerFactoryAspect = new ApimsKafkaListenerContainerFactoryAspect();
//        apimsKafkaTemplateAspect = new ApimsKafkaTemplateAspect(
//                apimsAspectAroundHandler,
//                Map.of("x-sky-app-domain", "test"),
//                Map.of("x-sky-app-my-test-header", "test-header"));
//        apimsAroundNewTraceIdAspect = new ApimsAroundNewTraceIdAspect(tracer, apimsMdc);
//
//        final HttpHeaders headers = new HttpHeaders();
//        headers.setContentLength(0);
//        lenient().when(request.getMethod()).thenReturn("GET");
//        lenient().when(request.getRequestURI()).thenReturn("http://testcase");
//        lenient().when(request.getQueryString()).thenReturn("foo=bar");
//        lenient().when(request.getHeaderNames()).thenReturn(Collections.enumeration(headers.keySet()));
//        lenient()
//                .when(request.getHeaders(anyString()))
//                .thenReturn(Collections.enumeration(headers.get((HttpHeaders.CONTENT_LENGTH))));
//        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
//
//        apimsComponentAspect = new ApimsComponentAspect(apimsAspectAroundHandler);
//        apimsControllerAspect = new ApimsControllerAspect(apimsAspectAroundHandler);
//        apimsCouchbaseNativeEndpointAspect = new ApimsCouchbaseNativeEndpointAspect(apimsAspectAroundHandler);
//        apimsPubSubTemplateAspect = new ApimsPubSubTemplateAspect(apimsAspectAroundHandler);
//        apimsIntegrationMessageHandlerAspect = new ApimsIntegrationMessageHandlerAspect(apimsAspectAroundHandler);
//        apimsRepositoryAspect = new ApimsRepositoryAspect(apimsAspectAroundHandler);
//        apimsCouchbaseNativeRepositoryAspect = new ApimsCouchbaseNativeRepositoryAspect(apimsAspectAroundHandler);
//        apimsRestControllerAspect = new ApimsRestControllerAspect(apimsAspectAroundHandler, true);
//        apimsRestTemplateAspect = new ApimsRestTemplateAspect(apimsAspectAroundHandler);
//        apimsServiceAspect = new ApimsServiceAspect(apimsAspectAroundHandler);
//        apimsStorageTemplateAspect = new ApimsStorageTemplateAspect(apimsAspectAroundHandler);
//        apimsAspects = new ArrayList<>(Arrays.stream(new ApimsAspect[] {
//                    apimsComponentAspect,
//                    apimsControllerAspect,
//                    apimsCouchbaseNativeEndpointAspect,
//                    apimsPubSubTemplateAspect,
//                    apimsIntegrationMessageHandlerAspect,
//                    apimsRepositoryAspect,
//                    apimsCouchbaseNativeRepositoryAspect,
//                    apimsKafkaTemplateAspect,
//                    apimsAroundNewTraceIdAspect,
//                    apimsRestControllerAspect,
//                    apimsRestTemplateAspect,
//                    apimsServiceAspect,
//                    apimsStorageTemplateAspect
//                })
//                .toList());
//    }
//
//    @Test
//    void apimsAspectTypeTest() {
//        testStart();
//        for (ApimsAspectType type : ApimsAspectType.values()) {
//            assertNotNull(type.internalValue());
//            assertNotNull(type.toString());
//        }
//    }
//
//    @Test
//    void springOrderedTest() {
//        testStart();
//        for (ApimsAspect apimsAspect : apimsAspects) {
//            if (apimsAspect instanceof Ordered ordered) {
//                ordered.getOrder();
//            }
//        }
//        assertNotEquals(0, apimsAspects.size());
//    }
//
//    @Test
//    void pointcutMethodsTest() {
//        testStart();
//        pointcutMethodsTest(apimsAspects.toArray(new Object[0]));
//        pointcutMethodsTest(apimsKafkaListenerContainerFactoryAspect);
//        pointcutMethodsTest(apimsCouchbaseNativeRepositoryAspect);
//    }
//
//    @Test
//    void aspectsTest() throws Throwable {
//        testStart();
//        TestComponent component = new TestComponent();
//        MockedProceedingJoinPoint proceedingJoinPoint = new MockedProceedingJoinPoint(
//                component, ReflectionUtils.findMethod(component.getClass(), "testMethod", String.class));
//        List<String> inputMessages = component.getInputMessages();
//        Span.Builder spanBuilder = Span.Builder.NOOP;
//        when(tracer.spanBuilder()).thenReturn(spanBuilder);
//
//        for (ApimsAspect aspect : apimsAspects) {
//            final String inputMessage = "TEST ASPECT CLASS " + aspect.getClass().getName();
//            log.info(inputMessage);
//
//            final String outputMessage = TestComponent.OUTPUT_MESSAGE_PREFIX + inputMessage;
//            proceedingJoinPoint.setTargetMethodArgs(inputMessage);
//            String resultMessage = (String) aspect.aroundMethod(proceedingJoinPoint);
//            assertAll(
//                    "Should contain valid data",
//                    () -> assertEquals(inputMessage, inputMessages.get(inputMessages.size() - 1)),
//                    () -> assertEquals(outputMessage, resultMessage));
//        }
//
//        ApimsWebServiceTemplateAspect apimsWebServiceTemplateAspect = new ApimsWebServiceTemplateAspect(null);
//        ObjectUtils.invokeMethod(
//                ObjectUtils.findMethod(ApimsWebServiceTemplateAspect.class, "anyMethod", true),
//                apimsWebServiceTemplateAspect);
//        assertNotNull(apimsWebServiceTemplateAspect);
//    }
//
//    @Test
//    void aroundInterceptorResultTest() {
//        testStart();
//        ApimsAroundInterceptorResult result = new ApimsAroundInterceptorResult("test");
//        assertNotNull(result.getResult());
//    }
//
//    @Test
//    void apimsAspectAroundHandlerDefaultImplTest() {
//        testStart();
//        assertEquals("_COMPONENT", apimsAspectAroundHandler.calculateFixedTypeValue(null));
//        assertEquals("_COMPONENT", apimsAspectAroundHandler.calculateFixedTypeValue(ApimsAspectType.COMPONENT));
//        assertEquals("CONTROLLER", apimsAspectAroundHandler.calculateFixedTypeValue(ApimsAspectType.CONTROLLER));
//        assertEquals("CONTROLLER", apimsAspectAroundHandler.calculateFixedTypeValue(ApimsAspectType.RESTCONTROLLER));
//    }
//
//    @Test
//    void aroundRepositoryTraceFileListenerTest() {
//        testStart();
//        ApimsAroundAspectRepositoryTraceFileHandler traceFileHandler =
//                mock(ApimsAroundAspectRepositoryTraceFileHandler.class);
//        ApimsAroundContext context = ApimsAroundContext.builder().build();
//        ApimsAroundRepositoryTraceFileListener listener = new ApimsAroundRepositoryTraceFileListener(traceFileHandler);
//        Method method = ObjectUtils.findMethod(listener.getClass(), "isTraceFileMethod", ApimsAroundContext.class);
//
//        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class);
//        Signature signature = mock(Signature.class);
//        context.setProceedingJoinPoint(proceedingJoinPoint);
//        lenient().when(signature.getName()).thenReturn("query");
//        lenient().when(proceedingJoinPoint.getSignature()).thenReturn(signature);
//
//        context.setDeclaringType(Repository.class);
//        assertEquals(Boolean.TRUE, ObjectUtils.invokeMethodAndMakeAccessible(method, listener, true, context));
//        context.setDeclaringType(ApimsCouchbaseNativeEndpoint.class);
//        assertEquals(Boolean.TRUE, ObjectUtils.invokeMethodAndMakeAccessible(method, listener, true, context));
//        context.setDeclaringType(Object.class);
//        assertEquals(Boolean.FALSE, ObjectUtils.invokeMethodAndMakeAccessible(method, listener, true, context));
//
//        context.setDeclaringType(ApimsCouchbaseNativeEndpoint.class);
//        assertEquals(Boolean.TRUE, ObjectUtils.invokeMethodAndMakeAccessible(method, listener, true, context));
//        lenient().when(signature.getName()).thenReturn("test");
//        assertEquals(Boolean.FALSE, ObjectUtils.invokeMethodAndMakeAccessible(method, listener, true, context));
//    }
//
//    protected void pointcutMethodsTest(Object... aspects) {
//        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
//        for (Object aspect : aspects) {
//            Class<?> targetClass = aspect.getClass();
//            Map<Method, Annotation> methodMap = findMethodAnnotation(targetClass, Pointcut.class);
//            for (Map.Entry<Method, Annotation> entry : methodMap.entrySet()) {
//                Method method = entry.getKey();
//                // log.info("invoke pointcut: {}.{} : {}", targetClass.getName(), method.getName(), entry.getValue());
//                ReflectionUtils.makeAccessible(method);
//                ReflectionUtils.invokeMethod(method, aspect);
//            }
//            methodMap = findMethodAnnotation(targetClass, Around.class);
//            for (Map.Entry<Method, Annotation> entry : methodMap.entrySet()) {
//                Method method = entry.getKey();
//                // log.info("invoke pointcut: {}.{} : {}", targetClass.getName(), method.getName(), entry.getValue());
//                ReflectionUtils.makeAccessible(method);
//                try {
//                    ReflectionUtils.invokeMethod(method, aspect, pjp);
//                } catch (Exception ignore) {
//                    // ignore
//                }
//            }
//        }
//    }
//
//    private static class ApimsTestAspectAroundHandlerDefaultImpl extends ApimsAspectAroundHandlerDefaultImpl {
//
//        public ApimsTestAspectAroundHandlerDefaultImpl(
//                boolean createNewSpan,
//                // ApimsTracingContext tracingContext,
//                List<ApimsAroundListener> listeners,
//                List<ApimsAroundInterceptor> interceptors) {
//            super(createNewSpan, listeners, interceptors);
//        }
//
//        @Override
//        protected String calculateFixedTypeValue(ApimsAspectType type) {
//            return super.calculateFixedTypeValue(type);
//        }
//    }
//}
