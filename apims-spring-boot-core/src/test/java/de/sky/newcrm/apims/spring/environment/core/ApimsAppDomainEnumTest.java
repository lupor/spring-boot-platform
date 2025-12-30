/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.environment.core;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

class ApimsAppDomainEnumTest {

    @Test
    void domainTest() {
        Arrays.stream(ApimsAppDomainEnum.values()).forEach(e -> domainTest(e.getValue()));
        assertEquals(ApimsAppDomainEnum.UNKNOWN, ApimsAppDomainEnum.fromValue(null));
        assertEquals(ApimsAppDomainEnum.UNKNOWN, ApimsAppDomainEnum.fromValue("NotExistsDoamin"));
    }

    void domainTest(String value) {
        ApimsAppDomainEnum enumValue = ApimsAppDomainEnum.fromValue(value);
        if (!ApimsAppDomainEnum.UNKNOWN.getValue().equalsIgnoreCase(value)) {
            assertNotEquals(ApimsAppDomainEnum.UNKNOWN, enumValue);
        }
        assertEquals(enumValue, ApimsAppDomainEnum.fromValue(enumValue.name()));
        assertEquals(enumValue, ApimsAppDomainEnum.fromValue(enumValue.name().toUpperCase()));
        assertEquals(
                enumValue, ApimsAppDomainEnum.fromValue(enumValue.getValue().toUpperCase()));
        assertEquals(enumValue, ApimsAppDomainEnum.valueOf(enumValue.name()));
        assertNotNull(enumValue.getValue());
        assertEquals(enumValue.getValue(), enumValue.toString());
    }
}
