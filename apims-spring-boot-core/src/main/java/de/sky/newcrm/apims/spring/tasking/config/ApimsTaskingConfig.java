package de.sky.newcrm.apims.spring.tasking.config;

import de.sky.newcrm.apims.spring.async.config.ApimsPoolConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("apims.tasking")
@Getter
@Setter
public class ApimsTaskingConfig extends ApimsPoolConfig {
    public ApimsTaskingConfig() {
        super("ApimsTaskExecuterThread-");
    }
}
