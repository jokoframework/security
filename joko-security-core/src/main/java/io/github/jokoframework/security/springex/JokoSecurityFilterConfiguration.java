package io.github.jokoframework.security.springex;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.jokoframework.security.api.JokoAuthorizationManager;
import io.github.jokoframework.security.services.ITokenService;

/**
 * Configuración del filtro de seguridad JWT.
 *
 * Esta configuración se activa cuando joko.authentication.enable=true (valor
 * por defecto). Provee el {@link JokoSecurityFilter} para que pueda ser usado
 * tanto por la configuración web automática de joko-security como por
 * configuraciones personalizadas en proyectos que usan la librería.
 *
 * El filtro se puede inyectar en configuraciones de Spring Security
 * personalizadas:
 *
 * <pre>
 * {@code
 * @Configuration
 * public class CustomSecurityConfig {
 *
 *     @Autowired
 *     private JokoSecurityFilter jokoSecurityFilter;
 *
 *     @Bean
 *     public SecurityFilterChain securityFilterChain(HttpSecurity http) {
 *         return http
 *             .addFilterBefore(jokoSecurityFilter, UsernamePasswordAuthenticationFilter.class)
 *             .build();
 *     }
 * }
 * }
 * </pre>
 */
@Configuration
@ConditionalOnProperty(name = "joko.authentication.enable", havingValue = "true", matchIfMissing = false)
public class JokoSecurityFilterConfiguration {

    @Autowired
    private ITokenService tokenService;

    @Autowired(required = false)
    private JokoAuthorizationManager jokoAuthorizationManager;

    /**
     * Provee el filtro de seguridad JWT para validación de tokens.
     *
     * @return instancia configurada de {@link JokoSecurityFilter}
     */
    @Bean
    public JokoSecurityFilter jokoSecurityFilter() {
        return new JokoSecurityFilter(tokenService, jokoAuthorizationManager);
    }
}
