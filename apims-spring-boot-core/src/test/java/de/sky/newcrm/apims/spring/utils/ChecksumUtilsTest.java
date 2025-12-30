/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.junit.jupiter.api.Test;

@SuppressWarnings({"java:S5976"})
class ChecksumUtilsTest {

    @Test
    void checksumByBytesTest() {
        String value = ChecksumUtils.calculateChecksum(
                "TESTKEY".getBytes(StandardCharsets.UTF_8), "TESTVALUE".getBytes(StandardCharsets.UTF_8));
        assertNotNull(value);
        assertEquals("8e616af2e9be3f4de020d4ec21bf7e85743b2ce58bb4b5230e0fe91faf7f37bc", value);
    }

    @Test
    void checksumByFileTest() throws IOException {
        File file = new File(this.getClass()
                .getResource("/testdata/utils/ChecksumUtilsTest.checksumByFileTest.txt")
                .getFile());
        String value = ChecksumUtils.calculateChecksum(file);
        assertNotNull(value);
        value = ChecksumUtils.calculateChecksum(file, true, true, true, false);
        assertNotNull(value);
    }

    @Test
    void checksumByFileWithContentOnlyTest() throws IOException {
        File file = new File(this.getClass()
                .getResource("/testdata/utils/ChecksumUtilsTest.checksumByFileTest.txt")
                .getFile());
        String value = ChecksumUtils.calculateChecksum(file, false, false, false, true);
        assertNotNull(value);
        assertEquals("32dc3fd7d262ec2a9912e45f009fe61f", value);
    }

    @Test
    void checksumByFileWithFileNameOnlyTest() throws IOException {
        File file = new File(Objects.requireNonNull(this.getClass()
                        .getResource("/testdata/utils/ChecksumUtilsTest.checksumByFileTest.txt"))
                .getFile());
        String value = ChecksumUtils.calculateChecksum(file, false, true, false, false);
        assertNotNull(value);
        assertEquals("66c1300b3e9ad80fd7902752214b7cf4", value);
    }

    @Test
    void checksumByFileWithPathOnlyTest() throws IOException {
        File file = new File(this.getClass()
                .getResource("/testdata/utils/ChecksumUtilsTest.checksumByFileTest.txt")
                .getFile());
        String value = ChecksumUtils.calculateChecksum(file, true, false, false, false);
        assertNotNull(value);
    }

    @Test
    void checksumByFileWithFileNameAndContentTest() throws IOException {
        File file = new File(this.getClass()
                .getResource("/testdata/utils/ChecksumUtilsTest.checksumByFileTest.txt")
                .getFile());
        String value = ChecksumUtils.calculateChecksum(file, false, true, false, true);
        assertNotNull(value);
        assertEquals("66c1300b3e9ad80fd7902752214b7cf432dc3fd7d262ec2a9912e45f009fe61f", value);
    }

    @Test
    void hmacSha1ByBytesTest() {
        String value = DigestUtils.hmacSha1DigestAsBase64(
                "TESTVALUE".getBytes(StandardCharsets.UTF_8), "TESTSECRET".getBytes(StandardCharsets.UTF_8));
        assertNotNull(value);
        assertEquals("crVAuI1kty/TQu5podT/y8618lQ=", value);
    }
}
