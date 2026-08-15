package io.github.jokoframework.security.springex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import io.github.jokoframework.security.ApiPaths;
import io.github.jokoframework.security.api.JokoAuthorizationManager;
import io.github.jokoframework.security.constantes.SecurityConstants;
import jakarta.servlet.http.HttpSession;

/**
 * Configuración web de Spring Security para joko-security.
 *
 * Esta configuración solo se activa cuando joko.security.web.enabled=true.
 * Si está en false, el proyecto que usa la librería debe proveer su propia
 * configuración de Spring Security.
 *
 * El filtro {@link JokoSecurityFilter} siempre está disponible para ser usado
 * en configuraciones personalizadas.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@ConditionalOnProperty(prefix = "joko.security.web", name = "enabled", havingValue = "true", matchIfMissing = false)
public class JokoWebSecurityConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(JokoWebSecurityConfig.class);

    @Autowired
    private JokoSecurityFilter jokoSecurityFilter;

    @Autowired(required = false)
    private JokoAuthorizationManager jokoAuthorizationManager;

    @Value("${joko.authentication.enable:true}")
    private Boolean authenticationEnable = true;

    /**
     * Spring Security will never create an {@link HttpSession} and it will
     * never use it to obtain the {@link SecurityContext}
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        if (!authenticationEnable) {
            LOGGER.warn(
                    "Authentication module is not enabled!! This configuration should only be used during development");
            http.anonymous(anonymous -> {
            })
                    .authorizeHttpRequests(auth -> auth.requestMatchers("/**").permitAll());
            return http.build();
        }

        http.authorizeHttpRequests(auth -> auth
                // Se tiene acceso al login para que cualquiera pueda intentar un login
                .requestMatchers(ApiPaths.LOGIN).permitAll()
                .requestMatchers(ApiPaths.LOGIN + "/").permitAll()
                .requestMatchers(ApiPaths.TOKEN_INFO).permitAll()
                .requestMatchers(ApiPaths.TOKEN_INFO + "/").permitAll()
                /*
                 * Solo teniendo acceso a un refresh token se puede pedir un access
                 * token, refrescar o hacer un logout
                 */
                // access token
                .requestMatchers(ApiPaths.TOKEN_USER_ACCESS).hasAnyAuthority(SecurityConstants.AUTHORIZATION_REFRESH)
                .requestMatchers(ApiPaths.TOKEN_USER_ACCESS + "/").hasAnyAuthority(SecurityConstants.AUTHORIZATION_REFRESH)
                // refrescar
                .requestMatchers(ApiPaths.TOKEN_REFRESH).hasAnyAuthority(SecurityConstants.AUTHORIZATION_REFRESH)
                .requestMatchers(ApiPaths.TOKEN_REFRESH + "/").hasAnyAuthority(SecurityConstants.AUTHORIZATION_REFRESH)
                // logout
                .requestMatchers(ApiPaths.LOGOUT).hasAnyAuthority(SecurityConstants.AUTHORIZATION_REFRESH)
                .requestMatchers(ApiPaths.LOGOUT + "/").hasAnyAuthority(SecurityConstants.AUTHORIZATION_REFRESH)
                //sessions
                .requestMatchers(ApiPaths.SESSIONS).hasAnyAuthority(SecurityConstants.AUTHORIZATION_REFRESH)
                .requestMatchers(ApiPaths.SESSIONS + "/").hasAnyAuthority(SecurityConstants.AUTHORIZATION_REFRESH)
                //qrcode
                .requestMatchers("/qrcode").permitAll()
        );

        if (jokoAuthorizationManager != null) {
            // Configuracion de URL particular para la aplicacion
            jokoAuthorizationManager.configure(http);
        }

        // Todo el resto queda por default denegado
        http.authorizeHttpRequests(auth -> auth.requestMatchers("/**").denyAll())
                .addFilterBefore(jokoSecurityFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                .authenticationEntryPoint(new Http401UnauthorizedEntryPoint())
                .accessDeniedHandler(new JokoAccessDeniedHandler()))
                .anonymous(anonymous -> {
                })
                .headers(headers -> headers.cacheControl(cache -> {
        }));

        return http.build();
    }
}
