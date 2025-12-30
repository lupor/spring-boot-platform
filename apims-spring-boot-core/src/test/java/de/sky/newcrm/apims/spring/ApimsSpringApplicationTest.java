/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring;

import de.sky.newcrm.apims.spring.context.core.ApimsSpringApplication;
import org.junit.jupiter.api.Test;

import static de.sky.newcrm.apims.spring.context.core.ApimsSpringApplication.MAIN_NOP_START_ARG;
import static org.junit.jupiter.api.Assertions.*;

class ApimsSpringApplicationTest {

    @Test
    void applicationTest() {
        System.setProperty("apims.user.timezone", "UTC");
        ApimsSpringApplication.init();
        ApimsSpringApplication app = new ApimsSpringApplication() {};
        assertNotNull(app);
        assertThrows(IllegalArgumentException.class, () -> ApimsSpringApplication.run(null, null));
        assertThrows(IllegalArgumentException.class, () -> ApimsSpringApplication.run(null, new String[] {}));
        assertThrows(
                IllegalArgumentException.class,
                () -> ApimsSpringApplication.run(null, new String[] {"args1=1", "args2=2"}));
        assertDoesNotThrow(() -> ApimsSpringApplication.run(null, new String[] {MAIN_NOP_START_ARG}));
    }
}
