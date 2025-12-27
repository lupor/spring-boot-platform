/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.telemetry.metrics.core;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.util.StringUtils;

public class ApimsMeterRegistryConfigurer {

    public MeterRegistry configure(MeterRegistry meterRegistry, Map<String, String> commonTags) {

        List<Tag> tags = new ArrayList<>();
        for (Map.Entry<String, String> entry : commonTags.entrySet()) {
            tags.add(Tag.of(entry.getKey(), resolveTagValue(entry.getKey(), entry.getValue())));
        }
        meterRegistry.config().commonTags(tags);
        return meterRegistry;
    }

    protected String resolveTagValue(String key, String value) {
        return !StringUtils.hasLength(value) && "apims_domain".equals(key) ? "default" : value;
    }
}
