/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@SuppressWarnings({"java:S6212"})
public abstract class DigestUtils {

    private DigestUtils() {}

    private static final String HMAC_SHA1_ALGORITHM_NAME = "HmacSHA1";
    private static final String MD5_ALGORITHM_NAME = "MD5";
    private static final String SHA512_ALGORITHM_NAME = "SHA-512";

    private static final char[] HEX_CHARS = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    @SuppressWarnings({"java:S112"})
    public static byte[] hmacSha1Digest(byte[] bytes, byte[] secret) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret, HMAC_SHA1_ALGORITHM_NAME);
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM_NAME); // Compliant
            mac.init(secretKeySpec);
            return mac.doFinal(bytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public static String hmacSha1DigestAsBase64(byte[] bytes, byte[] secret) {
        return Base64.getEncoder().encodeToString(hmacSha1Digest(bytes, secret));
    }

    public static byte[] md5Digest(byte[] bytes) {
        return digest(MD5_ALGORITHM_NAME, bytes);
    }

    public static String md5DigestAsHex(byte[] bytes) {
        return digestAsHexString(MD5_ALGORITHM_NAME, bytes);
    }

    public static StringBuilder appendMd5DigestAsHex(byte[] bytes, StringBuilder builder) {
        return appendDigestAsHex(MD5_ALGORITHM_NAME, bytes, builder);
    }

    public static byte[] sha512Digest(byte[] bytes) {
        return digest(SHA512_ALGORITHM_NAME, bytes);
    }

    public static String sha512DigestAsHex(byte[] bytes) {
        return digestAsHexString(SHA512_ALGORITHM_NAME, bytes);
    }

    public static StringBuilder appendSha512DigestAsHex(byte[] bytes, StringBuilder builder) {
        return appendDigestAsHex(SHA512_ALGORITHM_NAME, bytes, builder);
    }

    public static MessageDigest getSha512Digest() {
        return getDigest(SHA512_ALGORITHM_NAME);
    }

    public static MessageDigest getDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Could not find MessageDigest with algorithm \"" + algorithm + "\"", ex);
        }
    }

    public static byte[] digest(String algorithm, byte[] bytes) {
        return getDigest(algorithm).digest(bytes);
    }

    public static String digestAsHexString(String algorithm, byte[] bytes) {
        char[] hexDigest = digestAsHexChars(algorithm, bytes);
        return new String(hexDigest);
    }

    public static StringBuilder appendDigestAsHex(String algorithm, byte[] bytes, StringBuilder builder) {
        char[] hexDigest = digestAsHexChars(algorithm, bytes);
        return builder.append(hexDigest);
    }

    private static char[] digestAsHexChars(String algorithm, byte[] bytes) {
        byte[] digest = digest(algorithm, bytes);
        return encodeHex(digest);
    }

    public static char[] encodeHex(byte[] bytes) {
        char[] chars = new char[32];
        for (int i = 0; i < chars.length; i = i + 2) {
            byte b = bytes[i / 2];
            chars[i] = HEX_CHARS[(b >>> 0x4) & 0xf];
            chars[i + 1] = HEX_CHARS[b & 0xf];
        }
        return chars;
    }
}
