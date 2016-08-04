package io.github.jokoframework.security.dto;

import io.github.jokoframework.common.dto.JokoBaseResponse;
import io.github.jokoframework.security.JokoTokenWrapper;

/**
 * Se utiliza para devolver un token JWT que posee los accesos del usuario
 * 
 * @author danicricco
 *
 */
public class JokoTokenResponse extends JokoBaseResponse {

    private String secret;
    private long expiration;

    public JokoTokenResponse(JokoTokenWrapper tokenWrapper) {
        setSuccess(true);
        this.secret = tokenWrapper.getToken();
        this.expiration = tokenWrapper.getClaims().getExpiration().getTime();
    }

    public JokoTokenResponse(String errorCode) {
        setSuccess(false);
        setErrorCode(errorCode);
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

}
