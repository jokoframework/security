package io.github.jokoframework.security.errors;

/**
 * Indica que el usuario no tiene permitido realizar la operación
 * 
 * @author danicricco
 *
 */
public class JokoUnauthorizedException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -4947401727462048481L;

    public JokoUnauthorizedException(String msg) {
        super(msg);
    }

    public JokoUnauthorizedException() {

    }
}
