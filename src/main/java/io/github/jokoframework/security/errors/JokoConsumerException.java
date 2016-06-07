package io.github.jokoframework.security.errors;

import io.github.jokoframework.common.errors.BusinessException;

public class JokoConsumerException extends BusinessException {

    /**
     * 
     */
    private static final long serialVersionUID = -810278519963587949L;

    public static final String INVALID_ACESS_LEVEL = "consumer.accessLevel.invalid";
    public static final String MISSING_REQUIRED_DATA="consumer.field.missing";

    public JokoConsumerException(String errorCode, String message) {
        super(errorCode, message);

    }

}
