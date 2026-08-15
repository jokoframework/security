package io.github.jokoframework.security.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Auto-configuración para almacenamiento de joko-security.
 *
 * <p>Esta clase configura automáticamente el backend de almacenamiento
 * basándose en la propiedad {@code joko.security.storage.type}.</p>
 *
 * <p>Almacenamientos soportados:</p>
 * <ul>
 *   <li><b>postgres</b> (default): Almacenamiento en PostgreSQL via JPA</li>
 *   <li><b>redis</b>: Almacenamiento en Redis (requiere joko-security-storage-redis)</li>
 *   <li><b>in-memory</b>: Almacenamiento en memoria (solo para desarrollo/testing)</li>
 * </ul>
 *
 * @see JokoSecurityAutoConfiguration
 */
@AutoConfiguration(after = JokoSecurityAutoConfiguration.class)
public class JokoSecurityStorageAutoConfiguration {

    /**
     * Configuración para almacenamiento PostgreSQL.
     *
     * <p>Se activa cuando:</p>
     * <ul>
     *   <li>{@code joko.security.storage.type=postgres} (default)</li>
     *   <li>El módulo joko-security-storage-postgres está en el classpath</li>
     * </ul>
     */
    @Configuration
    @ConditionalOnProperty(
        name = "joko.security.storage.type",
        havingValue = "postgres",
        matchIfMissing = true
    )
    @ConditionalOnClass(name = "io.github.jokoframework.security.storage.postgres.entity.TokenEntity")
    @EnableJpaRepositories(basePackages = "io.github.jokoframework.security.storage.postgres.repository")
    @EntityScan(basePackages = "io.github.jokoframework.security.storage.postgres.entity")
    public static class PostgresStorageConfiguration {
        // Spring Data JPA auto-configura los repositorios
        // No se necesitan beans adicionales
    }

    /**
     * Configuración para almacenamiento Redis.
     *
     * <p>Se activa cuando:</p>
     * <ul>
     *   <li>{@code joko.security.storage.type=redis}</li>
     *   <li>El módulo joko-security-storage-redis está en el classpath</li>
     * </ul>
     *
     * <p><b>Nota:</b> El módulo redis aún no está implementado.</p>
     */
    @Configuration
    @ConditionalOnProperty(name = "joko.security.storage.type", havingValue = "redis")
    @ConditionalOnClass(name = "org.springframework.data.redis.core.RedisTemplate")
    public static class RedisStorageConfiguration {
        // TODO: Implementar cuando se cree el módulo joko-security-storage-redis
    }

    /**
     * Configuración para almacenamiento en memoria.
     *
     * <p>Se activa cuando:</p>
     * <ul>
     *   <li>{@code joko.security.storage.type=in-memory}</li>
     * </ul>
     *
     * <p><b>Advertencia:</b> Solo para desarrollo y testing. Los tokens se pierden
     * cuando se reinicia la aplicación.</p>
     */
    @Configuration
    @ConditionalOnProperty(name = "joko.security.storage.type", havingValue = "in-memory")
    public static class InMemoryStorageConfiguration {
        // Las implementaciones in-memory se configuran en JokoSecurityAutoConfiguration
        // si no hay otros beans de storage disponibles
    }
}
