/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sky.newcrm.apims.spring.environment.core.ApimsReportGeneratedHint;
import de.sky.newcrm.apims.spring.serialization.core.masker.ApimsAroundObjectMasker;
import de.sky.newcrm.apims.spring.utils.ExceptionUtils;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericContainer;
import org.springframework.util.Assert;

@Slf4j
@SuppressWarnings({"java:S6212"})
public class ApimsAroundObjectSerializerDefaultImpl implements ApimsAroundObjectSerializer {

    private final ObjectMapper objectMapper;
    private final List<ApimsAroundObjectSerializerTypeHandler> typeHandlers;
    private final ApimsAroundObjectMasker masker;
    private final int defaultMaxCharacters;

    public ApimsAroundObjectSerializerDefaultImpl(
            ObjectMapper objectMapper,
            ApimsAroundObjectMasker masker,
            List<ApimsAroundObjectSerializerTypeHandler> typeHandlers,
            int defaultMaxCharacters) {
        Assert.notNull(objectMapper, "[Assertion failed] - 'objectMapper' is required; it must not be null");
        Assert.notNull(masker, "[Assertion failed] - 'masker' is required; it must not be null");
        this.objectMapper = objectMapper;
        this.masker = masker;
        this.typeHandlers = typeHandlers == null ? new ArrayList<>(0) : typeHandlers;
        this.defaultMaxCharacters = defaultMaxCharacters;
    }

    @Override
    @SuppressWarnings({"java:S1135", "java:S3776", "java:S6201"})
    public String serialize(Object object) {
        return serialize(object, defaultMaxCharacters);
    }

    @Override
    @ApimsReportGeneratedHint
    @SuppressWarnings({"java:S1135", "java:S1181", "java:S3776", "java:S6201", "java:S6541"})
    public String serialize(Object object, int maxLength) {

        // todo: find a better solutions...
        final int nMaxLength = maxLength > 0 ? maxLength - 3 : 0;
        String data = null;
        if (object == null) {
            return null;
        } else if (object instanceof String string) {
            data = string;
            if (data.startsWith("http://") || data.startsWith("https://")) {
                data = masker.maskUrlParamsValue(data);
            }
        } else if (object instanceof Path) {
            data = String.valueOf(object);
        } else if (object.getClass().isArray()) {
            int i = 0;
            StringBuilder iBuf = new StringBuilder(500);
            try {
                Object[] array = ((Object[]) object);
                for (Object o : array) {
                    if (i != 0) {
                        iBuf.append(", ");
                    }
                    iBuf.append("[").append(serialize(o, maxLength)).append("]");
                    i++;
                    if (nMaxLength > 0 && iBuf.length() > nMaxLength) {
                        iBuf.append("...");
                        break;
                    }
                }
            } catch (Exception e) {
                iBuf.append("[").append(object).append("]");
            }
            data = iBuf.isEmpty() ? "[]" : iBuf.toString();
        } else if (object instanceof Iterable<?> iterable) {
            int i = 0;
            StringBuilder iBuf = new StringBuilder(500);
            for (Object o : iterable) {
                if (i != 0) {
                    iBuf.append(", ");
                }
                if (object.equals(o)) {
                    iBuf.append("[").append(o).append("]");
                } else {
                    iBuf.append("[").append(serialize(o, maxLength)).append("]");
                }
                i++;
                if (nMaxLength > 0 && iBuf.length() > nMaxLength) {
                    iBuf.append("...");
                    break;
                }
            }
            data = iBuf.isEmpty() ? "[]" : iBuf.toString();
        } else {
            for (ApimsAroundObjectSerializerTypeHandler typeHandler : typeHandlers) {
                if (typeHandler.canHandle(object)) {
                    try {
                        data = typeHandler.serialize(this, object, maxLength);
                    } catch (Exception e) {
                        log.warn(
                                "{}.serialze(object, maxLength) failed: {}",
                                typeHandler.getClass().getSimpleName(),
                                ExceptionUtils.getLastExceptionMessage(e));
                    }
                    break;
                }
            }
            if (data == null) {
                if (object instanceof GenericContainer) {
                    data = String.valueOf(object);
                } else {
                    try {
                        data = objectMapper.writeValueAsString(object);
                    } catch (Exception e) {
                        data = String.valueOf(object);
                    }
                }
            }
        }
        if (nMaxLength > 0 && data.length() > nMaxLength) {
            data = data.substring(0, nMaxLength) + "...";
        }
        return masker.maskJsonValue(data);
    }
}
