/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.environment.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ApimsAppDomainEnum {
    UNKNOWN("UNKNOWN"),
    NO_DOMAIN(""),
    DEFAULT("default"),
    ACCOUNT("account"),
    BILLING_REVENUE("billing-revenue"),
    CAIF("caif"),
    CAMPAIGN("campaign"),
    CARE("care"),
    CONTENT("content"),
    CONTRACT("contract"),
    COMMON("common"),
    DAZN("dazn"),
    EXAMPLE("example"),
    FRAUD("fraud"),
    IDENTITY("identity"),
    INTERACTION_CHANNELS("interaction-channels"),
    LOGISTICS("logistics"),
    NETFLIX("netflix"),
    ORDER_ENTRY("order-entry"),
    ORDER_MANAGEMENT("order-management"),
    PAYMENT("payment"),
    PRODUCT_OFFER("product-offer"),
    REWARDS("rewards"),
    RISK("risk"),
    SKY_GO("sky-go"),
    SKY_PIL("sky-pil"),
    TOOLS("tools"),
    TRANSCOMM("transcomm");

    private final String value;

    ApimsAppDomainEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @JsonCreator
    public static ApimsAppDomainEnum fromValue(String value) {
        for (ApimsAppDomainEnum b : ApimsAppDomainEnum.values()) {
            if (b.name().equalsIgnoreCase(value) || b.getValue().equalsIgnoreCase(value)) {
                return b;
            }
        }
        return UNKNOWN;
    }
}
