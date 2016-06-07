package io.github.jokoframework.security.api;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;

public interface JokoAuthentication extends Authentication {

    public String getSecurityProfile();

    public String getPassword();

    public String getUsername();

    /**
     * Devuelve una de las propiedades particulares del requet
     * 
     * @param key
     * @return
     */
    public Object getCustom(String key);

    /**
     * Devuelve un mapa con las propiedades especializadas que se utilizaron en
     * el login
     * 
     * @return
     */
    public Map<String, Object> getCustom();

    public List<String> getRoles();

    public void addRole(String role);
}
