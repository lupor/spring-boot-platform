/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils.scanner;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

@Slf4j
@SuppressWarnings({"java:S1610", "java:S6212"})
public abstract class ApimsClassScanner {

    private ApimsClassScanner() {}

    public static List<Class<?>> findClasses(Class<? extends Annotation> annotationType, String basePackage) {
        return findClasses(annotationType, false, basePackage);
    }

    public static List<Class<?>> findClasses(
            Class<? extends Annotation> annotationType, boolean scanAllBeanDefinitions, String basePackage) {
        return findClasses(annotationType, scanAllBeanDefinitions, false, false, false, basePackage);
    }

    public static List<Class<?>> findClasses(
            Class<? extends Annotation> annotationType,
            boolean scanAllBeanDefinitions,
            boolean considerInherited,
            boolean considerMetaAnnotations,
            boolean considerInterfaces,
            String basePackage) {

        ApimsClassPathScanningCandidateComponentProvider provider =
                new ApimsClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(
                annotationType, considerInherited, considerMetaAnnotations, considerInterfaces));
        provider.setAllBeanDefinitions(scanAllBeanDefinitions);
        Set<BeanDefinition> beanDefs = provider.findCandidateComponents(basePackage);
        List<Class<?>> clazzes = new ArrayList<>();
        for (BeanDefinition bd : beanDefs) {
            String clazzName = bd.getBeanClassName();
            if (!StringUtils.hasLength(clazzName)) {
                continue;
            }
            try {
                Class<?> clazz = ClassUtils.forName(clazzName, ApimsClassScanner.class.getClassLoader());
                clazzes.add(clazz);
            } catch (ClassNotFoundException | LinkageError e) {
                log.error("Class '{}' with annoatation {} not loaded: {}", clazzName, annotationType, e.getMessage());
            }
        }
        return clazzes;
    }
}
