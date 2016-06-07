package io.github.jokoframework.security.dto;

import io.github.jokoframework.common.dto.BaseDTO;

public class ConsumerAPIDTO implements BaseDTO {

    private String name;
    private String contactName;
    private String consumerId;
    private String accessLevel;
    private String secret;
    private Long pdvId;

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

    public String getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Long getPdvId() {
        return pdvId;
    }

    public void setPdvId(Long pdvId) {
        this.pdvId = pdvId;
    }

}
