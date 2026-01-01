/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.web.client;

import de.sky.newcrm.apims.spring.core.support.report.ApimsReportGeneratedHint;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.HeaderElement;
import org.apache.hc.core5.http.message.BasicHeaderElementIterator;
import org.apache.hc.core5.util.TimeValue;
import org.apache.http.protocol.HTTP;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@SuppressWarnings({"java:S6212"})
public class ApimsConnectionKeepAliveStrategy implements org.apache.hc.client5.http.ConnectionKeepAliveStrategy {

    private final long defaultKeepAliveTimeMillis;

    @Override
    @ApimsReportGeneratedHint
    public TimeValue getKeepAliveDuration(
            org.apache.hc.core5.http.HttpResponse response, org.apache.hc.core5.http.protocol.HttpContext context) {
        BasicHeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
        while (it.hasNext()) {
            HeaderElement he = it.next();
            String param = he.getName();
            String value = he.getValue();
            if (value != null && param.equalsIgnoreCase("timeout")) {
                return TimeValue.of(Long.parseLong(value), TimeUnit.SECONDS);
            }
        }
        return TimeValue.of(defaultKeepAliveTimeMillis, TimeUnit.MILLISECONDS);
    }
}
