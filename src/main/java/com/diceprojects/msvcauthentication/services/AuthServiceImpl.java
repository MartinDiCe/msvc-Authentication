package com.diceprojects.msvcauthentication.services;

import com.diceprojects.msvcauthentication.exceptions.ErrorHandler;
import com.diceprojects.msvcauthentication.persistences.dto.AuthResponse;
import com.diceprojects.msvcauthentication.persistences.dto.LoginRequest;
import com.diceprojects.msvcauthentication.security.CustomReactiveAuthenticationManager;
import com.diceprojects.msvcauthentication.security.JwtUtil;
import com.diceprojects.msvcauthentication.utils.EntityStatusService;
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
    private final EntityStatusService entityStatusService;

    /**
     * Constructor de AuthServiceImpl.
     *
     * @param customAuthenticationManager El gestor de autenticación reactiva personalizada.
     * @param jwtUtil                     La utilidad JWT para la generación de tokens.
     * @param entityStatusService         El servicio para obtener el estado activo desde la configuración.
     */
    public AuthServiceImpl(@Lazy CustomReactiveAuthenticationManager customAuthenticationManager,
                           JwtUtil jwtUtil, EntityStatusService entityStatusService) {
        this.customAuthenticationManager = customAuthenticationManager;
        this.jwtUtil = jwtUtil;
        this.entityStatusService = entityStatusService;
    }

    /**
     * Autentica a un usuario basado en los detalles de la solicitud de inicio de sesión.
     *
     * @param loginRequest La solicitud de inicio de sesión que contiene el nombre de usuario y la contraseña.
     * @return Un {@link Mono} que emite {@link AuthResponse} con el token JWT y la información relacionada si la autenticación es exitosa.
     */
    @Override
    public Mono<AuthResponse> authenticate(LoginRequest loginRequest) {
        return entityStatusService.obtenerEstadoActivo()
                .flatMap(activeStatus -> customAuthenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()))
                        .flatMap(authentication -> jwtUtil.generateToken(authentication)
                                .map(token -> new AuthResponse(authentication.getName(), token, jwtUtil.getExpiryDateFromToken(token))))
                        .onErrorResume(e -> Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas")))
                )
                .doOnError(e -> ErrorHandler.handleError("Error de autenticación", e, HttpStatus.INTERNAL_SERVER_ERROR));
    }

}
