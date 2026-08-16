package io.github.jokoframework.security.autoconfigure;

import io.github.jokoframework.security.config.JokoSecurityProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

/**
 * Auto-configuración principal para joko-security.
 *
 * Esta clase configura automáticamente los beans necesarios para el funcionamiento
 * de joko-security cuando se detecta en el classpath.
 *
 * <p>La auto-configuración incluye:</p>
 * <ul>
 *   <li>Habilitación de {@link JokoSecurityProperties} para configuración external</li>
 *   <li>Escaneo de componentes en paquetes de joko-security</li>
 *   <li>Configuración de beans core (servicios de token, autenticación, etc.)</li>
 * </ul>
 *
 * <p>Los beans solo se crean si no existen beans personalizados del mismo tipo,
 * permitiendo que los usuarios sobreescriban la configuración por defecto.</p>
 *
 * @see JokoSecurityProperties
 * @see JokoSecurityStorageAutoConfiguration
 */
@AutoConfiguration
@EnableConfigurationProperties(JokoSecurityProperties.class)
@ComponentScan(basePackages = {
    "io.github.jokoframework.security.services",
    "io.github.jokoframework.security.springex",
    "io.github.jokoframework.security.storage.postgres.services"
})
public class JokoSecurityAutoConfiguration {
    // Service implementations are auto-discovered via component scanning
}
