/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.couchbase.core.converter;

import java.util.Date;

import de.sky.newcrm.apims.spring.utils.DateTimeUtc;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class ApimsDateToStringCouchbaseConverter implements ApimsCouchbaseConverter<Date, String> {

    @Override
    public String convert(Date source) {
        return DateTimeUtc.format(source, DateTimeUtc.ISO.DATE_TIME);
    }
}
