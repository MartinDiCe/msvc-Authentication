package com.diceprojects.msvcauthentication.security;

import org.springframework.security.core.GrantedAuthority;

/**
 * Clase que implementa {@link GrantedAuthority} para representar una autoridad concedida a un usuario.
 */
public class Authority implements GrantedAuthority {

    private final String authority;

    /**
     * Constructor para crear una instancia de Authority.
     *
     * @param authority la autoridad concedida al usuario
     */
    public Authority(String authority) {
        this.authority = authority;
    }

    /**
     * Obtiene la autoridad concedida.
     *
     * @return la autoridad como una cadena de texto
     */
    @Override
    public String getAuthority() {
        return authority;
    }

}
