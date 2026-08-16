package io.github.jokoframework.security.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import io.github.jokoframework.common.errors.JokoApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.github.jokoframework.common.dto.JokoBaseResponse;
import io.github.jokoframework.security.ApiPaths;
import io.github.jokoframework.security.JokoJWTExtension.TOKEN_TYPE;
import io.github.jokoframework.security.JokoTokenWrapper;
import io.github.jokoframework.security.api.JokoAuthentication;
import io.github.jokoframework.security.api.JokoAuthenticationManager;
import io.github.jokoframework.security.dto.request.AuthenticationRequest;
import io.github.jokoframework.security.dto.JokoTokenResponse;
import io.github.jokoframework.security.services.ITokenService;
import io.github.jokoframework.security.springex.AuthenticationSpringWrapper;
import io.github.jokoframework.security.springex.JokoSecurityContext;
import io.github.jokoframework.security.util.JokoRequestContext;

@RestController
public class AuthenticationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired(required = false)
    private AuthenticationManager authenticationManager;

    @Autowired(required = false)
    private JokoAuthenticationManager jokoAuthenticationManager;

    @Autowired
    private ITokenService tokenService;

    @Operation(summary = "Realiza el login de un usuario", description = "La operación devuelve los datos del usuario y el refresh token que debe ser utilizado. ")
    @ApiResponses(value = { @ApiResponse(responseCode = "202", description = "El usuario se ha logueado exitosamente."),
            @ApiResponse(responseCode = "401", description = "El usuario introdujo una credencial inválida.") })
    @Parameter(name = SecurityConstants.VERSION_HEADER_NAME, in = ParameterIn.HEADER, required = false, description = "Version"/* defaultValue not supported in OpenAPI 3 */)
    @RequestMapping(value = ApiPaths.LOGIN, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JokoTokenResponse> login(@RequestBody @Valid AuthenticationRequest loginRequest,
            HttpServletRequest httpRequest) throws JokoApplicationException {

        LOGGER.trace("Authenticating request for " + loginRequest.getUsername());

        JokoRequestContext jokoRequest = new JokoRequestContext(httpRequest);

        Authentication authenticate;
        try {
            if (jokoAuthenticationManager != null) {
                authenticate = jokoAuthenticationManager.authenticate(new AuthenticationSpringWrapper(loginRequest));
            } else {
                authenticate = authenticationManager.authenticate(new AuthenticationSpringWrapper(loginRequest));
            }
        } catch (Exception e) {
            return processUnauthenticated(e);
        }

        if (authenticate != null && authenticate.isAuthenticated()) {
            return processLoginSucessfull(httpRequest, jokoRequest, authenticate, loginRequest.getSeed());
        }

        if(authenticationManager != null ) {
            // Si no excepciono y tampoco se indico como login exitoso entonces se
            // utiliza el default
            LOGGER.warn("The AuthenticationManager " + authenticationManager.getClass().getCanonicalName()
                    + " didn't specify the cause of the unauhtentication");
        }

        return new ResponseEntity<>(new JokoTokenResponse(SecurityConstants.ERROR_BAD_CREDENTIALS),
                HttpStatus.UNAUTHORIZED);

    }

    /**
     * 
     * En caso que haya sido un login exitoso
     * 
     * @param httpRequest
     * @param jokoRequest
     * @param authenticate
     * @return
     */
    private ResponseEntity<JokoTokenResponse> processLoginSucessfull(HttpServletRequest httpRequest,
            JokoRequestContext jokoRequest, Authentication authenticate, String seed) {
        String securityProfile = null;
        List<String> roles = null;
        if (authenticate instanceof JokoAuthentication) {
            JokoAuthentication jokoAuthentication = (JokoAuthentication) authenticate;
            securityProfile = jokoAuthentication.getSecurityProfile();
            roles = jokoAuthentication.getRoles();
        }
        if (securityProfile == null) {
            LOGGER.warn(
                    "Using default security profile. Please consider returning a securityProfile from your JokoAuthentication");
            securityProfile = SecurityConstants.DEFAULT_SECURITY_PROFILE;
        }

        JokoTokenWrapper token = tokenService.createAndStoreRefreshToken(authenticate.getName(), securityProfile,
                TOKEN_TYPE.REFRESH, jokoRequest.getUserAgent(), httpRequest.getRemoteAddr(), roles, seed);

        return new ResponseEntity<>(new JokoTokenResponse(token), HttpStatus.OK);
    }

    /**
     * En caso de que el AuthenticationManager haya respetado el contrato y
     * lanzado una excepcion
     * 
     * @param e
     * @return
     * @throws Exception
     */
    private ResponseEntity<JokoTokenResponse> processUnauthenticated(Exception e) throws JokoApplicationException {
        String errorCode;
        if (e instanceof DisabledException) {
            errorCode = SecurityConstants.ERROR_ACCOUNT_DISABLED;
        } else if (e instanceof LockedException) {
            errorCode = SecurityConstants.ERROR_ACCOUNT_LOCKED;
        } else if (e instanceof BadCredentialsException) {
            errorCode = SecurityConstants.ERROR_BAD_CREDENTIALS;
        } else {
            // No sabe como procesar esta exception, por lo tanto la pasa a la
            // siguiente capa
            throw new JokoApplicationException(e);
        }
        return new ResponseEntity<>(new JokoTokenResponse(errorCode), HttpStatus.UNAUTHORIZED);
    }

    @Operation(summary = "Realiza un logout del usuario", description = "Este metodo revoca el token (si es aún válido) que está siendo utilizado")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "El token se ha eliminado exitosamente."),
            @ApiResponse(responseCode = "409", description = "En caso de proveerse un parámetro inválido") })
    @Parameter(name = SecurityConstants.AUTH_HEADER_NAME, in = ParameterIn.HEADER, required = true, description = "Refresh Token")
    @RequestMapping(value = ApiPaths.LOGOUT, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JokoBaseResponse> logout() {

        tokenService.revokeToken(JokoSecurityContext.getClaims().getId());
        return new ResponseEntity<>(new JokoBaseResponse(true), HttpStatus.ACCEPTED);

    }
}
