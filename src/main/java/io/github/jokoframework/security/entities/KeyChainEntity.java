package io.github.jokoframework.security.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "keychain", schema = "sys")
public class KeyChainEntity {

    public static final int JOKO_TOKEN_SECRET = 1;
    private Integer id;
    private String value;

    @Id
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
