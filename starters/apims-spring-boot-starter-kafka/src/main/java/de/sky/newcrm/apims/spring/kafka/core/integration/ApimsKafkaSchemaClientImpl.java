/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core.integration;

import de.sky.newcrm.apims.spring.kafka.core.integration.entity.SchemaResponse;
import de.sky.newcrm.apims.spring.utils.AssertUtils;
import de.sky.newcrm.apims.spring.web.core.rest.ApimsRestClient;
import io.confluent.kafka.schemaregistry.client.rest.utils.UrlList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Component
@Slf4j
public class ApimsKafkaSchemaClientImpl extends ApimsRestClient implements ApimsKafkaSchemaClient {

    @Value("${spring.kafka.properties.schema.registry.url}")
    private String instanceUrl;

    private String selectedInstanceUrl = null;

    public ApimsKafkaSchemaClientImpl(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public SchemaResponse getSchema(String topic) {
        return getSchema(topic, "latest");
    }

    @Override
    public SchemaResponse getSchema(String topic, String version) {
        AssertUtils.hasLengthCheck("topic", topic);
        AssertUtils.hasLengthCheck("version", version);
        return getForEntity(
                getInstanceUrl() + "/subjects/{0}/versions/{1}", SchemaResponse.class, topic + "-value", version);
    }

    protected synchronized String getInstanceUrl() {
        if (selectedInstanceUrl == null) {
            selectedInstanceUrl = new UrlList(
                            Arrays.asList(StringUtils.tokenizeToStringArray(instanceUrl, ",", true, true)))
                    .current();
        }
        return selectedInstanceUrl;
    }
}
