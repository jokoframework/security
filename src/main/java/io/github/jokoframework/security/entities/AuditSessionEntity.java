package io.github.jokoframework.security.entities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by afeltes on 07/09/16.
 */
@Entity
@Table(name = "audit_session",schema = "joko_security")

public class AuditSessionEntity {

    public static final String USER_DATE = "userDate";

    @GenericGenerator(
            name = "audit_session_id_seq",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value =
                            "joko_security.audit_session_id_seq"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audit_session_id_seq")
    private Long id;

    @Column(name = "user_agent")
    private String userAgent;
    @Column(name = "user_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date userDate;
    @Column(name = "remote_ip")
    private String remoteIp;
    
    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    
    @ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "id_principal")
	private PrincipalSessionEntity principal;

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

    public void setUserDate(Date pSessionDate) {
        userDate = pSessionDate;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String pHost) {
        remoteIp = pHost;
    }


    public PrincipalSessionEntity getPrincipal() {
		return principal;
	}

	public void setPrincipal(PrincipalSessionEntity principal) {
		this.principal = principal;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
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
        AuditSessionEntity rhs = (AuditSessionEntity) obj;
        return new EqualsBuilder()
                .append(this.id, rhs.id)
                .append(this.userAgent, rhs.userAgent)
                .append(this.userDate, rhs.userDate)
                .append(this.remoteIp, rhs.remoteIp)
                .append(this.creationDate, rhs.creationDate)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(userAgent)
                .append(userDate)
                .append(remoteIp)
                .append(creationDate)
                .toHashCode();
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("userAgent", userAgent)
                .append(USER_DATE, userDate)
                .append("remoteIp", remoteIp)
                .append("creationDate", creationDate)
                .toString();
    }

    @PrePersist
    public void setDefaultData() {
        setUserDate(new Date());
    }
}
