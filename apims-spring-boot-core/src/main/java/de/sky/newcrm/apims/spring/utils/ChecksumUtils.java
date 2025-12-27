/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.util.Assert;

@SuppressWarnings({"java:S6212"})
public interface ChecksumUtils {

    static String calculateChecksum(File file) throws IOException {
        return calculateChecksum(file, true, false, true);
    }

    static String calculateChecksum(
            File file, boolean includePathAndFileName, boolean includeLastChanged, boolean includeFileContent)
            throws IOException {
        return calculateChecksum(
                file, includePathAndFileName, includePathAndFileName, includeLastChanged, includeFileContent);
    }

    static String calculateChecksum(
            File file,
            boolean includePathName,
            boolean includeFileName,
            boolean includeLastChanged,
            boolean includeFileContent)
            throws IOException {
        Assert.state(file != null, "[Assertion failed] - file must not be null!");
        Assert.state(file.exists(), "[Assertion failed] - file " + file.getPath() + " must by exists!");
        String data = "";
        if (includePathName && includeFileName) {
            data = file.getPath();
        } else if (includePathName) {
            data = file.getParentFile().getPath();
        } else if (includeFileName) {
            data = file.getName();
        }
        if (includeLastChanged) {
            data += "___";
            data += file.lastModified();
        }
        StringBuilder buf = new StringBuilder(50);
        calculateChecksum(buf, data.getBytes(StandardCharsets.UTF_8));

        if (includeFileContent) {
            MessageDigest messageDigest;
            FileChannel channel;
            try (FileInputStream inputStream = new FileInputStream(file)) {
                messageDigest = DigestUtils.getSha512Digest();
                channel = inputStream.getChannel();
                ByteBuffer buff = ByteBuffer.allocate(2048);
                while (channel.read(buff) != -1) {
                    buff.flip();
                    messageDigest.update(buff);
                    buff.clear();
                }
                buf.append(DigestUtils.encodeHex(messageDigest.digest()));
            }
        }
        return buf.toString();
    }

    static String calculateChecksum(byte[]... data) {
        StringBuilder buf = new StringBuilder(50);
        calculateChecksum(buf, data);
        return buf.toString();
    }

    static void calculateChecksum(StringBuilder target, byte[]... data) {
        Assert.state(target != null, "[Assertion failed] - target can not by null!");
        Assert.state(data != null && data.length != 0, "[Assertion failed] - data can not by null or empty!");
        for (byte[] item : data) {
            if (item != null && item.length != 0) {
                DigestUtils.appendSha512DigestAsHex(item, target);
            }
        }
    }
}
