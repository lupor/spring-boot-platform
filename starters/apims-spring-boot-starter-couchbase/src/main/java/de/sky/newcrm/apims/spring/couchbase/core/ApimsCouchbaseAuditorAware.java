/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.couchbase.core;


import java.util.Optional;
import org.springframework.data.domain.AuditorAware;

public class ApimsCouchbaseAuditorAware implements AuditorAware<String> {
    private String auditor = "auditor";

    public String getAuditor() {
        return auditor;
    }

    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(auditor);
    }
}
