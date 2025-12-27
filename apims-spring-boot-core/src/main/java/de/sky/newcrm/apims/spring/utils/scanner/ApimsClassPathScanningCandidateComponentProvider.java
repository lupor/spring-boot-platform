/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils.scanner;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;

public class ApimsClassPathScanningCandidateComponentProvider extends ClassPathScanningCandidateComponentProvider {

    private boolean allBeanDefinitions = false;

    public ApimsClassPathScanningCandidateComponentProvider() {}

    public ApimsClassPathScanningCandidateComponentProvider(boolean useDefaultFilters) {
        super(useDefaultFilters);
    }

    public ApimsClassPathScanningCandidateComponentProvider(boolean useDefaultFilters, Environment environment) {
        super(useDefaultFilters, environment);
    }

    public boolean isAllBeanDefinitions() {
        return allBeanDefinitions;
    }

    public void setAllBeanDefinitions(boolean allBeanDefinitions) {
        this.allBeanDefinitions = allBeanDefinitions;
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return allBeanDefinitions || super.isCandidateComponent(beanDefinition);
    }
}
