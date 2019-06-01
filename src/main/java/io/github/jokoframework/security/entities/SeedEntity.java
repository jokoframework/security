package io.github.jokoframework.security.entities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "seed",schema = "joko_security")
public class SeedEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long seedId;
    private String userId;
    private String seedSecret;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long getSeedId() {
        return seedId;
    }

    public void setSeedId(Long seedId) {
        this.seedId = seedId;
    }

    @Column(name = "user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name = "seed_secret")
    public String getSeedSecret() {
        return seedSecret;
    }

    public void setSeedSecret(String seedSecret) {
        this.seedSecret = seedSecret;
    }

    @Override
    public String toString() {
        return "SeedEntity{" +
                "seedId='" + seedId + '\'' +
                ", userId='" + userId + '\'' +
                ", seedSecret='" + seedSecret + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SeedEntity that = (SeedEntity) o;

        return new EqualsBuilder()
                .append(seedId, that.seedId)
                .append(userId, that.userId)
                .append(seedSecret, that.seedSecret)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(seedId)
                .append(userId)
                .append(seedSecret)
                .toHashCode();
    }
}
