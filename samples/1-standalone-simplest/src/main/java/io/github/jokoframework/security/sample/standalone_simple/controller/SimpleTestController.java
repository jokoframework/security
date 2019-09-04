package io.github.jokoframework.security.sample.standalone_simple.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import io.github.jokoframework.security.controller.SecurityConstants;
import io.github.jokoframework.security.errors.JokoUnauthenticatedException;
import io.github.jokoframework.security.errors.JokoUnauthorizedException;

@RestController
public class SimpleTestController {

    private class SimpleDTO {
        private String s;

        public SimpleDTO(String s) {
            this.s = s;
        }

        @SuppressWarnings("unused")
        public String getS() {
            return s;
        }

        @SuppressWarnings("unused")
        public void setS(String s) {
            this.s = s;
        }

    }

    @ApiOperation(value = "Prueba de metodo con permiso", notes = "Devuelve Hola Mundo si funciono la llamada", position = 1)
    @RequestMapping(value = "/api/test-public", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleDTO> test(HttpServletRequest httpRequest) {
        return new ResponseEntity<SimpleDTO>(new SimpleDTO("Hola mundo open"), HttpStatus.OK);
    }

    @ApiOperation(value = "Prueba de metodo con permiso", notes = "Devuelve Hola Mundo si funciono la llamada", position = 1)
    @RequestMapping(value = "/api/test-private", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = SecurityConstants.AUTH_HEADER_NAME, dataType = "String", paramType = "header", required = true, value = "Refresh Token")
    public ResponseEntity<SimpleDTO> test2(HttpServletRequest httpRequest) {
        return new ResponseEntity<SimpleDTO>(new SimpleDTO("Hola mundo secure"), HttpStatus.OK);
    }

    @ApiOperation(value = "Prueba de metodo que tira un error de no autorizado", notes = "Devuelve 403 siempre. Muestra que se puede simplemente lanzar una excepcion", position = 1)
    @RequestMapping(value = "/api/test-noautorizado", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleDTO> test3(HttpServletRequest httpRequest) {

        throw new JokoUnauthorizedException("Probando que devuelve error");
    }

    @ApiOperation(value = "Prueba de metodo que tira un error de no autorizado", notes = "Devuelve 403 siempre. Muestra que se puede simplemente lanzar una excepcion", position = 1)
    @RequestMapping(value = "/api/test-noautenticado", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleDTO> test4(HttpServletRequest httpRequest) {

        throw new JokoUnauthenticatedException(JokoUnauthenticatedException.ERROR_CODE_WRONG_CREDENTIALS);
    }
}
