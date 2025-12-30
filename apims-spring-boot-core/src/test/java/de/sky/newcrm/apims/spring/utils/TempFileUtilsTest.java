/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class TempFileUtilsTest {

    @Test
    void tempFileTest() {
        assertNotNull(TempFileUtils.getSystemTempDir());
        assertNotNull(TempFileUtils.createApimsTempFile());
        TempFileUtils.TempFileConfiguration tempFileConfiguration = null;
        String testContent = "content";
        Path tempFile = TempFileUtils.writeToTempFile((TempFileUtils.TempFileConfiguration) null, testContent);
        assertNotNull(tempFile);
        TempFileUtils.writeToTempFile(tempFile.toFile(), testContent);
        TempFileUtils.writeToTempFile(tempFile.toFile(), testContent.getBytes(StandardCharsets.UTF_8));
    }
}
