package io.github.jokoframework.security;

/**
 * Representa un token JWT con sus claims listos para ser leidos. Es una clase
 * conveniente para uso interno puesto que posee los datos parseados (claims) y
 * la representacion en JWT
 * 
 * @author danicricco
 *
 */
public class JokoTokenWrapper {

	private final JokoJWTClaims claims;

	private final String token;

	public JokoTokenWrapper(JokoJWTClaims claims, String token) {
		super();
		this.claims = claims;
		this.token = token;
	}

	public JokoJWTClaims getClaims() {
		return claims;
	}

	public String getToken() {
		return token;
	}

}
