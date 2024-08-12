package com.diceprojects.msvcauthentication.security;

import com.diceprojects.msvcauthentication.clients.AuthorizationClient;
import com.diceprojects.msvcauthentication.clients.ConfigurationClient;
import com.diceprojects.msvcauthentication.exceptions.ErrorHandler;
import com.diceprojects.msvcauthentication.persistences.dto.ParameterDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import com.diceprojects.msvcauthentication.persistences.dto.RoleDTO;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Clase utilitaria para manejar operaciones JWT como la generación y validación de tokens.
 */
@Component
public class JwtUtil implements ApplicationListener<ContextRefreshedEvent> {

    private final ConfigurationClient configurationClient;
    private final ObjectMapper objectMapper;
    private final AuthorizationClient authorizationClient;

    private Key key;
    private String jwtSecret;
    private int jwtExpirationMs;

    /**
     * Constructor de la clase JwtUtil.
     *
     * @param configurationClient Cliente de configuración utilizado para obtener y guardar parámetros.
     * @param objectMapper         Mapeador de objetos utilizado para convertir JSON a objetos Java.
     * @param authorizationClient  Cliente de autorización utilizado para obtener detalles del usuario.
     */
    public JwtUtil(@Lazy ConfigurationClient configurationClient, ObjectMapper objectMapper, AuthorizationClient authorizationClient) {
        this.configurationClient = configurationClient;
        this.objectMapper = objectMapper;
        this.authorizationClient = authorizationClient;
    }

    /**
     * Inicializa la clave secreta JWT cuando el contexto de la aplicación se refresca.
     * Si la clave JWT no está configurada, se genera una nueva y se guarda en la base de datos.
     *
     * @param event el evento de refresco del contexto.
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        configurationClient.getParameterByName("jwtSecretKey")
                .flatMap(parameter -> {
                    try {
                        Map<String, String> values = objectMapper.readValue(parameter.getValue(), Map.class);
                        this.jwtSecret = values.get("keyApplication");
                        this.jwtExpirationMs = Integer.parseInt(values.get("timeExpire"));
                        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
                        return Mono.just(parameter);
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("Error al leer los valores del parámetro", e));
                    }
                })
                .switchIfEmpty(Mono.defer(() -> {
                    this.key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
                    this.jwtSecret = Base64.getEncoder().encodeToString(key.getEncoded());
                    this.jwtExpirationMs = 3600000;

                    Map<String, String> values = Map.of(
                            "keyApplication", this.jwtSecret,
                            "timeExpire", String.valueOf(this.jwtExpirationMs)
                    );

                    ParameterDTO parameter = new ParameterDTO();
                    parameter.setParameterName("jwtSecretKey");
                    try {
                        parameter.setValue(objectMapper.writeValueAsString(values));
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("Error al escribir los valores del parámetro", e));
                    }
                    parameter.setDescription("JWT secret key and expiration time for signing tokens");

                    return configurationClient.saveOrUpdateParameter(parameter);
                }))
                .doOnNext(parameter -> System.out.println("Clave secreta JWT y tiempo de expiración inicializados exitosamente"))
                .doOnError(e -> ErrorHandler.handleError("Error inicializando la clave secreta JWT", e, HttpStatus.INTERNAL_SERVER_ERROR))
                .subscribe();
    }

    /**
     * Genera un token JWT para el objeto de autenticación dado.
     *
     * @param authentication el objeto de autenticación que contiene los detalles del usuario.
     * @return un {@link Mono} que emite el token JWT generado.
     */
    public Mono<String> generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return authorizationClient.getUserByUsername(username)
                .map(userDetails -> {
                    String roles = userDetails.getRoles().stream()
                            .map(RoleDTO::getRole)
                            .collect(Collectors.joining(","));

                    return Jwts.builder()
                            .setSubject(username)
                            .claim("roles", roles)
                            .setIssuedAt(now)
                            .setExpiration(expiryDate)
                            .signWith(key, SignatureAlgorithm.HS512)
                            .compact();
                });
    }

    /**
     * Recupera la fecha de expiración de un token JWT.
     *
     * @param token el token JWT.
     * @return la fecha de expiración del token.
     */
    public Date getExpiryDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    /**
     * Recupera las claims de un token JWT.
     *
     * @param token el token JWT.
     * @return las claims extraídas del token.
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Valida un token JWT.
     *
     * @param token el token JWT a validar.
     * @return {@code true} si el token es válido, {@code false} de lo contrario.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            ErrorHandler.handleError("Token JWT inválido", e, HttpStatus.UNAUTHORIZED);
        }
        return false;
    }
}