package io.github.jokoframework.security.dto.response;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

import io.github.jokoframework.security.dto.PrincipalSessionDTO;

/**
 * Created by afeltes on 07/09/16.
 */
public class AuditSessionResponseDTO {
    private String userAgent;
    private Date userDate;
    private String remoteIp;
    private PrincipalSessionDTO principal;

    public AuditSessionResponseDTO() {
    }

    public AuditSessionResponseDTO(String pUserAgent, String pRemoteIp) {
        setUserAgent(pUserAgent);
        setRemoteIp(pRemoteIp);
    }

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

	public PrincipalSessionDTO getPrincipal() {
		return principal;
	}

	public void setPrincipal(PrincipalSessionDTO principal) {
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
