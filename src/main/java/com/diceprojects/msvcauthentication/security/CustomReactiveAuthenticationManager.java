package com.diceprojects.msvcauthentication.security;

import com.diceprojects.msvcauthentication.clients.AuthorizationClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación personalizada de {@link ReactiveAuthenticationManager} para gestionar
 * la autenticación de usuarios de forma reactiva.
 */
@Component
public class CustomReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final AuthorizationClient authorizationClient;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor de la clase.
     *
     * @param authorizationClient Cliente que se utiliza para obtener detalles del usuario desde el servicio de autorización.
     * @param passwordEncoder Codificador de contraseñas utilizado para verificar la validez de la contraseña proporcionada.
     */
    public CustomReactiveAuthenticationManager(AuthorizationClient authorizationClient, @Lazy PasswordEncoder passwordEncoder) {
        this.authorizationClient = authorizationClient;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Autentica al usuario basado en las credenciales proporcionadas.
     *
     * @param authentication Objeto de autenticación que contiene el nombre de usuario y la contraseña.
     * @return Un {@link Mono} que emite un objeto de {@link Authentication} si la autenticación es exitosa,
     *         de lo contrario, emite un error de autenticación.
     */
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        return authorizationClient.getUserByUsername(username)
                .switchIfEmpty(Mono.error(new BadCredentialsException("Usuario no encontrado")))
                .flatMap(userDetails -> {
                    if (passwordEncoder.matches(password, userDetails.getPassword())) {

                        // Convierte la lista de roles a las autoridades de Spring Security
                        List<GrantedAuthority> authorities = userDetails.getRoles().stream()
                                .map(roleDTO -> new SimpleGrantedAuthority(roleDTO.getRole()))
                                .collect(Collectors.toList());

                        return Mono.just((Authentication) new UsernamePasswordAuthenticationToken(username, password, authorities));
                    } else {
                        return Mono.error(new BadCredentialsException("Credenciales inválidas"));
                    }
                })
                .onErrorResume(e -> {

                    return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));
                });
    }
}
