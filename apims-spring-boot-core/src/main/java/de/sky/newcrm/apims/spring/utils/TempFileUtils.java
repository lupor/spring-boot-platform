/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import com.veracode.annotation.FilePathCleanser;
import de.sky.newcrm.apims.spring.exceptions.ApimsRuntimeException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.*;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

public abstract class TempFileUtils {

    public static final String APIMS_TEMP_SUB_DIR = "apims";
    public static final String DEFAULT_FILE_NAME_PREFIX = "apims";
    public static final String DEFAULT_FILE_NAME_PREFIX_TIMESTAMP_PATTERN = "___yyyy-MM-dd___HHmmss___";
    public static final String DEFAULT_FILE_NAME_SUFFIX = ".tmp";
    private static final String SYSTEM_PROPERTY_USER_DIR = "user.dir";
    private static final String SYSTEM_PROPERTY_TEMP_DIR = "java.io.tmpdir";

    private TempFileUtils() {}

    public static Path getSystemTempDir() {
        return Path.of(getSystemTempDirPath());
    }

    public static Path createTempDir(String tempSubDir) {
        final Path target = StringUtils.hasLength(tempSubDir)
                ? Path.of(getApimsTempDirPath(), tempSubDir)
                : Path.of(getApimsTempDirPath());
        return FunctionUtils.execute(
                () -> Files.exists(target) ? target : Files.createDirectories(target), ApimsRuntimeException.class);
    }

    @FilePathCleanser
    public static Path getTempFile(TempFileConfiguration tempFileConfiguration) {
        tempFileConfiguration = resolveDefaults(tempFileConfiguration);
        String fileNamePrefix = getFileNamePrefix(tempFileConfiguration);
        Path dir = createTempDir(tempFileConfiguration.getTempSubDir());
        File file = new File(dir.toFile(), fileNamePrefix + tempFileConfiguration.getFileNameSuffix());
        return file.toPath();
    }

    public static Path createApimsTempFile() {
        return createApimsTempFile(IdUtils.nextId(true) + ".tmp");
    }

    @FilePathCleanser
    public static Path createApimsTempFile(String fileName) {
        final Path file = new File(getApimsTempDir(), fileName).toPath();
        return FunctionUtils.execute(
                () -> Files.exists(file) ? file : Files.createFile(file), ApimsRuntimeException.class);
    }

    public static Path createTempFile(
            final TempFileConfiguration tempFileConfiguration, final FileAttribute<?>... attrs) {
        TempFileConfiguration tempFileConfig = resolveDefaults(tempFileConfiguration);
        String fileNamePrefix = getFileNamePrefix(tempFileConfig) + "___";
        Path dir = createTempDir(tempFileConfig.getTempSubDir());
        return FunctionUtils.execute(
                () -> Files.createTempFile(dir, fileNamePrefix, tempFileConfig.getFileNameSuffix(), attrs),
                ApimsRuntimeException.class);
    }

    public static Path writeToTempFile(TempFileConfiguration tempFileConfiguration, String content) {
        return writeToTempFile(tempFileConfiguration, content.getBytes(StandardCharsets.UTF_8));
    }

    public static Path writeToTempFile(TempFileConfiguration tempFileConfiguration, byte[] content) {
        Path file = createTempFile(tempFileConfiguration);
        writeToTempFile(file, content);
        return file;
    }

    public static void writeToTempFile(File file, String content) {
        writeToTempFile(file.toPath(), content);
    }

    public static void writeToTempFile(Path file, String content) {
        writeToTempFile(file, content.getBytes(StandardCharsets.UTF_8));
    }

    public static void writeToTempFile(File file, byte[] content) {
        writeToTempFile(file.toPath(), content);
    }

    public static void writeToTempFile(Path file, byte[] content) {
        FunctionUtils.execute(
                () -> FileCopyUtils.copy(
                        new ByteArrayInputStream(content),
                        Files.newOutputStream(
                                file, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND)),
                ApimsRuntimeException.class);
    }

    @FilePathCleanser
    public static File getApimsTempDir() {
        File dir = new File(getApimsTempDirPath());
        FileUtils.createDirectories(dir);
        return dir;
    }

    public static String getApimsTempDirPath() {
        return getSystemTempDirPath() + FileUtils.getSystemsPathSeparator() + APIMS_TEMP_SUB_DIR;
    }

    private static String getSystemTempDirPath() {
        return System.getProperty(SYSTEM_PROPERTY_TEMP_DIR, System.getProperty(SYSTEM_PROPERTY_USER_DIR));
    }

    private static String getFileNamePrefix(TempFileConfiguration tempFileConfiguration) {
        String fileNamePrefix = tempFileConfiguration.getFileNamePrefix();
        if (tempFileConfiguration.isFileNamePrefixAppendTimestamp()) {
            String pattern = tempFileConfiguration.getFileNamePrefixAppendTimestampPattern();
            fileNamePrefix += LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern(
                            StringUtils.hasLength(pattern) ? pattern : DEFAULT_FILE_NAME_PREFIX_TIMESTAMP_PATTERN));
        }
        return fileNamePrefix;
    }

    private static TempFileConfiguration resolveDefaults(TempFileConfiguration tempFileConfiguration) {
        if (tempFileConfiguration == null) {
            return TempFileConfiguration.builder()
                    .tempSubDir(null)
                    .fileNamePrefix(DEFAULT_FILE_NAME_PREFIX)
                    .fileNamePrefixAppendTimestamp(true)
                    .fileNameSuffix(DEFAULT_FILE_NAME_SUFFIX)
                    .build();
        }
        if (!StringUtils.hasLength(tempFileConfiguration.getFileNamePrefix())) {
            tempFileConfiguration.setFileNamePrefix(DEFAULT_FILE_NAME_PREFIX);
        }
        if (!StringUtils.hasLength(tempFileConfiguration.getFileNameSuffix())) {
            tempFileConfiguration.setFileNameSuffix(DEFAULT_FILE_NAME_SUFFIX);
        }
        return tempFileConfiguration;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TempFileConfiguration {
        String tempSubDir;
        String fileNamePrefix;
        boolean fileNamePrefixAppendTimestamp;
        String fileNamePrefixAppendTimestampPattern;
        String fileNameSuffix;
    }
}
