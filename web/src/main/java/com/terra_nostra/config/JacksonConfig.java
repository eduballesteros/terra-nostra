package com.terra_nostra.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

/**
 * Configuración global para Jackson en la aplicación.
 * Define un `ObjectMapper` personalizado para manejar fechas de Java 8 correctamente.
 *
 * @author ebp
 * @version 1.0
 */

public class JacksonConfig {

    @Bean

    /**
     * Configura un `ObjectMapper` para manejar correctamente las fechas de Java 8.
     *
     * - Registra `JavaTimeModule` para permitir la serialización/deserialización de `LocalDateTime`.
     * - Deshabilita la escritura de fechas como timestamps para mejorar la legibilidad.
     *
     * @return Un `ObjectMapper` configurado con soporte para Java 8 Date/Time API.
     */


    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        return objectMapper;
    }
}
