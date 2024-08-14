package com.diceprojects.msvcauthentication.security;

import com.diceprojects.msvcauthentication.exceptions.ErrorHandler;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Convertidor de autenticación del servidor que maneja tokens JWT.
 */
public class JwtServerAuthenticationConverter implements ServerAuthenticationConverter {

    private final JwtUtil jwtUtil;

    /**
     * Constructor para crear una instancia de JwtServerAuthenticationConverter.
     *
     * @param jwtUtil el utilitario de JWT para validar y extraer información del token
     */
    public JwtServerAuthenticationConverter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Convierte la solicitud del servidor en una autenticación basada en JWT.
     *
     * @param exchange la solicitud del servidor
     * @return un Mono que emite la autenticación si el token es válido, o vacío si no lo es
     */
    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst("Authorization"))
                .filter(token -> token.startsWith("Bearer "))
                .flatMap(token -> {
                    String jwtToken = token.substring(7);
                    return jwtUtil.validateToken(jwtToken)
                            .flatMap(isValid -> {
                                if (isValid) {
                                    String username = jwtUtil.getClaimsFromToken(jwtToken).getSubject();
                                    Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, null);
                                    return Mono.just(authentication);
                                } else {
                                    return Mono.empty();
                                }
                            });
                })
                .onErrorResume(e -> {
                    ErrorHandler.handleError("Error converting JWT token", e, HttpStatus.UNAUTHORIZED);
                    return Mono.empty();
                });
    }

}
