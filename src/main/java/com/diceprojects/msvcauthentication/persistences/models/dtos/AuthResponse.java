package com.diceprojects.msvcauthentication.persistences.models.dtos;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * DTO para manejar las respuestas de autenticación.
 */
@Data
public class AuthResponse {

    private String username;
    private String token;
    private String expiryDate;

    /**
     * Constructor para crear una respuesta de autenticación.
     *
     * @param username   el nombre de usuario autenticado
     * @param token      el token JWT generado
     * @param expiryDate la fecha de expiración del token
     */
    public AuthResponse(String username, String token, Date expiryDate) {
        this.username = username;
        this.token = token;
        this.expiryDate = new SimpleDateFormat("HH:mm:ss").format(expiryDate);
    }
}
