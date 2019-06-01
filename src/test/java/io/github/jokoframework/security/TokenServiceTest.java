package io.github.jokoframework.security;

import static io.github.jokoframework.security.SecurityTestConstants.EXPIRATION_SECURITY_PROFILE;
import static io.github.jokoframework.security.SecurityTestConstants.REMOTE_IP;
import static io.github.jokoframework.security.SecurityTestConstants.ROLES;
import static io.github.jokoframework.security.SecurityTestConstants.SECURITY_PROFILE;
import static io.github.jokoframework.security.SecurityTestConstants.USER;
import static io.github.jokoframework.security.SecurityTestConstants.USER_AGENT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.security.GeneralSecurityException;
import java.util.Date;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import io.github.jokoframework.common.dto.JokoTokenInfoResponse;
import io.github.jokoframework.security.JokoJWTExtension.TOKEN_TYPE;
import io.github.jokoframework.security.errors.JokoUnauthenticatedException;
import io.github.jokoframework.security.services.ITokenService;
import io.jsonwebtoken.SignatureException;



/**
 * Data for this test is loaded from <code>src/test/resources/data.sql</code>
 * when the Application Context is started.
 * 
 * 
 * @author rodrigovillalba
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@Transactional
public class TokenServiceTest {

    private static final long ACCEPTED_DATE_DELTA = 1000;// 1 segundo de
    // diferencia

    @Autowired
    private ITokenService tokenService;

    
   
    @Rule
    public ExpectedException error = ExpectedException.none();
    
    
    @Test
    /**
     * Comprueba que se cree un token en la BD
     */
    public void testCreateRefreshToken() {
        
    	// Crea el token de refresh
        JokoTokenWrapper token = tokenService.createAndStoreRefreshToken(USER, SECURITY_PROFILE, TOKEN_TYPE.REFRESH,
                USER_AGENT, REMOTE_IP, ROLES, "a");

        // Comprueba que no esta revocado
        boolean hasBeenRevoked = tokenService.hasBeenRevoked(token.getClaims().getId());
        assertFalse(hasBeenRevoked);

    }
	

    /**
     * Comprueba que puede crear un token y volver a parsearlo
     */
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
        assertTrue(
                "Se esperaba la fecha de expiracion sea cerca de " + new Date(expectedTimeOut) + " con diferencia de "
                        + ACCEPTED_DATE_DELTA + " ms.",
                parsedToken.getExpiration().getTime() - expectedTimeOut <= ACCEPTED_DATE_DELTA);

        JokoJWTClaims originalClaims = tokenWrapper.getClaims();
        
        // El id se mantiene
        assertEquals(originalClaims.getId(), parsedToken.getId());
        assertEquals(originalClaims.getJoko().getType(), parsedToken.getJoko().getType());
        assertEquals(originalClaims.getJoko().getRoles(), parsedToken.getJoko().getRoles());
        assertEquals(originalClaims.getSubject(), parsedToken.getSubject());
      
    }

    @Test
    public void testCreateAccessToken() throws GeneralSecurityException {
        
        // Crea el token de refresh
        JokoTokenWrapper token = tokenService.createAndStoreRefreshToken(USER, SECURITY_PROFILE, TOKEN_TYPE.REFRESH,
                USER_AGENT, REMOTE_IP, ROLES, "a");
        
        JokoJWTClaims refreshToken = token.getClaims();
        JokoTokenWrapper accessToken = tokenService.createAccessToken(refreshToken, "123456");
        JokoJWTClaims jwtClaims = accessToken.getClaims();
        
        assertEquals(TOKEN_TYPE.ACCESS, jwtClaims.getJoko().getType());
        assertEquals(refreshToken.getJoko().getRoles(), jwtClaims.getJoko().getRoles());
        assertEquals(refreshToken.getSubject(), jwtClaims.getSubject());
    }
    
    @Test
    public void gettingTokenShouldReturnInfo() {
    	// 1. Creamos el refresh token
    	JokoTokenWrapper token = tokenService.createAndStoreRefreshToken(USER, SECURITY_PROFILE, TOKEN_TYPE.REFRESH,
                USER_AGENT, REMOTE_IP, ROLES, "a");
        
        // 2. Obtenemos su información
    	JokoTokenInfoResponse response = tokenService.tokenInfo(token.getToken());
    	
    	assertNotNull(response);
    	assertEquals(USER, response.getUserId());
    	assertThat(response.getExpiresIn(), greaterThan(0L));
    	
    }

    @Test
    public void gettingRevokedTokenShouldThrowException() {
    	// 1. Creamos el refresh token
    	JokoTokenWrapper token = tokenService.createAndStoreRefreshToken(USER, SECURITY_PROFILE, TOKEN_TYPE.REFRESH,
                USER_AGENT, REMOTE_IP, ROLES, "a");
        
        // 2. Lo revocamos
    	tokenService.revokeToken(token.getClaims().getId());
    	
    	// 3. Obtenemos su información
    	try {
    		tokenService.tokenInfo(token.getToken());
    	} catch(Throwable e) {
    		assertThat(e, instanceOf(JokoUnauthenticatedException.class));
    		JokoUnauthenticatedException je = (JokoUnauthenticatedException) e;
    		assertEquals(je.getErrorCode(), JokoUnauthenticatedException.ERROR_REVOKED_TOKEN);
    	}
    	
    }

    @Test
    public void gettingExpiredTokenShouldThrowException() {
    	JokoTokenWrapper token = tokenService.createAndStoreRefreshToken(USER, EXPIRATION_SECURITY_PROFILE, TOKEN_TYPE.REFRESH,
                USER_AGENT, REMOTE_IP, ROLES, "a");
    	try {
    		tokenService.tokenInfo(token.getToken());
    		Assert.fail("tokenInfo() call should not have succeeded");
    	} catch (RuntimeException e) {
    		assertThat(e, instanceOf(JokoUnauthenticatedException.class));
    		JokoUnauthenticatedException je = (JokoUnauthenticatedException) e;
    		assertEquals(je.getErrorCode(), JokoUnauthenticatedException.ERROR_EXPIRED_TOKEN);
    	}
    }
    
    @Test
    public void gettingTamperedTokenShouldThrowException() {
    	JokoTokenWrapper token = tokenService.createAndStoreRefreshToken(USER, SECURITY_PROFILE , TOKEN_TYPE.REFRESH,
                USER_AGENT, REMOTE_IP, ROLES, "a");
    	
    	error.expect(SignatureException.class);
    	
    	tokenService.tokenInfo(token.getToken() + "tampered");
    }
    
}
