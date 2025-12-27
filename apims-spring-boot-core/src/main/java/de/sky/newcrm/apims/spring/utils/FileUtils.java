/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import com.veracode.annotation.FilePathCleanser;
import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import de.sky.newcrm.apims.spring.exceptions.ApimsRuntimeException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.SneakyThrows;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.util.StringUtils;

public class FileUtils {

    public static final char UTF_BOM = ByteOrderMark.UTF_BOM;
    private static final FileUtils INSTANCE = new FileUtils();
    private final boolean systemsPathSeparatorLinuxStyle;
    private final char systemPathSeparatorChar;
    private final String systemPathSeparator;

    @ApimsReportGeneratedHint
    private FileUtils() {
        systemPathSeparator = FileSystems.getDefault().getSeparator();
        systemPathSeparatorChar = systemPathSeparator.toCharArray()[0];
        systemsPathSeparatorLinuxStyle = "/".equals(systemPathSeparator);
    }

    public static String getSystemsPathSeparator() {
        return INSTANCE.systemPathSeparator;
    }

    public static char getSystemsPathSeparatorChar() {
        return INSTANCE.systemPathSeparatorChar;
    }

    public static boolean isSystemsPathSeparatorLinuxStyle() {
        return INSTANCE.systemsPathSeparatorLinuxStyle;
    }

    public static String getFileExtension(Path file, String defaultValue) {
        AssertUtils.notNullCheck("file", file);
        return getFileExtension(file.getFileName().toString(), defaultValue);
    }

    public static String getFileExtension(File file, String defaultValue) {
        AssertUtils.notNullCheck("file", file);
        return getFileExtension(file.getName(), defaultValue);
    }

    @FilePathCleanser
    public static String getFileExtension(String fileName, String defaultValue) {
        AssertUtils.hasLengthCheck("fileName", fileName);
        fileName = new File(fileName).getName();
        int dotIndex = fileName.lastIndexOf(46);
        return dotIndex == -1 ? defaultValue : fileName.substring(dotIndex);
    }

    public static String getFileNameWithoutExtension(Path file) {
        AssertUtils.notNullCheck("file", file);
        return getFileNameWithoutExtension(file.getFileName().toString());
    }

    public static String getFileNameWithoutExtension(File file) {
        AssertUtils.notNullCheck("file", file);
        return getFileNameWithoutExtension(file.getName());
    }

    @FilePathCleanser
    public static String getFileNameWithoutExtension(String fileName) {
        AssertUtils.hasLengthCheck("fileName", fileName);
        fileName = new File(fileName).getName();
        int dotIndex = fileName.lastIndexOf(46);
        return dotIndex == -1 ? fileName : fileName.substring(0, dotIndex);
    }

    public static long getFileSize(File file) {
        return exists(file) ? getFileSize(file.toPath()) : -1;
    }

    public static long getFileSize(Path file) {
        return exists(file) ? FunctionUtils.execute(() -> Files.size(file)) : -1;
    }

    public static long getLastModified(File file) {
        return exists(file) ? FunctionUtils.execute(file::lastModified) : 0;
    }

    public static long getLastModified(Path file) {
        return exists(file) ? getLastModified(file.toFile()) : 0;
    }

    public static String toSystemPath(String path) {
        return replacePathSeparatorWith(path, getSystemsPathSeparatorChar());
    }

    public static String toLinuxPath(String path) {
        return replacePathSeparatorWith(path, '/');
    }

    public static String replacePathSeparatorWith(String path, char pathSeparator) {
        if (!StringUtils.hasLength(path)) {
            return path;
        }
        final char searchSeparator = '\\' == pathSeparator ? '/' : '\\';
        return path.replace(searchSeparator, pathSeparator);
    }

    public static File createFile(File file) {
        return createFile(file.toPath()).toFile();
    }

    @SneakyThrows
    public static Path createFile(Path file) {
        return Files.createFile(file);
    }

    public static void createDirectories(File file) {
        if (file != null) {
            createDirectories(file.toPath());
        }
    }

    public static void createDirectories(Path file) {
        if (file != null && !Files.exists(file)) {
            FunctionUtils.execute(() -> Files.createDirectories(file));
        }
    }

    public static boolean exists(File file) {
        return file != null && exists(file.toPath());
    }

    public static boolean exists(Path file) {
        return file != null && Files.exists(file);
    }

    public static void delete(File file) {
        delete(file, true);
    }

    public static void delete(File file, boolean silent) {
        if (file != null && file.exists()) {
            delete(file.toPath(), silent);
        }
    }

    public static void delete(Path file) {
        delete(file, true);
    }

    @ApimsReportGeneratedHint
    public static void delete(Path file, boolean silent) {
        if (file != null && Files.exists(file)) {
            try {
                Files.delete(file);
            } catch (IOException e) {
                if (!silent) {
                    throw new ApimsRuntimeException("Delete File '" + file + "' failed!", e);
                }
            }
        }
    }

    public static ByteOrderMark getFileBom(File file) {

        return FunctionUtils.execute(() -> {
            try (InputStream inputStream = new FileInputStream(file)) {
                BOMInputStream bOMInputStream =
                        BOMInputStream.builder().setInputStream(inputStream).get();
                return bOMInputStream.getBOM();
            }
        });
    }

    public static Charset getFileBomCharset(File file, Charset defaultCharset) {
        ByteOrderMark bom = getFileBom(file);
        return bom == null ? defaultCharset : Charset.forName(bom.getCharsetName());
    }
}
