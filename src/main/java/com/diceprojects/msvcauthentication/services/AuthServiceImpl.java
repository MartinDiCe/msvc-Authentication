package com.diceprojects.msvcauthentication.services;

import com.diceprojects.msvcauthentication.clients.AuthorizationClient;
import com.diceprojects.msvcauthentication.persistences.models.dtos.AuthResponse;
import com.diceprojects.msvcauthentication.persistences.models.dtos.LoginRequest;
import com.diceprojects.msvcauthentication.persistences.models.dtos.UserDetailsDTO;
import com.diceprojects.msvcauthentication.security.CustomReactiveAuthenticationManager;
import com.diceprojects.msvcauthentication.security.JwtUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

/**
 * Implementación de {@link AuthService} que proporciona servicios de autenticación.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final CustomReactiveAuthenticationManager customAuthenticationManager;
    private final JwtUtil jwtUtil;
    private final AuthorizationClient authorizationClient;

    /**
     * Constructor de AuthServiceImpl.
     *
     * @param customAuthenticationManager El gestor de autenticación reactiva personalizada.
     * @param jwtUtil                     La utilidad JWT para la generación de tokens.
     */
    public AuthServiceImpl(@Lazy CustomReactiveAuthenticationManager customAuthenticationManager,
                           JwtUtil jwtUtil, AuthorizationClient authorizationClient) {
        this.customAuthenticationManager = customAuthenticationManager;
        this.jwtUtil = jwtUtil;
        this.authorizationClient = authorizationClient;
    }

    /**
     * Autentica a un usuario basado en los detalles de la solicitud de inicio de sesión.
     *
     * @param loginRequest La solicitud de inicio de sesión que contiene el nombre de usuario y la contraseña.
     * @return Un {@link Mono} que emite {@link AuthResponse} con el token JWT y la información relacionada si la autenticación es exitosa.
     */
    @Override
    public Mono<AuthResponse> authenticate(LoginRequest loginRequest) {
        return customAuthenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()))
                .flatMap(authentication -> jwtUtil.generateToken(authentication)
                        .flatMap(token -> authorizationClient.getUserByUsername(authentication.getName())
                                .flatMap(userDetails -> authorizationClient.updateUserToken(userDetails.getId(), token)
                                        .then(Mono.just(new AuthResponse(authentication.getName(), token, jwtUtil.getExpiryDateFromToken(token))))
                                )
                        )
                )
                .onErrorResume(e -> Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas")));
    }

    /**
     * Valida un token JWT y recupera los detalles del usuario si el token es válido.
     *
     * @param token El token JWT a validar.
     * @return Un {@link Mono} que emite los detalles del usuario si el token es válido.
     */
    public Mono<UserDetailsDTO> validateAndGetUser(String token) {
        return jwtUtil.validateToken(token)
                .flatMap(isValid -> {
                    if (isValid) {
                        return jwtUtil.getUserFromToken(token, authorizationClient)
                                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado")));
                    } else {
                        return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido o expirado"));
                    }
                });
    }

}
