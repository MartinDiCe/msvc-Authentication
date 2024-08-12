package com.diceprojects.msvcauthentication.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * Configuración de Spring WebFlux para el proyecto.
 * Esta clase habilita y configura WebFlux, el framework reactivo de Spring para construir aplicaciones web.
 * Implementa {@link WebFluxConfigurer}, permitiendo personalizar aspectos de la configuración de WebFlux.
 *
 * @EnableWebFlux anotación que habilita las características de WebFlux en el contexto de la aplicación,
 * comparable con @EnableWebMvc
 * habilita características para Spring MVC Reactivo.
 */
@Configuration
@EnableWebFlux
public class WebFluxConfig implements WebFluxConfigurer {
}

