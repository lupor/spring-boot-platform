/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.apps.kafkacouchbase;

import com.couchbase.client.java.env.ClusterEnvironment;
import com.couchbase.client.java.kv.MutateInSpec;
import de.sky.newcrm.apims.spring.apps.kafkacouchbase.entity.CustomerDocument;
import de.sky.newcrm.apims.spring.couchbase.core.ApimsCouchbaseMutateInSupport;
import de.sky.newcrm.apims.spring.couchbase.core.entity.ApimsMutateInSpec;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.couchbase.core.mapping.CouchbaseDocument;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureJsonTesters
@SpringBootTest
@ActiveProfiles({"dev", "unit-test"})
@TestPropertySource(
        properties = {
            "spring.config.location=classpath:testdata/applications/kafkacouchbase/application.yml,classpath:testdata/applications/kafkacouchbase/application-dev.yml,classpath:testdata/applications/kafkacouchbase/application-unit-test.yml"
        })
class KafkaCouchbaseApplicationCommonTest {

    @Autowired
    ApimsCouchbaseMutateInSupport apimsCouchbaseMutateInSupport;

    @Autowired
    ClusterEnvironment clusterEnvironment;

    @Test
    void mutateInSupportTest() {
        ApimsTestCouchbaseMutateInSupport mutateInSupport =
                new ApimsTestCouchbaseMutateInSupport(clusterEnvironment, apimsCouchbaseMutateInSupport);

        String customerId = "42";
        String documentId = CustomerDocument.createDocumentId(customerId);
        CustomerDocument document = CustomerDocument.builder()
                .id(documentId)
                .customerId(customerId)
                .name("Max Mustermann")
                .email("Max.Mustermann@test-emea.de")
                .build();

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("emptyList", new ArrayList<>());
        dataMap.put("document", document);

        List<ApimsMutateInSpec> list = List.of(
                ApimsMutateInSpec.upsert("name", "name"),
                ApimsMutateInSpec.upsert("email", "email"),
                ApimsMutateInSpec.remove("email"),
                ApimsMutateInSpec.arrayAppend("pats", "cat"),
                ApimsMutateInSpec.upsert("map", dataMap));
        final ApimsMutateInSpec[] args = list.toArray(new ApimsMutateInSpec[0]);
        assertTrue(StringUtils.hasLength(mutateInSupport.toString(args)));
        assertTrue(mutateInSupport.isMutateInCustomConversionsEnabled());
        assertNotNull(mutateInSupport.getMappingCouchbaseConverter());
        assertNotNull(mutateInSupport.getCustomConversions());

        List<MutateInSpec> list2 = mutateInSupport.translate(args);
        assertEquals(list.size(), list2.size());

        CouchbaseDocument couchbaseDocument = mutateInSupport.encodeEntity(documentId, document);
        assertNotNull(couchbaseDocument);
        assertEquals(documentId, couchbaseDocument.getId());
        assertEquals(customerId, couchbaseDocument.get("customerId"));

        assertNotNull(mutateInSupport.convertRootObjectToWrite(documentId, document));
    }

    private static class ApimsTestCouchbaseMutateInSupport extends ApimsCouchbaseMutateInSupport {
        public ApimsTestCouchbaseMutateInSupport(
                ClusterEnvironment clusterEnvironment, ApimsCouchbaseMutateInSupport parent) {
            super(
                    parent.isMutateInCustomConversionsEnabled(),
                    parent.getMappingCouchbaseConverter(),
                    parent.getCustomConversions(),
                    clusterEnvironment.jsonSerializer());
        }

        @Override
        protected List<MutateInSpec> translate(ApimsMutateInSpec... inSpecs) {
            return super.translate(inSpecs);
        }

        @Override
        protected Object convertRootObjectToWrite(String id, Object entityToEncode) {
            return super.convertRootObjectToWrite(id, entityToEncode);
        }
    }
}
