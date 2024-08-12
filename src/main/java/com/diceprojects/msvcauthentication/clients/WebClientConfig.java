package com.diceprojects.msvcauthentication.clients;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuración de WebClient para la aplicación.
 *
 * Esta clase configura un bean de {@link WebClient.Builder} que será utilizado
 * para realizar llamadas HTTP no bloqueantes en los clientes Web dentro de la aplicación.
 */
@Configuration
public class WebClientConfig {

    /**
     * Define un bean de {@link WebClient.Builder} para ser utilizado en la aplicación.
     *
     * @return una instancia de {@link WebClient.Builder}.
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
