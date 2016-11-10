package io.github.jokoframework.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;

import java.io.Serializable;

public class JokoJWTClaims extends DefaultClaims implements Serializable{

    private static final long serialVersionUID = -8574310592676951264L;
    private JokoJWTExtension joko;
	private boolean revoked = false;

    public JokoJWTClaims(Claims claims) {
        this.setAudience(claims.getAudience());
        this.setId(claims.getId());
        this.setIssuedAt(claims.getIssuedAt());
        this.setIssuer(claims.getIssuer());
        this.setSubject(claims.getSubject());
        this.setExpiration(claims.getExpiration());
    }

    public JokoJWTClaims() {

    }

    public JokoJWTClaims(Claims claims, boolean revoked) {
		this(claims);
    	this.revoked  = revoked;
    }

	public JokoJWTExtension getJoko() {
        return joko;
    }

    public JokoJWTClaims setJoko(JokoJWTExtension joko) {
        this.joko = joko;
        return this;
    }
    
    public boolean getRevoked() {
    	return this.revoked;
    }

}
