package de.sky.newcrm.apims.spring.serialization.core.mapper.autoconfigure;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.SerializationFeature;

public class JacksonAutoConfigure {
    @Bean
    JsonMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder.enable(SerializationFeature.INDENT_OUTPUT);
    }
}
