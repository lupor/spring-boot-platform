package de.sky.newcrm.apims.spring.kafka.config;

import de.sky.newcrm.apims.spring.environment.config.ConditionalEnabled;import lombok.Getter;import lombok.Setter;import org.springframework.boot.context.properties.ConfigurationProperties;import java.util.LinkedHashMap;
import java.util.Map;import static de.sky.newcrm.apims.spring.environment.config.ApimsCoreProperties.DEFAULT_PLACEHOLDER_VALUE;

@ConfigurationProperties("apims.kafka")
@Getter
@Setter
public class ApimsKafkaConfig extends ConditionalEnabled {
    private KafkaConsumer consumer = new KafkaConsumer();
    private KafkaProducer producer = new KafkaProducer();

    public KafkaConsumer getConsumer() {
        return consumer;
    }

    public void setConsumer(KafkaConsumer consumer) {
        this.consumer = consumer;
    }

    public KafkaProducer getProducer() {
        return producer;
    }

    public void setProducer(KafkaProducer producer) {
        this.producer = producer;
    }

    @Getter
    @Setter
    public static class KafkaConsumer {

        private String groupId = "${apims.app.namespace-prefix}-${apims.app.name}";
        private boolean autoStartup = false;
        private long defaultErrorHandlerInterval = 5000;
        private long defaultErrorHandlerMaxFailures = 10;
        private Dlt dlt = new Dlt();
        private Map<String, TopicRetryProperties> retries = new LinkedHashMap<>();
        private Map<String, String> topics = new LinkedHashMap<>();

        public void setTopics(Map<String, String> topics) {
            topics.remove(DEFAULT_PLACEHOLDER_VALUE);
            this.topics = topics;
        }

        @Getter
        @Setter
        public static class TopicRetryProperties {
            private int retryAttempts = -1;
            private int lastRetryAttempts = -1;
            private long delay = -1L;
            private double multiplier = -1D;
        }

        @Getter
        @Setter
        public static class Dlt {
            private boolean enabled = true;
            private boolean groupBasedRetryAndDltTopics = false;
            private String dltTopicSuffix = "-dlt";
            private String retryTopicSuffix = "-retry";
            private int retryAttempts = 5;
            private int lastRetryAttempts = 10;
            private long delay = 6000L;
            private double multiplier = 10D;
            private String listenerContainerFactory = "";
            private boolean autoCreateTopics = true;
            private int autoCreateNumPartitions = 1;
            private short autoCreateReplicationFactor = 1;
            private boolean useDltTopicOnNoRetryableException = true;
            private String centralDltTopicName = "";
            private boolean centralDltTopicEnabled = true;
        }
    }

    @Getter
    @Setter
    public static class KafkaProducer {

        private Map<String, String> topics = new LinkedHashMap<>();
        private Map<String, String> headers = new LinkedHashMap<>();
        private Map<String, String> additionalHeaders = new LinkedHashMap<>();

        public void setTopics(Map<String, String> topics) {
            topics.remove(DEFAULT_PLACEHOLDER_VALUE);
            this.topics = topics;
        }

        public void setHeaders(Map<String, String> headers) {
            headers.remove(DEFAULT_PLACEHOLDER_VALUE);
            this.headers = headers;
        }

        public void setAdditionalHeaders(Map<String, String> additionalHeaders) {
            additionalHeaders.remove(DEFAULT_PLACEHOLDER_VALUE);
            this.additionalHeaders = additionalHeaders;
        }
    }
}
