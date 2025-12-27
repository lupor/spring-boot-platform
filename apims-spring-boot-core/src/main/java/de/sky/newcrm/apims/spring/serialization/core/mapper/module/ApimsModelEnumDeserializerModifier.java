/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core.mapper.module;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import java.io.IOException;
import lombok.Getter;
import org.springframework.util.StringUtils;

public class ApimsModelEnumDeserializerModifier extends BeanDeserializerModifier {

    @Override
    public JsonDeserializer<?> modifyEnumDeserializer(
            DeserializationConfig config, JavaType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        return type.isEnumType()
                ? new ApimsModelEnumDeserializer(config, type, beanDesc, deserializer)
                : super.modifyEnumDeserializer(config, type, beanDesc, deserializer);
    }

    @Getter
    protected static class ApimsModelEnumDeserializer extends JsonDeserializer<Object>
            implements ContextualDeserializer {

        private final DeserializationConfig config;
        private final JavaType enumType;
        private final JavaType paramType;
        private final JavaType inputType;
        private final BeanDescription beanDesc;
        private final JsonDeserializer<?> deserializer;
        private JsonDeserializer<?> contextualDeserializer = null;

        public ApimsModelEnumDeserializer(
                DeserializationConfig config,
                JavaType enumType,
                BeanDescription beanDesc,
                JsonDeserializer<?> deserializer) {
            this.config = config;
            this.enumType = enumType;
            this.paramType = beanDesc.getFactoryMethods().stream()
                    .filter(am -> am.getParameterCount() != 0
                            && (!am.getParameterType(0).hasRawClass(String.class)))
                    .findFirst()
                    .map(am -> am.getParameterType(0))
                    .orElse(null);
            inputType = (paramType == null
                            || paramType.hasRawClass(String.class)
                            || paramType.hasRawClass(CharSequence.class))
                    ? null
                    : paramType;
            this.beanDesc = beanDesc;
            this.deserializer = deserializer;
        }

        @Override
        public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.hasToken(JsonToken.VALUE_STRING)) {
                String value = p.getText().trim();
                if (StringUtils.hasLength(value)) {
                    Class<?> enumClass = enumType.getRawClass();
                    for (Object enumConstant : enumClass.getEnumConstants()) {
                        if (((Enum<?>) enumConstant).name().equals(value)) {
                            return enumConstant;
                        }
                    }
                }
            }
            return deserializer.deserialize(p, ctxt);
        }

        @Override
        public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
                throws JsonMappingException {

            // special case: enum with annotation and param != String
            if (contextualDeserializer == null
                    && inputType != null
                    && deserializer instanceof ContextualDeserializer ctxDeserializer) {
                contextualDeserializer = ctxDeserializer.createContextual(ctxt, property);
                return contextualDeserializer;
            }
            return this;
        }

        @Override
        public Boolean supportsUpdate(DeserializationConfig config) {
            return Boolean.FALSE;
        }

        @Override
        public LogicalType logicalType() {
            return LogicalType.Enum;
        }

        @Override
        public boolean isCachable() {
            return true;
        }
    }
}
