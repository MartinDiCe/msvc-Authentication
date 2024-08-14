package com.diceprojects.msvcauthentication.persistences.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de creación de un nuevo rol.
 * Este objeto es utilizado para transferir los datos necesarios para crear un rol en el sistema.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoleRequest {

    /**
     * El nombre del rol a ser creado.
     */
    private String roleName;

    /**
     * La descripción del rol, proporcionando detalles sobre su propósito y uso.
     */
    private String description;
}
