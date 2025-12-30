/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.aspects.core.around;

import de.sky.newcrm.apims.spring.aspects.MockedProceedingJoinPoint;
import de.sky.newcrm.apims.spring.context.core.ApimsMdc;
import de.sky.newcrm.apims.spring.environment.core.ApimsMockUtils;
import de.sky.newcrm.apims.spring.environment.core.IncidentManagement;
import de.sky.newcrm.apims.spring.exceptions.ApimsRuntimeException;
import de.sky.newcrm.apims.spring.telemetry.mdc.aspect.ApimsAroundMdcErrorListener;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApimsAroundMdcErrorListenerTest {

    private ApimsAroundMdcErrorListener listener;

    @Mock
    private ApimsMdc mdc;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        IncidentManagement incidentManagement = new IncidentManagement();
        incidentManagement.setServiceCi("testServiceCi");
        IncidentManagement.BusinessCI businessCI = new IncidentManagement.BusinessCI();
        businessCI.setId("businessId");
        businessCI.setName("businessName");
        IncidentManagement.ExceptionCI exceptionCI = new IncidentManagement.ExceptionCI();
        exceptionCI.setId("exceptionId");
        exceptionCI.setPackageInfo("de.sky.newcrm.apims.spring.core.support.exception.ApimsRuntimeException");
        IncidentManagement.RestEndpointCI restEndpointCI = new IncidentManagement.RestEndpointCI();
        restEndpointCI.setId("restEndpointId");
        restEndpointCI.setUrl("http://test/123");
        incidentManagement.setBusinessCis(List.of(businessCI));
        incidentManagement.setExceptionCis(List.of(exceptionCI));
        incidentManagement.setRestEndpointCis(List.of(restEndpointCI));

        MockitoAnnotations.openMocks(this);
        listener = new ApimsAroundMdcErrorListener(incidentManagement);
        ApimsMockUtils.injectField(listener, "mdc", mdc);
    }

    @Test
    void testAfterAroundMethod_WithHttpStatusCodeException() {
        // Arrange
        HttpStatusCodeException exception = new HttpClientErrorException(HttpStatusCode.valueOf(404), "Not Found");
        ApimsAroundContext apimsAroundContext = ApimsAroundContext.builder()
                .type(ApimsAspectType.RESTCLIENT)
                .declaringType(this.getClass())
                .proceedingJoinPoint(new MockedProceedingJoinPoint(null, null, new Object[]{"http://xxx", "GET"}))
                .signature("methodName")
                .build();

        // Act
        listener.afterAroundMethod(apimsAroundContext, null, exception);

        // Assert
        verify(mdc).putErrorInfo("hash", String.valueOf(exception.hashCode()));
        verify(mdc).putErrorInfo("componentType", ApimsAspectType.RESTCLIENT.name());
        verify(mdc).putErrorInfo("componentClass", getClass().getName());
        verify(mdc).putErrorInfo("componentMethod", "methodName");
        verify(mdc).putErrorInfo("exceptionClass", exception.getClass().getName());
        verify(mdc).putErrorInfo("exceptionText", "404 Not Found");
        verify(mdc).putErrorInfo("restclient.statusCode", "404");
        verify(mdc).putErrorInfo("restclient.clientError", "true");
    }

    @Test
    void testAfterAroundMethod_WithResourceAccessException() {
        // Arrange
        ResourceAccessException exception = new ResourceAccessException("Connection Error");
        ApimsAroundContext apimsAroundContext = ApimsAroundContext.builder()
                .type(ApimsAspectType.RESTCLIENT)
                .declaringType(this.getClass())
                .proceedingJoinPoint(new MockedProceedingJoinPoint(null, null, new Object[]{"http://xxx", "GET"}))
                .signature("methodName")
                .build();

        // Act
        listener.afterAroundMethod(apimsAroundContext, null, exception);

        // Assert
        verify(mdc).putErrorInfo("hash", String.valueOf(exception.hashCode()));
        verify(mdc).putErrorInfo("componentType", ApimsAspectType.RESTCLIENT.name());
        verify(mdc).putErrorInfo("componentClass", getClass().getName());
        verify(mdc).putErrorInfo("componentMethod", "methodName");
        verify(mdc).putErrorInfo("exceptionClass", exception.getClass().getName());
        verify(mdc).putErrorInfo("exceptionText", "Connection Error");
        verify(mdc).putErrorInfo("restclient.statusCode", "n/a");
    }

    @Test
    void testAfterAroundMethod_WithNullError() {
        // Arrange
        ApimsAroundContext apimsAroundContext =
                ApimsAroundContext.builder().type(ApimsAspectType.RESTCLIENT).build();

        // Act
        listener.afterAroundMethod(apimsAroundContext, null, null);

        // Assert
        verify(mdc).clearError();
    }


    @Test
    void testIncidentManagement_WithExceptionCis() {
        ApimsRuntimeException exception = new ApimsRuntimeException("Exception Occured");
        ApimsAroundContext context = ApimsAroundContext.builder()
                .type(ApimsAspectType.REPOSITORY)
                .declaringType(this.getClass())
                .proceedingJoinPoint(new MockedProceedingJoinPoint(null, null, new Object[]{"docId"}))
                .signature("methodName")
                .build();
        when(mdc.getErrorInfo("exceptionClass"))
                .thenReturn("de.sky.newcrm.apims.spring.core.support.exception.ApimsRuntimeException");
        listener.afterAroundMethod(context, null, exception);

        verify(mdc).put("apims.incidentMgmt.remoteCi", "exceptionId");
        verify(mdc).put("apims.incidentMgmt.businessCis", "businessId");
        verify(mdc).put("apims.incidentMgmt.serviceCi", "testServiceCi");
    }

    @Test
    void testIncidentManagement_WithRestEndpointCis() {
        HttpStatusCodeException exception =
                new HttpClientErrorException(HttpStatusCode.valueOf(404), "http://test/123 Not Found");
        ApimsAroundContext context = ApimsAroundContext.builder()
                .type(ApimsAspectType.RESTCLIENT)
                .declaringType(this.getClass())
                .proceedingJoinPoint(new MockedProceedingJoinPoint(null, null, new Object[]{"http://test/123", "GET"}))
                .signature("methodName")
                .build();
        when(mdc.getErrorInfo("restclient.endpoint")).thenReturn("http://test/123");
        listener.afterAroundMethod(context, null, exception);

        verify(mdc).put("apims.incidentMgmt.remoteCi", "restEndpointId");
        verify(mdc).put("apims.incidentMgmt.businessCis", "businessId");
        verify(mdc).put("apims.incidentMgmt.serviceCi", "testServiceCi");
    }

    @Test
    void testIncidentManagement_WithDefaultCis() {
        IncidentManagement incidentManagement = new IncidentManagement();
        IncidentManagement.BusinessCI businessCI = new IncidentManagement.BusinessCI();
        businessCI.setId("businessId");
        businessCI.setName("businessName");
        IncidentManagement.DefaultCI defaultCI = new IncidentManagement.DefaultCI();
        defaultCI.setSalesforce("SVS-Salesforce");
        IncidentManagement.RestEndpointCI restEndpointCI = new IncidentManagement.RestEndpointCI();
        restEndpointCI.setId(defaultCI.getSalesforce());
        restEndpointCI.setUrl("http://test/123");
        incidentManagement.setBusinessCis(List.of(businessCI));
        incidentManagement.setRestEndpointCis(List.of(restEndpointCI));
        incidentManagement.setDefaultCis(defaultCI);

        listener = new ApimsAroundMdcErrorListener(incidentManagement);
        ApimsMockUtils.injectField(listener, "mdc", mdc);
        ApimsMockUtils.injectField(listener, "domain", "defaultDomainCi");

        HttpStatusCodeException exception =
                new HttpClientErrorException(HttpStatusCode.valueOf(404), "http://test/123 Not Found");
        ApimsAroundContext context = ApimsAroundContext.builder()
                .type(ApimsAspectType.RESTCLIENT)
                .declaringType(this.getClass())
                .proceedingJoinPoint(new MockedProceedingJoinPoint(null, null, new Object[]{"http://test/123", "GET"}))
                .signature("methodName")
                .build();
        when(mdc.getErrorInfo("restclient.endpoint")).thenReturn("http://test/123");
        listener.afterAroundMethod(context, null, exception);

        verify(mdc).put("apims.incidentMgmt.remoteCi", "SVS-Salesforce");
        verify(mdc).put("apims.incidentMgmt.businessCis", "businessId");
    }
}
