package io.github.jokoframework.security.springex;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import io.github.jokoframework.common.dto.JokoBaseResponse;
import io.github.jokoframework.security.controller.SecurityConstants;
import io.github.jokoframework.security.errors.JokoUnauthenticatedException;
import io.github.jokoframework.security.errors.JokoUnauthorizedException;

@ControllerAdvice
public class CommonErrorController {

    @ExceptionHandler(JokoUnauthorizedException.class)
    public ResponseEntity<JokoBaseResponse> handleError(JokoUnauthorizedException e) {
        JokoBaseResponse response = new JokoBaseResponse(SecurityConstants.ERROR_NOT_ALLOWED);
        response.setMessage(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(JokoUnauthenticatedException.class)
    public ResponseEntity<JokoBaseResponse> handleError(JokoUnauthenticatedException e) {
        JokoBaseResponse response = new JokoBaseResponse(e.getErrorCode());
        response.setMessage(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
}
