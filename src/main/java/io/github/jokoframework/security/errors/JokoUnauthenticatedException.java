package io.github.jokoframework.security.errors;

public class JokoUnauthenticatedException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -8391234267775404037L;
    
    public static final String ERROR_CODE_WRONG_CREDENTIALS = "joko.wrongCredentials";
    
    public static final String ERROR_TOO_MANY_OPEN_APPS = "joko.tooManyOpenApplications";
    
    public static final String ERROR_REVOKED_TOKEN = "joko.revokedToken";

	public static final String ERROR_EXPIRED_TOKEN = "joko.expiredToken";

    private final String username;
    
    public final String role;

    private final String errorCode;

    public JokoUnauthenticatedException(String errorCode) {
        this.username = null;
        this.role = null;
        this.errorCode = errorCode;

    }

    public JokoUnauthenticatedException() {
        this(ERROR_CODE_WRONG_CREDENTIALS);
    }

    public JokoUnauthenticatedException(Throwable e) {
        super(e);
        this.username = null;
        this.role = null;
        this.errorCode = ERROR_CODE_WRONG_CREDENTIALS;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String getMessage() {
        return "You shall not pass";
    }

}
