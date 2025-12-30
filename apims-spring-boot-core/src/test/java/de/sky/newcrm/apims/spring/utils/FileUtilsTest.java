/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import org.apache.commons.io.ByteOrderMark;
import org.junit.jupiter.api.Test;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

class FileUtilsTest {

    @Test
    void pathSeparatorTest() {
        String winPath = "\\test\\path\\file.txt";
        String linuxPath = "/test/path/file.txt";
        if (FileUtils.isSystemsPathSeparatorLinuxStyle()) {
            assertEquals("/", FileUtils.getSystemsPathSeparator());
            assertEquals('/', FileUtils.getSystemsPathSeparatorChar());
            assertEquals(linuxPath, FileUtils.toSystemPath(winPath));
            assertEquals(linuxPath, FileUtils.toSystemPath(linuxPath));
            assertEquals(linuxPath, FileUtils.toLinuxPath(winPath));
            assertEquals(linuxPath, FileUtils.toLinuxPath(linuxPath));
        }
        assertNull(FileUtils.toSystemPath(null));
        assertEquals(winPath, FileUtils.replacePathSeparatorWith(winPath, '\\'));
        assertEquals(winPath, FileUtils.replacePathSeparatorWith(linuxPath, '\\'));
    }

    @Test
    void commonTest() {
        FileUtils.createDirectories((File) null);
        FileUtils.createDirectories((Path) null);
        FileUtils.delete((File) null);
        FileUtils.delete((Path) null);
        assertEquals(-1L, FileUtils.getFileSize((File) null));
        assertEquals(-1L, FileUtils.getFileSize((Path) null));
        assertEquals(0L, FileUtils.getLastModified((File) null));
        assertEquals(0L, FileUtils.getLastModified((Path) null));

        File tempDir = TempFileUtils.getApimsTempDir();
        File tempSubDir = new File(tempDir, FileUtils.toSystemPath("test/fileutils"));
        File tempFile = new File(tempSubDir, "test.txt");
        FileUtils.delete(tempFile);
        assertFalse(FileUtils.exists(tempFile));
        assertEquals(-1L, FileUtils.getFileSize(tempFile));
        FileUtils.delete(tempSubDir);
        assertFalse(FileUtils.exists(tempSubDir));

        FileUtils.createDirectories(tempSubDir);
        FileUtils.createDirectories(tempSubDir);
        assertTrue(FileUtils.exists(tempSubDir));

        FileUtils.createFile(tempFile);
        assertTrue(FileUtils.exists(tempFile));
        assertThrows(FileAlreadyExistsException.class, () -> FileUtils.createFile(tempFile));

        assertDoesNotThrow(() -> FileCopyUtils.copy("test without bom".getBytes(StandardCharsets.UTF_8), tempFile));
        assertNotEquals(-1L, FileUtils.getFileSize(tempFile));
        assertNotEquals(0L, FileUtils.getLastModified(tempFile.toPath()));
        assertNull(FileUtils.getFileBom(tempFile));
        assertEquals(StandardCharsets.UTF_8, FileUtils.getFileBomCharset(tempFile, StandardCharsets.UTF_8));

        FileUtils.delete(tempFile);
        assertFalse(FileUtils.exists(tempFile));

        assertDoesNotThrow(() ->
                FileCopyUtils.copy((FileUtils.UTF_BOM + "test utf bom").getBytes(StandardCharsets.UTF_8), tempFile));
        assertEquals(ByteOrderMark.UTF_8, FileUtils.getFileBom(tempFile));
        assertEquals(StandardCharsets.UTF_8, FileUtils.getFileBomCharset(tempFile, StandardCharsets.UTF_8));

        FileUtils.delete(tempFile);
        assertFalse(FileUtils.exists(tempFile));
    }

    @Test
    void getFileExtensionTest() {
        executeExtensionTest("/tmp/testfile", ".tmp", null);
    }

    @Test
    void getFileExtensionByDefaultTest() {
        executeExtensionTest("/tmp/testfile", null, ".tmp");
    }

    @Test
    void getFileNameWithoutExtensionTest() {
        executeFileNameWithoutExtensionTest("/tmp/testfile.tmp", "testfile");
    }

    @Test
    void getFileNameWithoutExtension2Test() {
        executeFileNameWithoutExtensionTest("/tmp/.tmp", "");
    }

    @Test
    void getFileNameWithoutExtension3Test() {
        executeFileNameWithoutExtensionTest("tmp", "tmp");
    }

    private void executeExtensionTest(String baseName, String extension, String defaultExtension) {

        if (extension == null) {
            extension = "";
        }
        boolean validExtension = StringUtils.hasLength(extension) && extension.contains(".");
        String fileName = baseName;
        if (validExtension) {
            fileName += ".";
            fileName += extension;
        }

        File file = new File(fileName);
        String value = FileUtils.getFileExtension(fileName, defaultExtension);
        assertEquals(validExtension ? extension : defaultExtension, value);
        String byFileValue = FileUtils.getFileExtension(file, defaultExtension);
        assertEquals(validExtension ? extension : defaultExtension, byFileValue);
        String byPathValue = FileUtils.getFileExtension(file.toPath(), defaultExtension);
        assertEquals(validExtension ? extension : defaultExtension, byPathValue);
    }

    private void executeFileNameWithoutExtensionTest(String fileName, String expected) {

        File file = new File(fileName);
        String value = FileUtils.getFileNameWithoutExtension(fileName);
        assertEquals(expected, value);
        String byFileValue = FileUtils.getFileNameWithoutExtension(file);
        assertEquals(expected, byFileValue);
        String byPathValue = FileUtils.getFileNameWithoutExtension(file.toPath());
        assertEquals(expected, byPathValue);
    }
}
