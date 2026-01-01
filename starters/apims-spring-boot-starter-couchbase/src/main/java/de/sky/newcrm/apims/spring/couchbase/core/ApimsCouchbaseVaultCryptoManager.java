/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.couchbase.core;


import com.couchbase.client.core.encryption.CryptoManager;
import com.couchbase.client.encryption.DefaultCryptoManager;
import com.couchbase.client.encryption.Keyring;
import java.util.*;

import de.sky.newcrm.apims.spring.environment.core.ApimsSpringContext;
import de.sky.newcrm.apims.spring.exceptions.ApimsRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.Ciphertext;
import org.springframework.vault.support.Versioned;

@Slf4j
public class ApimsCouchbaseVaultCryptoManager implements CryptoManager {

    private static final String VAULT_COUCHBASE_DEK_PATH_PLACEHOLDER = "${apims.couchbase.encryption.dek-path}";
    private static final String VAULT_COUCHBASE_KEK_ENABLED_PLACEHOLDER = "${apims.couchbase.encryption.kek.enabled}";
    private static final String VAULT_COUCHBASE_KEK_PATH_PLACEHOLDER = "${apims.couchbase.encryption.kek.path}";
    private final DefaultCryptoManager cryptoManager;

    public ApimsCouchbaseVaultCryptoManager(VaultTemplate vaultTemplate) {
        String couchbaseDekPath = ApimsSpringContext.resolvePlaceholders(VAULT_COUCHBASE_DEK_PATH_PLACEHOLDER);
        String couchbaseKekPath = ApimsSpringContext.resolvePlaceholders(VAULT_COUCHBASE_KEK_PATH_PLACEHOLDER);
        String couchbaseKekEnabled = ApimsSpringContext.resolvePlaceholders(VAULT_COUCHBASE_KEK_ENABLED_PLACEHOLDER);
        VaultKeyRing keyring = new VaultKeyRing(
                vaultTemplate, couchbaseDekPath, Boolean.parseBoolean(couchbaseKekEnabled) ? couchbaseKekPath : null);

        ApimsAes256GcmProvider provider =
                ApimsAes256GcmProvider.builder().keyring(keyring).build();
        cryptoManager = DefaultCryptoManager.builder()
                .decrypter(provider.decrypter())
                .defaultEncrypter(provider.encrypterForKey(keyring.getEncKeyId()))
                .build();
    }

    @Override
    public Map<String, Object> encrypt(byte[] plaintext, String encrypterAlias) {
        return cryptoManager.encrypt(plaintext, encrypterAlias);
    }

    @Override
    public byte[] decrypt(Map<String, Object> encryptedNode) {
        return cryptoManager.decrypt(encryptedNode);
    }

    static class VaultKeyRing implements Keyring {

        private final Map<String, Key> couchbaseKeys = new HashMap<>();
        private final VaultTemplate vaultTemplate;
        private final String dekPath;
        private final String kekName;
        private final String kekEngine;

        public VaultKeyRing(VaultTemplate vaultTemplate, String couchbaseDekPath, String couchbaseKekPath) {
            this.vaultTemplate = vaultTemplate;
            this.dekPath = couchbaseDekPath;
            if (couchbaseKekPath != null) {
                String[] kekParts = couchbaseKekPath.split("/");
                this.kekName = kekParts[1];
                this.kekEngine = kekParts[0];
            } else {
                this.kekName = null;
                this.kekEngine = null;
            }

            Optional.ofNullable(loadVaultKey(Versioned.Version.unversioned(), false))
                    .ifPresent(vaultData -> couchbaseKeys.put(vaultData.getKey(), vaultData.getValue()));
        }

        private Map.Entry<String, Key> loadVaultKey(Versioned.Version version, boolean required) {
            Versioned<Map<String, Object>> couchbaseKey =
                    vaultTemplate.opsForVersionedKeyValue("apims").get(dekPath, version);
            if (couchbaseKey == null) {
                if (required) {
                    throw new IllegalStateException(String.format(
                            "Vault key not found for secret: '%s' and version: %d", dekPath, version.getVersion()));
                } else {
                    log.warn("No key found in Vault for secret: '{}'", dekPath);
                    return null;
                }
            }

            int keyVersion = couchbaseKey.getVersion().getVersion();
            String keyField = couchbaseKey.getData().keySet().stream()
                    .findFirst()
                    .orElseThrow(() -> new ApimsRuntimeException("No key field found in Vault for secret: " + dekPath));
            String keyData = String.valueOf(couchbaseKey.getData().get(keyField));
            byte[] decryptedKeyData = null;

            // decrypt DEK with KEK if configured
            if (kekEngine != null) {
                decryptedKeyData = vaultTemplate
                        .opsForTransit(kekEngine)
                        .decrypt(kekName, Ciphertext.of(keyData))
                        .getPlaintext();
            } else {
                decryptedKeyData = Base64.getDecoder().decode(keyData);
            }

            String keyNameAndVersion = createKeyName(keyField, keyVersion);
            return Map.entry(keyNameAndVersion, Key.create(keyNameAndVersion, decryptedKeyData));
        }

        private String createKeyName(String keyName, int keyVersion) {
            return String.format("%s:%d", keyName, keyVersion);
        }

        @Override
        public Optional<Key> get(String keyId) {
            Assert.notNull(keyId, "keyId must not be null");
            return Optional.of(couchbaseKeys.computeIfAbsent(keyId, key -> {
                // lazy load key from Vault
                String[] parts = key.split(":");
                if (parts.length < 2) {
                    throw new ApimsRuntimeException("Malformed key format: '" + key + "'. Expected format 'name:version'.");
                }
                int version;
                try {
                    version = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    throw new ApimsRuntimeException("Invalid key version in key: '" + key + "'.", e);
                }
                return Objects.requireNonNull(loadVaultKey(Versioned.Version.from(version), true))
                        .getValue();
            }));
        }

        public String getEncKeyId() {
            return couchbaseKeys.values().stream().findFirst().map(Key::id).orElse(null);
        }
    }
}
