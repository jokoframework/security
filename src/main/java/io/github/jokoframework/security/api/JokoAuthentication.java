package io.github.jokoframework.security.api;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;

public interface JokoAuthentication extends Authentication {

    String getSecurityProfile();

    String getPassword();

    String getUsername();

    /**
     * Devuelve una de las propiedades particulares del requet
     * 
     * @param key
     * @return la propiedad
     */
    Object getCustom(String key);

    /**
     * Devuelve un mapa con las propiedades especializadas que se utilizaron en
     * el login
     * 
     * @return
     */
    Map<String, Object> getCustom();

    List<String> getRoles();

    void addRole(String role);

    /**
     * Guarda el subject con el que el token sera emitido en caso que se
     * autentique
     * @param subject
     */
    public void setSubject(String subject);


}
