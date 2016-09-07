package io.github.jokoframework.security.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.http.HttpStatus;


/**
 * Created by afeltes on 06/05/16.
 */
public class BaseResponseDTO {
    private boolean success;
    private String errorCode;
    private String message;
    private HttpStatus httpStatus;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean pSuccess) {
        success = pSuccess;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String pErrorCode) {
        errorCode = pErrorCode;
    }

    public String getMessage() {
        return message;
    }

    public BaseResponseDTO setMessage(String pMessage) {
        message = pMessage;
        return this;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus pHttpStatus) {
        httpStatus = pHttpStatus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("success", success)
                .append("errorCode", errorCode)
                .append("message", message)
                .append("httpStatus", httpStatus != null ? httpStatus.value() : null)
                .toString();
    }

    public static BaseResponseDTO error() {
        BaseResponseDTO error = new BaseResponseDTO();
        error.setSuccess(false);
        return error;

    }

    public static BaseResponseDTO ok() {
        BaseResponseDTO ok = new BaseResponseDTO();
        ok.setSuccess(true);
        return ok;
    }
}
