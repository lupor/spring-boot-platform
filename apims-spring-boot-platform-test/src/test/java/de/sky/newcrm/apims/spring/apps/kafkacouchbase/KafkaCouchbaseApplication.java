/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.apps.kafkacouchbase;

import de.sky.newcrm.apims.spring.context.core.ApimsSpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KafkaCouchbaseApplication {
    public static void main(String[] args) {
        ApimsSpringApplication.run(KafkaCouchbaseApplication.class, args);
    }
}
