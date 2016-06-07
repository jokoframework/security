package io.github.jokoframework.security.springex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import io.github.jokoframework.security.ApiPaths;
import io.github.jokoframework.security.api.JokoAuthorizationManager;
import io.github.jokoframework.security.controller.SecurityConstants;
import io.github.jokoframework.security.services.ITokenService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
// TODO esto tiene que migrar a una clase separada
public class JokoWebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(JokoWebSecurityConfig.class);
    @Autowired
    private ITokenService tokenService;

    @Autowired(required = false)
    private JokoAuthorizationManager jokoAuthorizationManager;

    @Value("${joko.authentication.enable}")
    private Boolean authenticationEnable = true;

    public JokoWebSecurityConfig() {
        super(true);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        if (!authenticationEnable) {
            logger.warn(
                    "Authentication module is not enabled!! This configuration should only be used during development");
            http.anonymous().and().authorizeRequests().antMatchers("/**").permitAll();
            return;
        }

        http.authorizeRequests()

        // Se tiene acceso al login para que cualquiera pueda intentarp
        // un login
                .antMatchers(ApiPaths.LOGIN).permitAll().antMatchers(ApiPaths.LOGIN + "/").permitAll()

        /*
         * Solo teniendo acceso a un refresh token se puede pedir un access
         * token, refrescar o hacer un logout
         */
                // access token
                .antMatchers(ApiPaths.TOKEN_USER_ACCESS).hasAnyAuthority(SecurityConstants.AUTHORIZATION_REFRESH)
                .antMatchers(ApiPaths.TOKEN_USER_ACCESS + "/").hasAnyAuthority(SecurityConstants.AUTHORIZATION_REFRESH)
                // refrescar
                .antMatchers(ApiPaths.TOKEN_REFRESH).hasAnyAuthority(SecurityConstants.AUTHORIZATION_REFRESH)
                .antMatchers(ApiPaths.TOKEN_REFRESH + "/").hasAnyAuthority(SecurityConstants.AUTHORIZATION_REFRESH)
                // logout
                .antMatchers(ApiPaths.LOGOUT).hasAnyAuthority(SecurityConstants.AUTHORIZATION_REFRESH)
                .antMatchers(ApiPaths.LOGOUT + "/").hasAnyAuthority(SecurityConstants.AUTHORIZATION_REFRESH);

        if (jokoAuthorizationManager != null) {
            // Configuracion de URL particular para la aplicacion
            jokoAuthorizationManager.configure(http);

        }

        // Todo el resto queda por default denegado
        http.authorizeRequests().antMatchers("/**").denyAll().and()

        .addFilterBefore(new JokoSecurityFilter(tokenService, jokoAuthorizationManager),
                UsernamePasswordAuthenticationFilter.class)

        .sessionManagement().
                // Spring Security will never create an {@link HttpSession} and
                // it will never use it to obtain the {@link SecurityContext}
        sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().exceptionHandling()
                .authenticationEntryPoint(new Http401UnauthorizedEntryPoint())
                .accessDeniedHandler(new JokoAccessDeniedHandler()).and().anonymous().and().servletApi().and().headers()
                .cacheControl();

    }
}
