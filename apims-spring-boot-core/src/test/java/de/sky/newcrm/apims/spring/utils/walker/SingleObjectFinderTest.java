/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils.walker;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import de.sky.newcrm.apims.spring.environment.core.ApimsMockUtils;
import de.sky.newcrm.apims.spring.serialization.core.mapper.ObjectMapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class SingleObjectFinderTest {

    @Test
    void singleObjectFinderTest() throws IOException {
        String payload = loadTestFile("objectmapper/complex-object.json");
        Map<String, Object> data = ObjectMapperUtils.readMap(payload);
        SingleObjectFinder.SingleObjectFinderResult result = new SingleObjectFinder<>(
                        item -> "salesOrderOverview.pricePeriods[0]".equals(item.getPath()))
                .walk(data)
                .getResult();
        assertNotNull(result);
        assertEquals("salesOrderOverview.pricePeriods[0]", result.getPath());
        assertEquals("[0]", result.getKey());
        assertEquals(12, result.getValue());

        result = new SingleObjectFinder<>(item -> "salesOrderOverview".equals(item.getPath()))
                .walk(data)
                .getResult();
        assertNotNull(result);
        assertNotNull(result.getValue());
        assertTrue(result.getValue() instanceof Map);
        assertEquals(7, ((Map<?, ?>) result.getValue()).size());

        result = new SingleObjectFinder<>(item -> "salesOrderOverview.pricePeriods".equals(item.getPath()))
                .walk(data)
                .getResult();
        assertNotNull(result);
        assertNotNull(result.getValue());
        assertTrue(result.getValue() instanceof List);
        assertEquals(2, ((List<?>) result.getValue()).size());

        result = new SingleObjectFinder<>(
                        item -> "salesOrderOverview.yearlyPromoPackages[0].commitmentPrice".equals(item.getPath()))
                .walk(data)
                .getResult();
        assertNotNull(result);
        assertNotNull(result.getValue());
        assertTrue(result.getValue() instanceof Integer);
        assertEquals(4100, result.getValue());

        result = new SingleObjectFinder<>("default", item -> "notExists".equals(item.getPath()))
                .walk(data)
                .getResult();
        assertNotNull(result);
        assertNull(result.getPath());
        assertNull(result.getKey());
        assertEquals("default", result.getValue());
    }

    protected String loadTestFile(String resourceName) throws IOException {
        return ApimsMockUtils.loadTestFile(resourceName);
    }
}
