/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.aspects.core.around;

import java.net.URI;
import java.net.URISyntaxException;

class ApimsAroundHelper {
    private ApimsAroundHelper() {}

    @SuppressWarnings({"java:S1452"})
    //    @ApimsReportGeneratedHint
    //    static ProducerRecord<?, ?> getProducerRecord(ApimsAroundContext context) {
    //        for (Object object : context.getProceedingJoinPoint().getArgs()) {
    //            if (object instanceof ProducerRecord<?, ?> producerRecord) {
    //                return producerRecord;
    //            }
    //        }
    //        return null;
    //    }

    static URI getURI(ApimsAroundContext context) {
        for (Object object : context.getProceedingJoinPoint().getArgs()) {
            if (object instanceof URI uri) {
                return uri;
            } else if (object instanceof String url
                    && (url.toLowerCase().startsWith("http:")
                            || url.toLowerCase().startsWith("https:"))) {
                if (url.contains(" ")) {
                    url = url.replace(" ", "+");
                }
                try {
                    return new URI(url);
                } catch (URISyntaxException ignore) {
                    // ignore
                }
            }
        }
        return null;
    }

    //    static HttpMethod getHttpMethod(ApimsAroundContext context) {
    //        for (Object object : context.getProceedingJoinPoint().getArgs()) {
    //            if (object instanceof HttpMethod httpMethod) {
    //                return httpMethod;
    //            }
    //        }
    //        return null;
    //    }
    //
    //    static ApimsSftpDownloadedFile getSftpDownloadedFile(ApimsAroundContext context) {
    //        for (Object object : context.getProceedingJoinPoint().getArgs()) {
    //            if (object instanceof ApimsSftpDownloadedFile downloadedFile) {
    //                return downloadedFile;
    //            }
    //        }
    //        return null;
    //    }
}
