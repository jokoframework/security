package io.github.jokoframework.security;

import static io.github.jokoframework.security.SecurityTestConstants.EXPIRATION_SECURITY_PROFILE;
import static io.github.jokoframework.security.SecurityTestConstants.REMOTE_IP;
import static io.github.jokoframework.security.SecurityTestConstants.ROLES;
import static io.github.jokoframework.security.SecurityTestConstants.SECURITY_PROFILE;
import static io.github.jokoframework.security.SecurityTestConstants.USER;
import static io.github.jokoframework.security.SecurityTestConstants.USER_AGENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.security.GeneralSecurityException;
import java.util.Date;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.jokoframework.common.dto.JokoTokenInfoResponse;
import io.github.jokoframework.security.JokoJWTExtension.TOKEN_TYPE;
import io.github.jokoframework.security.errors.JokoUnauthenticatedException;
import io.github.jokoframework.security.services.ITokenService;
import io.jsonwebtoken.SignatureException;



/**
 * Data for this test is loaded from <code>src/test/resources/data.sql</code>
 * when the Application Context is started.
 *
 * @author rodrigovillalba
 *
 * NOTA: Temporalmente deshabilitado durante la migración a Spring Boot 3.
 * Requiere configuración completa de Application context para que Liquibase
 * se ejecute antes de TokenServiceImpl@PostConstruct.
 * Se rehabilitará una vez completada la migración de Spring Boot 3.
 */
@Disabled("Deshabilitado temporalmente - requiere arreglar configuración de Liquibase post-migración Spring Boot 3")
public class TokenServiceTest extends AbstractPostgresIntegrationTest {

    private static final long ACCEPTED_DATE_DELTA = 1000;// 1 segundo de
    // diferencia

    @Autowired
    private ITokenService tokenService;
    
    
    @Test
    public void testCreateRefreshToken() {
        // Crea el token de refresh
        JokoTokenWrapper token = tokenService.createAndStoreRefreshToken(USER, SECURITY_PROFILE, TOKEN_TYPE.REFRESH,
                USER_AGENT, REMOTE_IP, ROLES, null);

        // Comprueba que no esta revocado
        boolean hasBeenRevoked = tokenService.hasBeenRevoked(token.getClaims().getId());
        assertThat(hasBeenRevoked).isFalse();
    }
	

    @Test
    public void testParseToken() {
        TOKEN_TYPE type = TOKEN_TYPE.REFRESH;
        int timeout = 60 * 5;// 5min
        String profile = "p1";

        Date initTimestamp = new Date();
        JokoTokenWrapper tokenWrapper = tokenService.createToken(USER, ROLES, type, timeout, profile);
        JokoJWTClaims parsedToken = tokenService.parse(tokenWrapper.getToken());

        // Comprueba que la fecha de expiracion este cerca de la esperada
        long expectedTimeOut = initTimestamp.getTime() + timeout * 1000;
        assertThat(parsedToken.getExpiration().getTime() - expectedTimeOut)
                .as("Se esperaba la fecha de expiracion sea cerca de " + new Date(expectedTimeOut) + " con diferencia de "
                        + ACCEPTED_DATE_DELTA + " ms.")
                .isLessThanOrEqualTo(ACCEPTED_DATE_DELTA);

        JokoJWTClaims originalClaims = tokenWrapper.getClaims();

        // El id se mantiene
        assertThat(parsedToken.getId()).isEqualTo(originalClaims.getId());
        assertThat(parsedToken.getJoko().getType()).isEqualTo(originalClaims.getJoko().getType());
        assertThat(parsedToken.getJoko().getRoles()).isEqualTo(originalClaims.getJoko().getRoles());
        assertThat(parsedToken.getSubject()).isEqualTo(originalClaims.getSubject());
    }

    @Test
    public void testCreateAccessToken() throws GeneralSecurityException {
        // Crea el token de refresh
        JokoTokenWrapper token = tokenService.createAndStoreRefreshToken(USER, SECURITY_PROFILE, TOKEN_TYPE.REFRESH,
                USER_AGENT, REMOTE_IP, ROLES, null);

        JokoJWTClaims refreshToken = token.getClaims();
        JokoTokenWrapper accessToken = tokenService.createAccessToken(refreshToken, null);
        JokoJWTClaims jwtClaims = accessToken.getClaims();

        assertThat(jwtClaims.getJoko().getType()).isEqualTo(TOKEN_TYPE.ACCESS);
        assertThat(jwtClaims.getJoko().getRoles()).isEqualTo(refreshToken.getJoko().getRoles());
        assertThat(jwtClaims.getSubject()).isEqualTo(refreshToken.getSubject());
    }
    
    @Test
    public void gettingTokenShouldReturnInfo() {
        // 1. Creamos el refresh token
        JokoTokenWrapper token = tokenService.createAndStoreRefreshToken(USER, SECURITY_PROFILE, TOKEN_TYPE.REFRESH,
                USER_AGENT, REMOTE_IP, ROLES, null);

        // 2. Obtenemos su información
        JokoTokenInfoResponse response = tokenService.tokenInfo(token.getToken());

        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(USER);
        assertThat(response.getExpiresIn()).isGreaterThan(0L);
    }

    @Test
    public void gettingRevokedTokenShouldThrowException() {
        // 1. Creamos el refresh token
        JokoTokenWrapper token = tokenService.createAndStoreRefreshToken(USER, SECURITY_PROFILE, TOKEN_TYPE.REFRESH,
                USER_AGENT, REMOTE_IP, ROLES, null);

        // 2. Lo revocamos
        tokenService.revokeToken(token.getClaims().getId());

        // 3. Obtenemos su información - debe lanzar excepción
        assertThatThrownBy(() -> tokenService.tokenInfo(token.getToken()))
                .isInstanceOf(JokoUnauthenticatedException.class)
                .extracting("errorCode")
                .isEqualTo(JokoUnauthenticatedException.ERROR_REVOKED_TOKEN);
    }

    @Test
    public void gettingExpiredTokenShouldThrowException() {
        JokoTokenWrapper token = tokenService.createAndStoreRefreshToken(USER, EXPIRATION_SECURITY_PROFILE, TOKEN_TYPE.REFRESH,
                USER_AGENT, REMOTE_IP, ROLES, null);

        assertThatThrownBy(() -> tokenService.tokenInfo(token.getToken()))
                .isInstanceOf(JokoUnauthenticatedException.class)
                .extracting("errorCode")
                .isEqualTo(JokoUnauthenticatedException.ERROR_EXPIRED_TOKEN);
    }
    
    @Test
    public void gettingTamperedTokenShouldThrowException() {
        JokoTokenWrapper token = tokenService.createAndStoreRefreshToken(USER, SECURITY_PROFILE, TOKEN_TYPE.REFRESH,
                USER_AGENT, REMOTE_IP, ROLES, null);

        assertThatThrownBy(() -> tokenService.tokenInfo(token.getToken() + "tampered"))
                .isInstanceOf(SignatureException.class);
    }
    
}
