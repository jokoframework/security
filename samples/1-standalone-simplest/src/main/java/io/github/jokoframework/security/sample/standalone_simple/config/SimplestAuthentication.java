package io.github.jokoframework.security.sample.standalone_simple.config;

import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import io.github.jokoframework.security.api.JokoAuthentication;
import io.github.jokoframework.security.api.JokoAuthenticationManager;

@Component
public class SimplestAuthentication implements JokoAuthenticationManager {

    private static final String DEFAULT_PIN = "123456";

    @Override
    public JokoAuthentication authenticate(JokoAuthentication authentication) throws AuthenticationException {
        authentication.getCustom("entidad");
        if (DEFAULT_PIN.equals(authentication.getPassword())) {
            authentication.setAuthenticated(true);
            authentication.addRole("boss");
        } else {
            authentication.setAuthenticated(false);
        }

        return authentication;

    }

}
