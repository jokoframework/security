package io.github.jokoframework.security.dto.request;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by afeltes on 07/09/16.
 */
public class AuditSessionRequestDTO {
    private String userAgent;
    private Date userDate;
    private String remoteIp;
    
    private PrincipalSessionRequestDTO principal;

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String pUserAgent) {
        userAgent = pUserAgent;
    }

    public Date getUserDate() {
        return userDate;
    }

    public void setUserDate(Date pUserDate) {
        userDate = pUserDate;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String pRemoteIp) {
        remoteIp = pRemoteIp;
    }

	public PrincipalSessionRequestDTO getPrincipal() {
		return principal;
	}

	public void setPrincipal(PrincipalSessionRequestDTO principal) {
		this.principal = principal;
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("userAgent", userAgent).append("userDate", userDate).append("remoteIp", remoteIp)
				.append("principal", principal);
		return builder.toString();
	}
}
