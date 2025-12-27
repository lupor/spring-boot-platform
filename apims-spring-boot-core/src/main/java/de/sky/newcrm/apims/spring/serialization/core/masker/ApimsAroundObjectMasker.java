/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core.masker;

import java.util.List;

public interface ApimsAroundObjectMasker {

    List<String> getMaskKeys();

    String getMaskValue();

    String maskJsonValue(String source);

    String maskUrlParamsValue(String source);
}
