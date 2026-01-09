/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.exceptions;


import de.sky.newcrm.apims.spring.context.core.ApimsSpringApplication;
import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import de.sky.newcrm.apims.spring.environment.core.ApimsSpringContext;
import de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3.DefaultJacksonObjectFactory;
import de.sky.newcrm.apims.spring.utils.AssertUtils;
import de.sky.newcrm.apims.spring.utils.scanner.ApimsClassScanner;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@SuppressWarnings({"java:S1874", "java:S6201", "java:S6212"})
public class ApimsBaseExceptionResolver {
    protected static final String BUSINESS_EXCEPTION_DETAILS_KEY = "details";
    protected static final String BUSINESS_EXCEPTION_ERROR_CODE_KEY = "code";

    private static ApimsBaseExceptionResolver instance = null;
    private static final Object instanceLock = new Object();
    private static final Logger log = LoggerFactory.getLogger(ApimsBaseExceptionResolver.class);
    private final ObjectMapper objectMapper =
            DefaultJacksonObjectFactory.createDefaultJsonObjectMapper();
    private final Map<String, ApimsBusinessExceptionCacheItem> exceptionMap = new ConcurrentHashMap<>();

    private ApimsBaseExceptionResolver() {
        registerExceptions();
    }

    @SuppressWarnings("java:S2168")
    private static ApimsBaseExceptionResolver getInstance() {
        if (instance == null) {
            synchronized (instanceLock) {
                if (instance == null) {
                    instance = new ApimsBaseExceptionResolver();
                }
            }
        }
        return instance;
    }

    public static Exception getException(String errorCode) {
        return getInstance().resolveException(errorCode);
    }

    public static Exception parseException(String body) {
        return getInstance().resolveApimsBaseException(body);
    }

    public static Map<String, Object> parseBodyAsMap(String body) {
        return getInstance().getBodyAsMap(body);
    }

    private Exception resolveException(String errorCode) {
        AssertUtils.hasLengthCheck("errorCode", errorCode);
        errorCode = errorCode.toUpperCase();
        if (exceptionMap.containsKey(errorCode)) {
            return instantiateException(exceptionMap.get(errorCode));
        } else {
            return resolveUnknownException(errorCode);
        }
    }

    private synchronized ApimsBaseException resolveUnknownException(String errorCode) {
        ApimsBaseException bex = BusinessException.build();
        if (!BusinessExceptionErrorCodes.BUSINESS_ERROR.equals(errorCode)) {
            bex.setDetail(ApimsBaseException.DETAILS_KEY_ERROR_CODE, errorCode);
        }
        return bex;
    }

    private ApimsBusinessExceptionCacheItem createApimsBaseExceptionCacheItem(
            String code, Class<? extends Exception> clazz, Constructor<?> constructor) {
        return new ApimsBusinessExceptionCacheItem(code, clazz, constructor);
    }

    private Exception instantiateException(ApimsBusinessExceptionCacheItem item) {
        return item.instantiateException();
    }

    @SuppressWarnings({"unchecked", "java:S1168", "java:S3776"})
    private Map<String, Object> getBodyAsMap(String body) {

        Map<String, Object> map = null;

        if (body.startsWith("[")) {
            try {
                Map<String, Object>[] maps = objectMapper.readValue(body, Map[].class);
                if (maps.length != 0) {
                    map = maps[0];
                }
            } catch (JacksonException e) {
                log.trace("Business Exception Class not parsable (Array Object).", e);
                map = new HashMap<>();
            }
        }
        if (map == null) {
            try {
                map = objectMapper.readValue(body, Map.class);
            } catch (JacksonException e) {
                log.trace("Business Exception Class not parsable.", e);
                map = new HashMap<>();
            }
        }
        return map;
    }

    private Exception resolveApimsBaseException(String body) {

        if (!body.contains("{") || !body.contains("\"" + BUSINESS_EXCEPTION_ERROR_CODE_KEY + "\"")) {
            return null;
        }

        Map<String, Object> map = getBodyAsMap(body);
        String code = (String) map.get(BUSINESS_EXCEPTION_ERROR_CODE_KEY);
        if (!StringUtils.hasLength(code)) {
            log.trace(
                    "Business Exception Class not parsable. '{}' must not be null or empty.",
                    BUSINESS_EXCEPTION_ERROR_CODE_KEY);
            return null;
        }
        Map<String, Serializable> details = parseDetails(map);
        Exception bex = resolveException(code);
        if (bex instanceof ApimsDetailsAwareException dae
                && dae.getDetails() != null
        ) {
                dae.getDetails().putAll(details);
            }

        return bex;
    }

    @SuppressWarnings("unchecked")
    @ApimsReportGeneratedHint
    private Map<String, Serializable> parseDetails(Map<String, Object> bodyMap) {
        if (bodyMap != null && bodyMap.containsKey(BUSINESS_EXCEPTION_DETAILS_KEY)) {
            try {
                return (Map<String, Serializable>) bodyMap.get(BUSINESS_EXCEPTION_DETAILS_KEY);
            } catch (Exception _) {
                // ignore
            }
        }
        return Collections.emptyMap();
    }

    private void registerExceptions() {
        scanExceptions();
        registerDefaultExceptions();
    }

    @SuppressWarnings("unchecked")
    @ApimsReportGeneratedHint
    private void scanExceptions() {
        log.info("Scan ApimsBusinessExceptions...");
        final String coreBasePackage = ApimsSpringApplication.class.getPackageName();
        final String servicePackage = ApimsSpringContext.getSpringApplication() == null
                ? "de.sky.newcrm"
                : ApimsSpringContext.getSpringApplication()
                        .getMainApplicationClass()
                        .getPackageName();
        List<Class<?>> clazzes = ApimsClassScanner.findClasses(ApimsBusinessException.class, "de.sky.newcrm");
        for (Class<?> clazz : clazzes) {
            try {
                Class<? extends Exception> exceptionClazz = (Class<? extends Exception>) clazz;
                final boolean coreClass = exceptionClazz.getPackageName().startsWith(coreBasePackage);
                final boolean serviceClass = exceptionClazz.getPackageName().startsWith(servicePackage);

                if (coreClass || serviceClass) {
                    log.debug(
                            "Register ApimsBusinessException ({} exception): {}",
                            serviceClass ? "service" : "core",
                            exceptionClazz.getName());
                    registerException(exceptionClazz);
                } else {
                    log.trace(
                            "Skip ApimsBusinessException {}, not in package {} or {}",
                            exceptionClazz.getName(),
                            coreBasePackage,
                            servicePackage);
                }
            } catch (ClassCastException e) {
                throw new ApimsBaseExceptionResolverException(
                        "Class '" + clazz
                                + "' with annoatation @ApimsBusinessException not loaded. Class must be of type CLass<? extends Exception>");
            }
        }
        log.info("Scan ApimsBusinessExceptions complete: {} execptions registered.", exceptionMap.size());
    }

    private void registerDefaultExceptions() {
        // force some known class
        registerException(InvalidRequestDataBusinessException.class);
        registerException(BusinessException.class);
    }

    @ApimsReportGeneratedHint
    private void registerException(Class<? extends Exception> clazz) {
        ApimsBusinessExceptionCacheItem item;
        Constructor<?> defaultConstructor;
        Exception exceptionClazz;
        try {
            defaultConstructor = clazz.getDeclaredConstructor();
            ReflectionUtils.makeAccessible(defaultConstructor);
            exceptionClazz = (Exception) defaultConstructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new ApimsBaseExceptionResolverException("Default constructor missing", e);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | ClassCastException e) {
            throw new ApimsBaseExceptionResolverException("Instantiation failed", e);
        }
        String code = BusinessExceptionErrorCodes.calculateErrorCode(clazz, true);
        item = createApimsBaseExceptionCacheItem(code, exceptionClazz.getClass(), defaultConstructor);
        exceptionMap.put(code, item);
    }

    private static class ApimsBusinessExceptionCacheItem {

        String errorCode;
        Class<? extends Exception> clazz;
        Constructor<?> clazzConstructor;

        public ApimsBusinessExceptionCacheItem(
                String errorCode, Class<? extends Exception> clazz, Constructor<?> clazzConstructor) {
            this.errorCode = errorCode;
            this.clazz = clazz;
            this.clazzConstructor = clazzConstructor;
        }

        @SuppressWarnings("java:S112")
        @ApimsReportGeneratedHint
        private Exception instantiateException() {
            Exception bex;
            if (InvalidRequestDataBusinessException.class.equals(clazz)) {
                bex = new InvalidRequestDataBusinessException();
            } else if (BusinessException.class.equals(clazz)) {
                bex = BusinessException.build();
                if (!BusinessExceptionErrorCodes.BUSINESS_ERROR.equals(errorCode)) {
                    ((BusinessException) bex).setDetail(ApimsBaseException.DETAILS_KEY_ERROR_CODE, errorCode);
                }
            } else {
                try {
                    bex = (Exception) clazzConstructor.newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
            return bex;
        }
    }

    public static class ApimsBaseExceptionResolverException extends RuntimeException {

        public ApimsBaseExceptionResolverException() {}

        public ApimsBaseExceptionResolverException(String message) {
            super(message);
        }

        public ApimsBaseExceptionResolverException(String message, Throwable cause) {
            super(message, cause);
        }

        public ApimsBaseExceptionResolverException(Throwable cause) {
            super(cause);
        }

        public ApimsBaseExceptionResolverException(
                String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
