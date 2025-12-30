/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils.walker;

import static de.sky.newcrm.apims.spring.environment.core.ApimsMockUtils.loadTestFile;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.*;

import de.sky.newcrm.apims.spring.serialization.core.mapper.ObjectMapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class FlattenMapCreatorTest {

    @Test
    void flattenMapCreatorTest() throws IOException {
        String payload = loadTestFile("objectmapper/complex-object.json");
        Map<String, Object> data = ObjectMapperUtils.readMap(payload);
        assertNotNull(data);
        FlattenMap flattenMap = new FlattenMapCreator<>().walk(data).getResult();
        validate(flattenMap);
    }

    @Test
    void flattenMapCreatorRootNullTest() {
        Map<String, Object> flattenMap = new FlattenMapCreator<>().walk(null).getResult();
        assertNotNull(flattenMap);
        assertTrue(flattenMap.isEmpty());
    }

    @Test
    void flattenMapCreatorCollectionTest() throws IOException {
        String payload = loadTestFile("objectmapper/complex-object.json");
        Map<String, Object> data = ObjectMapperUtils.readMap(payload);
        assertNotNull(data);
        List<Map<String, Object>> list = new ArrayList<>(List.of(data));
        FlattenMap flattenMap = new FlattenMapCreator<>().walk(list).getResult();
        assertNotNull(flattenMap);
        assertFalse(flattenMap.isEmpty());
        assertEquals(1, flattenMap.get("size"));
        assertEquals(2, flattenMap.get("[0].salesOrderOverview.pricePeriods.size"));
        assertEquals(12, flattenMap.get("[0].salesOrderOverview.pricePeriods[0]"));
    }

    protected void validate(FlattenMap flattenMap) {
        assertNotNull(flattenMap);
        assertEquals(2, flattenMap.get("salesOrderOverview.pricePeriods.size"));
        assertEquals(12, flattenMap.get("salesOrderOverview.pricePeriods[0]"));
        assertEquals(13, flattenMap.get("salesOrderOverview.pricePeriods[1]"));
        assertEquals(13, flattenMap.get("salesOrderOverview.pricePeriods[1]", 42));
        assertEquals(42, flattenMap.get("salesOrderOverview.pricePeriods[2]", 42));
        assertEquals("Sky Q IPTV Box 2", flattenMap.get("otherOrderedDevices[0]"));
        assertEquals("Sky Q IPTV Box 3", flattenMap.get("otherOrderedDevices[1]"));
        assertEquals(2900, flattenMap.get("otDetails.otPackageDetails[0].finalOneTimePrice"));
        assertEquals(0, flattenMap.get("notExists.size", 0));
        assertEquals("default", flattenMap.get("notExists.name", "default"));
        assertNotNull(flattenMap.get("notExists.object", new Object()));
    }
}
