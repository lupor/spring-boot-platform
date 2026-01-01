/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.couchbase.core;


import com.couchbase.client.core.encryption.CryptoManager;
import com.couchbase.client.java.codec.JacksonJsonSerializer;
import com.couchbase.client.java.encryption.databind.jackson.EncryptionModule;
import com.couchbase.client.java.json.JsonValueModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.sky.newcrm.apims.spring.serialization.core.mapper.legacy.module.ApimsModelEnumModule;

public class ApimsCouchbaseSerializerFactory {

    private final CryptoManager cryptoManager;
    private final ObjectMapper couchbaseObjectMapper;
    private final JacksonJsonSerializer couchbaseJsonSerializer;

    public ApimsCouchbaseSerializerFactory() {
        this(null);
    }

    public ApimsCouchbaseSerializerFactory(CryptoManager cryptoManager) {
        this.cryptoManager = cryptoManager;
        couchbaseObjectMapper = createCouchbaseObjectMapper();
        couchbaseJsonSerializer = JacksonJsonSerializer.create(couchbaseObjectMapper);
    }

    public CryptoManager getCryptoManager() {
        return cryptoManager;
    }

    public ObjectMapper getCouchbaseObjectMapper() {
        return couchbaseObjectMapper;
    }

    public JacksonJsonSerializer getCouchbaseJsonSerializer() {
        return couchbaseJsonSerializer;
    }

    @SuppressWarnings({"java:S1117"})
    public ObjectMapper createCouchbaseObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new ApimsModelEnumModule());
        mapper.registerModule(new JsonValueModule());
        CryptoManager cryptoManager = getCryptoManager();
        if (cryptoManager != null) {
            mapper.registerModule(new EncryptionModule(cryptoManager));
        }
        return mapper;
    }
}
