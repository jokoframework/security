package io.github.jokoframework.security.services;

import io.github.jokoframework.security.JokoTokenWrapper;
import io.github.jokoframework.security.JokoJWTClaims;
import io.github.jokoframework.security.entities.SecurityProfile;
import io.github.jokoframework.security.entities.TokenEntity;

/**
 * Agrupa metodos convenientes para trabajar con los tokens.
 * 
 * @author danicricco
 *
 */
public class TokenUtils {

	public static TokenEntity toEntity(JokoTokenWrapper token, SecurityProfile securityProfile) {
		TokenEntity entity = new TokenEntity();
		JokoJWTClaims claims = token.getClaims();
		entity.setId(claims.getId());
		entity.setSecurityProfile(securityProfile);
		entity.setUserId(claims.getSubject());
		entity.setIssuedAt(claims.getIssuedAt());
		entity.setExpiration(claims.getExpiration());
		entity.setTokenType(claims.getJoko().getType());
		return entity;
	}
}
