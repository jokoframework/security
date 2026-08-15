package io.github.jokoframework.security;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import io.jsonwebtoken.Claims;

/**
 * Wrapper class for JWT Claims that adds Joko-specific extensions. In JJWT
 * 0.12.x, we use composition instead of extending DefaultClaims.
 */
public class JokoJWTClaims implements Serializable {

    private static final long serialVersionUID = -8574310592676951264L;

    private Claims claims;
    private JokoJWTExtension joko;

    // Standard Claims fields for direct access
    private String id;
    private String issuer;
    private String subject;
    private Set<String> audience;
    private Date expiration;
    private Date notBefore;
    private Date issuedAt;

    public JokoJWTClaims(Claims claims, JokoJWTExtension joko) {
        this.claims = claims;
        if (claims != null) {
            this.id = claims.getId();
            this.issuer = claims.getIssuer();
            this.subject = claims.getSubject();
            this.audience = claims.getAudience();
            this.expiration = claims.getExpiration();
            this.notBefore = claims.getNotBefore();
            this.issuedAt = claims.getIssuedAt();
        }
        this.joko = joko;
    }

    public JokoJWTClaims() {
    }

    public JokoJWTClaims(Claims body) {
        this(body, null);
    }

    // Getters and setters for standard claims
    public String getId() {
        return id;
    }

    public JokoJWTClaims setId(String id) {
        this.id = id;
        return this;
    }

    public String getIssuer() {
        return issuer;
    }

    public JokoJWTClaims setIssuer(String issuer) {
        this.issuer = issuer;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public JokoJWTClaims setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public Set<String> getAudience() {
        return audience;
    }

    public JokoJWTClaims setAudience(Set<String> audience) {
        this.audience = audience;
        return this;
    }

    public Date getExpiration() {
        return expiration;
    }

    public JokoJWTClaims setExpiration(Date expiration) {
        this.expiration = expiration;
        return this;
    }

    public Date getNotBefore() {
        return notBefore;
    }

    public JokoJWTClaims setNotBefore(Date notBefore) {
        this.notBefore = notBefore;
        return this;
    }

    public Date getIssuedAt() {
        return issuedAt;
    }

    public JokoJWTClaims setIssuedAt(Date issuedAt) {
        this.issuedAt = issuedAt;
        return this;
    }

    // Joko extension
    public JokoJWTExtension getJoko() {
        return joko;
    }

    public JokoJWTClaims setJoko(JokoJWTExtension joko) {
        this.joko = joko;
        return this;
    }

    // Access to underlying Claims if needed
    public Claims getClaims() {
        return claims;
    }

    public void setClaims(Claims claims) {
        this.claims = claims;
    }
}
