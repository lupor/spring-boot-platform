package de.sky.newcrm.apims.spring.web.config;

import de.sky.newcrm.apims.spring.environment.config.ConditionalEnabled;
import de.sky.newcrm.apims.spring.web.core.oauth.entity.ApimsConfigKeySourceTypeEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

import static de.sky.newcrm.apims.spring.environment.config.ApimsCoreProperties.DEFAULT_PLACEHOLDER_VALUE;

@ConfigurationProperties("apims.web")
@Getter
@Setter
public class ApimsWebConfig extends ConditionalEnabled {

    private HttpComponents httpComponents = new HttpComponents();
    private Logging logging = new Logging();
    private Auth auth = new Auth();

    @Getter
    @Setter
    public static class HttpComponents extends ConditionalEnabled {

        private int connectTimeout = 10000;
        private int requestTimeout = 120000;
        private int socketTimeout = 120000;
        private int connectionTimeToLiveSecs = 300;
        private int maxTotalConnections = 180;
        private int defaultMaxConnectionsPerRoute = 30;
        private int defaultKeepAliveTimeMillis = 5000;
        private int closeIdleConnectionWaitTimeSecs = 20;
        private boolean hostnameVerifierDisabled = false;
        private boolean trustAllCertificates = false;
    }

    @Getter
    @Setter
    public static class Auth extends ConditionalEnabled {

        public Auth() {
            super(false);
        }

        private String defaultRoles = null;
        private TrustedServices trustedServices = new TrustedServices();
        private Map<String, String> roleMapping = new LinkedHashMap<>();
        private String jwtClaimNameRoles = "roles";
        private AuthJwtTokenValidator aadTokenValidator =
                new AuthJwtTokenValidator(true, "https://login.microsoftonline.com/");
        private AuthJwtTokenValidator additionalTokenValidator = new AuthJwtTokenValidator(false, "");
        private AuthJwtTokenValidator serviceTokenValidator = new AuthJwtTokenValidator(true, "de.sky.apims");
        private AuthJwtTokenValidator testTokenValidator =
                new AuthJwtTokenValidator(false, "https://testcase.com/");

        public void setRoleMapping(Map<String, String> roleMapping) {
            roleMapping.remove(DEFAULT_PLACEHOLDER_VALUE);
            this.roleMapping = roleMapping;
        }

        @Getter
        @Setter
        public static class TrustedServices extends ConditionalEnabled {

            private boolean trustBySkyHeaders = false;
            private String domainsDefaultRoles = null;
            private Map<String, String> domainsRoles = new LinkedHashMap<>();
            private String servicesDefaultRoles = null;
            private Map<String, String> servicesRoles = new LinkedHashMap<>();

            public void setDomainsRoles(Map<String, String> domainsRoles) {
                domainsRoles.remove(DEFAULT_PLACEHOLDER_VALUE);
                this.domainsRoles = domainsRoles;
            }

            public void setServicesRoles(Map<String, String> servicesRoles) {
                servicesRoles.remove(DEFAULT_PLACEHOLDER_VALUE);
                this.servicesRoles = servicesRoles;
            }
        }
    }

    @Getter
    @Setter
    public static class Logging {

        private boolean enabled = true;
        private boolean includeQueryString = true;
        private boolean includeSkyHeaders = true;
        private boolean includeHeaders = false;
        private boolean includePayload = false;
        private int maxPayloadLength = 1000;
        private String headerPredicate = "x-sky, x-b3";

    }

    @Getter
    @Setter
    public static class AuthJwtTokenValidator extends ConditionalEnabled {

        protected AuthJwtTokenValidator(String issuer) {
            this.issuer = issuer;
        }

        protected AuthJwtTokenValidator(boolean enabled, String issuer) {
            super(enabled);
            this.issuer = issuer;
        }

        private String issuer;
        private ApimsConfigKeySourceTypeEnum keySourceType = ApimsConfigKeySourceTypeEnum.RESOURCE_LOCATION;
        private String keySourceValue = "";
        private boolean explicitAudienceCheck = false;
        private String validAudiences = "";

    }
}