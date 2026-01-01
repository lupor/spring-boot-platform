/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.couchbase.core;


import java.util.Collections;
import org.springframework.data.convert.DefaultTypeMapper;
import org.springframework.data.convert.TypeAliasAccessor;
import org.springframework.data.core.TypeInformation;
import org.springframework.data.couchbase.core.convert.CouchbaseTypeMapper;
import org.springframework.data.couchbase.core.convert.TypeAwareTypeInformationMapper;
import org.springframework.data.couchbase.core.mapping.CouchbaseDocument;
import org.springframework.data.mapping.Alias;
import org.springframework.util.StringUtils;

public class ApimsCouchbaseTypeMapper extends DefaultTypeMapper<CouchbaseDocument> implements CouchbaseTypeMapper {

    private final String typeKey;
    private final String typeValue;

    public ApimsCouchbaseTypeMapper(String typeKey, String writeTypeKey, String typeValue) {
        super(
                new CouchbaseDocumentTypeAliasAccessor(typeKey, writeTypeKey, typeValue),
                null,
                Collections.singletonList(new TypeAwareTypeInformationMapper()));
        this.typeKey = typeKey;
        this.typeValue = typeValue;
    }

    @Override
    public String getTypeKey() {
        return typeKey;
    }

    public static final class CouchbaseDocumentTypeAliasAccessor implements TypeAliasAccessor<CouchbaseDocument> {

        private final String typeKey;
        private final String writeTypeKey;
        private final String typeValue;

        public CouchbaseDocumentTypeAliasAccessor(final String typeKey, String writeTypeKey, String typeValue) {
            this.typeKey = typeKey;
            this.writeTypeKey = writeTypeKey;
            this.typeValue = typeValue;
        }

        @Override
        public Alias readAliasFrom(final CouchbaseDocument source) {
            if (!StringUtils.hasLength(typeKey)) {
                return Alias.NONE;
            } else if (StringUtils.hasLength(typeValue)) {
                return Alias.of(typeValue);
            }
            return Alias.ofNullable(source.get(typeKey));
        }

        @Override
        public void writeTypeTo(final CouchbaseDocument sink, final Object alias) {
            if (writeTypeKey != null) {
                sink.put(writeTypeKey, StringUtils.hasLength(typeValue) ? typeValue : alias);
            } else if (typeKey != null) {
                sink.put(typeKey, StringUtils.hasLength(typeValue) ? typeValue : alias);
            }
        }
    }

    @Override
    public Alias getTypeAlias(TypeInformation<?> info) {
        return StringUtils.hasLength(typeValue) ? Alias.of(typeValue) : getAliasFor(info);
    }
}
