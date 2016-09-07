package io.github.jokoframework.security.dto;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Created by afeltes on 07/09/16.
 */
public class AuditSessionDTO {
    private Long id;
    private String userAgent;
    private Date userDate;
    private String remoteIp;

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
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
}
