package com.diceprojects.msvcauthentication.persistences.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

/**
 * DTO para la solicitud de creación de un nuevo usuario.
 * Este objeto es utilizado para transferir los datos necesarios para crear un usuario en el sistema.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    /**
     * El nombre de usuario del nuevo usuario a ser creado.
     */
    private String username;

    /**
     * La contraseña del nuevo usuario a ser creada.
     */
    private String password;

    /**
     * El conjunto de roles asignados al nuevo usuario.
     */
    private Set<RoleDTO> roles;
}

