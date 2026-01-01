package de.sky.newcrm.apims.spring.web.config;

import de.sky.newcrm.apims.spring.environment.config.ConditionalEnabled;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

import static de.sky.newcrm.apims.spring.environment.config.ApimsCoreProperties.DEFAULT_PLACEHOLDER_VALUE;


@ConfigurationProperties("apims.rest")
@Getter
@Setter
public class ApimsRestConfig extends ConditionalEnabled {

        private boolean expandUriVars = true;
        private boolean autoValidateRequest = false;
        private boolean autoValidateResponse = false;
        private boolean preventDoubleEncoding = false;
        private int reportNotHandledHttpErrorsAsStatusCode = -1;
        private Map<String, String> headers = new LinkedHashMap<>();
        private Map<String, String> additionalHeaders = new LinkedHashMap<>();

        public Map<String, String> getHeaders() {
            return headers;
        }

        public void setHeaders(Map<String, String> headers) {
            headers.remove(DEFAULT_PLACEHOLDER_VALUE);
            this.headers = headers;
        }

        public boolean isExpandUriVars() {
            return expandUriVars;
        }

        public boolean isAutoValidateResponse() {
            return autoValidateResponse;
        }

        public boolean isAutoValidateRequest() {
            return autoValidateRequest;
        }

        public boolean isPreventDoubleEncoding() {
            return preventDoubleEncoding;
        }
        public int getReportNotHandledHttpErrorsAsStatusCode() {
            return reportNotHandledHttpErrorsAsStatusCode;
        }

        public Map<String, String> getAdditionalHeaders() {
            return additionalHeaders;
        }

        public void setAdditionalHeaders(Map<String, String> additionalHeaders) {
            additionalHeaders.remove(DEFAULT_PLACEHOLDER_VALUE);
            this.additionalHeaders = additionalHeaders;
        }
    }