/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.environment.core;

import de.sky.newcrm.apims.spring.utils.CastUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.lang.Nullable;

public class ApimsValueResolver implements BeanFactoryAware {

    private ConfigurableBeanFactory beanFactory = null;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableBeanFactory) beanFactory;
    }

    public String resolve(String value) {
        return this.beanFactory.resolveEmbeddedValue(value);
    }

    public BeanExpressionResolver getBeanExpressionResolver() {
        return beanFactory.getBeanExpressionResolver();
    }

    public BeanExpressionContext createBeanExpressionContext(@Nullable Scope scope) {
        return new BeanExpressionContext(beanFactory, scope);
    }

    public Object resolveExpression(String value) {
        return resolveExpression(getBeanExpressionResolver(), createBeanExpressionContext(null), value);
    }

    @SuppressWarnings({"java:S6201"})
    @ApimsReportGeneratedHint
    public String resolveExpression(String value, String defaultValue) {
        Object data = resolveExpression(value);
        if (data == null) {
            return defaultValue;
        }
        if (data instanceof String string) {
            if ("null".equalsIgnoreCase(string)) {
                return defaultValue;
            }
            return string;
        }
        String s = CastUtils.getValue(String.class, value);
        return s == null ? defaultValue : s;
    }

    public Object resolveExpression(BeanExpressionResolver resolver, BeanExpressionContext context, String value) {
        return resolver.evaluate(resolve(value), context);
    }
}
