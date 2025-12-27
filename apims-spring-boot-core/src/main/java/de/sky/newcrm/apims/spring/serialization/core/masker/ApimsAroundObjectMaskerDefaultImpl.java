/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core.masker;

import de.sky.newcrm.apims.spring.utils.JSONStringMasker;
import de.sky.newcrm.apims.spring.utils.UrlParamsStringMasker;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class ApimsAroundObjectMaskerDefaultImpl implements ApimsAroundObjectMasker {

    public static final String DEFAULT_MASK_VALUE = "___masked___";
    private final List<String> maskKeys;
    private final String maskValue;

    @Override
    public List<String> getMaskKeys() {
        return maskKeys;
    }

    @Override
    public String getMaskValue() {
        return StringUtils.hasLength(maskValue) ? maskValue : DEFAULT_MASK_VALUE;
    }

    @Override
    public String maskJsonValue(String source) {
        return JSONStringMasker.mask(source, getMaskValue(), maskKeys);
    }

    @Override
    public String maskUrlParamsValue(String source) {
        return UrlParamsStringMasker.mask(source, getMaskValue(), maskKeys);
    }
}
