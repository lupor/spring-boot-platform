package de.sky.newcrm.apims.spring.telemetry.metrics.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

import java.util.LinkedHashMap;
import java.util.Map;

@ConfigurationProperties("apims.metrics")
@Getter
@Setter
public class ApimsMetricsConfig {
    @Setter
    private boolean countedAnnotationEnabled = false;
    @Setter
    private boolean timedAnnotationEnabled = false;
    private final Map<String, String> commonTags = new LinkedHashMap<>();
}
