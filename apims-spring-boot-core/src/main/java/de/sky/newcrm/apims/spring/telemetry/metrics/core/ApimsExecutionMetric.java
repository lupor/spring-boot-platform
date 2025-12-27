/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.telemetry.metrics.core;

import io.micrometer.core.instrument.Tag;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApimsExecutionMetric {

    @Builder.Default
    private String componentName = "";

    @Builder.Default
    private String componentFullName = "";

    @Builder.Default
    private String componentType = "";

    @Builder.Default
    private String componmentMethod = "";

    @Builder.Default
    private String requestTag = "";

    @Builder.Default
    private String metricType = "UNKNOWN";

    @Builder.Default
    private boolean resultOK = true;

    private boolean resultFailed;

    @Builder.Default
    private String resultTag = "";

    public List<Tag> calculateTags() {
        List<Tag> list = new ArrayList<>();
        list.add(Tag.of(getTagName("component_name"), getTagValue(getComponentName())));
        list.add(Tag.of(getTagName("component_full_name"), getTagValue(getComponentFullName())));
        list.add(Tag.of(getTagName("component_type"), getTagValue(getComponentType())));
        list.add(Tag.of(getTagName("component_method"), getTagValue(getComponmentMethod())));
        list.add(Tag.of(getTagName("request_tag"), getTagValue(getRequestTag())));
        list.add(Tag.of(getTagName("result"), getTagValue(isResultFailed() ? "ERROR" : "OK")));
        list.add(Tag.of(getTagName("result_tag"), getTagValue(getResultTag())));
        list.add(Tag.of(getTagName("metric_type"), getTagValue(getMetricType())));
        return list;
    }

    protected String getTagName(String key) {
        return "apims_" + key;
    }

    protected String getTagValue(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
