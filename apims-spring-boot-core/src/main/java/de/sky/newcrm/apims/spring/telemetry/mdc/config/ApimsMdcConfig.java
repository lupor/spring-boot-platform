package de.sky.newcrm.apims.spring.telemetry.mdc.config;

import de.sky.newcrm.apims.spring.telemetry.mdc.core.ApimsMdc;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.LinkedHashMap;
import java.util.Map;

@ConfigurationProperties("apims.aspects.listeners.mdc")
@Getter
@Setter
public class ApimsMdcConfig {
    private String prefix = "";
    private String globalFieldsPrefix = "";
    private Map<String, String> globalFields = new LinkedHashMap<>();
    private Map<String, String> remoteFields = new LinkedHashMap<>();
}

