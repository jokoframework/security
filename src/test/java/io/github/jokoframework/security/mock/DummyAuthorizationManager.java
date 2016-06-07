package io.github.jokoframework.security.mock;

import java.util.Collection;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import io.github.jokoframework.security.JokoJWTClaims;
import io.github.jokoframework.security.api.JokoAuthorizationManager;

//@Component
public class DummyAuthorizationManager implements JokoAuthorizationManager {

    @Override
    public void configure(HttpSecurity http) throws Exception {

    }

    @Override
    public Collection<? extends GrantedAuthority> authorize(JokoJWTClaims claims,
            Collection<? extends GrantedAuthority> authorization) {
        return authorization;
    }

}
