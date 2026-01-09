package de.sky.newcrm.apims.spring.couchbase.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

// TODO: Fix the integration part
@ConfigurationProperties("apims.couchbase")
@Getter
@Setter
@PropertySource("classpath:apims-couchbase__${spring.profiles.active}.yml")
public class ApimsCouchbaseConfig {
    private boolean autoResolveEnabledFlag = true;

    private ApimsCouchbaseCachingConfig caching = new ApimsCouchbaseCachingConfig();
    private ApimsCouchbaseEncryptionConfig encryption = new ApimsCouchbaseEncryptionConfig();
    private ApimsCouchbaseReEncryptionConfig reEncryption = new ApimsCouchbaseReEncryptionConfig();
}
