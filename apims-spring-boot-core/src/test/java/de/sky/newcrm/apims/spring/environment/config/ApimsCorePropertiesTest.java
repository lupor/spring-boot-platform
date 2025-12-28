/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.environment.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ApimsPropertiesTest {

    @Test
    @SuppressWarnings("java:S5961")
    void codeCoverageTest() {

        ApimsCoreProperties properties = new ApimsCoreProperties();
        //        TODO: Belongs to WS
        //        ApimsProperties.WsSecurityHandler wsSecurityHandler = new ApimsCoreProperties.WsSecurityHandler(null);
        //        wsSecurityHandler.setIncludePattern("include-pattern");
        //        wsSecurityHandler.setExcludePattern("exclude-pattern");
        //        assertEquals("include-pattern", wsSecurityHandler.getIncludePattern());
        //        assertEquals("exclude-pattern", wsSecurityHandler.getExcludePattern());
        //        assertNotNull(wsSecurityHandler.getSecurity());
        //        wsSecurityHandler.setSecurity(null);
        //        assertNull(wsSecurityHandler.getSecurity());
        //        TODO: Belongs to SSH
        //        ApimsProperties.Ssh ssh = new ApimsProperties.Ssh();
        //        ssh.setPrivateKeyPassphrase("private-key-passphrase");
        //        assertEquals("private-key-passphrase", ssh.getPrivateKeyPassphrase());

        //        TODO: Belongs to SFTP
        //        ApimsProperties.Sftp sftp = new ApimsProperties.Sftp();
        //        assertNotNull(sftp.getDownloadDirectory());
        //        assertFalse(sftp.isDownloadPollerCacheRemoteFileNames());
        //        assertFalse(sftp.isDownloadDirectoryReadOnly());
        //        assertFalse(sftp.isAutoPreventDuplicateFileDownloads());
        //        assertNotNull(sftp.getUploadDirectory());
        //        assertNotNull(sftp.getDownloadPollerCronExpression());
        //        assertEquals(-1, sftp.getDownloadPollerMaxMessagesPerPoll());
        //        assertNotNull(sftp.getDownloadFileFilter());
        //        assertNotNull(sftp.getDownloadExcludeFileFilter());
        //        assertFalse(sftp.isDeleteDownloadedFilesImmediately());
        //        assertTrue(sftp.isSaveProcessedFilesInArchiveDirectory());
        //        assertNotNull(sftp.getDownloadToLocalDirectory());

        //        TODO: Belongs to rest
        //        ApimsProperties.Rest rest = new ApimsProperties.Rest();
        //        assertEquals(-1, rest.getReportNotHandledHttpErrorsAsStatusCode());
        //        assertFalse(rest.isPreventDoubleEncoding());
        //        assertFalse(rest.isAutoValidateRequest());
        //        assertFalse(rest.isAutoValidateResponse());
        //        assertTrue(rest.isExpandUriVars());
        //        assertNotNull(rest.getHeaders());
        //        assertNotNull(rest.getAdditionalHeaders());

        //        TODO: Belongs to MDC
        //        ApimsCoreProperties.Aspects.Listeners.Mdc mdc = new ApimsCoreProperties.Aspects.Listeners.Mdc();
        //        mdc.setPrefix("apims.");
        //        mdc.setGlobalFieldsPrefix("apims.");
        //        assertEquals("apims.", mdc.getPrefix());
        //        assertEquals("apims.", mdc.getGlobalFieldsPrefix());
        //        TODO: Belongs to serialization
        //        ApimsProperties.ObjectMapperConfig objectMapperConfig = new ApimsProperties.ObjectMapperConfig();
        //        assertFalse(objectMapperConfig.isDateTimeSerializerWriteIsoDateWithTimezone());
    }
}
