/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.environment.core;

import de.sky.newcrm.apims.spring.utils.AssertUtils;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class ApimsMockedApplicationContext implements ApimsApplicationContext {

    private final Map<String, BeanProxy> beanMap = new HashMap<>();

    ApimsMockedApplicationContext() {
        ApimsSpringContext.getApplicationContext().setFallbackApplicationContext(this);
    }

    public void unregisterAllBeans() {
        beanMap.clear();
    }

    public void registerBean(String name, Class<?> beanType, Object bean) {
        AssertUtils.hasLengthCheck("name", name);
        AssertUtils.notNullCheck("beanType", beanType);
        AssertUtils.notNullCheck("bean", bean);
        beanMap.put(name, BeanProxy.builder().beanType(beanType).bean(bean).build());
    }

    public void unRegisterBean(String name) {
        AssertUtils.hasLengthCheck("name", name);
        beanMap.remove(name);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        Map<String, T> beans = getBeansOfType(requiredType);
        if (beans.size() > 1) {
            throw new NoUniqueBeanDefinitionException(requiredType, beans.keySet());
        }
        Optional<T> value = beans.values().stream().findFirst();
        if (value.isEmpty()) {
            throw new NoSuchBeanDefinitionException(requiredType);
        }
        return value.get();
    }

    @Override
    public Object getBean(String name) {
        BeanProxy beanProxy = beanMap.get(name);
        if (beanProxy == null) {
            throw new NoSuchBeanDefinitionException(name);
        }
        return beanProxy.getBean();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> getBeansOfType(Class<T> type) {
        Map<String, T> map = new HashMap<>();
        Map<String, BeanProxy> beans = getBeans(type);
        for (Map.Entry<String, BeanProxy> entry : beans.entrySet()) {
            map.put(entry.getKey(), (T) entry.getValue().bean);
        }
        return map;
    }

    private Map<String, BeanProxy> getBeans(Class<?> type) {
        Map<String, BeanProxy> map = new HashMap<>();
        for (Map.Entry<String, BeanProxy> entry : beanMap.entrySet()) {
            if (type.isAssignableFrom(entry.getValue().getBeanType())) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return map;
    }

    @Builder
    @Getter
    private static class BeanProxy {
        private Class<?> beanType;
        private Object bean;
    }
}
