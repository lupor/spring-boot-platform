/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.environment.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public class ApimsVersion {

    private ApimsVersion() {}

    @SuppressWarnings("java:S3400")
    public static String getParentVersion() {
        Properties p =
                loadSource(new ClassPathResource("META-INF/maven/de.sky.newcrm/spring-boot-apims-core/pom.properties"));
        return p.getProperty("version", "SNAPSHOT");
    }

    public static String getAppBuildVersion() {
        Properties p = loadSource(new ClassPathResource("META-INF/build-info.properties"));
        return p.getProperty("build.version", "");
    }

    private static Properties loadSource(Resource location) {
        try {
            return PropertiesLoaderUtils.loadProperties(new EncodedResource(location, StandardCharsets.UTF_8));
        } catch (IOException e) {
            return new Properties();
        }
    }
}
