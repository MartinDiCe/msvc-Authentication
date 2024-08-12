package com.diceprojects.msvcauthentication.security;

import com.diceprojects.msvcauthentication.exceptions.ErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;

import static com.diceprojects.msvcauthentication.utils.AuthWhitelist.AUTH_WHITELIST;

/**
 * Configuración de seguridad para la aplicación utilizando Spring WebFlux.
 * Esta clase define los filtros de seguridad y la gestión de autenticación JWT.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final CustomReactiveAuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    /**
     * Constructor de la clase SecurityConfig.
     *
     * @param authenticationManager Gestor de autenticación reactiva personalizada.
     * @param jwtUtil                Utilidad JWT para la generación y validación de tokens.
     */
    public SecurityConfig(CustomReactiveAuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Define la cadena de filtros de seguridad para la aplicación.
     *
     * @param http El objeto ServerHttpSecurity para configurar la seguridad.
     * @return Un {@link SecurityWebFilterChain} que especifica la cadena de filtros de seguridad.
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        try {
            return http
                    .authorizeExchange(exchanges -> exchanges
                            .pathMatchers(AUTH_WHITELIST).permitAll()
                            .anyExchange().authenticated()
                    )
                    .addFilterAt(jwtAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .build();
        } catch (Exception e) {
            ErrorHandler.handleError("Error configuring SecurityWebFilterChain", e, HttpStatus.INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    /**
     * Configura un filtro de autenticación basado en JWT.
     *
     * @return Un {@link AuthenticationWebFilter} que maneja la autenticación con JWT.
     */
    @Bean
    public AuthenticationWebFilter jwtAuthenticationFilter() {
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(authenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(jwtServerAuthenticationConverter());
        return authenticationWebFilter;
    }

    /**
     * Convierte las solicitudes entrantes para extraer y validar tokens JWT.
     *
     * @return Un {@link ServerAuthenticationConverter} que convierte las solicitudes en objetos de autenticación basados en JWT.
     */
    @Bean
    public ServerAuthenticationConverter jwtServerAuthenticationConverter() {
        return new JwtServerAuthenticationConverter(jwtUtil);
    }

    /**
     * Proveedor de codificación de contraseñas.
     *
     * @return Un {@link PasswordEncoder} que utiliza BCrypt para codificar las contraseñas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
