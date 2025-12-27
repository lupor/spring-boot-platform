/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.environment.config;

import de.sky.newcrm.apims.spring.environment.core.ApimsAppTeamEnum;
import de.sky.newcrm.apims.spring.environment.core.ApimsAppTypeEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("apims.app")
@Getter
@Setter
public class CoreConfig {
    private ApimsAppTypeEnum type = ApimsAppTypeEnum.UNKNOWN;
    private String team = ApimsAppTeamEnum.UNKNOWN.name();
    private String name = "${APP_NAME:UNKNOWN}";
    private String configProfile = "dev";
    private String env = "${APP_ENV:dev}";
    private String namespace = "${NAMESPACE:dev}";
    private String resourcePrefix = "${RESOURCE_PREFIX:dev}";
    private String domain = "${DOMAIN:}";
    private boolean serviceStartupListenerEnabled = true;
    private MockConfig mocks = new MockConfig();
    private IncidentManagementConfig incidentMgmt = new IncidentManagementConfig();
}
