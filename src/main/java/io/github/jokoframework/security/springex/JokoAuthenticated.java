package io.github.jokoframework.security.springex;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;

import io.github.jokoframework.common.errors.JokoApplicationException;
import io.github.jokoframework.security.JokoJWTClaims;
import io.github.jokoframework.security.api.JokoAuthentication;

public class JokoAuthenticated implements JokoAuthentication, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -5060564314503748847L;

    private final JokoJWTClaims claims;

    //BEGIN-IGNORE-SONARQUBE
    private Collection<? extends GrantedAuthority> authorities;
    //END-IGNORE-SONARQUBE

    public JokoAuthenticated(JokoJWTClaims claims, Collection<? extends GrantedAuthority> authorities) {
        this.claims = claims;
        this.authorities = authorities;

    }

    @Override
    public String getName() {
        return claims.getSubject();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return claims;
    }

    @Override
    public Object getDetails() {
        return claims;
    }

    @Override
    public Object getPrincipal() {
        return claims.getSubject();
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new JokoApplicationException("Should NOT modify an authenticated principal");

    }

    @Override
    public String getSecurityProfile() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPassword() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getUsername() {
        return claims.getSubject();
    }

    @Override
    public Object getCustom(String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> getCustom() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getRoles() {
        return this.claims.getJoko().getRoles();
    }

    public void addRole(String r) {
        throw new JokoApplicationException("Should NOT modify an authenticated principal");
    }

}
