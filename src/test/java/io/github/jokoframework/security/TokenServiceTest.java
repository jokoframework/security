package io.github.jokoframework.security;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import io.github.jokoframework.security.JokoJWTExtension.TOKEN_TYPE;
import io.github.jokoframework.security.entities.SecurityProfile;
import io.github.jokoframework.security.services.ISecurityProfileService;
import io.github.jokoframework.security.services.ITokenService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { Application.class })
@WebAppConfiguration
public class TokenServiceTest {

    private static final long ACCEPTED_DATE_DELTA = 1000;// 1 segundo de
    // diferencia

    @Autowired
    private ITokenService tokenService;

    @Autowired
    private ISecurityProfileService securityProfileService;

    @Test
    /**
     * Comprueba que se cree un token en la BD
     */
    public void testCreateRefreshToken() {
        String user = "juan";
        String securityProfile = "profileV1";
        String userAgent = "mozilla";
        String remoteIP = "10.0.3.3";

        // Guarda la configuración de seguridad
        SecurityProfile profile = SecurityMockObjects.getSecurityProfile(securityProfile);
        SecurityProfile storedProfile = securityProfileService.getOrSaveProfile(securityProfile, profile);
        Assert.assertNotNull(storedProfile);

        ArrayList<String> roles = new ArrayList<String>();
        roles.add("boss");
        // Crea el token de refresh
        JokoTokenWrapper token = tokenService.createAndStoreRefreshToken(user, securityProfile, TOKEN_TYPE.REFRESH,
                userAgent, remoteIP, roles);

        // Comprueba que no esta revocado
        boolean hasBeenRevoked = tokenService.hasBeenRevoked(token.getClaims().getId());
        Assert.assertFalse(hasBeenRevoked);

    }

    @Test
    /**
     * Comprueba que puede crear un token y volver a parsearlo
     */
    public void testParseToken() {

        String user = "juan";

        ArrayList<String> roles = new ArrayList<String>();
        roles.add("boss");
        TOKEN_TYPE type = TOKEN_TYPE.REFRESH;
        int timeout = 60 * 5;// 5min
        String profile = "p1";

        Date initTimestamp = new Date();
        JokoTokenWrapper tokenWrapper = tokenService.createToken(user, roles, type, timeout, profile);
        JokoJWTClaims parsedToken = tokenService.parse(tokenWrapper.getToken());

        // Comprueba que la fecha de expiracion este cerca de la esperada
        long expectedTimeOut = initTimestamp.getTime() + timeout * 1000;
        Assert.assertTrue(
                "Se esperaba la fecha de expiracion sea cerca de " + new Date(expectedTimeOut) + " con diferencia de "
                        + ACCEPTED_DATE_DELTA + " ms.",
                parsedToken.getExpiration().getTime() - expectedTimeOut <= ACCEPTED_DATE_DELTA);

        JokoJWTClaims originalClaims = tokenWrapper.getClaims();
        // El id se mantiene
        Assert.assertEquals(originalClaims.getId(), parsedToken.getId());
        Assert.assertEquals(originalClaims.getJoko().getType(), parsedToken.getJoko().getType());
        Assert.assertEquals(originalClaims.getJoko().getRoles(), parsedToken.getJoko().getRoles());
        Assert.assertEquals(originalClaims.getSubject(), parsedToken.getSubject());

    }

    @Test
    public void testCreateAccessToken() {
        String user = "juan";
        String securityProfile = "profileV1";
        String userAgent = "mozilla";
        String remoteIP = "10.0.3.3";

        // Guarda la configuración de seguridad
        SecurityProfile profile = SecurityMockObjects.getSecurityProfile(securityProfile);
        SecurityProfile storedProfile = securityProfileService.getOrSaveProfile(securityProfile, profile);
        Assert.assertNotNull(storedProfile);

        ArrayList<String> roles = new ArrayList<String>();
        roles.add("boss");
        // Crea el token de refresh
        JokoTokenWrapper token = tokenService.createAndStoreRefreshToken(user, securityProfile, TOKEN_TYPE.REFRESH,
                userAgent, remoteIP, roles);
        JokoJWTClaims refreshToken = token.getClaims();
        JokoTokenWrapper accessToken = tokenService.createAccessToken(refreshToken);
        JokoJWTClaims jwtClaims = accessToken.getClaims();
        Assert.assertEquals(TOKEN_TYPE.ACCESS, jwtClaims.getJoko().getType());
        Assert.assertEquals(refreshToken.getJoko().getRoles(), jwtClaims.getJoko().getRoles());
        Assert.assertEquals(refreshToken.getSubject(), jwtClaims.getSubject());
    }
}
