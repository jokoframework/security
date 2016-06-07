package io.github.jokoframework.common.errors;

import io.github.jokoframework.common.JokoUtils;

/**
 * Indica que es un error de logica de negocios. No corresponde a un fallo del
 * sistema sino a un problema en el procesamiento. en general debería ser
 * ocasionado por un dato mal proporcionado
 * 
 * @author danicricco
 *
 */
public abstract class BusinessException extends Exception {

    public enum FIELDS_POSSIBLE_ERRORS {
        REQUIRED, INVALID
    }

    /**
     * 
     */
    private static final long serialVersionUID = 8943855572101122016L;

    private final String errorCode;

    protected String offendingField;
    protected FIELDS_POSSIBLE_ERRORS fieldErrorType;

    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.offendingField = null;
        this.fieldErrorType = null;

    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (message != null) {
            return message;
        }
        if (offendingField != null) {
            if (fieldErrorType != null && fieldErrorType.equals(FIELDS_POSSIBLE_ERRORS.REQUIRED)) {
                return "The field " + JokoUtils.formatLogString(offendingField) + " is required";
            } else {
                return "The field " + JokoUtils.formatLogString(offendingField) + " is invalid";
            }
        }
        return message;
    }

}
