package io.github.jokoframework.security.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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

import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

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

    @ApiOperation(value = "Realiza el login de un usuario", notes = "La operación devuelve los datos del usuario y el refresh token que debe ser utilizado. ", position = 1)
    @ApiResponses(value = { @ApiResponse(code = 202, message = "El usuario se ha logueado exitosamente."),
            @ApiResponse(code = 401, message = "El usuario introdujo una credencial inválida.") })
    @ApiImplicitParam(name = SecurityConstants.VERSION_HEADER_NAME, dataType = "String", paramType = "header", required = false, value = "Version", defaultValue = "1.0")
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
            return processLoginSucessfull(httpRequest, jokoRequest, authenticate);
        }

        // Si no excepciono y tampoco se indico como login exitoso entonces se
        // utiliza el default
        LOGGER.warn("The AuthenticationManager " + authenticationManager.getClass().getCanonicalName()
                + " didn't specify the cause of the unauhtentication");

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
            JokoRequestContext jokoRequest, Authentication authenticate) {
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
                TOKEN_TYPE.REFRESH, jokoRequest.getUserAgent(), httpRequest.getRemoteAddr(), roles);

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

    @ApiOperation(value = "Realiza un logout del usuario", notes = "Este metodo revoca el token (si es aún válido) que está siendo utilizado", position = 3)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "El token se ha eliminado exitosamente."),
            @ApiResponse(code = 409, message = "En caso de proveerse un parámetro inválido") })
    @ApiImplicitParam(name = SecurityConstants.AUTH_HEADER_NAME, dataType = "String", paramType = "header", required = true, value = "Refresh Token")
    @RequestMapping(value = ApiPaths.LOGOUT, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JokoBaseResponse> logout() {

        tokenService.revokeToken(JokoSecurityContext.getClaims().getId());
        return new ResponseEntity<>(new JokoBaseResponse(true), HttpStatus.ACCEPTED);

    }
}
