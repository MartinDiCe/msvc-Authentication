package com.diceprojects.msvcauthentication.controllers;

import com.diceprojects.msvcauthentication.persistences.dto.AuthResponse;
import com.diceprojects.msvcauthentication.persistences.dto.LoginRequest;
import com.diceprojects.msvcauthentication.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Controlador para manejar las solicitudes de autenticación.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Maneja la solicitud de inicio de sesión.
     *
     * @param loginRequest el objeto de solicitud de inicio de sesión que contiene el nombre de usuario y la contraseña
     * @return un {@link Mono} que emite una {@link ResponseEntity} con la respuesta de autenticación
     */
    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody LoginRequest loginRequest) {
        return authService.authenticate(loginRequest)
                .map(ResponseEntity::ok);
    }
}
