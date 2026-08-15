package io.github.jokoframework.security.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Propiedades de configuración para joko-security.
 * Todas las propiedades vienen del application.yml del proyecto que usa la biblioteca.
 *
 * NOTA IMPORTANTE: Los TTL (Time To Live) de los tokens NO se configuran aquí.
 * Se configuran en la base de datos a través de la tabla 'security_profile'.
 *
 * Ejemplo de configuración:
 * <pre>
 * joko:
 *   security:
 *     jwt:
 *       secret: ${JWT_SECRET}
 *       issuer: my-app
 *       audience: my-app-users
 *     storage:
 *       type: postgres
 *     web:
 *       enabled: false
 * </pre>
 */
@Data
@Validated
@ConfigurationProperties(prefix = "joko.security")
public class JokoSecurityProperties {

    @Valid
    private JwtProperties jwt = new JwtProperties();

    @Valid
    private StorageProperties storage = new StorageProperties();

    @Valid
    private WebProperties web = new WebProperties();

    @Valid
    private SecretProperties secret = new SecretProperties();

    /**
     * Propiedades de JWT (generación y validación de tokens)
     *
     * NOTA: Los TTL (Time To Live) de los tokens se configuran en la base de datos
     * a través de la tabla 'security_profile', no mediante properties.
     */
    @Data
    public static class JwtProperties {
        /**
         * Clave secreta para firmar tokens (mínimo 64 caracteres para HS512)
         */
        @NotBlank(message = "JWT secret must not be blank")
        private String secret;

        /**
         * Issuer del token JWT (claim estándar 'iss')
         */
        private String issuer = "joko-security";

        /**
         * Audience del token JWT (claim estándar 'aud')
         */
        private String audience = "joko-app";
    }

    /**
     * Propiedades de almacenamiento (refresh tokens, blacklist)
     */
    @Data
    public static class StorageProperties {
        /**
         * Tipo de storage: postgres, redis, in-memory
         */
        private String type = "postgres";

        /**
         * Prefijo para keys en storage
         */
        private String keyPrefix = "joko:security:";

        /**
         * TTL para blacklist de tokens (en segundos)
         */
        private long blacklistTtl = 86400;

        // PostgreSQL specific
        private String tableName = "refresh_tokens";
        private String blacklistTableName = "token_blacklist";
        private boolean autoCreateTables = true;
    }

    /**
     * Propiedades para módulo web (controllers opcionales)
     */
    @Data
    public static class WebProperties {
        /**
         * Habilitar controllers de joko-security (por defecto deshabilitados)
         */
        private boolean enabled = false;

        /**
         * Base path para los controllers
         */
        private String basePath = "/api/auth";
    }

    /**
     * Propiedades para el secret key de JWT
     */
    @Data
    public static class SecretProperties {
        /**
         * Modo de almacenamiento del secret: "BD" (base de datos) o "FILE" (archivo)
         * Por defecto: BD
         */
        private String mode = "BD";

        /**
         * Ruta al archivo del secret (solo requerido cuando mode=FILE)
         */
        private String file;
    }
}
