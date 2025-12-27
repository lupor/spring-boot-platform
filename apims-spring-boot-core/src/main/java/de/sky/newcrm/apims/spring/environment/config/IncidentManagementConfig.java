/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.environment.config;

import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("apims.app")
@Getter
@Setter
public class IncidentManagementConfig {
    private String serviceCi;
    private DefaultCI defaultCis;
    private List<BusinessCI> businessCis;
    private List<ExceptionCI> exceptionCis;
    private List<RestEndpointCI> restEndpointCis;

    @Data
    public static class BusinessCI {
        private String id;
        private String name;
    }

    @Data
    public static class ExceptionCI {
        private String id;
        private String packageInfo;
    }

    @Data
    public static class RestEndpointCI {
        private String url;
        private String id;
    }

    @Data
    public static class DefaultCI {
        private Map<String, String> domains;
        private String kafka;
        private String couchbase;
        private String salesforce;
    }
}
