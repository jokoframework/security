package io.github.jokoframework.security.springex;

import java.io.IOException;
import java.util.Collection;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.jokoframework.common.JokoUtils;
import io.github.jokoframework.security.JokoJWTClaims;
import io.github.jokoframework.security.api.JokoAuthorizationManager;
import io.github.jokoframework.security.controller.SecurityConstants;
import io.github.jokoframework.security.services.ITokenService;
import io.jsonwebtoken.JwtException;

/**
 * Comprueba los requests hechos en busca del token de autenticación. El token
 * de autenticación se encuentra siempre en
 * {@value SecurityConstants#AUTH_HEADER_NAME}.
 *
 * Si se encuentra un token se llamara al autoriza
 *
 * @author danicricco
 *
 */
public class JokoSecurityFilter extends OncePerRequestFilter {

    private static final Logger JOKO_LOGGER = LoggerFactory.getLogger(JokoSecurityFilter.class);
    private ITokenService tokenService;

    private JokoAuthorizationManager jokoAuthorizationManager;

    public JokoSecurityFilter(ITokenService tokenService, JokoAuthorizationManager jokoAuthorizationManager) {
        this.tokenService = tokenService;
        this.jokoAuthorizationManager = jokoAuthorizationManager;
    }

    public static String getTokenFromHeader(HttpServletRequest pRequest) {
        String token = pRequest.getHeader(SecurityConstants.AUTH_HEADER_NAME);
        return token;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        JokoJWTClaims claims = validateToken(request);
        if (claims != null) {

            Collection<? extends GrantedAuthority> baseAuthorizations = JokoSecurityContext.determineAuthorizations(claims);
            Collection<? extends GrantedAuthority> authorities = baseAuthorizations;

            if (jokoAuthorizationManager != null) {
                authorities = jokoAuthorizationManager.authorize(claims, baseAuthorizations);
            }

            JokoAuthenticated authentication = new JokoAuthenticated(claims, authorities);
            JokoSecurityContext.setAuthentication(authentication);

            if (JOKO_LOGGER.isDebugEnabled()) {

                String uri = request.getRequestURI();

                JOKO_LOGGER.debug("Authorized user " + JokoUtils.formatLogString(claims.getSubject()) + " to: "
                        + JokoUtils.join(authorities, ",") + " Request-URI " + uri + " jti " + claims.getId());
            }

        } else {
            JokoSecurityContext.clearContext();
        }
        filterChain.doFilter(request, response);
        JokoSecurityContext.clearContext();

    }

    /**
     * Si el token es valido retorna un {@link JokoJWTClaims}. Si el token NO es
     * valido retorna null
     *
     * @param request
     * @return
     */
    private JokoJWTClaims validateToken(HttpServletRequest request) {
        String token = getTokenFromHeader(request);
        if (token == null) {
            return null;
        }

        try {
        	return tokenService.tokenInfoAsClaims(token).orElse(null);
        } catch (JwtException | IllegalArgumentException e) {

            String uri = request.getRequestURI();
            String userAgent = request.getHeader("User-Agent");
            JOKO_LOGGER.debug(uri + " from User-Agent: " + userAgent + " Unable to authenticate " + e.getClass() + ": "
                    + e.getMessage());
            JOKO_LOGGER.debug("Token received: " + token);
            JOKO_LOGGER.trace("Error validando el token.", e);
            return null;
        }
    }

	
}
