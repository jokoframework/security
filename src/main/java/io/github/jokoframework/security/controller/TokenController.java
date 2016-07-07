package io.github.jokoframework.security.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

import io.github.jokoframework.security.ApiPaths;
import io.github.jokoframework.security.JokoTokenWrapper;
import io.github.jokoframework.security.dto.JokoTokenResponse;
import io.github.jokoframework.security.services.ITokenService;
import io.github.jokoframework.security.springex.JokoSecurityContext;
import io.github.jokoframework.security.util.JokoRequestContext;

@RestController
public class TokenController {

    @Autowired
    private ITokenService tokenService;

    @ApiOperation(value = "Crea un token de acceso de usuario", notes = "Dependiendo del security profile utilizado el token se creara con mayor o menor tiempo de expiración. ", position = 4)
    @ApiResponses(value = { @ApiResponse(code = 202, message = "El token se ha creado exitosamente."),
            @ApiResponse(code = 403, message = "En caso de proveerse un refresh token inválido") })
    @ApiImplicitParam(name = SecurityConstants.AUTH_HEADER_NAME, dataType = "String", paramType = "header", required = true, value = "Refresh Token")
    @RequestMapping(value = ApiPaths.TOKEN_USER_ACCESS, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JokoTokenResponse> createTokenUserAccess(HttpServletRequest request) {

        JokoTokenWrapper accessTokenWrapper = tokenService.createAccessToken(JokoSecurityContext.getClaims());
        return new ResponseEntity<>(new JokoTokenResponse(accessTokenWrapper), HttpStatus.OK);

    }

    @ApiOperation(value = "Refresca un token, y vuelve a setear su tiempo de duración", notes = "El token viene en la variable "
            + SecurityConstants.AUTH_HEADER_NAME + " de la  cabecera. "
            + "Si el token es válido y no ha sido revocado se puede refrescar", position = 2)
    @ApiResponses(value = { @ApiResponse(code = 202, message = "El token se ha renovado exitosamente."),
            @ApiResponse(code = 409, message = "En caso de proveerse un parámetro inválido") })
    @ApiImplicitParam(name = SecurityConstants.AUTH_HEADER_NAME, dataType = "String", paramType = "header", required = true, value = "Refresh token")
    @RequestMapping(value = ApiPaths.TOKEN_REFRESH, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JokoTokenResponse> refreshToken(HttpServletRequest httpRequest) {

        JokoRequestContext jokoRequest = new JokoRequestContext(httpRequest);

        JokoTokenWrapper refreshedToken = tokenService.refreshToken(JokoSecurityContext.getClaims(),
                jokoRequest.getUserAgent(), httpRequest.getRemoteAddr());

        return new ResponseEntity<>(new JokoTokenResponse(refreshedToken), HttpStatus.OK);

    }
}
