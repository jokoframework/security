package io.github.jokoframework.security.development;

import io.github.jokoframework.security.api.JokoAuthentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Development implementation of JokoAuthentication with full support
 * for security profiles, roles, and custom attributes.
 */
public class DevJokoAuthentication implements JokoAuthentication {

    private static final long serialVersionUID = 1L;

    private final String username;
    private final String securityProfile;
    private String subject;
    private boolean authenticated = false;
    private List<String> roles = new ArrayList<>();
    private Map<String, Object> custom = new HashMap<>();

    public DevJokoAuthentication(String username, String securityProfile) {
        this.username = username;
        this.securityProfile = securityProfile;
        this.subject = username;
    }

    @Override
    public String getName() {
        return subject != null ? subject : username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return username;
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
        return securityProfile;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Object getCustom(String key) {
        return custom.get(key);
    }

    @Override
    public Map<String, Object> getCustom() {
        return custom;
    }

    @Override
    public List<String> getRoles() {
        return roles;
    }

    @Override
    public void addRole(String role) {
        this.roles.add(role);
    }

    @Override
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void addCustom(String key, Object value) {
        this.custom.put(key, value);
    }
}
