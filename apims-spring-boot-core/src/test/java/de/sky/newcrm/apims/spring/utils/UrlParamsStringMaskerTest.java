/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.text.MessageFormat;
import org.junit.jupiter.api.Test;

class UrlParamsStringMaskerTest {

    @Test
    void commonTest() {
        String maskValue = "___masked_by_test___";
        String urlPattern =
                "https://my-test-key1-domain.com/services/oauth2/token?client_id={0}&client_secret={1}&username={2}&password={3}";
        String url = MessageFormat.format(
                urlPattern,
                "3MVG96mGXeuuwTZiNeAhpfAiHTLhKKxDLGsjlX6AqITPkvGo23yWwWYANBiUxz2Dl7RfjDo2G6qvtad_A1RS7",
                "6B861844CB9BBCE8DDF1298734DB9369A637AE993A9730133F7E5BD5CFA4BE91",
                "apims-user%40test.de.devsf1",
                "password");
        String expectedUrl = MessageFormat.format(
                urlPattern,
                "3MVG96mGXeuuwTZiNeAhpfAiHTLhKKxDLGsjlX6AqITPkvGo23yWwWYANBiUxz2Dl7RfjDo2G6qvtad_A1RS7",
                maskValue,
                "apims-user%40test.de.devsf1",
                maskValue);
        String resultUrl = UrlParamsStringMasker.mask(url, maskValue, "client_secret", "password");
        assertNotNull(resultUrl);
        assertEquals(expectedUrl, resultUrl);
    }
}
