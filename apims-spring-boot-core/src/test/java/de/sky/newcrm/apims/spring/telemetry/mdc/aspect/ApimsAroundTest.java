/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.telemetry.mdc.aspect;

import de.sky.newcrm.apims.spring.telemetry.mdc.core.ApimsMdc;
import de.sky.newcrm.apims.spring.utils.ObjectUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static de.sky.newcrm.apims.spring.environment.core.ApimsMockUtils.injectField;
import static org.junit.jupiter.api.Assertions.*;

class ApimsAroundTest {
    Map<String, String> globalFields = Map.of("team", "NCE");
    ApimsMdc apimsMdc = new ApimsMdc(globalFields);


    @Test
    void apimsAroundMdcListenerTest() throws Exception {

        Map<String, String> remoteFields = Map.of("correlationid", "correlationId", "customerid", "customerId", "middlewareid", "middlewareId");
        injectField(apimsMdc, "mdcPrefix", "apims.");
        injectField(apimsMdc, "globalFieldsMdcPrefix", "apims.");
        apimsMdc.afterPropertiesSet();
        apimsMdc.putGlobalFields();
        ApimsAroundMdcListener aroundMdcListener = new ApimsAroundMdcListener(remoteFields);
        injectField(aroundMdcListener, "mdc", apimsMdc);
        aroundMdcListener.afterPropertiesSet();

        Map<String, Object> flattenMap = Map.of("correlationid", "TestCorrelationId", "subpath.customerid", "TestCustomerId", "subpath.property", "TestProperty");
        aroundMdcListener.saveMdc(flattenMap);
        Map<String, String> savedData = ObjectUtils.invokeMethod(ObjectUtils.findMethod(ApimsMdc.class, "getCopyOfContextMap"), apimsMdc);
        assertNotNull(savedData);
        assertFalse(savedData.isEmpty());
        assertEquals(3, savedData.size());
        assertEquals("TestCorrelationId", savedData.get("apims.correlationid"));
        assertEquals("NCE", savedData.get("apims.team"));
        assertEquals("TestCustomerId", savedData.get("apims.customerid"));
        savedData = ObjectUtils.invokeMethod(ObjectUtils.findMethod(ApimsMdc.class, "getApimsMap"), apimsMdc);
        assertNotNull(savedData);
        assertFalse(savedData.isEmpty());
        assertEquals(3, savedData.size());
        assertEquals("TestCorrelationId", savedData.get("apims.correlationid"));
        assertEquals("NCE", savedData.get("apims.team"));
        assertEquals("TestCustomerId", savedData.get("apims.customerid"));
        apimsMdc.removeAllApimsValues();
    }
}
