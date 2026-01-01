/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.aspects;

import de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3.ObjectMapperUtils;
import de.sky.newcrm.apims.spring.serialization.core.masker.ApimsAroundObjectMasker;
import de.sky.newcrm.apims.spring.serialization.core.masker.ApimsAroundObjectMaskerDefaultImpl;
import de.sky.newcrm.apims.spring.serialization.core.serializer.ApimsAroundObjectSerializer;
import de.sky.newcrm.apims.spring.serialization.core.serializer.ApimsAroundObjectSerializerDefaultImpl;
import de.sky.newcrm.apims.spring.serialization.core.serializer.ApimsAroundObjectSerializerTypeHandler;
import de.sky.newcrm.apims.spring.serialization.core.serializer.ApimsAroundObjectSerializerTypeHandlerUri;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@Slf4j
class AspectsSerializerTest {

    private static final String MASK_KEYS_CSV =
            "password, newPassword, oldPassword, new-password, old-password, pin, newPin, oldPin, codepin, role-id, secret-id, token, Token, BearerToken, client_secret, client_token, access_token, role_id, secret_id, ciphertext, signed_string, plaintext, authorization, x-forwarded-client-cert, x-envoy-peer-metadata";
    private static final List<String> MASK_KEYS =
            Arrays.asList(StringUtils.tokenizeToStringArray(MASK_KEYS_CSV, ",", true, true));
    public static final String MASK_VALUE = "___masked___";

    @Test
    void objectMaskerDefaultImplTest() {
        assertEquals(
                1,
                new ApimsAroundObjectMaskerDefaultImpl(List.of("password"), MASK_VALUE)
                        .getMaskKeys()
                        .size());
        assertEquals(MASK_VALUE, new ApimsAroundObjectMaskerDefaultImpl(List.of("password"), null).getMaskValue());
        assertEquals("masked", new ApimsAroundObjectMaskerDefaultImpl(List.of("password"), "masked").getMaskValue());
    }

    @Test
    void uriHandlerTest() throws URISyntaxException, MalformedURLException {
        executeTest(new ApimsAroundObjectSerializerTypeHandlerUri(), new URI("http://test.com/path"));
        executeTest(new ApimsAroundObjectSerializerTypeHandlerUri(), new URI("http://test.com/path").toURL());
    }

    @Test
    void aroundObjectSerializerDefaultImplTest() {
        ApimsAroundObjectMasker masker = new ApimsAroundObjectMaskerDefaultImpl(List.of(MASK_KEYS_CSV), "masked");
        ApimsAroundObjectSerializerDefaultImpl instance = new ApimsAroundObjectSerializerDefaultImpl(
                ObjectMapperUtils.getApimsObjectMapperJson().unwrap(), masker, null, 100);
        assertNotNull(instance.serialize("{\"test\": \"value\"}"));
    }

    protected String executeTest(ApimsAroundObjectSerializerTypeHandler handler, Object object) {
        return executeTest(handler, object, 4000);
    }

    protected String executeTest(ApimsAroundObjectSerializerTypeHandler handler, Object object, int maxLength) {
        ApimsAroundObjectSerializer parent = mock(ApimsAroundObjectSerializer.class);
        lenient().when(parent.serialize(any())).thenReturn("parent-test-result");
        lenient().when(parent.serialize(any(), anyInt())).thenReturn("parent-test-result");
        assertTrue(handler.canHandle(object));
        String result = handler.serialize(parent, object, maxLength);
        log.info("{} : {}", handler.getClass().getSimpleName(), result);
        assertNotNull(result);
        assertNotNull(handler.serialize(parent, object, 0));
        return result;
    }

    @Builder
    @Getter
    @Setter
    private static class TestEntity {
        String key;
        String value;
    }
}
