package io.github.jokoframework.security.development;

import io.github.jokoframework.security.api.JokoAuthentication;
import io.github.jokoframework.security.api.JokoAuthenticationManager;
import io.github.jokoframework.security.constantes.SecurityConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enhanced development authentication manager with multiple test users.
 * Provides realistic authentication scenarios for development/testing.
 *
 * DO NOT use in production!
 */
@Component
public class DevAuthenticationManager implements JokoAuthenticationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DevAuthenticationManager.class);

    // Test users: username -> (password, roles, securityProfile)
    private static final Map<String, DevUser> TEST_USERS = new HashMap<>();

    static {
        TEST_USERS.put("admin", new DevUser("admin123",
            List.of("ROLE_ADMIN", "ROLE_USER"), "ADMIN"));
        TEST_USERS.put("testuser", new DevUser("test123",
            List.of("ROLE_USER"), SecurityConstants.DEFAULT_SECURITY_PROFILE));
        TEST_USERS.put("mobileuser", new DevUser("mobile123",
            List.of("ROLE_USER", "ROLE_MOBILE"), "MOBILE"));
        TEST_USERS.put("readonly", new DevUser("readonly123",
            List.of("ROLE_READONLY"), SecurityConstants.DEFAULT_SECURITY_PROFILE));
    }

    @Override
    public JokoAuthentication authenticate(JokoAuthentication authentication) throws AuthenticationException {
        String username = authentication.getUsername();
        String password = authentication.getPassword();

        LOGGER.info("🔐 DevAuthenticationManager - Attempting login for username: {}", username);

        DevUser devUser = TEST_USERS.get(username);

        if (devUser == null) {
            LOGGER.warn("❌ Authentication failed - Unknown user: {}", username);
            throw new BadCredentialsException("Invalid username or password");
        }

        if (!devUser.password.equals(password)) {
            LOGGER.warn("❌ Authentication failed - Invalid password for user: {}", username);
            throw new BadCredentialsException("Invalid username or password");
        }

        // Authentication successful
        LOGGER.info("✅ Authentication successful for user: {} with roles: {} and profile: {}",
            username, devUser.roles, devUser.securityProfile);

        // Create a wrapper with security profile support
        DevJokoAuthentication devAuth = new DevJokoAuthentication(username, devUser.securityProfile);
        devAuth.setSubject(username);
        devUser.roles.forEach(devAuth::addRole);
        devAuth.setAuthenticated(true);

        return devAuth;
    }

    /**
     * Helper class to store dev user information
     */
    private static class DevUser {
        final String password;
        final List<String> roles;
        final String securityProfile;

        DevUser(String password, List<String> roles, String securityProfile) {
            this.password = password;
            this.roles = roles;
            this.securityProfile = securityProfile;
        }
    }
}
