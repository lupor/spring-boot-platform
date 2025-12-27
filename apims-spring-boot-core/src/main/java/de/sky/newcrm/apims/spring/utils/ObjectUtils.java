/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import de.sky.newcrm.apims.spring.exceptions.ApimsRuntimeException;
import de.sky.newcrm.apims.spring.serialization.core.mapper.ObjectMapperUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import lombok.Builder;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.NullValueInNestedPathException;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

@SuppressWarnings({"java:S135", "java:S1192"})
public class ObjectUtils {

    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    private ObjectUtils() {}

    public static boolean isEquals(Object value, Object otherValue) {
        return isEquals(value, otherValue, false);
    }

    public static boolean isEquals(Object value, Object otherValue, boolean deepCheck) {
        if (value == null && otherValue == null) {
            return true;
        } else if (value == null || otherValue == null) {
            return false;
        } else {
            boolean flag = value.equals(otherValue);
            if (flag || !deepCheck) {
                return flag;
            }
            return ObjectMapperUtils.writeValueAsString(value).equals(ObjectMapperUtils.writeValueAsString(otherValue));
        }
    }

    public static <T> T getIfCondition(boolean condition, T value, T defaultValue) {
        return condition ? value : defaultValue;
    }

    public static <T> T getIfNotNull(T value, T defaultValue) {
        return getIfCondition(value != null, value, defaultValue);
    }

    public static String getIfHasText(String value, String defaultValue) {
        return getIfCondition(StringUtils.hasText(value), value, defaultValue);
    }

    public static String getIfHasLength(String value, String defaultValue) {
        return getIfCondition(StringUtils.hasLength(value), value, defaultValue);
    }

    public static <T> T getOrElse(Optional<T> optional, T defaultValue) {
        return optional.orElse(defaultValue);
    }

    @SuppressWarnings("java:S1452")
    public static Collection<?> asCollection(final Object source) {
        if (source instanceof Collection<?> collection) {
            return collection;
        }
        return source.getClass().isArray() ? CollectionUtils.arrayToList(source) : Collections.singleton(source);
    }

    public static boolean hasField(Class<?> clazz, String name, Class<?> type) {
        return ReflectionUtils.findField(clazz, name, type) != null;
    }

    public static Field findField(Class<?> clazz, String name) {
        return findField(clazz, name, null, true);
    }

    public static Field findField(Class<?> clazz, String name, boolean makeAccessible) {
        return findField(clazz, name, null, makeAccessible);
    }

    public static Field findField(Class<?> clazz, String name, Class<?> type, boolean makeAccessible) {
        Field field = ReflectionUtils.findField(clazz, name, type);
        if (field == null) {
            return null;
        }
        if (makeAccessible) {
            ReflectionUtils.makeAccessible(field);
        }
        return field;
    }

    public static <T> T getPropertyValue(Object rootObject, String path) {
        return getPropertyValue(rootObject, path, false);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getPropertyValue(Object rootObject, String path, boolean handleNotReadablePropertyAsNull) {
        if (rootObject == null) {
            return null;
        }
        BeanWrapper wrapper = new BeanWrapperImpl(rootObject);
        try {
            return (T) wrapper.getPropertyValue(path);
        } catch (NullValueInNestedPathException _) {
            return null;
        } catch (NotReadablePropertyException e) {
            if (handleNotReadablePropertyAsNull) {
                return null;
            }
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object rootObject, String path) {
        if (rootObject == null) {
            return null;
        }
        String[] names = StringUtils.tokenizeToStringArray(path, ".", true, true);
        Object object = rootObject;
        Class<?> objectClass = rootObject.getClass();
        Field field;
        for (String name : names) {
            field = ReflectionUtils.findField(objectClass, name);
            if (field == null) {
                object = null;
                break;
            }
            object = getField(field, object, true);
            if (object == null) {
                break;
            }
            objectClass = object.getClass();
        }
        return (T) object;
    }

    public static <A extends Annotation> A findClassAnnotation(Class<?> clazz, Class<A> annotationClazz) {
        return findClassAnnotation(clazz, annotationClazz, false);
    }

    public static <A extends Annotation> A findClassAnnotation(
            Class<?> clazz, Class<A> annotationClazz, boolean topClassOnly) {
        if (topClassOnly) {
            return clazz.getDeclaredAnnotation(annotationClazz);
        }
        return AnnotatedElementUtils.findMergedAnnotation(clazz, annotationClazz);
    }

    public static Field findAnnotatedField(Class<?> clazz, Class<? extends Annotation> annotationType) {
        return findAnnotatedField(clazz, annotationType, true);
    }

    public static Field findAnnotatedField(
            Class<?> clazz, Class<? extends Annotation> annotationType, boolean makeAccessible) {
        for (Field field : clazz.getDeclaredFields()) {
            Annotation annotation = AnnotatedElementUtils.findMergedAnnotation(field, annotationType);
            if (annotation != null) {
                if (makeAccessible) {
                    ReflectionUtils.makeAccessible(field);
                }
                return field;
            }
        }
        return null;
    }

    public static List<Field> findAnnotatedFields(Class<?> clazz, Class<? extends Annotation> annotationType) {
        return findAnnotatedFields(clazz, annotationType, true);
    }

    public static List<Field> findAnnotatedFields(
            Class<?> clazz, Class<? extends Annotation> annotationType, boolean makeAccessible) {
        List<Field> list = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            Annotation annotation = AnnotatedElementUtils.findMergedAnnotation(field, annotationType);
            if (annotation != null) {
                if (makeAccessible) {
                    ReflectionUtils.makeAccessible(field);
                }
                list.add(field);
            }
        }
        return list;
    }

    public static List<Method> findAnnotatedMethods(Class<?> clazz, Class<? extends Annotation> annotationType) {
        return findAnnotatedMethods(clazz, annotationType, true);
    }

    public static List<Method> findAnnotatedMethods(
            Class<?> clazz, Class<? extends Annotation> annotationType, boolean makeAccessible) {
        List<Method> list = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            Annotation annotation = AnnotatedElementUtils.findMergedAnnotation(method, annotationType);
            if (annotation != null) {
                if (makeAccessible) {
                    ReflectionUtils.makeAccessible(method);
                }
                list.add(method);
            }
        }
        return list;
    }

    public static <T> T getField(Field field, Object target) {
        return getField(field, target, false);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getField(Field field, Object target, boolean makeAccessible) {
        AssertUtils.notNullCheck("field", field);
        AssertUtils.notNullCheck("target", target);
        if (makeAccessible) {
            ReflectionUtils.makeAccessible(field);
        }
        return (T) ReflectionUtils.getField(field, target);
    }

    public static <T> T getField(String name, Object target) {
        AssertUtils.notNullCheck("target", target);
        AssertUtils.hasLengthCheck("name", name);
        return getField(target.getClass(), name, null, target);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getField(Field field, Class<T> fieldType, Object target) {
        AssertUtils.notNullCheck("field", field);
        AssertUtils.notNullCheck("fieldType", fieldType);
        AssertUtils.notNullCheck("target", target);
        return (T) ReflectionUtils.getField(field, target);
    }

    public static <T> T getField(String name, Class<T> fieldType, Object target) {
        AssertUtils.notNullCheck("target", target);
        AssertUtils.hasLengthCheck("name", name);
        return getField(target.getClass(), name, fieldType, target);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getField(Class<?> clazz, String name, Class<T> type, Object target) {
        Field field = ReflectionUtils.findField(clazz, name, type);
        if (field == null) {
            return null;
        }
        ReflectionUtils.makeAccessible(field);
        return (T) ReflectionUtils.getField(field, target);
    }

    public static void setField(Object target, String name, Object value) {
        AssertUtils.notNullCheck("target", target);
        AssertUtils.hasLengthCheck("name", name);
        Field field = ReflectionUtils.findField(target.getClass(), name);
        if (field != null) {
            ReflectionUtils.makeAccessible(field);
            ReflectionUtils.setField(field, target, value);
        }
    }

    public static void setField(Field field, Object target, Object value) {
        AssertUtils.notNullCheck("target", target);
        AssertUtils.notNullCheck("field", field);
        ReflectionUtils.setField(field, target, value);
    }

    public static Method findMethod(Class<?> clazz, String name) {
        return findMethod(clazz, name, true, (Class<?>[]) null);
    }

    public static Method findMethod(Class<?> clazz, String name, @Nullable Class<?>... paramTypes) {
        return findMethod(clazz, name, true, paramTypes);
    }

    public static Method findMethod(
            Class<?> clazz, String name, boolean makeAccessible, @Nullable Class<?>... paramTypes) {
        Method method = ReflectionUtils.findMethod(clazz, name, paramTypes);
        if (method == null) {
            return null;
        }
        if (makeAccessible) {
            ReflectionUtils.makeAccessible(method);
        }
        return method;
    }

    public static <T> T invokeMethod(Method method, @Nullable Object target) {
        return invokeMethodAndMakeAccessible(method, target, false, EMPTY_OBJECT_ARRAY);
    }

    public static <T> T invokeMethodAndMakeAccessible(Method method, @Nullable Object target, boolean makeAccessible) {
        return invokeMethodAndMakeAccessible(method, target, makeAccessible, EMPTY_OBJECT_ARRAY);
    }

    public static <T> T invokeMethod(Method method, @Nullable Object target, @Nullable Object... args) {
        return invokeMethodAndMakeAccessible(method, target, false, args);
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeMethodAndMakeAccessible(
            Method method, @Nullable Object target, boolean makeAccessible, @Nullable Object... args) {
        if (makeAccessible) {
            ReflectionUtils.makeAccessible(method);
        }
        return (T) ReflectionUtils.invokeMethod(method, target, args);
    }

    public static Method[] getDeclaredMethods(Class<?> clazz) {
        return ReflectionUtils.getDeclaredMethods(clazz);
    }

    public static Class<?> getClass(String className) {
        return getClass(className, false);
    }

    public static Class<?> getClass(String className, boolean silent) {
        if (silent && !StringUtils.hasLength(className)) {
            return null;
        }
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            if (silent) {
                return null;
            }
            throw new ApimsRuntimeException(e);
        }
    }

    public static <T> T createInstance(String className) {
        return createInstance(
                CreateInstanceDefinition.builder().className(className).build());
    }

    public static <T> T createInstance(Class<?> clazz) {
        return createInstance(CreateInstanceDefinition.builder().clazz(clazz).build());
    }

    @SuppressWarnings("java:S3516")
    public static <T> T createInstanceByDefinitions(CreateInstanceDefinition... definitions) {
        RuntimeException lastException = null;
        for (CreateInstanceDefinition definition : definitions) {
            try {
                return createInstance(definition);
            } catch (RuntimeException e) {
                lastException = e;
            }
        }
        if (lastException != null) {
            throw lastException;
        }
        return null;
    }

    @SuppressWarnings({"unchecked", "java:S4449"})
    public static <T> T createInstance(CreateInstanceDefinition definition) {

        try {
            Class<?> clazz = definition.getClazz();
            if (clazz == null) {
                AssertUtils.hasLengthCheck("definition.className", definition.getClassName());
                clazz = Class.forName(definition.getClassName());
            }
            Class<?>[] constructorTypes = definition.getConstructorTypes();
            if (constructorTypes == null) {
                constructorTypes = new Class<?>[0];
            }
            Object[] constructorArgs = definition.getConstructorArgs();
            if (constructorArgs == null) {
                constructorArgs = new Object[0];
            }
            if (constructorArgs.length < constructorTypes.length) {
                List<Object> args = new ArrayList<>(Arrays.asList(constructorArgs));
                while (args.size() < constructorTypes.length) {
                    args.add(null);
                }
                constructorArgs = args.toArray(new Object[0]);
            }
            Constructor<?> constructor = clazz.getDeclaredConstructor(constructorTypes);
            ReflectionUtils.makeAccessible(constructor);
            T object = (T) constructor.newInstance(constructorArgs);
            Map<String, Object> fieldData = definition.getFieldData();
            if (fieldData != null && !fieldData.isEmpty()) {
                for (Map.Entry<String, Object> entry : fieldData.entrySet()) {
                    Field field = ReflectionUtils.findField(clazz, entry.getKey());
                    AssertUtils.notNullCheck("field " + clazz.getName() + "." + entry.getKey(), field);
                    if (field != null) {
                        ReflectionUtils.makeAccessible(field);
                        ReflectionUtils.setField(field, object, entry.getValue());
                    }
                }
            }
            return object;
        } catch (Exception e) {
            throw new ApimsRuntimeException(e);
        }
    }

    @Builder
    @Getter
    public static class CreateInstanceDefinition {
        private String className;
        private Class<?> clazz;
        private Class<?>[] constructorTypes;
        private Object[] constructorArgs;
        private Map<String, Object> fieldData;
    }
}
