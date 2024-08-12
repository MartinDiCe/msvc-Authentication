package com.diceprojects.msvcauthentication.persistences.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * DTO que representa los detalles de un usuario en el sistema.
 * Este objeto es utilizado para transferir los datos de un usuario, incluyendo su nombre de usuario,
 * contraseña, estado y roles asociados.
 */
@Getter
@Setter
public class UserDetailsDTO {

    private String id;
    private String username;
    private String password;
    private String status;
    private Set<RoleDTO> roles;

    /**
     * Constructor que inicializa todos los campos del UserDetailsDTO.
     *
     * @param id       El identificador único del usuario.
     * @param username El nombre de usuario.
     * @param password La contraseña del usuario.
     * @param status   El estado actual del usuario.
     * @param roles    Los roles asociados al usuario.
     */
    public UserDetailsDTO(String id, String username, String password, String status, Set<RoleDTO> roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.status = status;
        this.roles = roles;
    }

}
