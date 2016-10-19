package io.github.jokoframework.security.entities;

import io.github.jokoframework.common.dto.BaseDTO;
import io.github.jokoframework.common.dto.DTOConvertable;
import io.github.jokoframework.security.dto.ConsumerAPIDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

/**
 * Usuarios con acceso a nivel de API
 *
 * @author danicricco
 */

@Entity
@Table(name = "consumer_api")
@SequenceGenerator(name = "consumer_api_id_seq", sequenceName = "consumer_api_id_seq", initialValue = 1, allocationSize = 1)
public class ConsumerApiEntity implements DTOConvertable {

    // FIXME podriamos llevar a otro lugar
    // DODO deprecar
    public enum ACCESS_LEVEL {
        ON_BEHALF_USER, ON_BEHALF_USER_LAZY, ADMIN
    }

    private Long id;
    private String documentNumber;
    private String name;
    private String contactName;
    private String consumerId;
    private String secret;
    private ACCESS_LEVEL accessLevel;


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "consumer_api_id_seq")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Enumerated(EnumType.STRING)
    public ACCESS_LEVEL getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(ACCESS_LEVEL accessLevel) {
        this.accessLevel = accessLevel;
    }


    @Override
    public BaseDTO toDTO() {
        ConsumerAPIDTO dto = new ConsumerAPIDTO();
        dto.setAccessLevel(getAccessLevel().toString());
        dto.setName(getName());
        dto.setConsumerId(getConsumerId());
        dto.setContactName(getContactName());
        // No se expone el secret al convertir a DTO.
        return dto;
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
        ConsumerApiEntity rhs = (ConsumerApiEntity) obj;
        return new EqualsBuilder()
                .append(this.id, rhs.id)
                .append(this.documentNumber, rhs.documentNumber)
                .append(this.name, rhs.name)
                .append(this.contactName, rhs.contactName)
                .append(this.consumerId, rhs.consumerId)
                .append(this.secret, rhs.secret)
                .append(this.accessLevel, rhs.accessLevel)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(documentNumber)
                .append(name)
                .append(contactName)
                .append(consumerId)
                .append(secret)
                .append(accessLevel)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("documentNumber", documentNumber)
                .append("name", name)
                .append("contactName", contactName)
                .append("consumerId", consumerId)
                .append("secret", secret)
                .append("accessLevel", accessLevel)
                .toString();
    }


}
