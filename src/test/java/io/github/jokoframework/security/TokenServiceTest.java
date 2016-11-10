package io.github.jokoframework.security;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import io.github.jokoframework.common.dto.JokoTokenInfoResponse;
import io.github.jokoframework.security.JokoJWTExtension.TOKEN_TYPE;
import io.github.jokoframework.security.entities.SecurityProfile;
import io.github.jokoframework.security.services.ISecurityProfileService;
import io.github.jokoframework.security.services.ITokenService;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.MatcherAssert.assertThat;



@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { Application.class })
@Transactional
public class TokenServiceTest {

    private static final long ACCEPTED_DATE_DELTA = 1000;// 1 segundo de
    // diferencia

    @Autowired
    private ITokenService tokenService;

    @Autowired
    private ISecurityProfileService securityProfileService;
	
	private static String USER = "juan";
    
	private static String SECURITY_PROFILE = "profileV1";
    
    private static String USER_AGENT = "mozilla";
    
    private static String REMOTE_IP = "10.0.3.3";

    private static List<String> ROLES = Arrays.asList("boss");
   
    @Before
    public void setup() {

    }
    
    @Test
    /**
     * Comprueba que se cree un token en la BD
     */
    public void testCreateRefreshToken() {
        
        saveSecurityProfile();
        // Crea el token de refresh
        JokoTokenWrapper token = tokenService.createAndStoreRefreshToken(USER, SECURITY_PROFILE, TOKEN_TYPE.REFRESH,
                USER_AGENT, REMOTE_IP, ROLES);

        // Comprueba que no esta revocado
        boolean hasBeenRevoked = tokenService.hasBeenRevoked(token.getClaims().getId());
        assertFalse(hasBeenRevoked);

    }

	

    @Test
    /**
     * Comprueba que puede crear un token y volver a parsearlo
     */
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
    public void testCreateAccessToken() {
        
        saveSecurityProfile();

        
        // Crea el token de refresh
        JokoTokenWrapper token = tokenService.createAndStoreRefreshToken(USER, SECURITY_PROFILE, TOKEN_TYPE.REFRESH,
                USER_AGENT, REMOTE_IP, ROLES);
        JokoJWTClaims refreshToken = token.getClaims();
        JokoTokenWrapper accessToken = tokenService.createAccessToken(refreshToken);
        JokoJWTClaims jwtClaims = accessToken.getClaims();
        assertEquals(TOKEN_TYPE.ACCESS, jwtClaims.getJoko().getType());
        assertEquals(refreshToken.getJoko().getRoles(), jwtClaims.getJoko().getRoles());
        assertEquals(refreshToken.getSubject(), jwtClaims.getSubject());
    }
    
    @Test
    public void gettingTokenInfoShouldReturnInfo() {
    	
    	// 1. Creamos el refresh token
    	saveSecurityProfile();
        JokoTokenWrapper token = tokenService.createAndStoreRefreshToken(USER, SECURITY_PROFILE, TOKEN_TYPE.REFRESH,
                USER_AGENT, REMOTE_IP, ROLES);
        
        // 2. Lo revocamos
    	//tokenService.revokeToken(token.getClaims().getId());
    	
    	// 3. Obtenemos su información
    	JokoTokenInfoResponse response = tokenService.tokenInfo(token.getToken());
    	
    	assertNotNull(response);
    	assertEquals(Boolean.FALSE, response.getRevoked());
    	assertEquals(USER, response.getUserId());
    	assertThat(response.getExpiresIn(), greaterThan(0L));
    	
    }

	private void saveSecurityProfile() {
		// Guarda la configuración de seguridad
        SecurityProfile profile = SecurityMockObjects.getSecurityProfile(SECURITY_PROFILE);
        SecurityProfile storedProfile = securityProfileService.getOrSaveProfile(SECURITY_PROFILE, profile);
        assertNotNull(storedProfile);
        
	}
}
