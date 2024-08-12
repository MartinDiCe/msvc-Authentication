package com.diceprojects.msvcauthentication.persistences.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO que representa un rol en el sistema.
 * Este objeto es utilizado para transferir los datos de un rol entre capas de la aplicación.
 */
@Getter
@Setter
public class RoleDTO {

    /**
     * El identificador único del rol.
     */
    private String id;

    /**
     * El nombre del rol.
     */
    private String role;

    /**
     * El estado actual del rol.
     */
    private String status;

    /**
     * Constructor para inicializar todos los campos del RoleDTO.
     *
     * @param id     El identificador único del rol.
     * @param role   El nombre del rol.
     * @param status El estado actual del rol.
     */
    public RoleDTO(String id, String role, String status) {
        this.id = id;
        this.role = role;
        this.status = status;
    }
}
