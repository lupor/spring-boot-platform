/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

public class AnnotationUtils {

    protected AnnotationUtils() {}

    public static <A extends Annotation> A createAnnotation(Class<A> annotationType) {
        return createAnnotation(annotationType, null);
    }

    @SuppressWarnings("unchecked")
    public static <A extends Annotation> A createAnnotation(Class<A> annotationType, Map<String, Object> values) {
        return (A) Proxy.newProxyInstance(
                annotationType.getClassLoader(),
                new Class[] {annotationType},
                new AnnotationInvocationHandler(annotationType, values));
    }

    static class AnnotationInvocationHandler implements Annotation, InvocationHandler {

        private static final Map<Class<?>, Class<?>> primitiveWrapperMap = new HashMap<>();

        static {
            primitiveWrapperMap.put(Boolean.TYPE, Boolean.class);
            primitiveWrapperMap.put(Byte.TYPE, Byte.class);
            primitiveWrapperMap.put(Character.TYPE, Character.class);
            primitiveWrapperMap.put(Short.TYPE, Short.class);
            primitiveWrapperMap.put(Integer.TYPE, Integer.class);
            primitiveWrapperMap.put(Long.TYPE, Long.class);
            primitiveWrapperMap.put(Double.TYPE, Double.class);
            primitiveWrapperMap.put(Float.TYPE, Float.class);
        }

        private final Class<? extends Annotation> annotationType;
        private final Map<String, Object> values;

        private AnnotationInvocationHandler(Class<? extends Annotation> annotationType, Map<String, Object> values)
                throws IllegalStateException {
            this.annotationType = annotationType;
            this.values = Collections.unmodifiableMap(getValidatedValues(annotationType, values));
        }

        @SuppressWarnings("java:S3776")
        private static Map<String, Object> getValidatedValues(
                Class<? extends Annotation> annotationType, Map<String, Object> values) throws IllegalStateException {
            if (values == null) {
                values = Collections.emptyMap();
            }
            Set<String> missing = new HashSet<>();
            Set<String> invalid = new HashSet<>();
            Map<String, Object> valid = new HashMap<>();
            for (Method element : annotationType.getDeclaredMethods()) {
                String elementName = element.getName();
                if (values.containsKey(elementName)) {
                    Class<?> returnType = element.getReturnType();
                    if (returnType.isPrimitive()) {
                        returnType = primitiveWrapperMap.get(returnType);
                    }

                    if (returnType.isInstance(values.get(elementName))) {
                        valid.put(elementName, values.get(elementName));
                    } else {
                        invalid.add(elementName);
                    }
                } else {
                    if (element.getDefaultValue() != null) {
                        valid.put(elementName, element.getDefaultValue());
                    } else {
                        missing.add(elementName);
                    }
                }
            }
            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing annotation value(s) for " + String.join(",", missing));
            } else if (!invalid.isEmpty()) {
                throw new IllegalStateException(
                        "Incompatible annotation type(s) provided for " + String.join(",", invalid));
            }
            return valid;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return annotationType;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return values.containsKey(method.getName()) ? values.get(method.getName()) : method.invoke(this, args);
        }
    }
}
