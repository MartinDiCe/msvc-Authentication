package com.diceprojects.msvcauthentication.services;

import com.diceprojects.msvcauthentication.persistences.models.dtos.AuthResponse;
import com.diceprojects.msvcauthentication.persistences.models.dtos.LoginRequest;
import com.diceprojects.msvcauthentication.persistences.models.dtos.UserDetailsDTO;
import reactor.core.publisher.Mono;

/**
 * Interfaz que proporciona servicios de autenticación.
 */
public interface AuthService {

    /**
     * Autentica a un usuario basado en la solicitud de inicio de sesión proporcionada.
     *
     * @param loginRequest la solicitud de inicio de sesión que contiene el nombre de usuario y la contraseña
     * @return un Mono que emite la respuesta de autenticación que contiene el token JWT y la fecha de expiración
     */
    Mono<AuthResponse> authenticate(LoginRequest loginRequest);

    /**
     * Valida un token JWT y recupera los detalles del usuario si el token es válido.
     *
     * @param token El token JWT a validar.
     * @return Un {@link Mono} que emite los detalles del usuario si el token es válido.
     */
    Mono<UserDetailsDTO> validateAndGetUser(String token);

}
