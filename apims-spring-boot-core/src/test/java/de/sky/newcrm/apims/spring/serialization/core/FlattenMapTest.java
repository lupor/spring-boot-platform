/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core;

import static de.sky.newcrm.apims.spring.environment.core.ApimsMockUtils.loadTestFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.sky.newcrm.apims.spring.serialization.core.mapper.ApimsJsonMapper;
import de.sky.newcrm.apims.spring.serialization.core.mapper.ObjectMapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class FlattenMapTest {

    @Test
    void toFlattenMapTest() throws IOException {
        String payload = loadTestFile("objectmapper/complex-object.json");
        Map<String, Object> flattenMap = ObjectMapperUtils.toFlattenMap(payload);
        validate(flattenMap);
        // log.info("{}", ObjectMapperUtils.writeValueAsString(flattenMap));
    }

    @Test
    void resolveFlattenMapTest() throws IOException {
        String payload = loadTestFile("objectmapper/complex-object.json");
        Map<String, Object> data = ObjectMapperUtils.readMap(payload);
        Map<String, Object> flattenMap = new HashMap<>();
        flattenMap.put("salesOrderOverview.pricePeriods[0]", 42);
        ApimsJsonMapper.resolveFlattenMap(data, flattenMap);
        validate(flattenMap);
        // log.info("{}", ObjectMapperUtils.writeValueAsString(flattenMap));
    }

    protected void validate(Map<String, Object> flattenMap) {
        assertNotNull(flattenMap);
        assertEquals(12, flattenMap.get("salesOrderOverview.pricePeriods[0]"));
        assertEquals(13, flattenMap.get("salesOrderOverview.pricePeriods[1]"));
        assertEquals("Sky Q IPTV Box 2", flattenMap.get("otherOrderedDevices[0]"));
        assertEquals("Sky Q IPTV Box 3", flattenMap.get("otherOrderedDevices[1]"));
        assertEquals(2900, flattenMap.get("otDetails.otPackageDetails[0].finalOneTimePrice"));
    }
}
