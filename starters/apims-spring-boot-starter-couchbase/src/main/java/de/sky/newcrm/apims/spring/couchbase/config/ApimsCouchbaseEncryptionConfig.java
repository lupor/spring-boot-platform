package de.sky.newcrm.apims.spring.couchbase.config;

import de.sky.newcrm.apims.spring.environment.config.ConditionalEnabled;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@ConfigurationProperties("apims.couchbase.encryption")
@Getter
@Setter
@PropertySource("classpath:apims-couchbase__${spring.profiles.active}.properties")
public class ApimsCouchbaseEncryptionConfig {
    private String dekPath;
    private ApimsCouchbaseEncryptionKekConfig kek;

    @Setter
    @Getter
    public static class ApimsCouchbaseEncryptionKekConfig extends ConditionalEnabled {
        private String path;
    }
}