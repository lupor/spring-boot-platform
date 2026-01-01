/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.couchbase.core;


import com.couchbase.client.core.encryption.CryptoManager;
import com.couchbase.client.encryption.EncryptionResult;
import java.util.Base64;
import java.util.Map;

public class ApimsCouchbasePlainCryptoManager implements CryptoManager {

    @Override
    public Map<String, Object> encrypt(byte[] plaintext, String encrypterAlias) {
        return EncryptionResult.forAlgorithm("base64")
                .put("encrypted", Base64.getEncoder().encodeToString(plaintext))
                .put("kid", "none")
                .asMap();
    }

    @Override
    public byte[] decrypt(Map<String, Object> encryptedNode) {
        return "none".equals(encryptedNode.get("kid"))
                ? Base64.getDecoder()
                        .decode(EncryptionResult.fromMap(encryptedNode).getString("encrypted"))
                : "\"*****\"".getBytes();
    }
}
