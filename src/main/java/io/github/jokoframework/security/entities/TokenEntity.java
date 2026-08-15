package io.github.jokoframework.security.entities;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import io.github.jokoframework.security.JokoJWTExtension.TOKEN_TYPE;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Tokens de acceso al sistema
 */
@Entity
@Table(name = "tokens",schema = "joko_security")
public class TokenEntity implements Serializable {

    public static final int MAX_USER_AGENT_LENGTH = 150;
    public static final int MAX_IP_LENTH = 15;

    private static final long serialVersionUID = -2750291513208038443L;
    private String id;
    private String userId;
    private SecurityProfile securityProfile;
    private String remoteIP;
    private String userAgent;
    private Date issuedAt;
    private Date expiration;
    private TOKEN_TYPE tokenType;

    /**
     * Identificador del token
     *
     * @return id
     */
    @Id
    @Column(name = "id")
    public String getId() {
        return id;
    }

    /**
     * Identificador de usuario
     *
     * @return userId
     */
    @Column(name = "user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Aplicación
     *
     * @return
     */
    @ManyToOne
    @JoinColumn(name = "security_profile_id")
    public SecurityProfile getSecurityProfile() {
        return securityProfile;
    }

    public void setSecurityProfile(SecurityProfile application) {
        this.securityProfile = application;
    }

    /**
     * Direccion IP remota
     *
     * @return remoteIP
     */
    @Column(name = "remote_ip")
    public String getRemoteIP() {
        return remoteIP;
    }

    public void setRemoteIP(String remoteIP) {
        this.remoteIP = remoteIP;
    }

    /**
     * Agente de usuario
     *
     * @return userAgent
     */
    @Column(name = "user_agent")
    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * Fecha de creación
     *
     * @return issuedAt
     */
    @Column(name = "issued_at")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Date issuedAt) {
        this.issuedAt = issuedAt;
    }

    /**
     * Fecha de expiración
     *
     * @return expieration
     */
    @Column(name = "expiration")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expires) {
        this.expiration = expires;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Tipo de token
     *
     * @return tokenType
     */
    @Column(name = "token_type")
    @Enumerated(EnumType.STRING)
    public TOKEN_TYPE getTokenType() {
        return tokenType;
    }

    public void setTokenType(TOKEN_TYPE tokenType) {
        this.tokenType = tokenType;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        TokenEntity rhs = (TokenEntity) obj;
        return new EqualsBuilder()
                .append(this.id, rhs.id)
                .append(this.userId, rhs.userId)
                .append(this.securityProfile, rhs.securityProfile)
                .append(this.remoteIP, rhs.remoteIP)
                .append(this.userAgent, rhs.userAgent)
                .append(this.issuedAt, rhs.issuedAt)
                .append(this.expiration, rhs.expiration)
                .append(this.tokenType, rhs.tokenType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(userId)
                .append(securityProfile)
                .append(remoteIP)
                .append(userAgent)
                .append(issuedAt)
                .append(expiration)
                .append(tokenType)
                .toHashCode();
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("userId", userId)
                .append("securityProfile", securityProfile)
                .append("remoteIP", remoteIP)
                .append("userAgent", userAgent)
                .append("issuedAt", issuedAt)
                .append("expiration", expiration)
                .append("tokenType", tokenType)
                .toString();
    }
}
