/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.environment.config;

import de.sky.newcrm.apims.spring.environment.core.ApimsAppTeamEnum;
import de.sky.newcrm.apims.spring.environment.core.ApimsAppTypeEnum;
import de.sky.newcrm.apims.spring.environment.core.IncidentManagement;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("apims")
@SuppressWarnings("all")
// TODO: Clean this up and strip it to the bare minimum
@Getter
@Setter
public class ApimsCoreProperties {

    private static final String DEFAULT_PLACEHOLDER_VALUE = "_placeholder";

    private App app = new App();
    private Aspects aspects = new Aspects();

    //    TODO: check if this remains in core. Move it for now
    //    private Async async = new Async();
    //    TODO: Move to relevant starters
    //    private Cli cli = new Cli();
    //    private Cloud cloud = new Cloud();
    //    private Couchbase couchbase = new Couchbase();
    //    private Integration integration = new Integration();
    //    TODO: Move to relevant starters
    //    private Jobs jobs = new Jobs();
    //    private Kafka kafka = new Kafka();

    //    TODO: Move to relevant starters
    //    private Rest rest = new Rest();
    //    private Scheduling scheduling = new Scheduling();
    //    private Sftp sftp = new Sftp();
    //    private Sftp2 sftp2 = new Sftp2();
    //    private Ssh ssh = new Ssh();
    //    TODO: Move to tasking module

    private Tasking tasking = new Tasking();

    //    This is for testing
    //    TODO: Move to test module
    //    private Trace trace = new Trace();

    //    TODO: Move to relevant starters
    //    private Web web = new Web();
    //    private Ws ws = new Ws();

    //
    //    TODO: Reduce this to the absolute bare minimum
    @Getter
    @Setter
    public static class App {
        private Mocks mocks = new Mocks();
        private ApimsAppTypeEnum type = ApimsAppTypeEnum.UNKNOWN;
        private String team = ApimsAppTeamEnum.UNKNOWN.name();
        private String name = "${APP_NAME:UNKNOWN}";
        private String configProfile = "dev";
        private String env = "${APP_ENV:dev}";
        private String namespace = "${NAMESPACE:dev}";
        private String resourcePrefix = "${RESOURCE_PREFIX:dev}";
        private String domain = "${DOMAIN:}";
        private boolean serviceStartupListenerEnabled = true;
        private IncidentManagement incidentMgmt = new IncidentManagement();

        @Getter
        @Setter
        public static class Mocks {
            private boolean embeddedSftpServerEnabled = false;
            private boolean apiMockFileHeaderChecksumEnabled = false;
        }
    }

    //    TODO: Reduce this to the bare minimum
    @Getter
    @Setter
    public static class Aspects extends ConditionalEnabled {
        private boolean createNewSpan = true;
        private Listeners listeners = new Listeners();
        private Serializer serializer = new Serializer();
        private boolean componentEnabled = true;
        private boolean controllerEnabled = true;
        private boolean controllerAutoValidateResponse = false;
        private boolean endpointEnabled = true;
        private boolean messageHandlerEnabled = true;
        private boolean kafkaTemplateEnabled = true;
        private boolean pubsubEnabled = true;
        private boolean repositoryEnabled = true;
        private boolean restContollerEnabled = true;
        private boolean restClientEnabled = true;
        private boolean serviceEnabled = true;
        private boolean storageEnabled = true;
        private boolean webServiceClientEnabled = true;

        //        TODO: What is this for and which parts are still relevant?
        @Getter
        @Setter
        public static class Listeners {
            private Logging logging = new Logging();
            private Metrics metrics = new Metrics();
            private Tracing tracing = new Tracing();
            private NewTraceId newTraceId = new NewTraceId();
            private Mdc mdc = new Mdc();

            @Getter
            @Setter
            public static class Logging extends ConditionalEnabled {
                private String saveLogLinesAsSpanTag = "all";
                private long maxLength = 4000;
            }

            @Setter
            @Getter
            public static class Metrics extends ConditionalEnabled {
                private String ignoredComponents;
            }

            @Setter
            @Getter
            public static class Tracing extends ConditionalEnabled {
                private boolean traceAll = false;
            }

            public static class NewTraceId extends ConditionalEnabled {
                public NewTraceId() {
                    super(false);
                }
            }

            public static class Mdc extends ConditionalEnabled {
                @Setter
                @Getter
                private String prefix = "";

                @Setter
                @Getter
                private String globalFieldsPrefix = "";

                @Getter
                private Map<String, String> globalFields = new LinkedHashMap<>();

                @Getter
                private Map<String, String> remoteFields = new LinkedHashMap<>();

                public void setGlobalFields(Map<String, String> globalFields) {
                    globalFields.remove(DEFAULT_PLACEHOLDER_VALUE);
                    this.globalFields = globalFields;
                }

                public void setRemoteFields(Map<String, String> remoteFields) {
                    remoteFields.remove(DEFAULT_PLACEHOLDER_VALUE);
                    this.remoteFields = remoteFields;
                }
            }
        }

        @Setter
        @Getter
        public static class Serializer {
            private String maskKeys =
                    "email, password, newPassword, oldPassword, new-password, old-password, pin, newPin, oldPin";
            private String additionalMaskKeys = "";
            private String maskValue = "___masked___";
            private int defaultMaxCharacters = 4000;
        }
    }

    @Setter
    @Getter
    public static class Async extends ConditionalEnabled {
        private int corePoolSize = 1;
        private int maxPoolSize = Integer.MAX_VALUE;
        private int keepAliveSeconds = 60;
        private int queueCapacity = Integer.MAX_VALUE;
        private boolean allowCoreThreadTimeOut = false;
        private boolean prestartAllCoreThreads = false;
        private String threadNamePrefix = "ApimsAsyncThread-";

        public Async() {
            this(1, Integer.MAX_VALUE, 60, Integer.MAX_VALUE, false, false, "ApimsAsyncThread-");
        }

        public Async(String threadNamePrefix) {
            this.threadNamePrefix = threadNamePrefix;
        }

        public Async(
                int corePoolSize,
                int maxPoolSize,
                int keepAliveSeconds,
                int queueCapacity,
                boolean allowCoreThreadTimeOut,
                boolean prestartAllCoreThreads,
                String threadNamePrefix) {
            this.corePoolSize = corePoolSize;
            this.maxPoolSize = maxPoolSize;
            this.keepAliveSeconds = keepAliveSeconds;
            this.queueCapacity = queueCapacity;
            this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
            this.prestartAllCoreThreads = prestartAllCoreThreads;
            this.threadNamePrefix = threadNamePrefix;
            this.threadNamePrefix = threadNamePrefix;
        }
    }

    @Setter
    @Getter
    public static class Caching extends ConditionalEnabled {

        private int expireMinutes = 5;
        private long maximumSize = 1000;
        private boolean recordStats = false;
        private int secondCacheManagerExpireMinutes = 30;
        private long secondCacheManagerMaximumSize = 1000;
        private boolean secondCacheManagerRecordStats = false;
    }

    @Setter
    @Getter
    public static class CacheConfig extends ConditionalEnabled {
        private String name;
        private Long expirySeconds;
    }

    @Setter
    @Getter
    public static class Features extends ConditionalEnabled {
        private Map<String, String> serviceFeatures = new LinkedHashMap<>();
    }

    public static class Jackson extends ConditionalEnabled {}

    public static class Metrics extends ConditionalEnabled {
        @Setter
        @Getter
        private boolean countedAnnotationEnabled = false;

        @Setter
        @Getter
        private boolean timedAnnotationEnabled = false;

        @Getter
        private Map<String, String> commonTags = new LinkedHashMap<>();

        public void setCommonTags(Map<String, String> commonTags) {
            commonTags.remove(DEFAULT_PLACEHOLDER_VALUE);
            this.commonTags = commonTags;
        }
    }

    public static class Tasking extends Async {

        public Tasking() {
            super("ApimsTaskExecuterThread-");
        }
    }

    @Setter
    @Getter
    public static class Logging extends ConditionalEnabled {
        private boolean includeQueryString = true;
        private boolean includeSkyHeaders = true;
        private boolean includeHeaders = false;
        private boolean includePayload = false;
        private int maxPayloadLength = 1000;
        private String headerPredicate = "x-sky, x-b3";
    }
}
