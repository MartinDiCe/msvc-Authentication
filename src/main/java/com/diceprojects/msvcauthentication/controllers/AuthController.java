package com.diceprojects.msvcauthentication.controllers;

import com.diceprojects.msvcauthentication.persistences.models.dtos.AuthResponse;
import com.diceprojects.msvcauthentication.persistences.models.dtos.LoginRequest;
import com.diceprojects.msvcauthentication.persistences.models.dtos.UserDetailsDTO;
import com.diceprojects.msvcauthentication.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    /**
     * Valida un token JWT y devuelve los detalles del usuario.
     *
     * @param token El token JWT a validar.
     * @return Un {@link Mono} que emite los detalles del usuario si el token es válido.
     */
    @GetMapping("/validate")
    public Mono<ResponseEntity<UserDetailsDTO>> validateToken(@RequestHeader("Authorization") String token) {
        return authService.validateAndGetUser(token)
                .map(ResponseEntity::ok);
    }

}
