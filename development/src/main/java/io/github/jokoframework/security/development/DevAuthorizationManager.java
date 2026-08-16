package io.github.jokoframework.security.development;

import io.github.jokoframework.security.JokoJWTClaims;
import io.github.jokoframework.security.api.JokoAuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Development authorization configuration.
 * Configures URL security rules for the development environment.
 */
@Component
public class DevAuthorizationManager implements JokoAuthorizationManager {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // Disable CSRF for stateless REST API
        http.csrf(csrf -> csrf.disable());

        // Disable frame options for H2 console
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        // Add development-specific public endpoints
        // Note: Don't call anyRequest() here - joko-security handles the default deny-all
        // Also, /api/login and /api/token/** are already configured by joko-security
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/actuator/**").permitAll()
            .requestMatchers("/h2-console/**").permitAll()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> authorize(JokoJWTClaims claims,
                                                            Collection<? extends GrantedAuthority> authorization) {
        // For development, just return the default authorization
        return authorization;
    }
}
