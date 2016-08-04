package io.github.jokoframework.security.sample.standalone_simple.config;

import java.util.Collection;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import io.github.jokoframework.security.JokoJWTClaims;
import io.github.jokoframework.security.api.JokoAuthorizationManager;

@Component
public class SimpleAuthorization implements JokoAuthorizationManager {

    

    @Override
    public void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                // Permite el acceso al api de swagger
                .antMatchers("/swagger/**").permitAll().antMatchers("/api-docs").permitAll().antMatchers("/api-docs/**")
                .permitAll()
                // Cualquier puede ejecutar el api/test-public
                .antMatchers("/api/test-public").permitAll().antMatchers("/api/test-public/").permitAll()
                .antMatchers("/api/test-noautorizado").permitAll().antMatchers("/api/test-noautorizado/").permitAll()
                .antMatchers("/api/test-noautenticado").permitAll().antMatchers("/api/test-noautenticado/").permitAll()
                
        // solo el jefe logueado con la autorizacion boss
                .antMatchers("/api/test-private").hasAnyAuthority("boss");

    }

    @Override
    public Collection<? extends GrantedAuthority> authorize(JokoJWTClaims claims,
            Collection<? extends GrantedAuthority> authorization) {
        
        return authorization;
    }

}
