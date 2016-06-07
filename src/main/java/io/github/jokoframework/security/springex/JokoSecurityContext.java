package io.github.jokoframework.security.springex;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import io.github.jokoframework.security.JokoJWTClaims;
import io.github.jokoframework.security.JokoJWTExtension;
import io.github.jokoframework.security.JokoJWTExtension.TOKEN_TYPE;
import io.github.jokoframework.security.controller.SecurityConstants;
import io.github.jokoframework.security.errors.JokoUnauthenticatedException;

/**
 * <p>
 * Es un wrapper de spring-security para tener conversion de tipos a los objetos
 * propios de Joko-security.
 * </p>
 * <p>
 * La clase es thread safe y permite acceder al contexto de seguridad que se
 * guarda dentro de un {@link ThreadLocal}
 * </p>
 * 
 * @author danicricco
 *
 */
public class JokoSecurityContext {

    public static JokoAuthenticated getPrincipal() {
        JokoAuthenticated auth = (JokoAuthenticated) SecurityContextHolder.getContext().getAuthentication();
        return auth;
    }

    public static JokoJWTClaims getClaims() {
        JokoAuthenticated auth = (JokoAuthenticated) SecurityContextHolder.getContext().getAuthentication();

        JokoJWTClaims claims = (JokoJWTClaims) auth.getCredentials();
        return claims;
    }

    /**
     * Este metodo se comprta como {@link #getClaims()} pero ademas realiza un
     * control extra para comprobar que el dato devuelto nunca sea null. En caso
     * que sea null lanza una excepcion del tipo
     * {@link JokoUnauthenticatedException}
     * 
     * @return
     */
    public static JokoJWTClaims getSafetyClaims() throws JokoUnauthenticatedException {
        JokoJWTClaims claims = getClaims();
        if (claims == null) {
            throw new JokoUnauthenticatedException();
        }
        return claims;
    }

    public static void setAuthentication(JokoAuthenticated authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public static void clearContext() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Traduce los claims hechos con JWT a autorizaciones que son entendibles
     * por Spring-security.
     * <ul>
     * <li>Type REFRESH o REFRESH_C se traduce a:
     * {@link SecurityConstants.AUTHORIZATION_REFRESH}</li>
     * <li>Type REFRESH_C se traduce a:
     * {@link SecurityConstants.AUTHORIZATION_REFRESH_CONSUMER}</li>
     * <li>Type ACCESS se traduce a una autorizacion por cada rol que el usuario
     * haya cargado</li>
     * </ul>
     * {@link JokoWebSecurityConfig}
     * 
     * @param claims
     * @return
     */

    public static List<SimpleGrantedAuthority> determineAuthorizations(JokoJWTClaims claims) {
        ArrayList<SimpleGrantedAuthority> list = new ArrayList<SimpleGrantedAuthority>();
        JokoJWTExtension jokoExtension = claims.getJoko();
        if (jokoExtension.getType().equals(TOKEN_TYPE.REFRESH)
                || jokoExtension.getType().equals(TOKEN_TYPE.REFRESH_C)) {
            // Lo unico que puede hacer con un token refresh es la autorizacion
            // refresh
            list.add(new SimpleGrantedAuthority(SecurityConstants.AUTHORIZATION_REFRESH));
        } else if (jokoExtension.getType().equals(TOKEN_TYPE.ACCESS)) {
            // Agrega todos los roles
            List<String> roles = claims.getJoko().getRoles();
            if (roles != null) {
                for (String r : roles) {
                    list.add(new SimpleGrantedAuthority(r));
                }
            }
        }
        if (jokoExtension.getType().equals(TOKEN_TYPE.REFRESH_C)) {
            list.add(new SimpleGrantedAuthority(SecurityConstants.AUTHORIZATION_REFRESH_CONSUMER));
        }
        return list;
    }

}
