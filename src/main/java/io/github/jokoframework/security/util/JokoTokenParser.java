package io.github.jokoframework.security.util;

import java.io.IOException;

import io.github.jokoframework.security.JokoJWTClaims;

/**
 * Esta clase permite validar un token si es que se cuenta con el secreto. El
 * objetivo es poder leer el secreto desde un archivo y validar tokens de manera
 * independiente al framework Spring
 * 
 * @author danicricco
 *
 */
public class JokoTokenParser {

    private String base64EncodedKeyBytes;

    public JokoTokenParser(String filePath) throws IOException {
        this.base64EncodedKeyBytes = SecurityUtils.readFileToBase64(filePath);
    }

    public JokoJWTClaims parse(String token) {
        return SecurityUtils.parseToken(token, base64EncodedKeyBytes);
    }
}
