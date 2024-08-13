package com.diceprojects.msvcauthentication.clients;

import com.diceprojects.msvcauthentication.persistences.dto.UserDetailsDTO;
import io.netty.handler.logging.LogLevel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

/**
 * Cliente para comunicarse con el microservicio de msvc-authorization.
 */
@Component
public class AuthorizationClient {

    private final WebClient webClient;

    /**
     * Constructor de AuthorizationClient.
     *
     * @param authorizationServiceUrl la URL base del servicio de autorización, inyectada desde el archivo de configuración.
     */
    public AuthorizationClient(@Value("${msvc.authorization.url}") String authorizationServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(authorizationServiceUrl)
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .compress(true)
                                .wiretap("reactor.netty.client.HttpClient", LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)
                ))
                .build();
    }

    /**
     * Obtiene los detalles de un usuario por su nombre de usuario desde el microservicio de msvc-authorization.
     *
     * @param username el nombre de usuario.
     * @return un {@link Mono} que emite los detalles del usuario encontrado.
     */
    public Mono<UserDetailsDTO> getUserByUsername(String username) {
        return webClient.get()
                .uri("/user/{username}", username)
                .retrieve()
                .bodyToMono(UserDetailsDTO.class)
                .onErrorResume(e -> Mono.error(new RuntimeException("Error retrieving user details", e)));
    }


    /**
     * Actualiza el token de seguridad de un usuario en el microservicio de msvc-authorization.
     *
     * @param userId el ID del usuario.
     * @param token el nuevo token de seguridad.
     * @return un {@link Mono} que emite los detalles del usuario actualizado.
     */
    public Mono<UserDetailsDTO> updateUserToken(String userId, String token) {
        return webClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/user/updateToken/{userId}")
                        .queryParam("token", token)
                        .build(userId))
                .retrieve()
                .bodyToMono(UserDetailsDTO.class);
    }
}
