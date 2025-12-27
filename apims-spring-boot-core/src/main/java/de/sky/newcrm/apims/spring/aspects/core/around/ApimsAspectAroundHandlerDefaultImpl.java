/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.aspects.core.around;

import de.sky.newcrm.apims.spring.context.core.ApimsMdc;
import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import de.sky.newcrm.apims.spring.exceptions.ApimsBusinessException;
import de.sky.newcrm.apims.spring.exceptions.ApimsUndeclaredThrowableException;
import de.sky.newcrm.apims.spring.exceptions.InvalidRequestDataBusinessException;
import de.sky.newcrm.apims.spring.exceptions.NoRetryableException;
import de.sky.newcrm.apims.spring.flow.ApimsFlowContext;
import de.sky.newcrm.apims.spring.utils.ExceptionUtils;
import de.sky.newcrm.apims.spring.utils.ObjectUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.OrderComparator;
import org.springframework.util.Assert;

@Slf4j
@SuppressWarnings({"java:S6212", "java:S6813"})
public class ApimsAspectAroundHandlerDefaultImpl implements ApimsAspectAroundHandler {

    private static final int TYPE_NAME_MAX_LEN = 10;
    private static final String THIS_PACKAGE_PREFIX = "de.sky.";

    @Value("${apims.aspects.listeners.logging.save-log-lines-span-tag-max-length}")
    private int saveLogLinesSpanTagMaxLength;

    private final boolean createNewSpan;
    private final List<ApimsAroundListener> listenersBeforeList = new ArrayList<>();
    private final List<ApimsAroundListener> listenersAfterList = new ArrayList<>();
    private final List<ApimsAroundInterceptor> interceptorList = new ArrayList<>();
    private final ThreadLocal<Integer> activeCalls = new ThreadLocal<>();

    @Autowired(required = false)
    private ApimsMdc mdc;

    public ApimsAspectAroundHandlerDefaultImpl(
            boolean createNewSpan, List<ApimsAroundListener> listeners, List<ApimsAroundInterceptor> interceptors) {
        this.createNewSpan = createNewSpan;
        if (listeners != null) {
            listenersBeforeList.addAll(listeners);
            listenersBeforeList.sort(new OrderComparator());
            for (ApimsAroundListener listener : listenersBeforeList) {
                listenersAfterList.add(0, listener);
            }
        }
        if (interceptors != null) {
            interceptorList.addAll(interceptors);
            interceptorList.sort(new OrderComparator());
        }
    }

    @Override
    @SuppressWarnings({"java:S1193", "java:S3776", "java:S6541"})
    @ApimsReportGeneratedHint
    public Object aroundMethod(ApimsAspectType type, ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Assert.notNull(type, "[Assertion failed] - 'type' is required; it must not be null");
        Assert.notNull(
                proceedingJoinPoint, "[Assertion failed] - 'proceedingJoinPoint' is required; it must not be null");
        if (mdc != null && getActiveCallsCount() == 0) {
            mdc.removeAllApimsValues();
            mdc.putGlobalFields();
        }
        final Class<?> declaringType = getDeclaringType(proceedingJoinPoint);
        if (Object.class.equals(declaringType)
                || declaringType.getPackage().getName().contains("autoconfigure")) {
            return proceedingJoinPoint.proceed();
        }
        final String signature = proceedingJoinPoint.getSignature().toString();
        String shortSignature = proceedingJoinPoint.getSignature().toShortString();
        if (!shortSignature.startsWith(declaringType.getSimpleName())) {
            shortSignature = declaringType.getSimpleName() + shortSignature.substring(shortSignature.indexOf("."));
        }
        final boolean voidMethodFlag = signature.startsWith("void ");
        final String returnType = voidMethodFlag ? "void" : signature.substring(0, signature.indexOf(" "));
        final String typeValue = calculateFixedTypeValue(type);
        final String loglineIntro = "[" + typeValue + "] : " + returnType + " " + shortSignature;
        ApimsAroundContext context = ApimsAroundContext.builder()
                .proceedingJoinPoint(proceedingJoinPoint)
                .declaringType(declaringType)
                .type(type)
                .signature(signature)
                .shortSignature(shortSignature)
                .voidMethod(voidMethodFlag)
                .returnType(returnType)
                .loglineIntro(loglineIntro)
                .data(new HashMap<>())
                .activeCallsCount(incrementActiveCallsCount())
                // .tracingContext(tracingContext)
                .spanTagMaxLength(saveLogLinesSpanTagMaxLength)
                .createNewSpan(createNewSpan)
                .logger(LoggerFactory.getLogger(declaringType))
                .build();

        List<ApimsAroundListener> calledListeners = new ArrayList<>();
        ApimsAroundFilterDelegateException listenerException = null;
        for (ApimsAroundListener listener : listenersBeforeList) {
            calledListeners.add(listener);
            try {
                listener.beforeAroundMethod(context);
            } catch (Exception e) {
                if (e instanceof ApimsAroundFilterDelegateException delegateException) {
                    listenerException = delegateException;
                    break;
                } else {
                    log.warn("[APIMS ASPECT] aroundMethod failed.", e);
                }
            }
        }
        Object result = null;
        Exception resultError = listenerException == null ? null : listenerException.getCause();
        if (resultError == null) {
            try {
                ApimsAroundInterceptorResult interceptorResult = null;
                for (ApimsAroundInterceptor interceptor : interceptorList) {
                    interceptorResult = interceptor.intercept(context);
                    if (interceptorResult != null) {
                        break;
                    }
                }
                if (interceptorResult != null) {
                    result = interceptorResult.getResult();
                } else {
                    result = proceedingJoinPoint.proceed();
                }
            } catch (Exception e) {
                Exception resolvedException = resolveProceedingJoinPointException(e);
                Exception reportError = ExceptionUtils.resolveUndeclaredThrowableException(resolvedException);
                if (e instanceof ApimsUndeclaredThrowableException) {
                    resultError = e;
                } else if (e instanceof UndeclaredThrowableException) {
                    resultError = new ApimsUndeclaredThrowableException(reportError);
                } else {
                    resultError = resolvedException;
                }
                ApimsBusinessException annotation = reportError.getClass().getAnnotation(ApimsBusinessException.class);
                boolean reportAsError = annotation == null || annotation.logAsError();
                if (reportAsError) {
                    // context.setSpanError(reportError);
                    if (context.getActiveCallsCount() == 1
                            && !(resultError instanceof NoRetryableException)
                            && !(resultError.getCause() instanceof InvalidRequestDataBusinessException)
                            && !ApimsAspectType.RESTCONTROLLER.equals(
                                    type)) { // controller case: see ApimsErrorAttributes
                        // log stacktrace
                        context.getLogger().error(reportError.getMessage(), reportError);
                    }
                }
            }
        }
        resultError = onAfterAroundMethod(context, result, resultError, calledListeners);
        decrementActiveCallsCount();
        if (resultError != null) {
            throw resultError;
        }
        return result;
    }

    protected Exception onAfterAroundMethod(
            ApimsAroundContext context,
            Object result,
            Exception resultError,
            List<ApimsAroundListener> calledListeners) {
        for (ApimsAroundListener listener : listenersAfterList) {
            if (calledListeners.contains(listener)) {
                try {
                    listener.afterAroundMethod(context, result, resultError);
                } catch (Exception e) {
                    if (resultError == null && e instanceof ApimsAroundFilterDelegateException delegateException) {
                        resultError = delegateException.getCause();
                    } else {
                        log.warn("[APIMS ASPECT] aroundMethod failed.", e);
                    }
                }
            }
        }
        return resultError;
    }

    protected int getActiveCallsCount() {
        Integer value = activeCalls.get();
        if (value == null) {
            value = 0;
        }
        return value;
    }

    protected int incrementActiveCallsCount() {
        Integer value = activeCalls.get();
        if (value == null) {
            value = 0;
        }
        value++;
        activeCalls.set(value);
        return value;
    }

    protected void decrementActiveCallsCount() {
        Integer value = activeCalls.get();
        if (value == null) {
            return;
        }
        value--;
        if (value < 1) {
            activeCalls.remove();
        } else {
            activeCalls.set(value);
        }
    }

    protected String calculateFixedTypeValue(ApimsAspectType type) {
        String typeValue = type == null ? ApimsAspectType.COMPONENT.internalValue() : type.internalValue();
        if (typeValue.length() > TYPE_NAME_MAX_LEN) {
            typeValue = typeValue.substring(typeValue.length() - TYPE_NAME_MAX_LEN);
        }
        return new String(new char[TYPE_NAME_MAX_LEN - typeValue.length()]).replace('\0', '_') + typeValue;
    }

    @ApimsReportGeneratedHint
    protected Class<?> getDeclaringType(ProceedingJoinPoint proceedingJoinPoint) {
        if (proceedingJoinPoint.getTarget() != null) {
            Class<?> declaringType = proceedingJoinPoint.getTarget().getClass();
            if (declaringType.getPackage().getName().startsWith(THIS_PACKAGE_PREFIX)) {
                return declaringType;
            }
        }
        Class<?> declaringType = proceedingJoinPoint.getSignature().getDeclaringType();
        if (Object.class.equals(declaringType)
                || declaringType.getPackage().getName().startsWith(THIS_PACKAGE_PREFIX)) {
            return declaringType;
        }
        for (Class<?> item : proceedingJoinPoint.getThis().getClass().getInterfaces()) {
            if (item.getName().startsWith(THIS_PACKAGE_PREFIX)) {
                return item;
            }
        }
        return declaringType;
    }

    protected Exception resolveProceedingJoinPointException(Exception e) {
        Exception detailException = ExceptionUtils.resolveUndeclaredThrowableException(e);
        if (detailException == null) {
            return null;
        }
        Exception resultError = detailException;
        ApimsAroundExceptionMappings apimsAroundExceptionMappings =
                findCurrentMethodAnnotation(ApimsAroundExceptionMappings.class);
        if (apimsAroundExceptionMappings != null) {
            Optional<ApimsAroundExceptionMapping> mapping = Arrays.stream(apimsAroundExceptionMappings.value())
                    .filter(m -> Arrays.stream(m.on()).anyMatch(c -> c.isAssignableFrom(detailException.getClass())))
                    .findFirst();
            if (mapping.isPresent()) {
                Class<?> clazz = mapping.get().raise();
                resultError = ObjectUtils.createInstanceByDefinitions(
                        ObjectUtils.CreateInstanceDefinition.builder()
                                .clazz(clazz)
                                .constructorTypes(new Class<?>[] {String.class, Throwable.class})
                                .constructorArgs(new Object[] {detailException.getMessage(), detailException})
                                .build(),
                        ObjectUtils.CreateInstanceDefinition.builder()
                                .clazz(clazz)
                                .constructorTypes(new Class<?>[] {Throwable.class})
                                .constructorArgs(new Object[] {detailException})
                                .build(),
                        ObjectUtils.CreateInstanceDefinition.builder()
                                .clazz(clazz)
                                .constructorTypes(new Class<?>[] {String.class})
                                .constructorArgs(new Object[] {detailException.getMessage()})
                                .build(),
                        ObjectUtils.CreateInstanceDefinition.builder()
                                .clazz(clazz)
                                .build());
            }
        }
        return resultError;
    }

    protected <A extends Annotation> A findCurrentMethodAnnotation(Class<A> annotationType) {
        return ApimsFlowContext.get().findCurrentMethodAnnotation(annotationType);
    }
}
