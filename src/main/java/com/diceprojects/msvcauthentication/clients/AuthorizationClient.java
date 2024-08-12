package com.diceprojects.msvcauthentication.clients;

import com.diceprojects.msvcauthentication.persistences.dto.CreateRoleRequest;
import com.diceprojects.msvcauthentication.persistences.dto.CreateUserRequest;
import com.diceprojects.msvcauthentication.persistences.dto.RoleDTO;
import com.diceprojects.msvcauthentication.persistences.dto.UserDetailsDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * Cliente para comunicarse con el microservicio de msvc-authorization.
 */
@Component
public class AuthorizationClient {

    private final WebClient.Builder webClientBuilder;

    public AuthorizationClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    // Métodos relacionados con User

    /**
     * Obtiene los detalles de un usuario por su nombre de usuario desde el microservicio de msvc-authorization.
     *
     * @param username el nombre de usuario.
     * @return un Mono que emite los detalles del usuario encontrado.
     */
    public Mono<UserDetailsDTO> getUserByUsername(String username) {
        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8003/api/user/{username}", username)
                .retrieve()
                .bodyToMono(UserDetailsDTO.class);
    }

    /**
     * Crea un nuevo usuario en el microservicio de msvc-authorization.
     *
     * @param username el nombre de usuario.
     * @param password la contraseña del usuario.
     * @param roles    los roles asignados al usuario.
     * @return un Mono que emite los detalles del usuario creado.
     */
    public Mono<UserDetailsDTO> createUser(String username, String password, Set<RoleDTO> roles) {
        return webClientBuilder.build()
                .post()
                .uri("http://localhost:8003/api/user/create")
                .bodyValue(new CreateUserRequest(username, password, roles))
                .retrieve()
                .bodyToMono(UserDetailsDTO.class);
    }

    /**
     * Obtiene un rol por su nombre desde el microservicio de msvc-authorization.
     *
     * @param roleName el nombre del rol.
     * @return un Mono que emite el rol encontrado.
     */
    public Mono<RoleDTO> getRoleByName(String roleName) {
        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8003/api/role/getRoleByName/{roleName}", roleName)
                .retrieve()
                .bodyToMono(RoleDTO.class);
    }

    /**
     * Crea un nuevo rol en el microservicio de msvc-authorization.
     *
     * @param roleName    el nombre del rol.
     * @param description la descripción del rol (opcional).
     * @return un Mono que emite el rol creado.
     */
    public Mono<RoleDTO> createRole(String roleName, String description) {
        return webClientBuilder.build()
                .post()
                .uri("http://localhost:8003/api/role/create")
                .bodyValue(new CreateRoleRequest(roleName, description))
                .retrieve()
                .bodyToMono(RoleDTO.class);
    }

    /**
     * Actualiza un rol existente en el microservicio de msvc-authorization.
     *
     * @param roleId      el ID del rol a actualizar.
     * @param roleName    el nuevo nombre del rol.
     * @param description la nueva descripción del rol (opcional).
     * @return un Mono que emite el rol actualizado.
     */
    public Mono<RoleDTO> updateRole(String roleId, String roleName, String description) {
        return webClientBuilder.build()
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path("http://localhost:8003/api/role/update/{roleId}")
                        .queryParam("roleName", roleName)
                        .queryParam("description", description)
                        .build(roleId))
                .retrieve()
                .bodyToMono(RoleDTO.class);
    }

    /**
     * Cambia el estado de un rol en el microservicio de msvc-authorization.
     *
     * @param roleId el ID del rol a actualizar.
     * @param status el nuevo estado del rol (activo/inactivo).
     * @return un Mono que emite el rol actualizado o un mensaje si el estado ya es el mismo.
     */
    public Mono<Object> changeRoleStatus(String roleId, String status) {
        return webClientBuilder.build()
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path("http://localhost:8003/api/role/changeStatus/{roleId}")
                        .queryParam("status", status)
                        .build(roleId))
                .retrieve()
                .bodyToMono(Object.class);
    }

    /**
     * Lista todos los roles desde el microservicio de msvc-authorization.
     *
     * @return un Flux que emite todos los roles.
     */
    public Flux<RoleDTO> listRoles() {
        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8003/api/role/listRoles")
                .retrieve()
                .bodyToFlux(RoleDTO.class);
    }
}
