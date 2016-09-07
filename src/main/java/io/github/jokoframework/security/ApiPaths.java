package io.github.jokoframework.security;

/**
 * Resume todos los URLs que expone el middleware
 *
 * @author danicricco
 */
public final class ApiPaths {

    public static final String SWAGGER_PATTERN = "/api/.*";

    /**
     * Autenticación
     */
    public static final String LOGIN = "/api/login";
    public static final String LOGOUT = "/api/logout";
    public static final String TOKEN_REFRESH = "/api/token/refresh";
    public static final String TOKEN_REFRESH_CODE = "/api/token/refresh/code";
    public static final String TOKEN_USER_ACCESS = "/api/token/user-access";
    public static final String TOKEN_USER_ACCESS_ON_BEHALF_USER = "/api/token/on-behalf-user";
    public static final String SESSIONS = "/api/sessions";

    private ApiPaths() {

    }

}
