package de.sky.newcrm.apims.spring.couchbase.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@ConfigurationProperties("apims.couchbase.caching")
@Getter
@Setter
@PropertySource("classpath:apims-couchbase__${spring.profiles.active}.properties")
public class ApimsCouchbaseCachingConfig {
    private String collection;
    private Long expirySeconds;
    private String cachePrefix;

    private List<CacheConfig> caches;


    @Getter
    @Setter
    public static class CacheConfig {
        private String name;
        private Long expirySeconds;
    }
}
