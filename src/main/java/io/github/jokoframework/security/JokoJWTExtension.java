package io.github.jokoframework.security;

import java.util.List;
import java.util.Map;

/**
 * Esta clase resume las extensiones que realizamos a JWS.
 * 
 * 
 * @author danicricco
 *
 */
public class JokoJWTExtension {

    public enum TOKEN_TYPE {
        REFRESH, // token de refresh para end user
        REFRESH_C, // token de refresh para consumer
        ACCESS, // token de acceso. Dependiendo del accessLevel pueden
                // ser mas o menos permisos

    };

    /**
     * El tipo de token que se utiliza
     */
    private TOKEN_TYPE type;

    /**
     * Los roles que el usuario determino son adecuados
     */
    private List<String> roles;

    private String profile;

    public JokoJWTExtension() {

    }

    public static final JokoJWTExtension fromMap(Map<String, Object> map) {
        String typeStr = (String) map.get("type");
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) map.get("roles");
        String profile =(String) map.get("profile");

        return new JokoJWTExtension(TOKEN_TYPE.valueOf(typeStr), roles, profile);
    }

    public JokoJWTExtension(TOKEN_TYPE type, List<String> roles, String profile) {
        this.type = type;
        this.roles = roles;
        this.profile = profile;

    }

    public TOKEN_TYPE getType() {
        return type;
    }

    public void setType(TOKEN_TYPE type) {
        this.type = type;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

}
