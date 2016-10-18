package io.github.jokoframework.security.dto.request;

/**
 * 
 * @author bsandoval
 *
 */
public class PrincipalSessionRequestDTO {
	private String appId;
    private String appDescription;
    private String userId;
    private String userDescription;
    
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
	
}
