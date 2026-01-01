/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.couchbase.core;


import com.couchbase.client.java.Cluster;
import org.springframework.boot.couchbase.health.CouchbaseHealthIndicator;

public class ApimsCouchbaseHealthIndicator extends CouchbaseHealthIndicator {

    public ApimsCouchbaseHealthIndicator(Cluster cluster) {
        super(cluster);
    }
}
