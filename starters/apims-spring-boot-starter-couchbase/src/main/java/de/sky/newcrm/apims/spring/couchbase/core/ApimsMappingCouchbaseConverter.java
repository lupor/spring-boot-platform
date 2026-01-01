/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.couchbase.core;


import org.springframework.data.couchbase.core.convert.MappingCouchbaseConverter;
import org.springframework.data.couchbase.core.mapping.CouchbasePersistentEntity;
import org.springframework.data.couchbase.core.mapping.CouchbasePersistentProperty;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.util.StringUtils;

public class ApimsMappingCouchbaseConverter extends MappingCouchbaseConverter {

    public ApimsMappingCouchbaseConverter(
            MappingContext<? extends CouchbasePersistentEntity<?>, CouchbasePersistentProperty> mappingContext,
            String typeKey,
            String typeValue) {
        super(mappingContext, typeKey);
        typeMapper = new ApimsCouchbaseTypeMapper(
                typeKey != null ? typeKey : TYPEKEY_DEFAULT,
                StringUtils.hasLength(typeKey) ? typeKey : TYPEKEY_DEFAULT,
                typeValue);
    }
}
