/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.couchbase.core;

import static java.util.Objects.requireNonNull;

import com.couchbase.client.encryption.Decrypter;
import com.couchbase.client.encryption.Encrypter;
import com.couchbase.client.encryption.EncryptionResult;
import com.couchbase.client.encryption.Keyring;
import com.couchbase.client.encryption.internal.Zeroizer;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Provider for AES-256 in GCM (Galois/Counter Mode) mode.
 * <p>
 * This is the modern recommended mode for authenticated encryption.
 * It provides both confidentiality and integrity/authenticity.
 * <p>
 * Requires a key size of 32 bytes (256 bits).
 * <p>
 * Create and configure a provider instance using the static
 * {@link #builder()} method.
 * <p>
 * The provider instance is a factory for a {@link Decrypter} and
 * associated {@link Encrypter}s, which can be created by calling
 * {@link #decrypter()} and {@link #encrypterForKey(String)}.
 */
public class ApimsAes256GcmProvider {
    private static final String ALGORITHM = "AES_256_GCM"; // A logical identifier for the algorithm
    private static final String JCA_ALGORITHM = "AES/GCM/NoPadding"; // JCA algorithm string

    // GCM requires a 96-bit (12-byte) IV.
    // The authentication tag length is typically 128 bits (16 bytes).
    private static final int GCM_IV_LENGTH_BYTES = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128; // Standard 128 bits for AES-GCM tag

    private final Aes256GcmCipher cipher;
    private final Keyring keyring;

    /**
     * Returns a builder for configuring new provider instances.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Keyring keyring;

        /**
         * Sets the keyring for obtaining data encryption keys.
         * <p>
         * Required.
         */
        public Builder keyring(Keyring keyring) {
            this.keyring = requireNonNull(keyring);
            return this;
        }

        public ApimsAes256GcmProvider build() {
            if (keyring == null) {
                throw new IllegalStateException("Keyring not set.");
            }
            return new ApimsAes256GcmProvider(new Aes256GcmCipher(), keyring);
        }
    }

    private ApimsAes256GcmProvider(Aes256GcmCipher cipher, Keyring keyring) {
        this.cipher = requireNonNull(cipher);
        this.keyring = requireNonNull(keyring);
    }

    /**
     * Returns a new encrypter that uses the encryption key with the given name.
     */
    public Encrypter encrypterForKey(String keyName) {
        return plaintext -> {
            try (Zeroizer zeroizer = new Zeroizer()) {
                final Keyring.Key key = keyring.getOrThrow(keyName);
                if (key.bytes().length * 8 != 256) {
                    throw new IllegalArgumentException("DEK key for AES-256 GCM must be 256 bits (32 bytes). Found "
                            + (key.bytes().length * 8) + " bits for key: " + key.id());
                }

                byte[] iv = cipher.generateIv(); // Generate a unique IV for each encryption

                byte[] ciphertextWithTag = cipher.encrypt(zeroizer.add(key.bytes()), plaintext, iv);

                // EncryptionResult will store 'kid', Base64 encoded 'iv', and Base64 encoded 'encrypted' (ciphertext +
                // tag)
                return EncryptionResult.forAlgorithm(ALGORITHM)
                        .put("kid", key.id())
                        .put("iv", iv) // Store Base64 encoded IV
                        .put("encrypted", ciphertextWithTag); // Store Base64 encoded ciphertext+tag
            }
        };
    }

    /**
     * Returns a new decrypter for this algorithm.
     */
    public Decrypter decrypter() {
        return new Decrypter() {
            @Override
            public String algorithm() {
                return ALGORITHM;
            }

            @Override
            public byte[] decrypt(EncryptionResult encrypted) throws Exception {
                try (Zeroizer zeroizer = new Zeroizer()) {
                    // Extract required fields from EncryptionResult
                    final String kid = encrypted.getString("kid");
                    final byte[] iv = encrypted.getBytes("iv");
                    final byte[] ciphertextWithTag = encrypted.getBytes("encrypted");

                    final Keyring.Key key = keyring.getOrThrow(kid);
                    if (key.bytes().length * 8 != 256) {
                        throw new IllegalArgumentException("DEK key for AES-256 GCM must be 256 bits (32 bytes). Found "
                                + (key.bytes().length * 8) + " bits for key: " + key.id());
                    }
                    if (iv.length != GCM_IV_LENGTH_BYTES) {
                        throw new IllegalArgumentException("Invalid IV length for AES-256 GCM. Expected "
                                + GCM_IV_LENGTH_BYTES + " bytes, got " + iv.length + " bytes for key: " + key.id());
                    }

                    // Perform decryption
                    return cipher.decrypt(zeroizer.add(key.bytes()), ciphertextWithTag, iv);
                }
            }
        };
    }

    /**
     * Internal class handling the raw AES-256 GCM crypto operations.
     */
    private static class Aes256GcmCipher {
        private final SecureRandom secureRandom = new SecureRandom();

        public Aes256GcmCipher() {
            // no special initialization
        }

        public byte[] generateIv() {
            byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
            secureRandom.nextBytes(iv);
            return iv;
        }

        public byte[] encrypt(byte[] keyBytes, byte[] plaintext, byte[] iv) throws Exception {
            SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance(JCA_ALGORITHM);

            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);
            return cipher.doFinal(plaintext);
        }

        public byte[] decrypt(byte[] keyBytes, byte[] ciphertextWithTag, byte[] iv) throws Exception {
            SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance(JCA_ALGORITHM);

            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);
            return cipher.doFinal(ciphertextWithTag);
        }
    }
}
