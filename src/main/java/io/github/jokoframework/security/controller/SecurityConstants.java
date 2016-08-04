package io.github.jokoframework.security.controller;

public class SecurityConstants {

    public static final String PATH_LOGIN = "/api/login";

    public static final int DEFAULT_MAX_NUMBER_DEVICES_PER_APP_TYPE_FOR_USER = 1;

    public static final int DEFAULT_REFRESH_TOKEN_TIMEOUT_SECONDS = 4 * 60 * 60;// 4
                                                                                // hours

    public static final String ERROR_BAD_CREDENTIALS = "joko.security.badcredentials";
    public static final String ERROR_ACCOUNT_DISABLED = "joko.security.account.disabled";
    public static final String ERROR_ACCOUNT_LOCKED = "joko.security.account.locked";

    public static final String DEFAULT_SECURITY_PROFILE = "DEFAULT";

    public static final String VERSION_HEADER_NAME = "X-JOKO-SECURITY-VERSION";
    public static final String AUTH_HEADER_NAME = "X-JOKO-AUTH";
    public static final String AUTHORIZATION_REFRESH = "Refresh";
    public static final String AUTHORIZATION_REFRESH_CONSUMER = "Refresh-consumer";
    public static final String AUTHORIZATION_ACCESS_TOKEN = "User-access";

    public static final long TOKEN_REMOVAL_INTERVAL = 5 * 60 * 1000;

    public static final String ERROR_NOT_ALLOWED = "joko.forbidden";

    // TODO candidato para util
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    /**
     * El valor delta a partir del cual dos números Double ya son considerados
     * iguales para el dominio de la aplicación
     */
    public static final float EPSILON = 0.000001f;

    public static final String SECRET_MODE_FILE = "FILE";
    public static final String SECRET_MODE_BD = "BD";

    private SecurityConstants() {
    }
}
