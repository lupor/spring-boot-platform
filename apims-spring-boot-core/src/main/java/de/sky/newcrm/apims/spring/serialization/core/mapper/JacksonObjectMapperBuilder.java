/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;

/**
 * A builder for creating Jackson ObjectMapper instances, similar to Spring's Jackson2ObjectMapperBuilder
 * but adapted for Jackson 3 (or newer Jackson 2 versions where Jackson2ObjectMapperBuilder is deprecated).
 */
public class JacksonObjectMapperBuilder {

    private boolean createXmlMapper = false;
    private DateFormat dateFormat;
    private JsonInclude.Include serializationInclusion;
    private final Map<Object, Boolean> features = new LinkedHashMap<>();
    private final List<com.fasterxml.jackson.databind.Module> modules = new ArrayList<>();
    private boolean findWellKnownModules = true;
    private final ClassLoader moduleClassLoader = getClass().getClassLoader();

    public JacksonObjectMapperBuilder createXmlMapper(boolean createXmlMapper) {
        this.createXmlMapper = createXmlMapper;
        return this;
    }

    public JacksonObjectMapperBuilder indentOutput(boolean indentOutput) {
        this.features.put(SerializationFeature.INDENT_OUTPUT, indentOutput);
        return this;
    }

    public JacksonObjectMapperBuilder dateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
        return this;
    }

    public JacksonObjectMapperBuilder simpleDateFormat(String format) {
        this.dateFormat = new SimpleDateFormat(format);
        return this;
    }

    public JacksonObjectMapperBuilder modulesToInstall(com.fasterxml.jackson.databind.Module... modules) {
        this.modules.addAll(Arrays.asList(modules));
        this.findWellKnownModules = true;
        return this;
    }

    public JacksonObjectMapperBuilder modulesToInstall(Consumer<List<com.fasterxml.jackson.databind.Module>> consumer) {
        consumer.accept(this.modules);
        this.findWellKnownModules = true;
        return this;
    }

    public JacksonObjectMapperBuilder serializationInclusion(JsonInclude.Include serializationInclusion) {
        this.serializationInclusion = serializationInclusion;
        return this;
    }

    public JacksonObjectMapperBuilder featuresToEnable(Object... featuresToEnable) {
        for (Object feature : featuresToEnable) {
            this.features.put(feature, Boolean.TRUE);
        }
        return this;
    }

    public JacksonObjectMapperBuilder featuresToDisable(Object... featuresToDisable) {
        for (Object feature : featuresToDisable) {
            this.features.put(feature, Boolean.FALSE);
        }
        return this;
    }

    public ObjectMapper build() {
        MapperBuilder<?, ?> builder;
        if (this.createXmlMapper) {
            builder = XmlMapper.builder();
        } else {
            builder = JsonMapper.builder();
        }

        configure(builder);

        return builder.build();
    }

    private void configure(MapperBuilder<?, ?> builder) {
        if (this.dateFormat != null) {
            builder.defaultDateFormat(this.dateFormat);
        }

        if (this.serializationInclusion != null) {
            builder.defaultPropertyInclusion(JsonInclude.Value.construct(this.serializationInclusion, JsonInclude.Include.USE_DEFAULTS));
        }

        if (this.findWellKnownModules) {
            registerWellKnownModulesIfAvailable(builder);
        }

        if (!this.modules.isEmpty()) {
            builder.addModules(this.modules);
        }

        this.features.forEach((feature, enabled) -> configureFeature(builder, feature, enabled));
    }

    private void registerWellKnownModulesIfAvailable(MapperBuilder<?, ?> builder) {
        try {
            Class<?> jdk8ModuleClass =
                    ClassUtils.forName("com.fasterxml.jackson.datatype.jdk8.Jdk8Module", this.moduleClassLoader);
            builder.addModule((com.fasterxml.jackson.databind.Module) BeanUtils.instantiateClass(jdk8ModuleClass.getConstructor()));
        } catch (ClassNotFoundException | NoSuchMethodException _) {
            // ignore
        }

        try {
            Class<?> parameterNamesModuleClass = ClassUtils.forName(
                    "com.fasterxml.jackson.module.paramnames.ParameterNamesModule", this.moduleClassLoader);
            builder.addModule(
                    (com.fasterxml.jackson.databind.Module) BeanUtils.instantiateClass(parameterNamesModuleClass.getConstructor()));
        } catch (ClassNotFoundException | NoSuchMethodException _) {
            // ignore
        }

        try {
            Class<?> javaTimeModuleClass = ClassUtils.forName(
                    "com.fasterxml.jackson.datatype.jsr310.JavaTimeModule", this.moduleClassLoader);
            builder.addModule((com.fasterxml.jackson.databind.Module) BeanUtils.instantiateClass(javaTimeModuleClass.getConstructor()));
        } catch (ClassNotFoundException | NoSuchMethodException _) {
            // ignore
        }

        try {
            Class<?> kotlinModuleClass =
                    ClassUtils.forName("com.fasterxml.jackson.module.kotlin.KotlinModule", this.moduleClassLoader);
            builder.addModule((com.fasterxml.jackson.databind.Module) BeanUtils.instantiateClass(kotlinModuleClass.getConstructor()));
        } catch (ClassNotFoundException | NoSuchMethodException _) {
            // ignore
        }
    }

    private void configureFeature(MapperBuilder<?, ?> builder, Object feature, boolean enabled) {
        switch (feature) {
            case JsonParser.Feature jsonParserFeature -> {
                builder.configure(jsonParserFeature, enabled);
            }
            case JsonGenerator.Feature jsonGeneratorFeature -> {
                builder.configure(jsonGeneratorFeature, enabled);
            }
            case SerializationFeature serializationFeature -> {
                builder.configure(serializationFeature, enabled);
            }
            case DeserializationFeature deserializationFeature -> {
                builder.configure(deserializationFeature, enabled);
            }
            case MapperFeature mapperFeature -> {
                builder.configure(mapperFeature, enabled);
            }
            default -> {
                throw new IllegalArgumentException("Unknown feature class: " + feature.getClass().getName());
            }
        }
    }
}
