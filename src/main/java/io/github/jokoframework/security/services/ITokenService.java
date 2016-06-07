package io.github.jokoframework.security.services;

import java.util.List;

import io.github.jokoframework.security.JokoJWTClaims;
import io.github.jokoframework.security.JokoJWTExtension.TOKEN_TYPE;
import io.github.jokoframework.security.JokoTokenWrapper;
import io.jsonwebtoken.JwtException;

public interface ITokenService {

    public void init();

    public JokoTokenWrapper createAndStoreRefreshToken(String user, String appKey, TOKEN_TYPE tokenType,
            String userAgent, String remoteIP, List<String> roles);

    public JokoTokenWrapper createToken(String user, List<String> roles, TOKEN_TYPE type, int timeout,
            String profileKey);

    public JokoTokenWrapper createAccessToken(JokoJWTClaims refreshToken);

    public JokoTokenWrapper refreshToken(JokoJWTClaims jokoToken, String userAgent, String remoteIP);

    public void revokeToken(String jti);

    /**
     * Devuelve false si el token no fue revocado, en cualquier otro caso
     * devuelve true
     * 
     * @param jti
     * @return
     */
    public boolean hasBeenRevoked(String jti);

    /**
     * Realiza el parsing del token JWT. Este metodo tira un
     * {@link JwtException} si no se pudo comprobar la firma o el certificado
     * expiro
     * 
     * @param token
     * @return
     */
    public JokoJWTClaims parse(String token);

    public int deleteExpiredTokens();
}
