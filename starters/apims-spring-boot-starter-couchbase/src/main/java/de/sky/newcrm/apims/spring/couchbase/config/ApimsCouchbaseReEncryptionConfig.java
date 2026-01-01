package de.sky.newcrm.apims.spring.couchbase.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@ConfigurationProperties("apims.couchbase.re-encryption")
@Getter
@Setter
public class ApimsCouchbaseReEncryptionConfig {
    private String cronExpression;
    private int maxExecutionMinutes;
    private int minDaysSinceLastUpdate;
    private List<JpaRepository<?, ?>> repositories;
}
