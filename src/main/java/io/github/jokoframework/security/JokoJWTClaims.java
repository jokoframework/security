package io.github.jokoframework.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;

public class JokoJWTClaims extends DefaultClaims {

    private JokoJWTExtension joko;

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

    public JokoJWTExtension getJoko() {
        return joko;
    }

    public JokoJWTClaims setJoko(JokoJWTExtension joko) {
        this.joko = joko;
        return this;
    }

}
