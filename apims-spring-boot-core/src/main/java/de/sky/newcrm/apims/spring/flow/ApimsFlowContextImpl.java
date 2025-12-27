/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.flow;

import de.sky.newcrm.apims.spring.utils.ObjectUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;

@SuppressWarnings({"java:S6212"})
public class ApimsFlowContextImpl implements ApimsFlowContext {

    private final Deque<ApimsFlowMethodReference> flowMethodReferenceStack = new ArrayDeque<>();
    private static final Map<String, Optional<Annotation>> annotationCacheMap = new ConcurrentHashMap<>();

    ApimsFlowContextImpl() {}

    @Override
    public ApimsFlowMethodReference getInboundMethodReference() {
        return flowMethodReferenceStack.isEmpty() ? null : flowMethodReferenceStack.getLast();
    }

    @Override
    public ApimsFlowMethodReference getCurrentMethodReference() {
        return flowMethodReferenceStack.isEmpty() ? null : flowMethodReferenceStack.getFirst();
    }

    @Override
    public <A extends Annotation> A findCurrentMethodOrClassAnnotation(Class<A> annotationType, boolean topClassOnly) {
        A annotation = findCurrentMethodAnnotation(annotationType);
        return annotation == null ? findCurrentClassAnnotation(annotationType, topClassOnly) : annotation;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends Annotation> A findCurrentMethodAnnotation(@Nullable Class<A> annotationType) {
        if (annotationType != null) {
            Method method = getCurrentMethod();
            if (method != null) {
                String key = method + ":" + annotationType.getName();
                return (A) annotationCacheMap
                        .computeIfAbsent(
                                key, s -> Optional.ofNullable(AnnotationUtils.findAnnotation(method, annotationType)))
                        .orElse(null);
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings({"unchecked", "java:S3776"})
    public <A extends Annotation> A findCurrentMethodParamAnnotation(
            @Nullable Class<A> annotationType, int paramIndex) {
        if (annotationType != null) {
            Method method = getCurrentMethod();
            if (method != null) {
                String key = method + "[" + paramIndex + "]:" + annotationType.getName();
                return (A) annotationCacheMap
                        .computeIfAbsent(key, s -> {
                            Annotation annotation = findMethodParamAnnotation(method, annotationType, paramIndex);
                            if (annotation == null) {
                                for (Class<?> implementedInterface :
                                        method.getDeclaringClass().getInterfaces()) {
                                    Method nextMethod = ObjectUtils.findMethod(
                                            implementedInterface, method.getName(), false, method.getParameterTypes());
                                    if (nextMethod != null) {
                                        annotation = findMethodParamAnnotation(nextMethod, annotationType, paramIndex);
                                        if (annotation != null) {
                                            break;
                                        }
                                    }
                                }
                            }
                            return Optional.ofNullable(annotation);
                        })
                        .orElse(null);
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends Annotation> A findCurrentClassAnnotation(Class<A> annotationType, boolean topClassOnly) {
        if (annotationType != null) {
            Method method = getCurrentMethod();
            if (method != null) {
                String key = method.getDeclaringClass() + ":" + annotationType.getName() + ":" + topClassOnly;
                return (A) annotationCacheMap
                        .computeIfAbsent(
                                key,
                                s -> Optional.ofNullable(ObjectUtils.findClassAnnotation(
                                        method.getDeclaringClass(), annotationType, topClassOnly)))
                        .orElse(null);
            }
        }
        return null;
    }

    void pushFlowMethodReference(ApimsFlowMethodReference reference) {
        flowMethodReferenceStack.push(reference);
    }

    void popFlowMethodReference() {
        if (!flowMethodReferenceStack.isEmpty()) {
            flowMethodReferenceStack.pop();
        }
    }

    Method getCurrentMethod() {
        ApimsFlowMethodReference methodReference = getCurrentMethodReference();
        return methodReference == null ? null : methodReference.getMethod();
    }

    @SuppressWarnings("unchecked")
    <A extends Annotation> A findMethodParamAnnotation(Method method, Class<A> annotationType, int paramIndex) {
        Annotation[][] allParameterAnnotations = method.getParameterAnnotations();
        Annotation[] parameterAnnotations = allParameterAnnotations[paramIndex];
        A annotation = null;
        for (Annotation parameterAnnotation : parameterAnnotations) {
            if (annotationType.equals(parameterAnnotation.annotationType())) {
                annotation = (A) parameterAnnotation;
                break;
            }
        }
        return annotation;
    }
}
