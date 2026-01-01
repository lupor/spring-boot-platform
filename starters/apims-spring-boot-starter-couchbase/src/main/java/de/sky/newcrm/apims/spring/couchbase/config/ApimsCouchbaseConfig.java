package de.sky.newcrm.apims.spring.couchbase.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

// TODO: Fix the integration part
@ConfigurationProperties("apims.couchbase.re-encryption")
@Getter
@Setter
public class ApimsCouchbaseConfig {
    private boolean autoResolveEnabledFlag = true;

    private ApimsCouchbaseCachingConfig caching = new ApimsCouchbaseCachingConfig();
    private ApimsCouchbaseEncryptionConfig encryption = new ApimsCouchbaseEncryptionConfig();
    private ApimsCouchbaseReEncryptionConfig reEncryption = new ApimsCouchbaseReEncryptionConfig();
}
