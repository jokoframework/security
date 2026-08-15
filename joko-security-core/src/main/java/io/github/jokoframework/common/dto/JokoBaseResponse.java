package io.github.jokoframework.common.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class JokoBaseResponse {

    private boolean success;
    private String errorCode;
    private String message;

    public JokoBaseResponse() {

    }

    public JokoBaseResponse(boolean success) {
        this.success = success;
    }

    public JokoBaseResponse(String errorCode) {
        this.success = false;
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("success", success)
                .append("errorCode", errorCode)
                .append("message", message)
                .toString();
    }
}
