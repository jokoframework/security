package io.github.jokoframework.security.entities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 *
 * Un security profile define las características de como deben de ser creados
 * los token de acceso.
 * 
 * @author danicricco
 *
 */
@Entity
@Table(name = "security_profile")
public class SecurityProfile implements Serializable {
	// Solo existe esta variable para poner referencias externas en mensajes de
	// log
	public static final String TABLE_NAME = "security_profile";
	private static final long serialVersionUID = 9134112281157665429L;
	private Long id;
	private String key;
	private String name;

	private Integer maxNumberOfConnectionsPerUser;
	private Integer maxNumberOfConnections;
	private Integer refreshTokenTimeoutSeconds;
	private Integer accessTokenTimeoutSeconds;
	private Boolean revocable;
	private Integer maxAccessTokenRequests;

	/**
	 * Id serial de la aplicación
	 * 
	 * @return id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "security_profile_id_seq")
	@SequenceGenerator(name = "security_profile_id_seq", sequenceName = "security_profile_id_seq", initialValue = 1, allocationSize = 1)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Id de aplicación
	 *
	 * @return key
	 */
	@Column(name = "key")
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Alias de la aplicación
	 *
	 * @return name
	 */
	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Cantidad máxima de dispositivos por usuario
	 *
	 * @return maxNumberOfDevicePerUser
	 */
	@Column(name = "max_number_devices_user")
	public Integer getMaxNumberOfDevicesPerUser() {
		return maxNumberOfConnectionsPerUser;
	}

	public void setMaxNumberOfDevicesPerUser(Integer maxNumberOfDevicesPerUser) {
		this.maxNumberOfConnectionsPerUser = maxNumberOfDevicesPerUser;
	}

	/**
	 * Cantidad máxima de conexiones
	 *
	 * @return maxNumberOfConnectrions
	 */
	@Column(name = "max_number_of_connections")
	public Integer getMaxNumberOfConnections() {
		return maxNumberOfConnections;
	}

	public void setMaxNumberOfConnections(Integer maxNumberOfConnections) {
		this.maxNumberOfConnections = maxNumberOfConnections;
	}

	/**
	 * Tiempo de expiración del refresh token
	 *
	 * @return refreshTokenTimeoutSeconds
	 */
	@Column(name = "refresh_token_timeout_seconds")
	public Integer getRefreshTokenTimeoutSeconds() {
		return refreshTokenTimeoutSeconds;
	}

	public void setRefreshTokenTimeoutSeconds(Integer refreshTokenTimeoutSeconds) {
		this.refreshTokenTimeoutSeconds = refreshTokenTimeoutSeconds;
	}

	/**
	 * Tiempo de expiración del access token
	 *
	 * @return accessTokenTimeoutSeconds
	 */
	@Column(name = "access_token_timeout_seconds")
	public Integer getAccessTokenTimeoutSeconds() {
		return accessTokenTimeoutSeconds;
	}

	public void setAccessTokenTimeoutSeconds(Integer accessTokenTimeoutSeconds) {
		this.accessTokenTimeoutSeconds = accessTokenTimeoutSeconds;
	}

	/**
	 * Marca de token revocable
	 *
	 * @return revocable
	 */
	@Column(name = "revocable")
	public Boolean getRevocable() {
		return revocable;
	}

	public void setRevocable(Boolean revocable) {
		this.revocable = revocable;
	}

	/**
	 * Cantidad maxima de peticción de access tokens
	 *
	 * @return maxAccessTokenRequests
	 */
	@Column(name = "max_access_token_requests")
	public Integer getMaxAccessTokenRequests() {
		return maxAccessTokenRequests;
	}

	public void setMaxAccessTokenRequests(Integer maxAccessTokenRequests) {
		this.maxAccessTokenRequests = maxAccessTokenRequests;
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
		SecurityProfile rhs = (SecurityProfile) obj;
		return new EqualsBuilder()
				.append(this.id, rhs.id)
				.append(this.key, rhs.key)
				.append(this.name, rhs.name)
				.append(this.maxNumberOfConnectionsPerUser, rhs.maxNumberOfConnectionsPerUser)
				.append(this.maxNumberOfConnections, rhs.maxNumberOfConnections)
				.append(this.refreshTokenTimeoutSeconds, rhs.refreshTokenTimeoutSeconds)
				.append(this.accessTokenTimeoutSeconds, rhs.accessTokenTimeoutSeconds)
				.append(this.revocable, rhs.revocable)
				.append(this.maxAccessTokenRequests, rhs.maxAccessTokenRequests)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(id)
				.append(key)
				.append(name)
				.append(maxNumberOfConnectionsPerUser)
				.append(maxNumberOfConnections)
				.append(refreshTokenTimeoutSeconds)
				.append(accessTokenTimeoutSeconds)
				.append(revocable)
				.append(maxAccessTokenRequests)
				.toHashCode();
	}


	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("id", id)
				.append("key", key)
				.append("name", name)
				.append("maxNumberOfConnectionsPerUser", maxNumberOfConnectionsPerUser)
				.append("maxNumberOfConnections", maxNumberOfConnections)
				.append("refreshTokenTimeoutSeconds", refreshTokenTimeoutSeconds)
				.append("accessTokenTimeoutSeconds", accessTokenTimeoutSeconds)
				.append("revocable", revocable)
				.append("maxAccessTokenRequests", maxAccessTokenRequests)
				.toString();
	}
}
