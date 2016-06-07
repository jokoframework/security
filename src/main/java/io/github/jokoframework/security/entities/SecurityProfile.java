package io.github.jokoframework.security.entities;

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
@Table(name = "security_profile", schema = "sys")
public class SecurityProfile implements Serializable {
	// Solo existe esta variable para poner referencias externas en mensajes de
	// log
	public static final String TABLE_NAME = "sys.security_profile";
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
	@SequenceGenerator(name = "security_profile_id_seq", sequenceName = "sys.security_profile_id_seq", schema = "sys", initialValue = 1, allocationSize = 1)
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
	@Column(name = "max_number_of_connections_per_user")
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accessTokenTimeoutSeconds == null) ? 0 : accessTokenTimeoutSeconds.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((maxAccessTokenRequests == null) ? 0 : maxAccessTokenRequests.hashCode());
		result = prime * result + ((maxNumberOfConnections == null) ? 0 : maxNumberOfConnections.hashCode());
		result = prime * result
				+ ((maxNumberOfConnectionsPerUser == null) ? 0 : maxNumberOfConnectionsPerUser.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((refreshTokenTimeoutSeconds == null) ? 0 : refreshTokenTimeoutSeconds.hashCode());
		result = prime * result + ((revocable == null) ? 0 : revocable.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SecurityProfile other = (SecurityProfile) obj;
		if (accessTokenTimeoutSeconds == null) {
			if (other.accessTokenTimeoutSeconds != null)
				return false;
		} else if (!accessTokenTimeoutSeconds.equals(other.accessTokenTimeoutSeconds))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (maxAccessTokenRequests == null) {
			if (other.maxAccessTokenRequests != null)
				return false;
		} else if (!maxAccessTokenRequests.equals(other.maxAccessTokenRequests))
			return false;
		if (maxNumberOfConnections == null) {
			if (other.maxNumberOfConnections != null)
				return false;
		} else if (!maxNumberOfConnections.equals(other.maxNumberOfConnections))
			return false;
		if (maxNumberOfConnectionsPerUser == null) {
			if (other.maxNumberOfConnectionsPerUser != null)
				return false;
		} else if (!maxNumberOfConnectionsPerUser.equals(other.maxNumberOfConnectionsPerUser))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (refreshTokenTimeoutSeconds == null) {
			if (other.refreshTokenTimeoutSeconds != null)
				return false;
		} else if (!refreshTokenTimeoutSeconds.equals(other.refreshTokenTimeoutSeconds))
			return false;
		if (revocable == null) {
			if (other.revocable != null)
				return false;
		} else if (!revocable.equals(other.revocable))
			return false;
		return true;
	}

}
