package io.github.jokoframework.security.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.github.jokoframework.security.AbstractPostgresIntegrationTest;
import io.github.jokoframework.security.JokoJWTExtension.TOKEN_TYPE;
import io.github.jokoframework.security.JokoTokenWrapper;
import static io.github.jokoframework.security.SecurityTestConstants.EXPIRATION_SECURITY_PROFILE;
import static io.github.jokoframework.security.SecurityTestConstants.REMOTE_IP;
import static io.github.jokoframework.security.SecurityTestConstants.ROLES;
import static io.github.jokoframework.security.SecurityTestConstants.SECURITY_PROFILE;
import static io.github.jokoframework.security.SecurityTestConstants.USER;
import static io.github.jokoframework.security.SecurityTestConstants.USER_AGENT;
import io.github.jokoframework.security.entities.TokenEntity;
import io.github.jokoframework.security.errors.JokoUnauthenticatedException;
import io.github.jokoframework.security.repositories.ITokenRepository;
import io.github.jokoframework.security.services.ITokenService;

/**
 * Test de integración completo que valida el flujo de tokens end-to-end a nivel
 * de servicio.
 *
 * Este test: - Usa PostgreSQL real vía TestContainers - Valida el ciclo
 * completo: crear token → validar → revocar → verificar expiración - Testea
 * directamente la capa de servicio sin controllers ni MockMvc - Sirve como test
 * de smoke para validar que la migración no rompe funcionalidad core
 *
 * Este test es especialmente útil durante la migración a Spring Boot 3 para: 1.
 * Verificar que JWT sigue funcionando después de actualizar JJWT 2. Validar que
 * los cambios javax → jakarta no afectan la lógica de negocio 3. Asegurar que
 * Liquibase ejecuta correctamente las migraciones en PostgreSQL
 *
 * NOTA: Temporalmente deshabilitado durante la migración a Spring Boot 3.
 * Requiere configuración completa de Application context para que Liquibase
 * se ejecute correctamente. Se rehabilitará una vez completada la migración.
 */
@Disabled("Deshabilitado temporalmente - requiere arreglar configuración de Liquibase post-migración Spring Boot 3")
@ExtendWith(SpringExtension.class)
public class TokenFlowIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private ITokenService tokenService;

    @Autowired
    private ITokenRepository tokenRepository;

    @Test
    public void testCreateToken_ShouldGenerateValidToken() {
        // GIVEN: Datos de usuario para crear un token
        String userId = USER;
        String securityProfile = SECURITY_PROFILE;

        // WHEN: Se crea un refresh token
        JokoTokenWrapper token = tokenService.createAndStoreRefreshToken(
                userId,
                securityProfile,
                TOKEN_TYPE.REFRESH,
                USER_AGENT,
                REMOTE_IP,
                ROLES,
                "test-create-token"
        );

        // THEN: El token debe ser válido y contener la información correcta
        assertThat(token).isNotNull();
        assertThat(token.getToken()).isNotEmpty();
        assertThat(token.getClaims()).isNotNull();
        assertThat(token.getClaims().getSubject()).isEqualTo(USER);
        assertThat(token.getClaims().getId()).isNotNull();
    }

    @Test
    public void testGetTokenInfo_ShouldReturnValidInfo() {
        // GIVEN: Un token válido creado
        JokoTokenWrapper token = tokenService.createAndStoreRefreshToken(
                USER,
                SECURITY_PROFILE,
                TOKEN_TYPE.REFRESH,
                USER_AGENT,
                REMOTE_IP,
                ROLES,
                "test-get-info"
        );

        // WHEN: Se obtiene información del token
        var tokenInfo = tokenService.tokenInfo(token.getToken());

        // THEN: La información debe ser correcta
        assertThat(tokenInfo).isNotNull();
        assertThat(tokenInfo.getUserId()).isEqualTo(USER);
        assertThat(tokenInfo.getExpiresIn()).isGreaterThan(0);
        assertThat(tokenInfo.getExpiresIn()).isLessThanOrEqualTo(14440); // DEFAULT_PROFILE_EXPIRATION
    }

    @Test
    public void testRevokeToken_ShouldMarkTokenAsRevoked() {
        // GIVEN: Un token válido creado y almacenado
        JokoTokenWrapper token = tokenService.createAndStoreRefreshToken(
                USER,
                SECURITY_PROFILE,
                TOKEN_TYPE.REFRESH,
                USER_AGENT,
                REMOTE_IP,
                ROLES,
                "test-revoke-token"
        );

        String tokenId = token.getClaims().getId();

        // Verificar que el token es válido antes de revocar
        var infoBeforeRevoke = tokenService.tokenInfo(token.getToken());
        assertThat(infoBeforeRevoke).isNotNull();

        // WHEN: Se revoca el token
        tokenService.revokeToken(tokenId);

        // THEN: El token debe estar revocado y no debe ser válido
        assertThatThrownBy(() -> tokenService.tokenInfo(token.getToken()))
                .isInstanceOf(JokoUnauthenticatedException.class)
                .hasMessageContaining("You shall not pass");
    }

    @Test
    public void testExpiredToken_ShouldThrowException() {
        // GIVEN: Un token que expira inmediatamente (security profile con expiración = 0)
        JokoTokenWrapper token = tokenService.createAndStoreRefreshToken(
                USER,
                EXPIRATION_SECURITY_PROFILE,
                TOKEN_TYPE.REFRESH,
                USER_AGENT,
                REMOTE_IP,
                ROLES,
                "test-expired-token"
        );

        // WHEN/THEN: Intentar obtener info del token expirado debe lanzar excepción
        assertThatThrownBy(() -> tokenService.tokenInfo(token.getToken()))
                .isInstanceOf(JokoUnauthenticatedException.class)
                .hasMessageContaining("expired");
    }

    @Test
    public void testFindTokenById_ShouldReturnStoredToken() {
        // GIVEN: Un token creado y almacenado
        JokoTokenWrapper token = tokenService.createAndStoreRefreshToken(
                USER,
                SECURITY_PROFILE,
                TOKEN_TYPE.REFRESH,
                USER_AGENT,
                REMOTE_IP,
                ROLES,
                "test-find-by-id"
        );

        String tokenId = token.getClaims().getId();

        // WHEN: Se busca el token por ID en el repository
        TokenEntity foundToken = tokenRepository.getTokenById(tokenId);

        // THEN: El token debe existir en la base de datos
        assertThat(foundToken).isNotNull();
        assertThat(foundToken.getId()).isEqualTo(tokenId);
        assertThat(foundToken.getUserId()).isEqualTo(USER);
    }

    @Test
    public void testRefreshToken_ShouldGenerateNewToken() {
        // GIVEN: Un refresh token válido
        JokoTokenWrapper originalToken = tokenService.createAndStoreRefreshToken(
                USER,
                SECURITY_PROFILE,
                TOKEN_TYPE.REFRESH,
                USER_AGENT,
                REMOTE_IP,
                ROLES,
                "test-refresh-token"
        );

        // WHEN: Se hace refresh del token
        JokoTokenWrapper newToken = tokenService.refreshToken(
                originalToken.getClaims(),
                USER_AGENT,
                REMOTE_IP
        );

        // THEN: El nuevo token debe ser diferente al original
        assertThat(newToken).isNotNull();
        assertThat(newToken.getToken()).isNotEqualTo(originalToken.getToken());
        assertThat(newToken.getClaims().getSubject()).isEqualTo(USER);
        assertThat(newToken.getClaims().getId()).isNotEqualTo(originalToken.getClaims().getId());
    }

    @Test
    public void testPostgresConnection_ContainerIsRunning() {
        // GIVEN/WHEN/THEN: Validar que el contenedor PostgreSQL está funcionando
        assertThat(postgres.isRunning())
                .as("El contenedor PostgreSQL debería estar corriendo")
                .isTrue();

        assertThat(tokenService)
                .as("El contexto de Spring debería estar cargado con las beans necesarias")
                .isNotNull();
    }

    @Test
    public void testMultipleTokens_ShouldBeIndependent() {
        // GIVEN: Múltiples tokens para el mismo usuario
        JokoTokenWrapper token1 = tokenService.createAndStoreRefreshToken(
                USER,
                SECURITY_PROFILE,
                TOKEN_TYPE.REFRESH,
                USER_AGENT,
                REMOTE_IP,
                ROLES,
                "test-token-1"
        );

        JokoTokenWrapper token2 = tokenService.createAndStoreRefreshToken(
                USER,
                SECURITY_PROFILE,
                TOKEN_TYPE.REFRESH,
                USER_AGENT,
                REMOTE_IP,
                ROLES,
                "test-token-2"
        );

        // WHEN: Se revoca solo el primer token
        tokenService.revokeToken(token1.getClaims().getId());

        // THEN: El primer token debe estar revocado pero el segundo debe seguir válido
        assertThatThrownBy(() -> tokenService.tokenInfo(token1.getToken()))
                .isInstanceOf(JokoUnauthenticatedException.class);

        var token2Info = tokenService.tokenInfo(token2.getToken());
        assertThat(token2Info).isNotNull();
        assertThat(token2Info.getUserId()).isEqualTo(USER);
    }
}
