/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.text.MessageFormat;
import java.util.Map;

@RequiredArgsConstructor
@SuppressWarnings({"java:S6212"})
public class ApimsRestClient {

    protected final RestTemplate restTemplate;

    @Value("${apims.rest.auto-validate-request:false}")
    private boolean autoValidateRequest;

    @Value("${apims.rest.auto-validate-response:false}")
    private boolean autoValidateResponse;

    protected boolean isAutoValidateResponse() {
        return autoValidateResponse;
    }

    protected String validateUrl(String url) {
        return VeracodeMitigationUtils.sanitizeUrl(url);
    }

    /* Do not use this anymore. Generating the URL before passing it to the RestTemplate breaks the HTTP metrics.
    A new metric is generated for each invocation of the same URL, as there is no way to know what part of the URL
    is dynamic. Result: Cardinality explosion. */
    @Deprecated
    protected String calculateUrl(String url, Object... arguments) {
        AssertUtils.hasLengthCheck("url", url);
        return validateUrl(MessageFormat.format(url, arguments));
    }

    protected String getJsonString(Object object) {
        return ObjectMapperUtils.writeValueAsString(object);
    }

    protected HttpEntity<Object> createHttpEntity(Object body) {
        return new HttpEntity<>(body);
    }

    protected HttpEntity<Object> createHttpEntity(Object body, Map.Entry<String, String>... headers) {
        return createHttpEntity(body, createHttpHeaders(headers));
    }

    protected HttpEntity<Object> createHttpEntity(Object body, HttpHeaders httpHeaders) {
        return new HttpEntity<>(body, httpHeaders);
    }

    protected HttpHeaders createHttpHeaders(Map.Entry<?, ?>... headers) {
        HttpHeaders h = new HttpHeaders();
        for (Map.Entry<?, ?> entry : headers) {
            if (entry.getKey() != null) {
                h.set(String.valueOf(entry.getKey()), entry.getValue() == null ? "" : String.valueOf(entry.getValue()));
            }
        }
        return h;
    }

    protected void delete(String uriTemplate, Object... uriVars) {
        exchangeForEntity(uriTemplate, HttpMethod.DELETE, null, Void.class, uriVars);
    }

    protected void delete(String uriTemplate, Map<String, ?> uriVars) {
        exchangeForEntity(uriTemplate, HttpMethod.DELETE, null, Void.class, uriVars);
    }

    /* Don't use this method for uri variables, use String uriTemplate  */
    @Deprecated
    protected <T> ResponseEntity<T> exchange(
            URI url, HttpMethod httpMethod, HttpEntity<?> httpEntity, Class<T> responseType) {
        validateRequest(httpEntity);
        return restTemplate.exchange(url, httpMethod, httpEntity, responseType);
    }

    /* Don't use this method for uri variables, use String uriTemplate  */
    @Deprecated
    protected <T> T exchangeForEntity(
            URI url, HttpMethod httpMethod, HttpEntity<?> httpEntity, Class<T> responseType, boolean mandatory) {
        validateRequest(httpEntity);
        return validateResponse(exchange(url, httpMethod, httpEntity, responseType), mandatory);
    }

    /* Don't use this method for uri variables, use String uriTemplate  */
    @Deprecated
    protected <T> T exchangeForEntity(
            URI url,
            HttpMethod httpMethod,
            HttpEntity<?> httpEntity,
            ParameterizedTypeReference<T> responseType,
            boolean mandatory) {
        validateRequest(httpEntity);
        return validateResponse(exchange(url, httpMethod, httpEntity, responseType), mandatory);
    }

    /* Don't use this method for uri variables, use String uriTemplate  */
    @Deprecated
    protected <T> ResponseEntity<T> exchange(
            URI url, HttpMethod httpMethod, HttpEntity<?> httpEntity, ParameterizedTypeReference<T> responseType) {
        validateRequest(httpEntity);
        return restTemplate.exchange(url, httpMethod, httpEntity, responseType);
    }

    protected <T> ResponseEntity<T> exchange(
            String uriTemplate,
            HttpMethod httpMethod,
            HttpEntity<?> httpEntity,
            Class<T> responseType,
            Object... uriVars) {
        validateRequest(httpEntity);
        return restTemplate.exchange(uriTemplate, httpMethod, httpEntity, responseType, uriVars);
    }

    protected <T> ResponseEntity<T> exchange(
            String uriTemplate,
            HttpMethod httpMethod,
            HttpEntity<?> httpEntity,
            Class<T> responseType,
            Map<String, ?> uriVars) {
        validateRequest(httpEntity);
        return restTemplate.exchange(uriTemplate, httpMethod, httpEntity, responseType, uriVars);
    }

    protected <T> ResponseEntity<T> exchange(
            String uriTemplate,
            HttpMethod httpMethod,
            HttpEntity<?> httpEntity,
            ParameterizedTypeReference<T> responseType,
            Object... uriVars) {
        validateRequest(httpEntity);
        return restTemplate.exchange(uriTemplate, httpMethod, httpEntity, responseType, uriVars);
    }

    protected <T> ResponseEntity<T> exchange(
            String uriTemplate,
            HttpMethod httpMethod,
            HttpEntity<?> httpEntity,
            ParameterizedTypeReference<T> responseType,
            Map<String, ?> uriVars) {
        validateRequest(httpEntity);
        return restTemplate.exchange(uriTemplate, httpMethod, httpEntity, responseType, uriVars);
    }

    protected <T> T getForEntity(String uriTemplate, Class<T> responseType, Object... uriVars) {
        return exchangeForEntity(uriTemplate, HttpMethod.GET, null, responseType, uriVars);
    }

    protected <T> T getForEntity(String uriTemplate, Class<T> responseType, Map<String, ?> uriVars) {
        return exchangeForEntity(uriTemplate, HttpMethod.GET, null, responseType, uriVars);
    }

    protected <T> T getForEntity(String uriTemplate, ParameterizedTypeReference<T> responseType, Object... uriVars) {
        return exchangeForEntity(uriTemplate, HttpMethod.GET, null, responseType, uriVars);
    }

    protected <T> T getForEntity(
            String uriTemplate, ParameterizedTypeReference<T> responseType, Map<String, ?> uriVars) {
        return exchangeForEntity(uriTemplate, HttpMethod.GET, null, responseType, uriVars);
    }

    protected <T> T getForEntity(String uriTemplate, HttpHeaders headers, Class<T> responseType, Object... uriVars) {
        return exchangeForEntity(
                uriTemplate,
                HttpMethod.GET,
                headers == null ? null : new HttpEntity<>(null, headers),
                responseType,
                uriVars);
    }

    protected <T> T getForEntity(
            String uriTemplate, HttpHeaders headers, Class<T> responseType, Map<String, ?> uriVars) {
        return exchangeForEntity(
                uriTemplate,
                HttpMethod.GET,
                headers == null ? null : new HttpEntity<>(null, headers),
                responseType,
                uriVars);
    }

    protected <T> T getForEntity(
            String uriTemplate, HttpHeaders headers, ParameterizedTypeReference<T> responseType, Object... uriVars) {
        return exchangeForEntity(
                uriTemplate,
                HttpMethod.GET,
                headers == null ? null : new HttpEntity<>(null, headers),
                responseType,
                uriVars);
    }

    protected <T> T getForEntity(
            String uriTemplate,
            HttpHeaders headers,
            ParameterizedTypeReference<T> responseType,
            Map<String, ?> uriVars) {
        return exchangeForEntity(
                uriTemplate,
                HttpMethod.GET,
                headers == null ? null : new HttpEntity<>(null, headers),
                responseType,
                uriVars);
    }

    protected <T> T exchangeForEntity(
            String uriTemplate,
            HttpMethod httpMethod,
            HttpEntity<?> httpEntity,
            Class<T> responseType,
            Object... uriVars) {
        validateRequest(httpEntity);
        return validateResponse(
                restTemplate.exchange(uriTemplate, httpMethod, httpEntity, responseType, uriVars),
                !Void.class.equals(responseType));
    }

    protected <T> T exchangeForEntity(
            String uriTemplate,
            HttpMethod httpMethod,
            HttpEntity<?> httpEntity,
            Class<T> responseType,
            Map<String, ?> uriVars) {
        validateRequest(httpEntity);
        return validateResponse(
                restTemplate.exchange(uriTemplate, httpMethod, httpEntity, responseType, uriVars),
                !Void.class.equals(responseType));
    }

    protected <T> T exchangeForEntity(
            String uriTemplate,
            HttpMethod httpMethod,
            HttpEntity<?> httpEntity,
            ParameterizedTypeReference<T> responseType,
            Object... uriVars) {
        validateRequest(httpEntity);
        return validateResponse(restTemplate.exchange(uriTemplate, httpMethod, httpEntity, responseType, uriVars));
    }

    protected <T> T exchangeForEntity(
            String uriTemplate,
            HttpMethod httpMethod,
            HttpEntity<?> httpEntity,
            ParameterizedTypeReference<T> responseType,
            Map<String, ?> uriVars) {
        validateRequest(httpEntity);
        return validateResponse(restTemplate.exchange(uriTemplate, httpMethod, httpEntity, responseType, uriVars));
    }

    protected void validateRequest(HttpEntity<?> httpEntity) throws BusinessException {
        if (autoValidateRequest && httpEntity != null && httpEntity.hasBody()) {
            InvalidRequestDataBusinessException.createValidator()
                    .validateAnnotations(httpEntity.getBody())
                    .throwIfContainsViolations();
        }
    }

    protected <T> T validateResponse(ResponseEntity<T> responseEntity) throws BusinessException {
        return validateResponse(responseEntity, true);
    }

    protected <T> T validateResponse(ResponseEntity<T> responseEntity, boolean mandatory) throws BusinessException {
        AssertUtils.isTrueCheck(
                "response.is2xxSuccessful()", responseEntity.getStatusCode().is2xxSuccessful());
        return validateResponseObject(responseEntity.getBody(), mandatory);
    }

    protected <T> T validateResponseObject(T responseObject, boolean mandatory) throws BusinessException {
        if (mandatory) {
            AssertUtils.notNullCheck("responseBody", responseObject);
        }
        if (responseObject != null && isAutoValidateResponse()) {
            InvalidResponseDataBusinessException.createValidator()
                    .validateAnnotations(responseObject)
                    .throwIfContainsViolations();
        }
        return responseObject;
    }
}
