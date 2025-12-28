/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.environment.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;

public enum ApimsAppTeamEnum {
    UNKNOWN("UNKNOWN"),
    ACC1("Account", ApimsAppDomainEnum.ACCOUNT, ApimsAppDomainEnum.IDENTITY),
    ASST("Assets", ApimsAppDomainEnum.CONTRACT),
    BILL("Billing & Collection", ApimsAppDomainEnum.BILLING_REVENUE),
    BOONE("Basket & Order 1", ApimsAppDomainEnum.ORDER_ENTRY),
    BOTWO("Basket & Order 2", ApimsAppDomainEnum.PRODUCT_OFFER, ApimsAppDomainEnum.REWARDS),
    CAMPB("Campaigns Blue", ApimsAppDomainEnum.CAMPAIGN, ApimsAppDomainEnum.TRANSCOMM),
    CARE1("Care", ApimsAppDomainEnum.CARE),
    CHAN2("Interaction Channels", ApimsAppDomainEnum.INTERACTION_CHANNELS),
    FULF1(
            "Fulfillment",
            ApimsAppDomainEnum.CAIF,
            ApimsAppDomainEnum.DAZN,
            ApimsAppDomainEnum.LOGISTICS,
            ApimsAppDomainEnum.NETFLIX,
            ApimsAppDomainEnum.ORDER_MANAGEMENT,
            ApimsAppDomainEnum.SKY_GO,
            ApimsAppDomainEnum.SKY_PIL),
    NCE("Expandables", ApimsAppDomainEnum.COMMON, ApimsAppDomainEnum.EXAMPLE, ApimsAppDomainEnum.TOOLS),
    PRM("Pay & Risk Management", ApimsAppDomainEnum.FRAUD, ApimsAppDomainEnum.RISK),
    REV("Revenue Management", ApimsAppDomainEnum.PAYMENT);

    @Getter
    private final String decription;

    private final ApimsAppDomainEnum[] domains;

    ApimsAppTeamEnum(String decription, ApimsAppDomainEnum... domains) {
        this.decription = decription;
        this.domains = domains;
    }

    public List<ApimsAppDomainEnum> getDomains() {
        return Arrays.stream(domains).toList();
    }

    @JsonCreator
    public static ApimsAppTeamEnum fromValue(String value) {
        for (ApimsAppTeamEnum b : ApimsAppTeamEnum.values()) {
            if (b.name().equalsIgnoreCase(value) || b.decription.equalsIgnoreCase(value)) {
                return b;
            }
        }
        return UNKNOWN;
    }

    public static ApimsAppTeamEnum findFirstByDomain(ApimsAppDomainEnum domainEnum) {
        List<ApimsAppTeamEnum> list = findByDomain(domainEnum);
        return list.isEmpty() ? UNKNOWN : list.getFirst();
    }

    public static List<ApimsAppTeamEnum> findByDomain(ApimsAppDomainEnum domainEnum) {
        List<ApimsAppTeamEnum> list = new ArrayList<>();
        if (domainEnum != null && !ApimsAppDomainEnum.UNKNOWN.equals(domainEnum)) {
            for (ApimsAppTeamEnum b : ApimsAppTeamEnum.values()) {
                if (b.getDomains().contains(domainEnum)) {
                    list.add(b);
                }
            }
        }
        return list;
    }
}
