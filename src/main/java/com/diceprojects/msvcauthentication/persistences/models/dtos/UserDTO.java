package com.diceprojects.msvcauthentication.persistences.models.dtos;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;
import java.util.Set;

/**
 * DTO que representa un usuario en el sistema.
 * Este objeto es utilizado para transferir los datos de un usuario, incluyendo
 * su nombre de usuario, contraseña, estado, fechas importantes, roles y otros atributos relevantes.
 */
@Getter
@Setter
public class UserDTO {

    private String id;
    private String username;
    private String password;
    private String status;
    private boolean deleted;
    private Date deleteDate;
    private Date createDate;
    private Date updateDate;
    private String securityToken;
    private boolean forcePasswordChange;
    private Set<String> roleIds;

    /**
     * Constructor que inicializa todos los campos del UserDTO.
     *
     * @param id                  El identificador único del usuario.
     * @param username            El nombre de usuario.
     * @param password            La contraseña del usuario.
     * @param status              El estado actual del usuario.
     * @param deleted             Indica si el usuario ha sido eliminado.
     * @param deleteDate          La fecha en que el usuario fue eliminado.
     * @param createDate          La fecha de creación del usuario.
     * @param updateDate          La fecha de la última actualización del usuario.
     * @param securityToken       El token de seguridad asociado al usuario.
     * @param forcePasswordChange Indica si el usuario debe cambiar su contraseña en el próximo inicio de sesión.
     * @param roleIds             Un conjunto de IDs de roles asociados al usuario.
     */
    public UserDTO(String id, String username, String password, String status, boolean deleted, Date deleteDate,
                   Date createDate, Date updateDate, String securityToken, boolean forcePasswordChange, Set<String> roleIds) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.status = status;
        this.deleted = deleted;
        this.deleteDate = deleteDate;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.securityToken = securityToken;
        this.forcePasswordChange = forcePasswordChange;
        this.roleIds = roleIds;
    }
}
