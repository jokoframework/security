package io.github.jokoframework.security.dto;

public class ONBehalfUserRequest {

    private String username;
    private String pin;

    public ONBehalfUserRequest() {

    }

    public ONBehalfUserRequest(String username, String pin) {
        super();
        this.username = username;
        this.pin = pin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

}
