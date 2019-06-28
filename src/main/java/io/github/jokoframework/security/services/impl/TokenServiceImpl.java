package io.github.jokoframework.security.services.impl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import io.github.jokoframework.security.util.TwoFactorAuthUtil;
import io.github.jokoframework.security.entities.SeedEntity;
import io.github.jokoframework.security.repositories.ISeedRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.github.jokoframework.common.JokoUtils;
import io.github.jokoframework.common.dto.JokoTokenInfoResponse;
import io.github.jokoframework.common.errors.JokoApplicationException;
import io.github.jokoframework.security.JokoJWTClaims;
import io.github.jokoframework.security.JokoJWTExtension;
import io.github.jokoframework.security.JokoJWTExtension.TOKEN_TYPE;
import io.github.jokoframework.security.JokoTokenWrapper;
import io.github.jokoframework.security.controller.SecurityConstants;
import io.github.jokoframework.security.entities.KeyChainEntity;
import io.github.jokoframework.security.entities.SecurityProfile;
import io.github.jokoframework.security.entities.TokenEntity;
import io.github.jokoframework.security.errors.JokoUnauthenticatedException;
import io.github.jokoframework.security.errors.JokoUnauthorizedException;
import io.github.jokoframework.security.repositories.IKeychainRepository;
import io.github.jokoframework.security.repositories.ITokenRepository;
import io.github.jokoframework.security.services.ISecurityProfileService;
import io.github.jokoframework.security.services.ITokenService;
import io.github.jokoframework.security.services.TokenUtils;
import io.github.jokoframework.security.util.SecurityUtils;
import io.github.jokoframework.security.util.TXUUIDGenerator;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Transactional
public class TokenServiceImpl implements ITokenService {

    private static final int DEFAULT_TOKEN_LENGTH = 20;

    private static final int SECRET_LENGTH = 250;

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenServiceImpl.class);

    private TwoFactorAuthUtil twoFactorAuthUtil = new TwoFactorAuthUtil();

    @Autowired
    private ISecurityProfileService appService;

    @Autowired
    private ITokenRepository tokenRepository;

    @Autowired
    private IKeychainRepository securityRepository;

    @Autowired
    private ISeedRepository seedRepository;



    private TXUUIDGenerator tokenGenerator;

    private String secret;

    @Value("${joko.secret.mode}")
    private String secretMode;

    @Value("${joko.secret.file}")
    private String secretFile;

    public TokenServiceImpl() {
        tokenGenerator = new TXUUIDGenerator(DEFAULT_TOKEN_LENGTH);
    }

    @Override
    @PostConstruct
    public void init() {
        if (secretMode == null || secretMode.equals(SecurityConstants.SECRET_MODE_BD)) {
            initSecretFromBD();
        } else if (secretMode.equals(SecurityConstants.SECRET_MODE_FILE)) {
            initSecretFromFile();
        } else {
            throw new IllegalThreadStateException("Unrecognized property value for joko.secret.mode. Please use "
                    + SecurityConstants.SECRET_MODE_BD + " or " +
                    SecurityConstants.SECRET_MODE_FILE);
        }

    }

    public void initSecretFromFile() {
        try {
            this.secret = SecurityUtils.readFileToBase64(secretFile);
        } catch (IOException e) {
            throw new JokoApplicationException(e);
        }
    }

    public void initSecretFromBD() {
        KeyChainEntity secretEntity = securityRepository.findOne(KeyChainEntity.JOKO_TOKEN_SECRET);
        if (secretEntity != null && secretEntity.getId() != null) {
            LOGGER.info("Re-using secret stored");
            this.secret = secretEntity.getValue();
        } else {
            LOGGER.info("Generating secret...");
            String randomString = JokoUtils.generateRandomString(SECRET_LENGTH);
            secretEntity = new KeyChainEntity();
            secretEntity.setId(KeyChainEntity.JOKO_TOKEN_SECRET);
            secretEntity.setValue(randomString);
            securityRepository.save(secretEntity);
            this.secret = randomString;
            LOGGER.info("Secret successfully created");
        }
    }

    @Override
    public JokoTokenWrapper createAndStoreRefreshToken(String user, String profileKey, TOKEN_TYPE tokenType,
                                                       String userAgent, String remoteIP, List<String> roles, String seed) {
        SecurityProfile securityProfile = appService.getProfileByKey(profileKey);
        if (securityProfile == null) {
            throw new JokoApplicationException("Unable to create refresh token without a valid security profile. The profile "
                    + profileKey + " does not exists");
        }
        // Busca los token previos que hayan sido emitidos con el mismo tipo de
        // aplicación
        revokePreviousTokenIfNeccesary(user, securityProfile);

        Integer timeOut = securityProfile.getRefreshTokenTimeoutSeconds();
        if (timeOut == null) {
            LOGGER.warn("The application {} didn't specify any expiration timeout. Falling back to default",
                    profileKey);
            timeOut = SecurityConstants.DEFAULT_MAX_NUMBER_DEVICES_PER_APP_TYPE_FOR_USER;

            LOGGER.warn("Using default {} sec for a refresh token asked by ", timeOut, JokoUtils.formatLogString(user));
            LOGGER.warn("If you want to specify a timeout please check {} ", SecurityProfile.TABLE_NAME);
        }

        // Un refresh token es siempre revocable
        JokoTokenWrapper token = createToken(user, roles, tokenType, timeOut, profileKey);
        storeToken(token, securityProfile, userAgent, remoteIP);
        String seedEntity = seedRepository.findOneByUserId(user).toString();
        if(seed != null && seedEntity == "Optional.empty") {
            storeSeed(seed, user);
            return token;
        }else if(seed == null){
            return token;
        }
        else{
            throw new JokoUnauthenticatedException(JokoUnauthenticatedException.DEFAULT_ERROR_MSG);
        }
    }

    /**
     * Crea un token de acceso basado en el refresh token proveido como
     * parametro
     *
     * @param refreshToken
     * @param otp
     * @return
     */
    public JokoTokenWrapper createAccessToken(JokoJWTClaims refreshToken, String otp) throws GeneralSecurityException{
        if (!hasBeenRevoked(refreshToken.getId())) {
            // Solo si el token de refresh esta activo produce token.
            // En este punto el token ya fue controlado por los filtros
            JokoJWTExtension jokoClaims = refreshToken.getJoko();
            SecurityProfile securityProfile = appService.getProfileByKey(jokoClaims.getProfile());
            if (securityProfile == null) {
                throw new JokoApplicationException("Unable to obtain a security profile. The profile " + jokoClaims.getProfile()
                        + " does not exists");
            }
            Integer timeOut = securityProfile.getAccessTokenTimeoutSeconds();
            if (timeOut == null) {
                throw new IllegalStateException(
                        "The application " + jokoClaims.getProfile() + " didn't specify any expiration timeout.");
            }
            JokoTokenWrapper token = createToken(refreshToken.getSubject(), jokoClaims.getRoles(), TOKEN_TYPE.ACCESS,
                    timeOut, jokoClaims.getProfile());
            TokenEntity entity = tokenRepository.getTokenById(refreshToken.getId());
            String userId = entity.getUserId();
            Optional<SeedEntity> check= seedRepository.findOneByUserId(userId);
            SeedEntity seed;
            if(check.isPresent()) {
                seed = seedRepository.findOneByUserId(userId).orElseThrow(() -> new JokoUnauthenticatedException(JokoUnauthenticatedException.DEFAULT_ERROR_MSG));
            }
            else{
                return token;
            }
            String secret = seed.getSeedSecret();
            String number;

            number = twoFactorAuthUtil.generateCurrentNumber(secret);
            if(number.equalsIgnoreCase(otp)) {
                return token;
            }else {
                throw new JokoApplicationException("The OTP doesnt match with the given number");
            }

        }
        throw new JokoUnauthorizedException();

    }

    /**
     * <p>
     * Basado en el numero de conexiones posibles para un usuario elimina los
     * tokens anteriores.
     * </p>
     * Todas los security profile tienen un numero maximo de conexiones que
     * puede realizar un usuario. Este numero se controla en base al tipo de
     * security profile
     * </p>
     * Por ejemplo: una aplicación móvil para iOS y una móvil para Android
     * podrían tener el mismo tipo. Si se determina que un tipo de aplicacion
     * puede tener como maximo 1 usuario, entonces el usuario no podra usar una
     * apliccion android y iphone a la vez.
     * </p>
     *
     * @param user usuario registrado
     * @param app  entidad de aplicacion
     */
    private void revokePreviousTokenIfNeccesary(String user, SecurityProfile app) {
        List<TokenEntity> tokensRegistered = tokenRepository.findByUser(user);
        Integer maxNumberOfDevicesPerUser = app.getMaxNumberOfDevicesPerUser();
        String appId = app.getKey();

        if (maxNumberOfDevicesPerUser == null) {
            maxNumberOfDevicesPerUser = SecurityConstants.DEFAULT_MAX_NUMBER_DEVICES_PER_APP_TYPE_FOR_USER;
            LOGGER.warn("The application {} didn't specify the max number of devices per app and user. "
                    + "Using default {} ", appId, maxNumberOfDevicesPerUser);
        }
        if (tokensRegistered != null && tokensRegistered.size() >= maxNumberOfDevicesPerUser) {
            long numberOfTokensToRevoke = tokensRegistered.size() - maxNumberOfDevicesPerUser + 1;

            LOGGER.warn("User {} has {} tokens, and it should only have {} for application {}. Revoking {} tokens",
                    JokoUtils.formatLogString(user), tokensRegistered.size(), maxNumberOfDevicesPerUser,
                    JokoUtils.formatLogString(appId), numberOfTokensToRevoke);
            // revoca todos los tokens anteriores de la misma aplicacion

            // Puede que un usuario tenga
            for (int i = 0; i < tokensRegistered.size() && i < numberOfTokensToRevoke; i++) {
                TokenEntity tokenEntity = tokensRegistered.get(i);
                revokeToken(tokenEntity.getId());
            }

        }
    }

    // FIXME en lugar de borrar el token lo que podriamos hacer es cargar en
    // memoria en un reddis. El tema es sincronizar los token revocados entre
    // las diferentes instancias.
    @Override
    public void revokeToken(String jti) {
        LOGGER.trace("Revoking token {} ", JokoUtils.formatLogString(jti));
        tokenRepository.delete(jti);
        LOGGER.trace("Token revoked: {}", jti);
    }

    /**
     * Crea un token JWT firmado por este servidor con los parametros asignados
     *
     * @param user    El usuario dueño del token
     * @param roles   La lista de roles que se le concederá al usuario para este
     *                token en particular
     * @param type
     * @param timeout
     * @return
     */
    public JokoTokenWrapper createToken(String user, List<String> roles, TOKEN_TYPE type, int timeout,
                                        String securityProfile) {
        if (timeout < 0) {
            throw new IllegalArgumentException("Unable to create a token with an expired timeout");
        }
        // Calcula la fecha de emision y la de expiración del token
        Calendar calendar = JokoUtils.getUTCCurrentTime();
        Date now = calendar.getTime();
        calendar.add(Calendar.SECOND, timeout);
        Date exp = calendar.getTime();

        // Genera un uuid para el JWT
        String uuid = tokenGenerator.generate();

        LOGGER.trace("Creating token {} ", JokoUtils.formatLogString(uuid));

        // CREA el token en si con las propiedades solicitadas
        JwtBuilder builder = Jwts.builder();

        JokoJWTExtension jokoExtension = new JokoJWTExtension(type, roles, securityProfile);

        JokoJWTClaims claims = new JokoJWTClaims();
        claims.setJoko(jokoExtension);

        // TODO evaluar de utilizar el issuer .iss()
        claims.setSubject(user).setExpiration(exp).setIssuedAt(now).setId(uuid);

        // Es clave setear primero todas las propiedades y luego los custom de
        // joko. Si el orden es inverso se borra el claim custom (con el
        // setClaims)
        builder.setClaims(claims);
        builder.claim("joko", claims.getJoko());

        // Obtiene el secreto para firmarlo
        builder.signWith(SignatureAlgorithm.HS512, getSecret());

        String token = builder.compact();
        return new JokoTokenWrapper(claims, token);
    }

    private String getSecret() {
        if (secret == null) {
            throw new IllegalStateException("Token service has been incorrectly initialized.");
        }
        return secret;
    }

    /**
     * Guarda el token dentro de la BD
     *
     * @param token     token generado
     * @param app       aplicacion
     * @param userAgent tipo de navegador
     * @param remoteIP  direccion remota
     */
    private void storeToken(JokoTokenWrapper token, SecurityProfile app, String userAgent, String remoteIP) {
        TokenEntity entity = TokenUtils.toEntity(token, app);

        if (userAgent != null && userAgent.length() > TokenEntity.MAX_USER_AGENT_LENGTH) {
            userAgent = userAgent.substring(0, TokenEntity.MAX_USER_AGENT_LENGTH);
            LOGGER.warn("While generating token " + JokoUtils.formatLogString(entity.getId())
                    + "... The user-agent was longer than maximum expected (" + TokenEntity.MAX_USER_AGENT_LENGTH
                    + "), it was truncated to avoid DB overflow. ");
        }
        if (remoteIP != null && remoteIP.length() > TokenEntity.MAX_IP_LENTH) {
            remoteIP = remoteIP.substring(0, TokenEntity.MAX_IP_LENTH);
            LOGGER.warn("While generating token " + JokoUtils.formatLogString(entity.getId())
                    + "... The remoteIP was longer than maximum expected (" + TokenEntity.MAX_IP_LENTH
                    + "), it was truncated to avoid DB overflow. ");
        }
        entity.setUserAgent(userAgent);
        entity.setRemoteIP(remoteIP);

        tokenRepository.save(entity);
    }

    private void storeSeed(String seed, String userId){
        SeedEntity seedEntity = new SeedEntity();
        seedEntity.setSeedSecret(seed);
        seedEntity.setUserId(userId);

        seedRepository.save(seedEntity);
    }

    @Override
    public boolean hasBeenRevoked(String jti) {
        LOGGER.trace("Verifying if token was revoked: {}", jti);
    	TokenEntity token = tokenRepository.getTokenById(jti);
        if (token == null) {
            // Si el token no está en la BD entonces se asume que fue revocado
            // (o
            // expiro el tiempo)
            return true;
        }
        return false;
    }

    @Override
    public JokoJWTClaims parse(String token) {
        return SecurityUtils.parseToken(token, getSecret());

    }

    @Override
    public void revokeTokensUntil(Date date) {
    	tokenRepository.deleteTokensFromDate(date);
    }

    @Override
    public int deleteExpiredTokens() {
        Date now = new Date();
        return tokenRepository.deleteExpiredTokens(now);
    }

    /**
     * Prueba si un jti ha sido revocado. En caso de haber sido revodado tira
     * una excepcion
     *
     * @param jti
     */
    public void failIfRevoked(String jti) {

        if (hasBeenRevoked(jti)) {
            throw new JokoUnauthenticatedException(JokoUnauthenticatedException.ERROR_REVOKED_TOKEN);
        }

    }

    @Override
    public JokoTokenWrapper refreshToken(JokoJWTClaims jokoToken, String userAgent, String remoteIP) {
        // Solo se puede refrescar con un token que no ha sido revocado
        failIfRevoked(jokoToken.getId());

        // revoca el token
        revokeToken(jokoToken.getId());

        // Crea uno nuevo con los mismos permisos que el anterior

        JokoTokenWrapper tokenWrapper = createAndStoreRefreshToken(jokoToken.getSubject(),
                jokoToken.getJoko().getProfile(), TOKEN_TYPE.REFRESH, userAgent, remoteIP,
                jokoToken.getJoko().getRoles(), null);
        return tokenWrapper;
    }

	@Override
	public JokoTokenInfoResponse tokenInfo(String accessToken) {
		Assert.notNull(accessToken, "El token es requerido");
		try {
			JokoJWTClaims claims =  this
					.tokenInfoAsClaims(accessToken)
					.orElseThrow(() -> new JokoUnauthenticatedException(JokoUnauthenticatedException.ERROR_REVOKED_TOKEN));
			JokoTokenInfoResponse response = new JokoTokenInfoResponse.Builder()
					.audience(claims.getAudience())
			        .userId(claims.getSubject())
			        .expiresIn(secondsFromNow(claims.getExpiration()))
			        .success(Boolean.TRUE)
			        .build();
			return response;
		} catch (ExpiredJwtException ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new JokoUnauthenticatedException(JokoUnauthenticatedException.ERROR_EXPIRED_TOKEN);
		}
	}
	
	private Long secondsFromNow(Date expiration) {
		Date now = new Date();
		long seconds = (expiration.getTime() - now.getTime()) / 1000;
		return seconds;
	}

	@Override
	public Optional<JokoJWTClaims> tokenInfoAsClaims(String token) {
		JokoJWTClaims claims = this.parse(token);
		// En este punto el token ya es valido sino habria tirado una
		// excepcion JwtException
		JokoJWTExtension jokoClaims = claims.getJoko();
		if (jokoClaims.getType().equals(JokoJWTExtension.TOKEN_TYPE.REFRESH)) {
		    // Solamente los tokens de refresh se pueden revocar
		    if (this.hasBeenRevoked(claims.getId())) {
		        return Optional.empty();
		    }
		}

		return Optional.of(new JokoJWTClaims(claims, jokoClaims));
	}

}
