package de.sky.newcrm.apims.spring.serialization.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@ConfigurationProperties("apims.object-mapper-config")
@Getter
@Setter
public class ApimsObjectMapperConfig {
    private boolean dateTimeSerializerWriteIsoDateWithTimezone = false;
}
