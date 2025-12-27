/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils.scanner;

import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import java.lang.annotation.Annotation;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter;
import org.springframework.util.ClassUtils;

@SuppressWarnings({"java:S6212"})
public class AnnotationTypeFilter extends AbstractTypeHierarchyTraversingFilter {

    private final Class<? extends Annotation> annotationType;

    private final boolean considerMetaAnnotations;

    public AnnotationTypeFilter(
            Class<? extends Annotation> annotationType,
            boolean considerInherited,
            boolean considerMetaAnnotations,
            boolean considerInterfaces) {

        super(considerInherited, considerInterfaces);
        this.annotationType = annotationType;
        this.considerMetaAnnotations = considerMetaAnnotations;
    }

    @ApimsReportGeneratedHint
    public final Class<? extends Annotation> getAnnotationType() {
        return this.annotationType;
    }

    @Override
    @ApimsReportGeneratedHint
    protected boolean matchSelf(MetadataReader metadataReader) {
        AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
        return annotationType == null
                || metadata.hasAnnotation(this.annotationType.getName())
                || (this.considerMetaAnnotations && metadata.hasMetaAnnotation(this.annotationType.getName()));
    }

    @Override
    @Nullable
    @ApimsReportGeneratedHint
    protected Boolean matchSuperClass(@Nullable String superClassName) {
        return hasAnnotation(superClassName);
    }

    @Override
    @Nullable
    @ApimsReportGeneratedHint
    protected Boolean matchInterface(@Nullable String interfaceName) {
        return hasAnnotation(interfaceName);
    }

    @Nullable
    @SuppressWarnings("java:S1181")
    @ApimsReportGeneratedHint
    protected Boolean hasAnnotation(String typeName) {
        if (Object.class.getName().equals(typeName)) {
            return false;
        } else if (typeName.startsWith("java")) {
            if (annotationType == null || !this.annotationType.getName().startsWith("java")) {
                // Standard Java types do not have non-standard annotations on them ->
                // skip any load attempt, in particular for Java language interfaces.
                return false;
            }
            try {
                Class<?> clazz = ClassUtils.forName(typeName, getClass().getClassLoader());
                return ((this.considerMetaAnnotations
                                ? AnnotationUtils.getAnnotation(clazz, this.annotationType)
                                : clazz.getAnnotation(this.annotationType))
                        != null);
            } catch (Throwable _) {
                // Class not regularly loadable - can't determine a match that way.
            }
        } else if (annotationType == null) {
            return true;
        }
        return null;
    }
}
