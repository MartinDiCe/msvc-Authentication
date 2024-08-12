package com.diceprojects.msvcauthentication.security;

import com.diceprojects.msvcauthentication.exceptions.ErrorHandler;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Convertidor de autenticación para extraer y convertir el token JWT desde la cabecera Authorization
 * de solicitudes HTTP a un {@link Authentication} para su uso con Spring Security.
 */
public class ServerHttpBearerAuthenticationConverter implements ServerAuthenticationConverter {

    /**
     * Convierte la cabecera de autorización de una solicitud HTTP en un objeto {@link Authentication}.
     *
     * Este método busca un token de autorización (comenzando con 'Bearer ') en la cabecera 'Authorization'
     * y lo convierte en un {@link UsernamePasswordAuthenticationToken}.
     *
     * @param exchange La interacción HTTP actual que incluye la solicitud y respuesta.
     * @return Un {@link Mono} conteniendo el objeto {@link Authentication} o vacío si la cabecera no es válida.
     */
    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        try {
            return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst("Authorization"))
                    .filter(authHeader -> authHeader.startsWith("Bearer "))
                    .map(authHeader -> authHeader.substring(7))
                    .map(authToken -> new UsernamePasswordAuthenticationToken(authToken, authToken));
        } catch (Exception e) {
            ErrorHandler.handleError("Error converting Bearer token", e, HttpStatus.UNAUTHORIZED);
            return Mono.empty();
        }
    }
}
