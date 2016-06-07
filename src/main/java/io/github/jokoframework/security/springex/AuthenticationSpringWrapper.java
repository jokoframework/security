package io.github.jokoframework.security.springex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;

import io.github.jokoframework.security.api.JokoAuthentication;
import io.github.jokoframework.security.dto.AuthenticationRequest;

public class AuthenticationSpringWrapper implements JokoAuthentication {

    /**
     * 
     */
    private static final long serialVersionUID = -2794388179668965327L;
    private final AuthenticationRequest request;
    private boolean authenticated = false;

    private ArrayList<String> roles;

    public AuthenticationSpringWrapper(AuthenticationRequest r) {
        this.request = r;
        this.roles = new ArrayList<String>();
    }

    @Override
    public String getName() {
        return request.getUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getCredentials() {
        return request.getPassword();
    }

    @Override
    public Object getDetails() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getPrincipal() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;

    }

    @Override
    public String getSecurityProfile() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPassword() {
        return request.getPassword();
    }

    @Override
    public String getUsername() {
        return request.getUsername();
    }

    @Override
    public Object getCustom(String key) {
        if (request.getCustom() != null) {
            return request.getCustom().get(key);
        } else {
            return null;
        }

    }

    @Override
    public Map<String, Object> getCustom() {
        return request.getCustom();
    }

    @Override
    public List<String> getRoles() {
        return this.roles;
    }

    @Override
    public void addRole(String role) {
        this.roles.add(role);

    }

}
