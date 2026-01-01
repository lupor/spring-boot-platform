/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.environment.core;

import de.sky.newcrm.apims.spring.exceptions.ApimsBaseException;
import de.sky.newcrm.apims.spring.exceptions.ApimsErrorAttributes;
import de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3.ObjectMapperUtils;
import de.sky.newcrm.apims.spring.utils.FileUtils;
import de.sky.newcrm.apims.spring.utils.TempFileUtils;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ReflectionUtils;
import tools.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.sky.newcrm.apims.spring.exceptions.ApimsErrorAttributes.BUSINESS_EXCEPTION_ERRORS_KEY;

@SuppressWarnings({"java:S1610", "java:S6212"})
public abstract class ApimsMockUtils {

    public static final String DEFAULT_BASE_TEMP_SUB_DIR_NAME = "test";

    protected ApimsMockUtils() {}

    public static void injectField(Object target, String fieldName, Object value) {
        Assert.notNull(target, "[Assertion failed] - argument 'target' is required; it must not be null");
        Field field = ReflectionUtils.findField(target.getClass(), fieldName);
        Assert.notNull(
                field,
                "[Assertion failed] - field '" + target.getClass() + "." + fieldName
                        + "' is required; it must not be null");
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, target, value);
    }

    public static Object readField(Object target, String fieldName) {
        Assert.notNull(target, "[Assertion failed] - argument 'target' is required; it must not be null");
        Field field = ReflectionUtils.findField(target.getClass(), fieldName);
        Assert.notNull(
                field,
                "[Assertion failed] - field '" + target.getClass() + "." + fieldName
                        + "' is required; it must not be null");
        ReflectionUtils.makeAccessible(field);
        return ReflectionUtils.getField(field, target);
    }

    public static Map<Method, Annotation> findMethodAnnotation(Class<?> annotatedClazz, Class<?> annotationClass) {
        Map<Method, Annotation> methodMap = new HashMap<>();
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(annotatedClazz);
        for (Method method : methods) {
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotationClass.equals(annotation.annotationType())) {
                    methodMap.put(method, annotation);
                    break;
                }
            }
        }
        return methodMap;
    }

    public static Annotation findClassAnnotation(Class<?> annotatedClazz, Class<?> annotationClass) {
        Annotation[] annotations = annotatedClazz.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotationClass.equals(annotation.annotationType())) {
                return annotation;
            }
        }
        return null;
    }

    public static Map<String, String> getValidationErrors(ApimsBaseException e) {

        Map<String, String> validationErrors = new HashMap<>();
        if (e == null || e.getDetails() == null) {
            return validationErrors;
        }
        Object errors = e.getDetails().get(BUSINESS_EXCEPTION_ERRORS_KEY);
        if (!(errors instanceof List<?> errorList)) {
            return validationErrors;
        }
        for (Object object : errorList) {
            if (!(object instanceof Map<?, ?> validationMap)) {
                continue;
            }
            if (validationMap.containsKey(ApimsErrorAttributes.VALIDATION_ERROR_FIELD_KEY)) {
                validationErrors.put(
                        String.valueOf(validationMap.get(ApimsErrorAttributes.VALIDATION_ERROR_FIELD_KEY)),
                        String.valueOf(validationMap.get(ApimsErrorAttributes.VALIDATION_ERROR_CODE_KEY)));
            }
        }
        return validationErrors;
    }

    public static Path copyToTempFile(Path file) throws IOException {

        Path newTempFile =
                createTempFile(FileUtils.getFileNameWithoutExtension(file), FileUtils.getFileExtension(file, ".tmp"));
        FileCopyUtils.copy(file.toFile(), newTempFile.toFile());
        return newTempFile;
    }

    public static Path createTempFile(Path file) {
        return createTempFile(FileUtils.getFileNameWithoutExtension(file), FileUtils.getFileExtension(file, ".tmp"));
    }

    public static Path createTempFile(String fileNamePrefix, String fileNameSuffix) {

        return TempFileUtils.createTempFile(TempFileUtils.TempFileConfiguration.builder()
                .tempSubDir(DEFAULT_BASE_TEMP_SUB_DIR_NAME)
                .fileNamePrefix(fileNamePrefix)
                .fileNamePrefixAppendTimestamp(true)
                .fileNameSuffix(fileNameSuffix)
                .build());
    }

    public static File getResourceFile(String resourceName) {
        if (!resourceName.startsWith("/testdata/")) {
            resourceName = "/testdata/" + resourceName;
        }
        return new File(ApimsMockUtils.class.getResource(resourceName).getFile());
    }

    public static String loadTestFile(String resourceName) throws IOException {
        return new String(FileCopyUtils.copyToByteArray(getResourceFile(resourceName)), StandardCharsets.UTF_8);
    }

    public static <T> List<T> readJsonListValueFromResourceFile(String resourceName, Class<T> type) throws IOException {
        return ObjectMapperUtils.getApimsObjectMapperJson().readListValue(loadTestFile(resourceName), type);
    }

    public static <T> T readJsonValueFromResourceFile(String resourceName, Class<T> type) throws IOException {
        return ObjectMapperUtils.getApimsObjectMapperJson().readValue(loadTestFile(resourceName), type);
    }

    public static <T> T readJsonValueFromResourceFile(String resourceName, TypeReference<T> valueTypeRef)
            throws IOException {
        return ObjectMapperUtils.getApimsObjectMapperJson().readValue(loadTestFile(resourceName), valueTypeRef);
    }

    public static List<Map<String, Object>> readJsonMapListFromResourceFile(String resourceName) throws IOException {
        return ObjectMapperUtils.getApimsObjectMapperJson().readList(loadTestFile(resourceName));
    }

    public static Map<String, Object> readJsonMapFromResourceFile(String resourceName) throws IOException {
        return ObjectMapperUtils.getApimsObjectMapperJson().readMap(loadTestFile(resourceName));
    }
}
