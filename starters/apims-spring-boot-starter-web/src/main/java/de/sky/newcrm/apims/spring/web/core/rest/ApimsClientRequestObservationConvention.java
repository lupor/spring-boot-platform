/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.rest;

import io.micrometer.common.KeyValues;
import org.springframework.http.client.observation.ClientHttpObservationDocumentation;
import org.springframework.http.client.observation.ClientRequestObservationContext;
import org.springframework.http.client.observation.DefaultClientRequestObservationConvention;

public class ApimsClientRequestObservationConvention extends DefaultClientRequestObservationConvention {
    private final boolean highCardinalityUri;

    public ApimsClientRequestObservationConvention(boolean highCardinalityUri) {
        this.highCardinalityUri = highCardinalityUri;
    }

    @Override
    public KeyValues getLowCardinalityKeyValues(ClientRequestObservationContext context) {
        final KeyValues lowCardinalityKeyValues = super.getLowCardinalityKeyValues(context);
        return highCardinalityUri
                ? KeyValues.of(lowCardinalityKeyValues.stream()
                        .filter(e -> !ClientHttpObservationDocumentation.LowCardinalityKeyNames.URI
                                .asString()
                                .equals(e.getKey()))
                        .toList())
                : lowCardinalityKeyValues;
    }

    public KeyValues getHighCardinalityKeyValues(ClientRequestObservationContext context) {
        final KeyValues highCardinalityKeyValues = super.getHighCardinalityKeyValues(context);
        return highCardinalityUri ? KeyValues.of(highCardinalityKeyValues.and(uri(context))) : highCardinalityKeyValues;
    }
}
