package io.github.jokoframework.security.api;

import java.util.Collection;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;

import io.github.jokoframework.security.JokoJWTClaims;

public interface JokoAuthorizationManager {

    /**
     * Este metodo sigue los mismos principios que un
     * {@link WebSecurityConfigurerAdapter}, solamente que ya se incluyen
     * configuraciones por default y solamente debería de enfocarse en las
     * particularidades de los URL del sitio a definir.
     * 
     * @param http
     * @throws Exception
     */
    public void configure(HttpSecurity http) throws Exception;

    /**
     * <p>
     * Si el usuario esta autenticado este metodo será ejecutado para
     * personalizar la autorizacion. Una implementacion sencilla puede ser
     * simplemente devolver el parámetro authorization
     * </p>
     * <p>
     * La lista de autorizaciones estará precargada de acuerdo a las reglas de
     * autorizaciones por defecto de Joko-security.
     * </p>
     * 
     * @param claims
     * @param authorization
     *            La lista de autorizationes concedidas por default a usuarios
     *            con este tipo de tokens. Esta debería de ser la base para la
     *            lista a retornar
     * @return
     */
    public Collection<? extends GrantedAuthority> authorize(JokoJWTClaims claims,
            Collection<? extends GrantedAuthority> authorization);

}
