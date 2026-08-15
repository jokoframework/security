package io.github.jokoframework.security.development;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Development application for testing joko-security library features.
 * This application includes all necessary dependencies and configuration
 * for developing and testing the joko-security library.
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "io.github.jokoframework.security",
    "io.github.jokoframework.security.development"
})
@EnableJpaRepositories(basePackages = "io.github.jokoframework.security.repositories")
@EntityScan(basePackages = "io.github.jokoframework.security.entities")
public class DevelopmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(DevelopmentApplication.class, args);
    }

    @Bean
    public CommandLineRunner displayDevelopmentInfo(@Autowired JdbcTemplate jdbcTemplate) {
        return args -> {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("🚀 Joko Security Development Environment");
            System.out.println("=".repeat(60));

            try {
                // Check if data exists
                Integer consumerCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM joko_security.consumer_api", Integer.class);
                Integer profileCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM joko_security.security_profile", Integer.class);
                Integer principalCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM joko_security.principal_session", Integer.class);

                System.out.println("\n📊 Database Status:");
                System.out.println("  • Consumer APIs: " + consumerCount);
                System.out.println("  • Security Profiles: " + profileCount);
                System.out.println("  • Principal Sessions: " + principalCount);

                if (consumerCount > 0) {
                    System.out.println("\n✅ Development data loaded successfully!");
                    System.out.println("\n📝 Available Test Data:");
                    System.out.println("  • Consumer API: dev-app / dev-secret-123");
                    System.out.println("  • Security Profiles: ADMIN, USER, MOBILE");
                    System.out.println("  • OTP Seeds: Available for 2FA testing");
                }

                System.out.println("\n👤 Test Users (DevAuthenticationManager):");
                System.out.println("  • admin / admin123 → [ROLE_ADMIN, ROLE_USER] (ADMIN profile)");
                System.out.println("  • testuser / test123 → [ROLE_USER] (DEFAULT profile)");
                System.out.println("  • mobileuser / mobile123 → [ROLE_USER, ROLE_MOBILE] (MOBILE profile)");
                System.out.println("  • readonly / readonly123 → [ROLE_READONLY] (DEFAULT profile)");

                System.out.println("\n🌐 Endpoints:");
                System.out.println("  • API: http://localhost:8080/joko-security-dev");
                System.out.println("  • H2 Console: http://localhost:8080/joko-security-dev/h2-console");
                System.out.println("    - JDBC URL: jdbc:h2:mem:app_db");
                System.out.println("    - Username: sa");
                System.out.println("    - Password: (empty)");

                System.out.println("\n" + "=".repeat(60) + "\n");

            } catch (Exception e) {
                System.out.println("\n⚠️  Database not ready: " + e.getMessage());
                System.out.println("Make sure Flyway migrations have run successfully.\n");
            }
        };
    }
}