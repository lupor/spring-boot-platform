/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.couchbase.core;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import de.sky.newcrm.apims.spring.environment.core.ApimsSpringContext;
import de.sky.newcrm.apims.spring.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.data.couchbase.repository.Collection;
import org.springframework.data.couchbase.repository.Scope;
import org.springframework.data.repository.Repository;
import org.springframework.util.StringUtils;

import static de.sky.newcrm.apims.spring.couchbase.core.ApimsCouchbaseNativeSupport.*;

@Slf4j
@SuppressWarnings({"java:S3824"})
public class ApimsCouchbaseCollectionNameResolver implements BeanPostProcessor {

    private final Map<Class<?>, String> beanNameMap = new HashMap<>();
    private final Map<Class<?>, String> bucketValueMap = new HashMap<>();
    private final Map<Class<?>, String> scopeValueMap = new HashMap<>();
    private final Map<Class<?>, String> collectionValueMap = new HashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        registerBean(bean, beanName);
        return bean;
    }

    public void registerBean(Object bean, String beanName) {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        registerRepository(targetClass, beanName);
    }

    public String getBucketName(Class<?> repository) {
        return getMapValue(bucketValueMap, repository, DEFAULT_BUCKET_NAME);
    }

    public String getScopeName(Class<?> repository) {
        return getMapValue(scopeValueMap, repository, DEFAULT_SCOPE_NAME);
    }

    public String getCollectionName(Class<?> repository) {
        return getMapValue(collectionValueMap, repository, DEFAULT_COLLECTION_NAME);
    }

    public void registerRepository(Class<?> targetClass) {
        registerRepository(targetClass, null);
    }

    protected <A extends Annotation> A findAnnotation(Class<?> clazz, Class<A> annotationClazz) {
        return ObjectUtils.findClassAnnotation(clazz, annotationClazz);
    }

    @SuppressWarnings({"java:S1874"})
    protected void registerRepository(Class<?> targetClass, String beanName) {
        boolean repositoryClass = Repository.class.isAssignableFrom(targetClass)
                || ApimsCouchbaseNativeRepository.class.isAssignableFrom(targetClass);
        if (repositoryClass) {
            if (StringUtils.hasLength(beanName)) {
                registerBeanName(targetClass, beanName);
            }
            registerBucketName(targetClass, DEFAULT_BUCKET_NAME, false);
            Bucket bucket = findAnnotation(targetClass, Bucket.class);
            String bucketName = bucket == null ? DEFAULT_BUCKET_NAME : bucket.value();
            registerBucketName(targetClass, bucketName);
            Scope scope = findAnnotation(targetClass, Scope.class);
            String scopeName = scope == null ? DEFAULT_SCOPE_NAME : scope.value();
            registerScopeName(targetClass, scopeName);
            Collection collection = findAnnotation(targetClass, Collection.class);
            String collectionName = collection == null ? DEFAULT_COLLECTION_NAME : collection.value();
            registerCollectionName(targetClass, collectionName);
        }
    }

    protected void registerBeanName(Class<?> targetClass, String name) {
        boolean repositoryClass = Repository.class.isAssignableFrom(targetClass);
        Class<?> registerClass = repositoryClass ? targetClass.getInterfaces()[0] : targetClass;
        log.trace("register bean name: {} -> '{}'", registerClass, name);
        beanNameMap.put(registerClass, name);
    }

    protected void registerScopeName(Class<?> targetClass, String name) {
        boolean repositoryClass = Repository.class.isAssignableFrom(targetClass);
        Class<?> registerClass = repositoryClass ? targetClass.getInterfaces()[0] : targetClass;
        log.trace("register scope name: {} -> '{}'", registerClass, name);
        scopeValueMap.put(registerClass, name);
    }

    protected void registerBucketName(Class<?> targetClass, String name) {
        registerBucketName(targetClass, name, true);
    }

    protected void registerBucketName(Class<?> targetClass, String name, boolean override) {
        boolean repositoryClass = Repository.class.isAssignableFrom(targetClass);
        Class<?> registerClass = repositoryClass ? targetClass.getInterfaces()[0] : targetClass;
        if (override || !bucketValueMap.containsKey(registerClass)) {
            log.trace("register bucket name: {} -> '{}'", registerClass, name);
            bucketValueMap.put(registerClass, name);
        }
    }

    protected void registerCollectionName(Class<?> targetClass, String name) {
        boolean repositoryClass = Repository.class.isAssignableFrom(targetClass);
        Class<?> registerClass = repositoryClass ? targetClass.getInterfaces()[0] : targetClass;
        log.trace("register collection name: {} -> '{}'", registerClass, name);
        collectionValueMap.put(registerClass, name);
    }

    protected String resolveRequiredPlaceholders(String value, String defaultValue) {
        return ApimsSpringContext.resolveRequiredPlaceholders(value, defaultValue);
    }

    protected String getMapValue(Map<Class<?>, String> map, Class<?> key, String defaultValue) {
        if (key == null) {
            return defaultValue;
        } else if (map.containsKey(key)) {
            return resolveRequiredPlaceholders(map.get(key), defaultValue);
        } else if (Repository.class.isAssignableFrom(key) && key.getInterfaces().length != 0) {
            return resolveRequiredPlaceholders(map.get(key.getInterfaces()[0]), defaultValue);
        } else {
            return defaultValue;
        }
    }
}
