/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.web.client;

import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import de.sky.newcrm.apims.spring.scheduling.ApimsAbstractJob;
import de.sky.newcrm.apims.spring.telemetry.logging.core.ApimsAroundLoggingListenerSuppress;
import de.sky.newcrm.apims.spring.telemetry.mdc.core.ApimsMdc;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.core5.pool.PoolStats;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ApimsIdleConnectionJob extends ApimsAbstractJob {

    private final ApimsPoolingHttpClientConnectionManager connectionManager;
    private final long closeIdleConnectionWaitTimeSecs;
    private long lastLeasedConnections = 0;

    @Autowired
    private ApimsMdc mdc;

    public ApimsIdleConnectionJob(
            ApimsPoolingHttpClientConnectionManager connectionManager,
            long closeIdleConnectionWaitTimeSecs,
            boolean enabled) {
        super(enabled);
        this.connectionManager = connectionManager;
        this.closeIdleConnectionWaitTimeSecs = closeIdleConnectionWaitTimeSecs;
    }

    @Scheduled(
            cron = "${apims.scheduling.rest-idle-connection-job.cron-expression:}",
            fixedDelayString = "${apims.scheduling.rest-idle-connection-job.fixed-delay:60000}",
            initialDelayString = "${apims.scheduling.rest-idle-connection-job.initial-delay:60000}")
    @Override
    @ApimsAroundLoggingListenerSuppress(suppressMethodCall = true)
    @ApimsReportGeneratedHint
    public void execute() {
        if (!isEnabled()) {
            log.info("[RESTPOOL] : Job is disabled.");
            return;
        }
        try {
            if (connectionManager != null) {
                mdc.put(ApimsMdc.MDC_LOG_TYPE_KEY, ApimsMdc.MDC_LOG_TYPE_HEALTH);
                PoolStats totalStats = connectionManager.getTotalStats();
                if (log.isTraceEnabled()
                        || (totalStats.getAvailable() == 0
                                && (totalStats.getLeased() != 0 || lastLeasedConnections != 0))) {
                    logTotalStats(totalStats);
                    logRouteStats();
                }
                if (totalStats.getAvailable() == 0) {
                    return;
                }
                if (log.isTraceEnabled()) {
                    log.trace("[RESTPOOL] : run IdleConnectionMonitor - Closing expired and idle connections...");
                }
                connectionManager.closeExpired();
                if (closeIdleConnectionWaitTimeSecs > 0 && totalStats.getAvailable() != 0) {
                    connectionManager.closeIdle(TimeValue.of(closeIdleConnectionWaitTimeSecs, TimeUnit.SECONDS));
                }
                totalStats = connectionManager.getTotalStats();
                lastLeasedConnections = totalStats.getLeased();
                logTotalStats(totalStats);
                logRouteStats();
            }
        } catch (Exception e) {
            log.warn("[RESTPOOL] : run IdleConnectionMonitor - Exception occurred. msg={}", e.getMessage());
        } finally {
            mdc.remove(ApimsMdc.MDC_LOG_TYPE_KEY);
        }
    }

    @ApimsReportGeneratedHint
    private void logTotalStats(PoolStats totalStats) {
        if (log.isInfoEnabled() && totalStats != null) {
            log.info(
                    "[RESTPOOL] : total available: {}, leased: {}, allocated: {} of {}",
                    totalStats.getAvailable(),
                    totalStats.getLeased(),
                    totalStats.getLeased() + totalStats.getAvailable(),
                    totalStats.getMax());
        }
    }

    @ApimsReportGeneratedHint
    private void logRouteStats() {
        if (log.isInfoEnabled()) {
            for (HttpRoute route : connectionManager.getRoutes()) {
                logRouteStats(route);
            }
        }
    }

    @ApimsReportGeneratedHint
    private void logRouteStats(final HttpRoute route) {
        if (log.isInfoEnabled() && route != null) {
            final PoolStats routeStats = connectionManager.getStats(route);
            log.info(
                    "[RESTPOOL] : route '{}', available: {}, leased: {}, allocated: {} of {}",
                    route.getTargetHost(),
                    routeStats.getAvailable(),
                    routeStats.getLeased(),
                    routeStats.getLeased() + routeStats.getAvailable(),
                    routeStats.getMax());
        }
    }
}
