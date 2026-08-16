package io.github.jokoframework.security.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;

/**
 * Registers joko-security REST controllers when {@code joko.security.web.enabled=true}.
 *
 * <p>Login, token and session endpoints live in {@code joko-security-web}. Without this
 * scan, a consuming application that only scans its own packages would not expose
 * {@code /api/login} and related routes.</p>
 */
@AutoConfiguration(after = JokoSecurityAutoConfiguration.class)
@ConditionalOnProperty(prefix = "joko.security.web", name = "enabled", havingValue = "true")
@ConditionalOnClass(name = "io.github.jokoframework.security.web.controller.AuthenticationController")
@ComponentScan(basePackages = "io.github.jokoframework.security.web")
public class JokoSecurityWebAutoConfiguration {
}
