package io.github.jokoframework.security.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 
 * @author bsandoval
 *
 */
@Entity
@Table(name = "principal_session", schema = "sys",
	uniqueConstraints={
			@UniqueConstraint(columnNames = {"app_id", "user_id"})
	})
@SequenceGenerator(name = "principal_session_id_seq", sequenceName = "sys.principal_session_id_seq", schema = "sys", initialValue = 1, allocationSize = 1)
public class PrincipalSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "principal_session_id_seq")
    private Long id;

    @Column(name = "app_id")
    private String appId;
    @Column(name = "app_description")
    private String appDescription;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "user_description")
    private String userDescription;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getAppDescription() {
		return appDescription;
	}
	public void setAppDescription(String appDescription) {
		this.appDescription = appDescription;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserDescription() {
		return userDescription;
	}
	public void setUserDescription(String userDescription) {
		this.userDescription = userDescription;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PrincipalSessionEntity [id=").append(id).append(", appId=").append(appId)
				.append(", appDescription=").append(appDescription).append(", userId=").append(userId)
				.append(", userDescription=").append(userDescription).append("]");
		return builder.toString();
	}
    
}
