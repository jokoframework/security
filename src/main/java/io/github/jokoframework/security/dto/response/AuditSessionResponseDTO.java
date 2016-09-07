package io.github.jokoframework.security.dto.response;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

/**
 * Created by afeltes on 07/09/16.
 */
public class AuditSessionResponseDTO {
    private String userAgent;
    private Date userDate;
    private String remoteIp;

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


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("userAgent", userAgent)
                .append("userDate", userDate)
                .append("remoteIp", remoteIp)
                .toString();
    }
}
