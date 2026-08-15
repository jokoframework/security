package io.github.jokoframework.security;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Clase base abstracta para tests de integración que requieren PostgreSQL.
 *
 * Esta clase:
 * - Inicia un contenedor PostgreSQL usando TestContainers
 * - Configura Spring Boot para usar la base de datos del contenedor
 * - Ejecuta script de seed con datos de test (security profiles, etc.)
 * - Habilita rollback automático con @Transactional
 * - Reutiliza el mismo contenedor para todos los tests (mejora performance)
 *
 * NOTA: Los tests que extienden esta clase están actualmente deshabilitados
 * durante la migración a Spring Boot 3. Cuando se rehabiliten, necesitarán
 * una clase de configuración apropiada que reemplace Application.class
 *
 * Uso:
 * <pre>
 * public class MiTest extends AbstractPostgresIntegrationTest {
 *     // tus tests aquí
 * }
 * </pre>
 */
// TODO: Descomentar y configurar con la clase apropiada cuando se rehabiliten los tests de integración
// @SpringBootTest(classes = TODO_CONFIGURATION_CLASS.class)
@Transactional
@Testcontainers
@Sql(scripts = "/db/sql/seed-test.sql")
public abstract class AbstractPostgresIntegrationTest {

    /**
     * Contenedor PostgreSQL compartido entre todos los tests.
     *
     * Usando @Container, el contenedor se inicia una sola vez antes de todos
     * los tests y se reutiliza, lo cual mejora significativamente la velocidad
     * de ejecución.
     *
     * La imagen postgresql:9.6-alpine es compatible con PostgreSQL 9.4+ que
     * requiere el proyecto según la documentación.
     */
    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:9.6-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    /**
     * Configura dinámicamente las propiedades de Spring para usar el contenedor PostgreSQL.
     *
     * Este método se ejecuta antes de inicializar el contexto de Spring y configura
     * la conexión a la base de datos del contenedor.
     */
    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

        // Configuración de Liquibase para que ejecute las migraciones en el contenedor
        registry.add("spring.liquibase.enabled", () -> "true");
        registry.add("spring.liquibase.change-log", () -> "classpath:db/liquibase/db-changelog.xml");
    }
}
