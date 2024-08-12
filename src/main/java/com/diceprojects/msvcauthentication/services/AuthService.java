package com.diceprojects.msvcauthentication.services;

import com.diceprojects.msvcauthentication.persistences.dto.AuthResponse;
import com.diceprojects.msvcauthentication.persistences.dto.LoginRequest;
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
}
