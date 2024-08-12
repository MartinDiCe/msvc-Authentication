package com.diceprojects.msvcauthentication.persistences.dto;

import lombok.Data;

/**
 * DTO para manejar las solicitudes de inicio de sesión.
 */
@Data
public class LoginRequest {

    private String username;
    private String password;

    /**
     * Obtiene el nombre de usuario.
     *
     * @return el nombre de usuario
     */
    public String getUsername() {
        return username;
    }

    /**
     * Establece el nombre de usuario.
     *
     * @param username el nombre de usuario
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Obtiene la contraseña.
     *
     * @return la contraseña
     */
    public String getPassword() {
        return password;
    }

    /**
     * Establece la contraseña.
     *
     * @param password la contraseña
     */
    public void setPassword(String password) {
        this.password = password;
    }
}

