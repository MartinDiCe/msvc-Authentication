package com.diceprojects.msvcauthentication.security;

import com.diceprojects.msvcauthentication.clients.AuthorizationClient;
import com.diceprojects.msvcauthentication.clients.ConfigurationClient;
import com.diceprojects.msvcauthentication.exceptions.ErrorHandler;
import com.diceprojects.msvcauthentication.persistences.models.dtos.ParameterDTO;
import com.diceprojects.msvcauthentication.persistences.models.dtos.UserDetailsDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.lang.NonNull;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import com.diceprojects.msvcauthentication.persistences.models.dtos.RoleDTO;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Date;
import java.util.Base64;
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

    private SecretKey key;
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
     * Método invocado al inicializar el contexto de la aplicación.
     * Carga la clave JWT desde la base de datos.
     *
     * @param event Evento de refresco del contexto.
     */
    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        loadKeyFromDatabase().subscribe();
    }

    /**
     * Carga la clave JWT desde la base de datos o la genera si no existe.
     *
     * @return Mono<Void> que indica la finalización de la operación.
     * Si la clave se encuentra en la base de datos, se carga en la aplicación y no se realiza ninguna acción adicional.
     * Si no se encuentra, se genera una nueva clave, se guarda en la base de datos y se carga en la aplicación.
     */
    private Mono<Void> loadKeyFromDatabase() {
        return configurationClient.getParameterByName("jwtSecretKey")
                .flatMap(parameter -> {
                    try {
                        Map<String, String> values = objectMapper.readValue(parameter.getValue(), new TypeReference<Map<String, String>>() {});
                        this.jwtSecret = values.get("keyApplication");
                        this.jwtExpirationMs = Integer.parseInt(values.get("timeExpire"));
                        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret));
                        return Mono.just(true);
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("Error al leer los valores del parámetro", e));
                    }
                })
                .switchIfEmpty(Mono.just(false))
                .flatMap(found -> {
                    if (!found) {
                        return generateAndSaveNewKey();
                    } else {
                        return Mono.empty();
                    }
                })
                .then();
    }

    /**
     * Genera una nueva clave secreta JWT y la guarda en la base de datos.
     *
     * @return Mono<Void> que indica la finalización de la operación.
     * Este método genera una nueva clave HMAC-SHA512, la guarda en la base de datos como un parámetro junto con el tiempo de expiración,
     * y luego carga la clave en la aplicación para su uso posterior.
     */
    private Mono<Void> generateAndSaveNewKey() {
        byte[] keyBytes = generateSecureKey();
        this.key = Keys.hmacShaKeyFor(keyBytes);
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

        return configurationClient.saveOrUpdateParameter(parameter).then();
    }

    /**
     * Genera un token JWT para el objeto de autenticación dado.
     *
     * @param authentication el objeto de autenticación que contiene los detalles del usuario.
     * @return un {@link Mono} que emite el token JWT generado.
     */
    public Mono<String> generateToken(Authentication authentication) {
        return ensureKeyLoaded().then(Mono.defer(() -> {
            String username = authentication.getName();
            Instant now = Instant.now();
            Instant expiryDate = now.plusMillis(jwtExpirationMs);

            return authorizationClient.getUserByUsername(username)
                    .map(userDetails -> {
                        String roles = userDetails.getRoles().stream()
                                .map(RoleDTO::getRole)
                                .collect(Collectors.joining(","));

                        return Jwts.builder()
                                .claim("roles", roles)
                                .subject(username)
                                .issuedAt(Date.from(now))
                                .expiration(Date.from(expiryDate))
                                .signWith(key)
                                .compact();
                    });
        }));
    }

    /**
     * Garantiza que la clave JWT esté cargada.
     *
     * @return Mono vacío que indica la finalización de la operación.
     */
    private Mono<Void> ensureKeyLoaded() {
        if (this.key == null) {
            return loadKeyFromDatabase();
        } else {
            return Mono.empty();
        }
    }

    /**
     * Valida un token JWT.
     *
     * @param token el token JWT a validar.
     * @return un {@link Mono} que emite {@code true} si el token es válido, {@code false} de lo contrario.
     */
    public Mono<Boolean> validateToken(String token) {
        return ensureKeyLoaded().then(Mono.fromCallable(() -> {
            try {
                Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token);

                return true;
            } catch (Exception e) {
                ErrorHandler.handleError("Token JWT inválido", e, HttpStatus.UNAUTHORIZED);
                return false;
            }
        }));
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
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Obtiene los detalles del usuario a partir de un token JWT.
     *
     * @param token El token JWT del cual se extraerá el nombre de usuario.
     * @param authorizationClient El cliente de autorización utilizado para obtener los detalles del usuario.
     * @return Un {@link Mono} que emite los detalles del usuario asociado con el token JWT.
     */
    public Mono<UserDetailsDTO> getUserFromToken(String token, AuthorizationClient authorizationClient) {
        String username = getClaimsFromToken(token).getSubject();
        return authorizationClient.getUserByUsername(username);
    }

    /**
     * Genera una clave secreta segura para JWT.
     *
     * @return un array de bytes que representa la clave secreta generada.
     */
    private byte[] generateSecureKey() {
        byte[] keyBytes = new byte[64]; // 512 bits for HMAC-SHA512
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(keyBytes);
        return keyBytes;
    }
}
