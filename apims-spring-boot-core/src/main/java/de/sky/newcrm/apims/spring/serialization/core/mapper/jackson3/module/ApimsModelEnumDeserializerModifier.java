/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core.mapper.jackson3.module;

import org.springframework.util.StringUtils;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.*;
import tools.jackson.databind.deser.ValueDeserializerModifier;
import tools.jackson.databind.type.LogicalType;

public class ApimsModelEnumDeserializerModifier extends ValueDeserializerModifier {

    @Override
    public ValueDeserializer<?> modifyDeserializer(
            DeserializationConfig config, BeanDescription.Supplier beanDescSupplier, ValueDeserializer<?> deserializer) {

        BeanDescription beanDesc = beanDescSupplier.get();
        JavaType type = beanDesc.getType();
        if (!type.isEnumType()) {
            return deserializer;
        }

        // Check if there is a factory method (creator) with non-String/CharSequence parameter
        boolean hasNonStringCreator = beanDesc.getFactoryMethods().stream()
                .filter(am -> am.getParameterCount() > 0)
                .map(am -> am.getParameterType(0))
                .anyMatch(pt -> !pt.hasRawClass(String.class) && !pt.hasRawClass(CharSequence.class));

        if (hasNonStringCreator) {
            return deserializer;
        }

        return new ApimsModelEnumDeserializer(type, deserializer);
    }

    protected static class ApimsModelEnumDeserializer extends ValueDeserializer<Object> {

        private final JavaType enumType;
        private final ValueDeserializer<?> deserializer;

        public ApimsModelEnumDeserializer(JavaType enumType, ValueDeserializer<?> deserializer) {
            this.enumType = enumType;
            this.deserializer = deserializer;
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public Object deserialize(JsonParser p, DeserializationContext ctxt) {
            if (p.hasToken(JsonToken.VALUE_STRING)) {
                String value = p.getString().trim();
                if (StringUtils.hasLength(value)) {
                    try {

                        Class<Enum> enumClass = (Class<Enum>) enumType.getRawClass();
                        return Enum.valueOf(enumClass, value);
                    } catch (IllegalArgumentException | NullPointerException _) {
                        // ignore, fallback to default deserializer
                    }
                }
            }
            return ((ValueDeserializer<Object>) deserializer).deserialize(p, ctxt);
        }

        @Override
        public ValueDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
                throws JacksonException {
            ValueDeserializer<?> newDelegate = deserializer.createContextual(ctxt, property);
            if (newDelegate != deserializer) {
                return new ApimsModelEnumDeserializer(enumType, newDelegate);
            }
            return this;
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
