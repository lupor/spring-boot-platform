/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Random;
import java.util.random.RandomGenerator;
import org.springframework.util.StringUtils;

@SuppressWarnings({"java:S112", "java:S5164"})
public abstract class IdUtils {

    private static final ThreadLocal<char[]> PARSE_BUFFER = new ThreadLocal<>();
    private static final RandomGenerator RANDOM_GENERATOR = new Random();

    private static final char[] HEX_DIGITS = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    private IdUtils() {}

    @SuppressWarnings("unchecked")
    public static long nextLong() {
        Long nextId = null;
        while (nextId == null || nextId == 0L) {
            nextId = RANDOM_GENERATOR.nextLong();
        }
        return nextId;
    }

    public static String nextId() {
        return nextId(true);
    }

    public static String nextId(boolean timeBased) {
        return timeBased ? toLowerHex(Instant.now().toEpochMilli(), nextLong()) : toLowerHex(nextLong());
    }

    public static String toLowerHex(long value) {
        char[] data = parseBuffer();
        writeHexLong(data, 0, value);
        return new String(data, 0, 16);
    }

    public static String toLowerHex(long value1, long value2) {
        char[] data = parseBuffer();
        writeHexLong(data, 0, value1);
        writeHexLong(data, 16, value2);
        return new String(data, 0, 32);
    }

    public static String hexToAscii(String value) {
        if (value == null) {
            return null;
        }
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < value.length(); i += 2) {
            String str = value.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }

    public static String asciiToHex(String value) {
        if (value == null) {
            return null;
        }
        char[] chars = value.toCharArray();
        StringBuilder hex = new StringBuilder(chars.length);
        for (char ch : chars) {
            hex.append(Integer.toHexString(ch));
        }
        return hex.toString();
    }

    public static String encodeId(String value) {
        if (value == null) {
            return null;
        }
        return asciiToHex(new String(Base64.getEncoder().encode(value.getBytes(StandardCharsets.UTF_8))));
    }

    public static String decodeId(String value) {
        if (value == null) {
            return null;
        }
        return new String(Base64.getDecoder().decode(hexToAscii(value)), StandardCharsets.UTF_8);
    }

    public static String removeAndTrimAllWhiteSpaces(String value, boolean toUpperCase, String... removeValues) {
        if (StringUtils.hasLength(value)) {
            if (removeValues != null) {
                for (String c : removeValues) {
                    if (StringUtils.hasLength(c)) {
                        value = value.replace(c, " ");
                    }
                }
            }
            value = StringUtils.trimAllWhitespace(value);
            if (toUpperCase) {
                value = value.toUpperCase();
            }
        }
        return value;
    }

    private static void writeHexLong(char[] data, int pos, long v) {
        writeHexByte(data, pos, (byte) ((v >>> 56L) & 0xff));
        writeHexByte(data, pos + 2, (byte) ((v >>> 48L) & 0xff));
        writeHexByte(data, pos + 4, (byte) ((v >>> 40L) & 0xff));
        writeHexByte(data, pos + 6, (byte) ((v >>> 32L) & 0xff));
        writeHexByte(data, pos + 8, (byte) ((v >>> 24L) & 0xff));
        writeHexByte(data, pos + 10, (byte) ((v >>> 16L) & 0xff));
        writeHexByte(data, pos + 12, (byte) ((v >>> 8L) & 0xff));
        writeHexByte(data, pos + 14, (byte) (v & 0xff));
    }

    private static void writeHexByte(char[] data, int pos, byte b) {
        data[pos] = HEX_DIGITS[(b >> 4) & 0xf];
        data[pos + 1] = HEX_DIGITS[b & 0xf];
    }

    private static char[] parseBuffer() {
        char[] idBuffer = PARSE_BUFFER.get();
        if (idBuffer == null) {
            idBuffer = new char[32];
            PARSE_BUFFER.set(idBuffer);
        }
        return idBuffer;
    }
}
