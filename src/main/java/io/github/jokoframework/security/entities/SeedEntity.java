package io.github.jokoframework.security.entities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "seed",schema = "joko_security")
public class SeedEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    @GenericGenerator(
            name = "seed_id_seq",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value =
                            "joko_security.seed_id_seq"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "seed_id_seq")
    @Column(name = "id")
    private Long seedId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "seed_secret")
    private String seedSecret;

    public Long getSeedId() {
        return seedId;
    }

    public void setSeedId(Long seedId) {
        this.seedId = seedId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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
