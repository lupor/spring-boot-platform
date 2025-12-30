/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils.walker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Map;
import java.util.TreeMap;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class FlattenMapTest {

    @Test
    void flattenMapTest() {
        FlattenMap flattenMap = new FlattenMap();
        flattenMap.put("key", "value");
        assertEquals(1, flattenMap.size());

        flattenMap = new FlattenMap(Map.of("key", "value"));
        assertEquals(1, flattenMap.size());
        flattenMap = new FlattenMap(new TreeMap<>(Map.of("key", "value")));
        assertEquals(1, flattenMap.size());
        assertEquals("value", flattenMap.get("key", "default"));
        assertEquals("default", flattenMap.get("key2", "default"));

        flattenMap = new FlattenMap((o1, o2) -> o1.length() == o2.length() ? 0 : o1.compareTo(o2));
        flattenMap.put("key", "value");
        assertEquals(1, flattenMap.size());
        assertEquals("value", flattenMap.get("key", "default"));
        assertEquals("default", flattenMap.get("key2", "default"));
        assertEquals("value", flattenMap.get("123", "default"));
    }
}
