/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.environment.core;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

class ApimsAppTeamEnumTest {

    @Test
    void teamDomainTest() {
        assertEquals(ApimsAppTeamEnum.UNKNOWN, ApimsAppTeamEnum.findFirstByDomain(null));
        assertEquals(ApimsAppTeamEnum.UNKNOWN, ApimsAppTeamEnum.findFirstByDomain(ApimsAppDomainEnum.NO_DOMAIN));
        assertEquals(ApimsAppTeamEnum.NCE, ApimsAppTeamEnum.findFirstByDomain(ApimsAppDomainEnum.EXAMPLE));
    }

    @Test
    void teamTest() {
        Arrays.stream(ApimsAppTeamEnum.values()).forEach(e -> teamTest(e.name()));
        assertEquals(ApimsAppTeamEnum.UNKNOWN, ApimsAppTeamEnum.fromValue(null));
        assertEquals(ApimsAppTeamEnum.UNKNOWN, ApimsAppTeamEnum.fromValue(""));
        assertEquals(ApimsAppTeamEnum.UNKNOWN, ApimsAppTeamEnum.fromValue("NotExistsTeam"));
    }

    void teamTest(String value) {
        ApimsAppTeamEnum enumValue = ApimsAppTeamEnum.fromValue(value);
        if (!ApimsAppTeamEnum.UNKNOWN.name().equalsIgnoreCase(value)
                && !ApimsAppTeamEnum.UNKNOWN.getDecription().equalsIgnoreCase(value)) {
            assertNotEquals(ApimsAppTeamEnum.UNKNOWN, enumValue);
        }
        assertEquals(enumValue, ApimsAppTeamEnum.fromValue(enumValue.name()));
        assertEquals(enumValue, ApimsAppTeamEnum.fromValue(enumValue.name().toUpperCase()));
        assertNotNull(enumValue.getDomains());
    }
}
