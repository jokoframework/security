package io.github.jokoframework.security.services;

import java.util.List;

import io.github.jokoframework.security.JokoJWTClaims;
import io.github.jokoframework.security.JokoJWTExtension.TOKEN_TYPE;
import io.github.jokoframework.security.JokoTokenWrapper;
import io.jsonwebtoken.JwtException;

public interface ITokenService {

    void init();

    JokoTokenWrapper createAndStoreRefreshToken(String user, String appKey, TOKEN_TYPE tokenType,
                                                String userAgent, String remoteIP, List<String> roles);

    JokoTokenWrapper createToken(String user, List<String> roles, TOKEN_TYPE type, int timeout,
                                 String profileKey);

    JokoTokenWrapper createAccessToken(JokoJWTClaims refreshToken);

    JokoTokenWrapper refreshToken(JokoJWTClaims jokoToken, String userAgent, String remoteIP);

    void revokeToken(String jti);

    /**
     * Devuelve false si el token no fue revocado, en cualquier otro caso
     * devuelve true
     * 
     * @param jti
     * @return
     */
    boolean hasBeenRevoked(String jti);

    /**
     * Realiza el parsing del token JWT. Este metodo tira un
     * {@link JwtException} si no se pudo comprobar la firma o el certificado
     * expiro
     * 
     * @param token
     * @return
     */
    JokoJWTClaims parse(String token);

    int deleteExpiredTokens();
}
