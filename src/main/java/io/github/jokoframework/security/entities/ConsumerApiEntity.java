package io.github.jokoframework.security.entities;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import io.github.jokoframework.common.dto.BaseDTO;
import io.github.jokoframework.common.dto.DTOConvertable;
import io.github.jokoframework.security.dto.ConsumerAPIDTO;

/**
 * Usuarios con acceso a nivel de API
 * 
 * @author danicricco
 *
 */

@Entity
@Table(name = "consumer_api", schema = "profile")
@SequenceGenerator(name = "consumer_api_id_seq", sequenceName = "profile.consumer_api_id_seq", schema = "profile", initialValue = 1, allocationSize = 1)
public class ConsumerApiEntity implements DTOConvertable {

    // FIXME podriamos llevar a otro lugar
    // DODO deprecar
    public enum ACCESS_LEVEL {
        PDV, BANK, ON_BEHALF_USER, ATM, ON_BEHALF_USER_LAZY, ADMIN, BACKOFFICE
    }

    private Long id;
    private String documentNumber;
    private String name;
    private String contactName;
    private String consumerId;
    private String secret;
    private ACCESS_LEVEL accessLevel;

    private Long pdvId;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "consumer_api_id_seq")
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

    public Long getPdvId() {
        return pdvId;
    }

    public void setPdvId(Long pdvId) {
        this.pdvId = pdvId;
    }

    @Override
    public BaseDTO toDTO() {
        ConsumerAPIDTO dto = new ConsumerAPIDTO();
        dto.setAccessLevel(getAccessLevel().toString());
        dto.setName(getName());
        dto.setConsumerId(getConsumerId());
        dto.setContactName(getContactName());
        dto.setPdvId(getPdvId());
        // No se expone el secret al convertir a DTO.
        return dto;
    }

}
