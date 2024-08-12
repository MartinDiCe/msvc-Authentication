package com.diceprojects.msvcauthentication.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de ObjectMapper para la serialización y deserialización de JSON en la aplicación.
 *
 * Esta clase configura un bean de {@link ObjectMapper} que se utilizará en toda la aplicación
 * para manejar las conversiones entre objetos Java y representaciones JSON.
 */
@Configuration
public class ObjectMapperConfig {

    /**
     * Define un bean de {@link ObjectMapper} para ser utilizado en la aplicación.
     *
     * @return una instancia de {@link ObjectMapper}.
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}