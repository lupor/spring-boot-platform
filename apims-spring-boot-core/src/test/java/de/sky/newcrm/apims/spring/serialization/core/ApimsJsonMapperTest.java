/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3.ApimsJsonMapper;
import de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3.ObjectMapperUtils;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;

class ApimsJsonMapperTest {

    private static final String EXPECTED_JSON_VALUE =
            """
              {
              "testKey" : "COMMON_TEST_KEY",
              "testValue" : "COMMON_TEST_VALUE"
            }""";

    @Test
    void commonMapNullTest() {
        assertThrows(IllegalArgumentException.class, () -> ObjectMapperUtils.readMap(null));
        MismatchedInputException e = assertThrows(MismatchedInputException.class, () -> ObjectMapperUtils.readMap(""));
        assertTrue(e.getMessage().startsWith("No content to map due to end-of-input"));
    }

    @Test
    void commonMapReaderTest() {
        ApimsJsonMapper apimsJsonMapper = ObjectMapperUtils.getApimsObjectMapperJson();
        Map<String, Object> map = ObjectMapperUtils.readMap(EXPECTED_JSON_VALUE);
        List<Map<String, Object>> list = List.of(map);
        assertNotNull(apimsJsonMapper.buildMapReaderForMap(EXPECTED_JSON_VALUE));
        assertNotNull(apimsJsonMapper.buildMapReaderForList("[" + EXPECTED_JSON_VALUE + "]"));
        assertNotNull(apimsJsonMapper.build(map));
        assertNotNull(apimsJsonMapper.build(list));
        ObjectMapper objectMapper = apimsJsonMapper.unwrap();
        assertNotNull(ApimsJsonMapper.MapReader.buildForMap(objectMapper, EXPECTED_JSON_VALUE));
        assertNotNull(ApimsJsonMapper.MapReader.buildForList(objectMapper, "[" + EXPECTED_JSON_VALUE + "]"));
        assertNotNull(ApimsJsonMapper.MapReader.build(map));
        assertNotNull(ApimsJsonMapper.MapReader.build(list));

        ApimsJsonMapper.MapReader reader = ApimsJsonMapper.MapReader.build(map);
        assertNotNull(reader);
        assertNotNull(reader.getRootObject());
        assertNotNull(reader.getCurrentNodeAsMap());
        assertNotNull(reader.getCurrentNode());
        assertNotNull(reader.newReaderForCurrentNode());
        assertEquals("COMMON_TEST_KEY", reader.getCurrentNodeMapValue("testKey"));
        assertEquals("COMMON_TEST_KEY", reader.getCurrentNodeMapValue("testKey", "default"));
        assertNull(reader.getCurrentNodeMapValue("key"));
        assertEquals("default", reader.getCurrentNodeMapValue("key", "default"));
        assertEquals("default", reader.getCurrentNodeMapValue("object.key", "default"));
    }
}
