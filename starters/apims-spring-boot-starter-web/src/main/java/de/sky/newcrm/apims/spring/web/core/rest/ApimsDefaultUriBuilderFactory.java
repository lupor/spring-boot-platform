/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.rest;

import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import de.sky.newcrm.apims.spring.utils.HttpRequestUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@RequiredArgsConstructor
public class ApimsDefaultUriBuilderFactory extends DefaultUriBuilderFactory {

    private final boolean expandUriVars;
    private final boolean preventDoubleEncoding;

    @Override
    @ApimsReportGeneratedHint
    public URI expand(String uriTemplate, Map<String, ?> uriVars) {
        return expandUriVars ? super.expand(uriTemplate, uriVars) : retrieveURI(uriTemplate);
    }

    @Override
    public URI expand(String uriTemplate, Object... uriVars) {
        return expandUriVars ? super.expand(uriTemplate, uriVars) : retrieveURI(uriTemplate);
    }

    protected URI retrieveURI(String uriTemplate) {
        if (preventDoubleEncoding && HttpRequestUtils.isUrlEncoded(uriTemplate)) {
            return URI.create(uriTemplate);
        }
        return UriComponentsBuilder.fromUriString(uriTemplate).build().encode().toUri();
    }
}
