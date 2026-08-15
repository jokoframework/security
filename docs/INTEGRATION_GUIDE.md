# Joko Security - Guía de Integración

Esta guía te muestra cómo integrar **joko-security** como biblioteca JAR en tu proyecto Spring Boot existente para agregar autenticación JWT y manejo de sesiones.

## 📋 Requisitos

- Java 17+
- Spring Boot 3.3.1+
- PostgreSQL 9.4+ (u otra BD compatible con JPA)
- Maven o Gradle

---

## 🚀 Integración Paso a Paso

### 1. Agregar Dependencia

#### Opción A: Maven

Agrega joko-security y sus dependencias peer a tu `pom.xml`:

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/jokoframework/security</url>
    </repository>
</repositories>

<dependencies>
    <!-- Joko Security - Starter (incluye todo lo necesario) -->
    <dependency>
        <groupId>io.github.jokoframework</groupId>
        <artifactId>joko-security-starter</artifactId>
        <version>2.0.0</version>
    </dependency>

    <!-- Spring Boot - Dependencias requeridas -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <!-- Driver de Base de Datos -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>

    <!-- Migraciones (Elige una opción) -->

    <!-- Opción A: Flyway (Recomendado) -->
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
    </dependency>
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-database-postgresql</artifactId>
    </dependency>

    <!-- Opción B: Liquibase -->
    <!--
    <dependency>
        <groupId>org.liquibase</groupId>
        <artifactId>liquibase-core</artifactId>
    </dependency>
    -->
</dependencies>
```

#### Opción B: Gradle (Groovy DSL)

Agrega joko-security a tu `build.gradle`:

```gradle
repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/jokoframework/security")
        credentials {
            username = project.findProperty("gpr.user") ?: System.getenv("GITHUB_USERNAME")
            password = project.findProperty("gpr.token") ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    // Joko Security - Starter (incluye todo lo necesario)
    implementation 'io.github.jokoframework:joko-security-starter:2.0.0'

    // Spring Boot - Dependencias requeridas
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-security'

    // Driver de Base de Datos
    runtimeOnly 'org.postgresql:postgresql'

    // Migraciones - Elige una opción

    // Opción A: Flyway (Recomendado)
    implementation 'org.flywaydb:flyway-core'
    implementation 'org.flywaydb:flyway-database-postgresql'

    // Opción B: Liquibase
    // implementation 'org.liquibase:liquibase-core'
}
```

#### Opción C: Gradle (Kotlin DSL)

Agrega joko-security a tu `build.gradle.kts`:

```kotlin
repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/jokoframework/security")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
            password = project.findProperty("gpr.token") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    // Joko Security - Starter (incluye todo lo necesario)
    implementation("io.github.jokoframework:joko-security-starter:2.0.0")

    // Spring Boot - Dependencias requeridas
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Driver de Base de Datos
    runtimeOnly("org.postgresql:postgresql")

    // Migraciones - Elige una opción

    // Opción A: Flyway (Recomendado)
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    // Opción B: Liquibase
    // implementation("org.liquibase:liquibase-core")
}
```

**Configurar credenciales de GitHub Packages:**

Crea `~/.gradle/gradle.properties`:

```properties
gpr.user=tu-usuario-github
gpr.token=ghp_TuTokenPersonalDeGitHub
```

O exporta variables de entorno:

```bash
export GITHUB_USERNAME=tu-usuario-github
export GITHUB_TOKEN=ghp_TuTokenPersonalDeGitHub
```

**Generar GitHub Personal Access Token:**

1. Ve a: https://github.com/settings/tokens
2. Click en "Generate new token (classic)"
3. Selecciona scope: `read:packages`
4. Copia el token generado

### 2. Configurar Base de Datos

#### Opción A: Flyway (Recomendado para nuevos proyectos)

**2.1. Copiar templates de migración:**

```bash
# Desde el root de joko-security
cp database-templates/flyway/sql/*.template tu-proyecto/src/main/resources/db/migration/

# Remover extensión .template
cd tu-proyecto/src/main/resources/db/migration/
rename 's/\.template$//' *.template
```

**2.2. Configurar Flyway en `application.properties`:**

```properties
# Habilitar Flyway
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true

# Configuración de base de datos
spring.datasource.url=jdbc:postgresql://localhost:5432/tu_database
spring.datasource.username=tu_usuario
spring.datasource.password=tu_password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
```

**2.3. Agregar datos semilla (security profiles):**

Crea `V4__seed_security_profiles.sql` en `db/migration/`:

```sql
-- Security Profiles
INSERT INTO "joko_security".security_profile (id, name, "key", access_token_timeout_seconds, refresh_token_timeout_seconds, max_access_token_requests, max_number_of_connections, max_number_devices_user, revocable)
VALUES
    (1, 'Default Profile', 'DEFAULT', 1800, 86400, 50, 5, 3, true),
    (2, 'Admin Profile', 'ADMIN', 3600, 172800, 100, 10, 5, true),
    (3, 'Mobile Profile', 'MOBILE', 900, 604800, 30, 3, 2, true);

-- JWT Signing Secret (genera uno propio con base64)
INSERT INTO "joko_security".keychain (id, "value")
VALUES (1, 'TU-SECRET-SEGURO-BASE64-AQUI');
```

⚠️ **Importante:** Genera tu propio secret seguro. No uses el de ejemplo en producción.

#### Opción B: Liquibase (Para proyectos existentes con Liquibase)

**2.1. Incluir changesets de joko-security:**

En tu `db-changelog-master.xml`:

```xml
<databaseChangeLog>
    <!-- Tus changesets existentes -->

    <!-- Incluir changesets de joko-security -->
    <include file="classpath:db/liquibase/db-changelog-joko-security.xml"/>
</databaseChangeLog>
```

**2.2. Copiar changesets:**

```bash
cp src/main/resources/db/liquibase/*.xml tu-proyecto/src/main/resources/db/liquibase/
```

### 3. Configuración de Joko Security

Crea `application.properties` con la configuración específica:

```properties
# ============================================
# JOKO SECURITY CONFIGURATION
# ============================================

# Context Path (opcional, ajustar según tu aplicación)
server.servlet.context-path=/tu-app

# Modo de almacenamiento del secret JWT
# BD = En base de datos (tabla keychain)
# FILE = En archivo del filesystem
joko.secret.mode=BD

# Si usas FILE mode, especifica la ruta
# joko.secret.file=/ruta/segura/secret.key

# Habilitar/deshabilitar autenticación (solo false en desarrollo)
joko.authentication.enable=true
```

### 4. Implementar Managers Requeridos

Joko Security requiere que implementes dos interfaces para integrarse con tu lógica de negocio:

#### 4.1. JokoAuthenticationManager (Requerido)

Implementa la lógica de autenticación de usuarios:

```java
package com.tu.proyecto.security;

import io.github.jokoframework.security.api.JokoAuthentication;
import io.github.jokoframework.security.api.JokoAuthenticationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomAuthenticationManager implements JokoAuthenticationManager {

    @Autowired
    private UserRepository userRepository;  // Tu repositorio de usuarios

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public JokoAuthentication authenticate(JokoAuthentication authentication)
            throws AuthenticationException {

        String username = authentication.getUsername();
        String password = authentication.getPassword();

        // 1. Buscar usuario en tu base de datos
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        // 2. Validar password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        // 3. Verificar que el usuario esté activo
        if (!user.isActive()) {
            throw new DisabledException("User is disabled");
        }

        // 4. Crear JokoAuthentication con datos del usuario
        JokoAuthentication jokoAuth = new JokoAuthentication() {
            @Override
            public String getUsername() {
                return username;
            }

            @Override
            public String getPassword() {
                return password;
            }
        };

        // 5. Configurar subject (identificador del usuario)
        jokoAuth.setSubject(username);

        // 6. Agregar roles del usuario
        user.getRoles().forEach(role -> jokoAuth.addRole(role.getName()));

        // 7. Configurar security profile (DEFAULT, ADMIN, MOBILE, etc.)
        jokoAuth.setSecurityProfile(determineSecurityProfile(user));

        // 8. Marcar como autenticado
        jokoAuth.setAuthenticated(true);

        return jokoAuth;
    }

    private String determineSecurityProfile(User user) {
        // Lógica para determinar qué security profile usar
        if (user.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN"))) {
            return "ADMIN";
        }
        if (user.isMobileUser()) {
            return "MOBILE";
        }
        return "DEFAULT";
    }
}
```

#### 4.2. JokoAuthorizationManager (Opcional)

Configura reglas de autorización adicionales para tus endpoints:

```java
package com.tu.proyecto.security;

import io.github.jokoframework.security.JokoJWTClaims;
import io.github.jokoframework.security.api.JokoAuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class CustomAuthorizationManager implements JokoAuthorizationManager {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // Configurar rutas públicas y protegidas de tu aplicación
        http.authorizeHttpRequests(auth -> auth
            // Rutas públicas
            .requestMatchers("/public/**").permitAll()
            .requestMatchers("/actuator/health").permitAll()

            // Rutas que requieren roles específicos
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers("/api/users/**").hasAnyRole("ADMIN", "USER")

            // Todo lo demás requiere autenticación
            .requestMatchers("/api/**").authenticated()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> authorize(
            JokoJWTClaims claims,
            Collection<? extends GrantedAuthority> baseAuthorizations) {

        // Puedes agregar autoridades adicionales basadas en los claims del JWT
        // Por defecto, retorna las autoridades base
        return baseAuthorizations;
    }
}
```

### 5. Crear Entidad de Usuario (Ejemplo)

```java
package com.tu.proyecto.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;  // Encriptado con BCrypt

    private boolean active = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    // Getters y setters
}

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;  // ROLE_USER, ROLE_ADMIN, etc.

    // Getters y setters
}
```

### 6. Configurar PasswordEncoder

```java
package com.tu.proyecto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

## 🔑 Uso de los Endpoints

Una vez integrado, joko-security expone automáticamente estos endpoints:

### Login
```http
POST /api/login
Content-Type: application/json

{
  "username": "usuario",
  "password": "password"
}

# Respuesta:
{
  "success": true,
  "secret": "eyJhbGciOiJIUzI1NiJ9...",  // Refresh Token
  "expiration": 1703001600000
}
```

### Obtener Access Token
```http
POST /api/token/user-access
X-JOKO-AUTH: {refresh_token}

# Respuesta:
{
  "success": true,
  "secret": "eyJhbGciOiJIUzI1NiJ9...",  // Access Token
  "expiration": 1703001600000
}
```

### Refrescar Token
```http
POST /api/token/refresh
X-JOKO-AUTH: {refresh_token}

# Respuesta: Nuevo refresh token
```

### Logout (Revocar Token)
```http
POST /api/logout
X-JOKO-AUTH: {refresh_token}

# Respuesta:
{
  "success": true
}
```

### Usar Access Token en Tus Endpoints
```http
GET /api/tu-endpoint
X-JOKO-AUTH: {access_token}
```

---

## 📊 Security Profiles

Los security profiles definen los timeouts de tokens. Configurados en la tabla `security_profile`:

| Profile | Access Token | Refresh Token | Uso Recomendado |
|---------|--------------|---------------|-----------------|
| DEFAULT | 30 min | 24 horas | Usuarios web estándar |
| ADMIN | 1 hora | 48 horas | Administradores |
| MOBILE | 15 min | 7 días | Aplicaciones móviles |

Puedes crear perfiles adicionales según tus necesidades.

---

## 🔐 Flujo de Autenticación

```
1. Usuario → POST /api/login
            ↓
2. CustomAuthenticationManager valida credenciales
            ↓
3. ← Refresh Token (larga duración, permisos limitados)
            ↓
4. Cliente → POST /api/token/user-access con Refresh Token
            ↓
5. ← Access Token (corta duración, permisos completos)
            ↓
6. Cliente → GET /api/recursos con Access Token
            ↓
7. (Antes de expirar) → POST /api/token/user-access
            ↓
8. ← Nuevo Access Token
```

**Ventajas:**
- Refresh tokens revocables (logout)
- Access tokens de corta duración (seguridad)
- No se almacenan access tokens en BD (stateless)

### 🔍 Diferencias entre REFRESH y ACCESS Tokens

| Característica | REFRESH Token | ACCESS Token |
|----------------|---------------|--------------|
| **Almacenamiento** | ✅ Se guarda en BD (tabla `token`) | ❌ NO se guarda en BD |
| **Revocación** | ✅ Se puede revocar (logout) | ❌ NO se puede revocar directamente |
| **Duración** | ⏰ Larga (días/semanas) | ⏱️ Corta (minutos/horas) |
| **Permisos** | 🔒 Limitados (solo renovar/logout) | 🔓 Completos (acceso a recursos) |
| **Validación** | 🔍 Firma + Expiración + Revocación BD | 🔍 Firma + Expiración únicamente |

**IMPORTANTE:**
- ⚠️ Los ACCESS tokens **NO se pueden revocar** individualmente porque no se guardan en BD
- ✅ Para "revocar" un access token, se revoca el REFRESH token asociado y se espera a que el access token expire naturalmente
- 💡 Por esto es crítico que los access tokens tengan una duración corta (recomendado: 15-60 minutos)

**Código correcto para validar tokens:**

```java
// ✅ CORRECTO: Usar tokenInfoAsClaims() que maneja ambos tipos
Optional<JokoJWTClaims> claims = tokenService.tokenInfoAsClaims(token);
if (claims.isEmpty()) {
    // Token inválido o revocado (si es REFRESH)
}

// ❌ INCORRECTO: No usar hasBeenRevoked() directamente en tokens genéricos
// porque retornará true para todos los ACCESS tokens
if (tokenService.hasBeenRevoked(claims.getId())) {
    // Esto siempre será true para ACCESS tokens!
}
```

---

## ⚙️ Configuración Avanzada

### Modo FILE para JWT Secret

Si prefieres almacenar el secret en filesystem:

```properties
joko.secret.mode=FILE
joko.secret.file=/etc/tu-app/joko-secret.key
```

Genera el archivo:
```bash
# Generar secret aleatorio base64
openssl rand -base64 64 > /etc/tu-app/joko-secret.key
chmod 400 /etc/tu-app/joko-secret.key
chown tu-app-user:tu-app-user /etc/tu-app/joko-secret.key
```

### Two-Factor Authentication (OTP/TOTP)

Joko Security soporta 2FA opcional. Ver tabla `seed` para configurar seeds OTP por usuario.

### Auditoría de Sesiones

Las tablas `principal_session` y `audit_session` registran:
- Inicios de sesión
- Direcciones IP
- User agents
- Dispositivos

Consulta estas tablas para análisis de seguridad.

---

## 🐛 Troubleshooting

### Error: "Unable to obtain a security profile"

**Causa:** No existe el security profile especificado en `JokoAuthentication.setSecurityProfile()`.

**Solución:** Verifica que el profile exista en la tabla `security_profile`:
```sql
SELECT * FROM "joko_security".security_profile;
```

### Error: "The secret key is not configured"

**Causa:** No hay secret en la tabla `keychain` o en el archivo configurado.

**Solución:**
```sql
-- Verificar keychain
SELECT * FROM "joko_security".keychain;

-- Insertar si falta
INSERT INTO "joko_security".keychain (id, "value")
VALUES (1, 'TU-SECRET-BASE64');
```

### Error: 401 Unauthorized en todos los endpoints

**Causa:** El filtro de seguridad no está procesando el token correctamente.

**Solución:** Verifica que:
1. Estás enviando el header `X-JOKO-AUTH` (NO `Authorization: Bearer`)
2. El token no ha expirado
3. Para endpoints protegidos, usas Access Token (no Refresh Token)

### Error: CSRF token missing

**Causa:** CSRF está habilitado para API REST.

**Solución:** Deshabilitar CSRF en tu `JokoAuthorizationManager`:
```java
@Override
public void configure(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable());  // Para APIs REST stateless
}
```

### Los cambios en usuarios no se reflejan

**Causa:** Los datos del usuario están en el JWT (no se consulta BD en cada request).

**Solución:** El usuario debe hacer logout y login nuevamente para obtener un nuevo token con datos actualizados.

---

## 📋 Checklist de Integración

- [ ] Agregar dependencia `joko-security` al pom.xml
- [ ] Agregar dependencias peer (web, jpa, security, driver BD)
- [ ] Copiar templates de migración Flyway/Liquibase
- [ ] Configurar `application.properties` (datasource, joko.secret.mode)
- [ ] Crear security profiles en base de datos
- [ ] Insertar JWT secret en keychain
- [ ] Implementar `JokoAuthenticationManager`
- [ ] (Opcional) Implementar `JokoAuthorizationManager`
- [ ] Crear/adaptar entidad de Usuario
- [ ] Configurar `PasswordEncoder`
- [ ] Probar flujo: Login → Access Token → Endpoint protegido

---

## 📖 JokoTokenAdapter - Gestión Genérica de Tokens

`JokoTokenAdapter` es una abstracción de alto nivel sobre `ITokenService` que provee una **API limpia, genérica y extensible** para operaciones con tokens JWT.

### Ventajas

- ✅ **Genérico**: No atado a ningún dominio específico (bancario, e-commerce, healthcare, etc.)
- ✅ **Sin leaky abstractions**: Encapsula detalles de HTTP y bajo nivel
- ✅ **Type-safe**: Builder pattern con validación en tiempo de compilación
- ✅ **Extensible**: Sistema de metadata para datos custom sin cambiar contratos
- ✅ **OAuth2 compatible**: Conversión automática a formato estándar
- ✅ **Testeable**: Sin dependencias HTTP, fácil de mockear

### Arquitectura

```
Tu Aplicación (Domain-specific logic)
       ↓ usa
JokoTokenAdapter (High-level facade)
       ↓ delega
ITokenService (joko-security core)
```

### 1. Clase Principal: JokoTokenAdapter

```java
package com.tuapp.security.adapter;

import io.github.jokoframework.security.JokoJWTClaims;
import io.github.jokoframework.security.JokoJWTExtension.TOKEN_TYPE;
import io.github.jokoframework.security.JokoTokenWrapper;
import io.github.jokoframework.security.services.ITokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.util.*;

@Service
public class JokoTokenAdapter {

    private final ITokenService tokenService;

    @Autowired
    public JokoTokenAdapter(ITokenService tokenService) {
        this.tokenService = tokenService;
    }

    /**
     * Genera par completo de tokens (refresh + access)
     */
    public TokenPairResponse generateTokens(TokenGenerationRequest request) {
        try {
            // 1. Crear refresh token
            JokoTokenWrapper refreshToken = tokenService.createAndStoreRefreshToken(
                request.getUserId(),
                request.getSecurityProfile(),
                TOKEN_TYPE.REFRESH,
                request.getClientContext().getUserAgent(),
                request.getClientContext().getRemoteIp(),
                request.getRoles(),
                request.getOtpSeed()
            );

            // 2. Crear access token
            JokoTokenWrapper accessToken = tokenService.createAccessToken(
                refreshToken.getClaims(),
                null
            );

            // 3. Construir respuesta
            return TokenPairResponse.builder()
                .accessToken(accessToken.getToken())
                .refreshToken(refreshToken.getToken())
                .accessTokenExpiresIn(calculateExpiresIn(accessToken.getClaims()))
                .refreshTokenExpiresIn(calculateExpiresIn(refreshToken.getClaims()))
                .userId(request.getUserId())
                .roles(request.getRoles())
                .securityProfile(request.getSecurityProfile())
                .metadata(request.getMetadata())
                .build();

        } catch (GeneralSecurityException e) {
            throw new JokoTokenException("TOKEN_GENERATION_FAILED",
                "Failed to generate tokens", e);
        }
    }

    /**
     * Crea nuevo access token desde refresh token
     */
    public AccessTokenResponse refreshAccessToken(RefreshTokenRequest request) {
        try {
            // 1. Parsear refresh token
            JokoJWTClaims refreshClaims = tokenService.parse(request.getRefreshToken());

            // 2. Verificar que no esté revocado
            if (tokenService.hasBeenRevoked(refreshClaims.getId())) {
                throw new TokenRevokedException("Refresh token has been revoked");
            }

            // 3. Crear nuevo access token
            JokoTokenWrapper accessToken = tokenService.createAccessToken(
                refreshClaims,
                request.getOtp()
            );

            // 4. Construir respuesta
            return AccessTokenResponse.builder()
                .accessToken(accessToken.getToken())
                .expiresIn(calculateExpiresIn(accessToken.getClaims()))
                .userId(refreshClaims.getSubject())
                .roles(refreshClaims.getJoko().getRoles())
                .build();

        } catch (JwtException e) {
            throw new TokenValidationException("Invalid refresh token: " + e.getMessage());
        } catch (GeneralSecurityException e) {
            throw new JokoTokenException("TOKEN_REFRESH_FAILED",
                "Failed to refresh access token", e);
        }
    }

    /**
     * Valida un token JWT (REFRESH o ACCESS)
     *
     * IMPORTANTE: Solo los REFRESH tokens se guardan en BD y pueden ser revocados.
     * Los ACCESS tokens solo se validan por firma y expiración.
     */
    public TokenValidationResponse validateToken(String token) {
        try {
            // tokenInfoAsClaims() maneja correctamente la revocación:
            // - Para REFRESH tokens: verifica revocación en BD
            // - Para ACCESS tokens: solo valida firma y expiración
            Optional<JokoJWTClaims> claimsOpt = tokenService.tokenInfoAsClaims(token);

            if (claimsOpt.isEmpty()) {
                return TokenValidationResponse.builder()
                    .valid(false)
                    .errorCode("TOKEN_REVOKED")
                    .errorMessage("Token has been revoked or is invalid")
                    .build();
            }

            JokoJWTClaims claims = claimsOpt.get();
            return TokenValidationResponse.builder()
                .valid(true)
                .userId(claims.getSubject())
                .roles(claims.getJoko().getRoles())
                .tokenType(claims.getJoko().getType().name())
                .expiresIn(calculateExpiresIn(claims))
                .claims(claims)
                .build();

        } catch (JwtException e) {
            return TokenValidationResponse.builder()
                .valid(false)
                .errorCode("TOKEN_INVALID")
                .errorMessage("Invalid token: " + e.getMessage())
                .build();
        }
    }

    /**
     * Revoca un token (logout)
     *
     * NOTA: Solo los REFRESH tokens se pueden revocar efectivamente.
     * Si se pasa un ACCESS token, no tendrá efecto (no está en BD).
     * Para hacer logout, siempre pasar el REFRESH token.
     */
    public void revokeToken(String token) {
        try {
            JokoJWTClaims claims = tokenService.parse(token);

            // Solo tendrá efecto si es un REFRESH token (está en BD)
            // Los ACCESS tokens no están en BD, por lo que revocarlos no tiene efecto
            tokenService.revokeToken(claims.getId());
        } catch (Exception e) {
            throw new TokenValidationException("Cannot revoke invalid token");
        }
    }

    /**
     * Obtiene información del token sin lanzar excepciones
     */
    public Optional<TokenInfoResponse> getTokenInfo(String token) {
        try {
            JokoTokenInfoResponse info = tokenService.tokenInfo(token);

            return Optional.of(TokenInfoResponse.builder()
                .userId(info.getUserId())
                .audience(info.getAudiencie())
                .expiresIn(info.getExpiresIn())
                .build());

        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private long calculateExpiresIn(JokoJWTClaims claims) {
        long expirationTime = claims.getExpiration().getTime();
        long currentTime = System.currentTimeMillis();
        return Math.max(0, (expirationTime - currentTime) / 1000);
    }
}
```

### 2. Request DTOs

#### ClientContext

```java
package com.tuapp.security.adapter.request;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Encapsula información del cliente, desacoplado de HTTP
 */
public class ClientContext {

    private final String userAgent;
    private final String remoteIp;
    private final String deviceId;
    private final Map<String, String> headers;

    private ClientContext(Builder builder) {
        this.userAgent = builder.userAgent;
        this.remoteIp = builder.remoteIp;
        this.deviceId = builder.deviceId;
        this.headers = new HashMap<>(builder.headers);
    }

    /**
     * Crea desde HttpServletRequest (para aplicaciones web)
     */
    public static ClientContext fromHttpRequest(HttpServletRequest request) {
        return builder()
            .userAgent(request.getHeader("User-Agent"))
            .remoteIp(request.getRemoteAddr())
            .deviceId(request.getHeader("X-Device-Id"))
            .build();
    }

    /**
     * Crea manualmente (para contextos no-HTTP)
     */
    public static Builder builder() {
        return new Builder();
    }

    public String getUserAgent() { return userAgent; }
    public String getRemoteIp() { return remoteIp; }
    public String getDeviceId() { return deviceId; }
    public Map<String, String> getHeaders() { return new HashMap<>(headers); }

    public static class Builder {
        private String userAgent = "unknown";
        private String remoteIp = "0.0.0.0";
        private String deviceId;
        private Map<String, String> headers = new HashMap<>();

        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Builder remoteIp(String remoteIp) {
            this.remoteIp = remoteIp;
            return this;
        }

        public Builder deviceId(String deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public Builder header(String name, String value) {
            this.headers.put(name, value);
            return this;
        }

        public ClientContext build() {
            return new ClientContext(this);
        }
    }
}
```

#### TokenGenerationRequest

```java
package com.tuapp.security.adapter.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Request para generar par de tokens (refresh + access)
 */
public class TokenGenerationRequest {

    private final String userId;
    private final String securityProfile;
    private final List<String> roles;
    private final ClientContext clientContext;
    private final Map<String, Object> metadata;
    private final String otpSeed;

    private TokenGenerationRequest(Builder builder) {
        this.userId = builder.userId;
        this.securityProfile = builder.securityProfile;
        this.roles = new ArrayList<>(builder.roles);
        this.clientContext = builder.clientContext;
        this.metadata = new HashMap<>(builder.metadata);
        this.otpSeed = builder.otpSeed;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getUserId() { return userId; }
    public String getSecurityProfile() { return securityProfile; }
    public List<String> getRoles() { return new ArrayList<>(roles); }
    public ClientContext getClientContext() { return clientContext; }
    public Map<String, Object> getMetadata() { return new HashMap<>(metadata); }
    public String getOtpSeed() { return otpSeed; }

    public static class Builder {
        private String userId;
        private String securityProfile = "DEFAULT";
        private List<String> roles = new ArrayList<>();
        private ClientContext clientContext;
        private Map<String, Object> metadata = new HashMap<>();
        private String otpSeed;

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder securityProfile(String profile) {
            this.securityProfile = profile;
            return this;
        }

        public Builder roles(List<String> roles) {
            this.roles = new ArrayList<>(roles);
            return this;
        }

        public Builder addRole(String role) {
            this.roles.add(role);
            return this;
        }

        public Builder clientContext(ClientContext context) {
            this.clientContext = context;
            return this;
        }

        public Builder metadata(String key, Object value) {
            this.metadata.put(key, value);
            return this;
        }

        public Builder metadata(Map<String, Object> metadata) {
            this.metadata.putAll(metadata);
            return this;
        }

        public Builder otpSeed(String seed) {
            this.otpSeed = seed;
            return this;
        }

        public TokenGenerationRequest build() {
            if (userId == null || userId.isEmpty()) {
                throw new IllegalArgumentException("userId is required");
            }
            if (clientContext == null) {
                throw new IllegalArgumentException("clientContext is required");
            }
            return new TokenGenerationRequest(this);
        }
    }
}
```

#### RefreshTokenRequest

```java
package com.tuapp.security.adapter.request;

/**
 * Request para refrescar access token
 */
public class RefreshTokenRequest {

    private final String refreshToken;
    private final String otp;
    private final ClientContext clientContext;

    private RefreshTokenRequest(Builder builder) {
        this.refreshToken = builder.refreshToken;
        this.otp = builder.otp;
        this.clientContext = builder.clientContext;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getRefreshToken() { return refreshToken; }
    public String getOtp() { return otp; }
    public ClientContext getClientContext() { return clientContext; }

    public static class Builder {
        private String refreshToken;
        private String otp;
        private ClientContext clientContext;

        public Builder refreshToken(String token) {
            this.refreshToken = token;
            return this;
        }

        public Builder otp(String otp) {
            this.otp = otp;
            return this;
        }

        public Builder clientContext(ClientContext context) {
            this.clientContext = context;
            return this;
        }

        public RefreshTokenRequest build() {
            if (refreshToken == null || refreshToken.isEmpty()) {
                throw new IllegalArgumentException("refreshToken is required");
            }
            return new RefreshTokenRequest(this);
        }
    }
}
```

### 3. Response DTOs

#### TokenPairResponse

```java
package com.tuapp.security.adapter.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Respuesta con par de tokens (refresh + access)
 */
public class TokenPairResponse {

    private final String accessToken;
    private final String refreshToken;
    private final long accessTokenExpiresIn;
    private final long refreshTokenExpiresIn;
    private final String userId;
    private final List<String> roles;
    private final String securityProfile;
    private final Map<String, Object> metadata;

    private TokenPairResponse(Builder builder) {
        this.accessToken = builder.accessToken;
        this.refreshToken = builder.refreshToken;
        this.accessTokenExpiresIn = builder.accessTokenExpiresIn;
        this.refreshTokenExpiresIn = builder.refreshTokenExpiresIn;
        this.userId = builder.userId;
        this.roles = builder.roles;
        this.securityProfile = builder.securityProfile;
        this.metadata = new HashMap<>(builder.metadata);
    }

    /**
     * Convierte a formato OAuth2 estándar
     */
    public Map<String, Object> toOAuth2Response() {
        Map<String, Object> response = new HashMap<>();
        response.put("access_token", accessToken);
        response.put("refresh_token", refreshToken);
        response.put("token_type", "Bearer");
        response.put("expires_in", accessTokenExpiresIn);
        response.putAll(metadata);
        return response;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public long getAccessTokenExpiresIn() { return accessTokenExpiresIn; }
    public long getRefreshTokenExpiresIn() { return refreshTokenExpiresIn; }
    public String getUserId() { return userId; }
    public List<String> getRoles() { return roles; }
    public String getSecurityProfile() { return securityProfile; }
    public Map<String, Object> getMetadata() { return new HashMap<>(metadata); }

    public static class Builder {
        private String accessToken;
        private String refreshToken;
        private long accessTokenExpiresIn;
        private long refreshTokenExpiresIn;
        private String userId;
        private List<String> roles;
        private String securityProfile;
        private Map<String, Object> metadata = new HashMap<>();

        public Builder accessToken(String token) {
            this.accessToken = token;
            return this;
        }

        public Builder refreshToken(String token) {
            this.refreshToken = token;
            return this;
        }

        public Builder accessTokenExpiresIn(long seconds) {
            this.accessTokenExpiresIn = seconds;
            return this;
        }

        public Builder refreshTokenExpiresIn(long seconds) {
            this.refreshTokenExpiresIn = seconds;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder roles(List<String> roles) {
            this.roles = roles;
            return this;
        }

        public Builder securityProfile(String profile) {
            this.securityProfile = profile;
            return this;
        }

        public Builder metadata(String key, Object value) {
            this.metadata.put(key, value);
            return this;
        }

        public Builder metadata(Map<String, Object> metadata) {
            this.metadata.putAll(metadata);
            return this;
        }

        public TokenPairResponse build() {
            return new TokenPairResponse(this);
        }
    }
}
```

#### AccessTokenResponse

```java
package com.tuapp.security.adapter.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Respuesta con nuevo access token
 */
public class AccessTokenResponse {

    private final String accessToken;
    private final long expiresIn;
    private final String userId;
    private final List<String> roles;
    private final Map<String, Object> metadata;

    private AccessTokenResponse(Builder builder) {
        this.accessToken = builder.accessToken;
        this.expiresIn = builder.expiresIn;
        this.userId = builder.userId;
        this.roles = builder.roles;
        this.metadata = new HashMap<>(builder.metadata);
    }

    public Map<String, Object> toOAuth2Response() {
        Map<String, Object> response = new HashMap<>();
        response.put("access_token", accessToken);
        response.put("token_type", "Bearer");
        response.put("expires_in", expiresIn);
        response.putAll(metadata);
        return response;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getAccessToken() { return accessToken; }
    public long getExpiresIn() { return expiresIn; }
    public String getUserId() { return userId; }
    public List<String> getRoles() { return roles; }
    public Map<String, Object> getMetadata() { return new HashMap<>(metadata); }

    public static class Builder {
        private String accessToken;
        private long expiresIn;
        private String userId;
        private List<String> roles;
        private Map<String, Object> metadata = new HashMap<>();

        public Builder accessToken(String token) {
            this.accessToken = token;
            return this;
        }

        public Builder expiresIn(long seconds) {
            this.expiresIn = seconds;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder roles(List<String> roles) {
            this.roles = roles;
            return this;
        }

        public Builder metadata(String key, Object value) {
            this.metadata.put(key, value);
            return this;
        }

        public AccessTokenResponse build() {
            return new AccessTokenResponse(this);
        }
    }
}
```

#### TokenValidationResponse

```java
package com.tuapp.security.adapter.response;

import io.github.jokoframework.security.JokoJWTClaims;
import java.util.List;

/**
 * Respuesta de validación de token
 */
public class TokenValidationResponse {

    private final boolean valid;
    private final String userId;
    private final List<String> roles;
    private final String tokenType;
    private final long expiresIn;
    private final JokoJWTClaims claims;
    private final String errorCode;
    private final String errorMessage;

    private TokenValidationResponse(Builder builder) {
        this.valid = builder.valid;
        this.userId = builder.userId;
        this.roles = builder.roles;
        this.tokenType = builder.tokenType;
        this.expiresIn = builder.expiresIn;
        this.claims = builder.claims;
        this.errorCode = builder.errorCode;
        this.errorMessage = builder.errorMessage;
    }

    public boolean isValid() { return valid; }
    public boolean isExpired() { return "TOKEN_EXPIRED".equals(errorCode); }
    public boolean isRevoked() { return "TOKEN_REVOKED".equals(errorCode); }

    public String getUserId() { return userId; }
    public List<String> getRoles() { return roles; }
    public String getTokenType() { return tokenType; }
    public long getExpiresIn() { return expiresIn; }
    public JokoJWTClaims getClaims() { return claims; }
    public String getErrorCode() { return errorCode; }
    public String getErrorMessage() { return errorMessage; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean valid;
        private String userId;
        private List<String> roles;
        private String tokenType;
        private long expiresIn;
        private JokoJWTClaims claims;
        private String errorCode;
        private String errorMessage;

        public Builder valid(boolean valid) {
            this.valid = valid;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder roles(List<String> roles) {
            this.roles = roles;
            return this;
        }

        public Builder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public Builder expiresIn(long seconds) {
            this.expiresIn = seconds;
            return this;
        }

        public Builder claims(JokoJWTClaims claims) {
            this.claims = claims;
            return this;
        }

        public Builder errorCode(String code) {
            this.errorCode = code;
            return this;
        }

        public Builder errorMessage(String message) {
            this.errorMessage = message;
            return this;
        }

        public TokenValidationResponse build() {
            return new TokenValidationResponse(this);
        }
    }
}
```

#### TokenInfoResponse

```java
package com.tuapp.security.adapter.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Información del token
 */
public class TokenInfoResponse {

    private final String userId;
    private final String audience;
    private final long expiresIn;
    private final List<String> roles;
    private final String securityProfile;
    private final String tokenType;
    private final Map<String, Object> customClaims;

    private TokenInfoResponse(Builder builder) {
        this.userId = builder.userId;
        this.audience = builder.audience;
        this.expiresIn = builder.expiresIn;
        this.roles = builder.roles;
        this.securityProfile = builder.securityProfile;
        this.tokenType = builder.tokenType;
        this.customClaims = new HashMap<>(builder.customClaims);
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getUserId() { return userId; }
    public String getAudience() { return audience; }
    public long getExpiresIn() { return expiresIn; }
    public List<String> getRoles() { return roles; }
    public String getSecurityProfile() { return securityProfile; }
    public String getTokenType() { return tokenType; }
    public Map<String, Object> getCustomClaims() { return new HashMap<>(customClaims); }

    public static class Builder {
        private String userId;
        private String audience;
        private long expiresIn;
        private List<String> roles;
        private String securityProfile;
        private String tokenType;
        private Map<String, Object> customClaims = new HashMap<>();

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder audience(String audience) {
            this.audience = audience;
            return this;
        }

        public Builder expiresIn(long seconds) {
            this.expiresIn = seconds;
            return this;
        }

        public Builder roles(List<String> roles) {
            this.roles = roles;
            return this;
        }

        public Builder securityProfile(String profile) {
            this.securityProfile = profile;
            return this;
        }

        public Builder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public Builder customClaim(String key, Object value) {
            this.customClaims.put(key, value);
            return this;
        }

        public TokenInfoResponse build() {
            return new TokenInfoResponse(this);
        }
    }
}
```

### 4. Excepciones

```java
package com.tuapp.security.adapter.exception;

/**
 * Excepción base para errores de tokens
 */
public class JokoTokenException extends RuntimeException {

    private final String errorCode;

    public JokoTokenException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public JokoTokenException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

/**
 * Token inválido o malformado
 */
public class TokenValidationException extends JokoTokenException {
    public TokenValidationException(String message) {
        super("TOKEN_INVALID", message);
    }
}

/**
 * Token expirado
 */
public class TokenExpiredException extends JokoTokenException {
    public TokenExpiredException(String message) {
        super("TOKEN_EXPIRED", message);
    }
}

/**
 * Token revocado
 */
public class TokenRevokedException extends JokoTokenException {
    public TokenRevokedException(String message) {
        super("TOKEN_REVOKED", message);
    }
}
```

### 5. Ejemplos de Uso

#### Ejemplo 1: Aplicación Web (Login)

```java
package com.tuapp.controller;

import com.tuapp.security.adapter.JokoTokenAdapter;
import com.tuapp.security.adapter.request.ClientContext;
import com.tuapp.security.adapter.request.TokenGenerationRequest;
import com.tuapp.security.adapter.response.TokenPairResponse;
import com.tuapp.service.UserService;
import com.tuapp.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    @Autowired
    private JokoTokenAdapter tokenAdapter;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletRequest httpRequest) {

        // 1. Autenticar usuario (tu lógica de negocio)
        User user = userService.authenticate(
            loginRequest.getUsername(),
            loginRequest.getPassword()
        );

        // 2. Generar tokens con JokoTokenAdapter
        TokenPairResponse tokens = tokenAdapter.generateTokens(
            TokenGenerationRequest.builder()
                .userId(user.getId())
                .securityProfile(user.getSecurityProfile())  // DEFAULT, ADMIN, MOBILE
                .roles(user.getRoles())
                .clientContext(ClientContext.fromHttpRequest(httpRequest))
                // Metadata custom para tu dominio
                .metadata("email", user.getEmail())
                .metadata("displayName", user.getDisplayName())
                .build()
        );

        // 3. Retornar respuesta OAuth2
        Map<String, Object> response = tokens.toOAuth2Response();
        response.put("user", user.toDTO());

        return ResponseEntity.ok(response);
    }
}
```

#### Ejemplo 2: Refresh Token

```java
@PostMapping("/refresh")
public ResponseEntity<Map<String, Object>> refresh(
        @RequestBody RefreshRequest refreshRequest) {

    AccessTokenResponse accessToken = tokenAdapter.refreshAccessToken(
        RefreshTokenRequest.builder()
            .refreshToken(refreshRequest.getRefreshToken())
            .build()
    );

    return ResponseEntity.ok(accessToken.toOAuth2Response());
}
```

#### Ejemplo 3: Logout

```java
@PostMapping("/logout")
public ResponseEntity<Void> logout(
        @RequestHeader("Authorization") String authHeader) {

    String token = authHeader.replace("Bearer ", "");
    tokenAdapter.revokeToken(token);

    return ResponseEntity.noContent().build();
}
```

#### Ejemplo 4: Background Job (No-HTTP)

```java
@Service
public class ScheduledTaskService {

    @Autowired
    private JokoTokenAdapter tokenAdapter;

    @Scheduled(cron = "0 0 2 * * *")  // 2 AM daily
    public void generateSystemToken() {
        // Generar token sin HttpServletRequest
        TokenPairResponse tokens = tokenAdapter.generateTokens(
            TokenGenerationRequest.builder()
                .userId("system-scheduler")
                .securityProfile("DEFAULT")
                .addRole("ROLE_SYSTEM")
                .clientContext(
                    ClientContext.builder()
                        .userAgent("ScheduledTaskService/1.0")
                        .remoteIp("127.0.0.1")
                        .deviceId("scheduler-node-1")
                        .build()
                )
                .metadata("jobType", "daily-report")
                .metadata("triggeredAt", Instant.now())
                .build()
        );

        // Usar token para operaciones autenticadas
        callExternalAPI(tokens.getAccessToken());
    }
}
```

#### Ejemplo 5: Microservicio / API Gateway

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JokoTokenAdapter tokenAdapter;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String token = extractToken(request);

        if (token != null) {
            TokenValidationResponse validation = tokenAdapter.validateToken(token);

            if (validation.isValid()) {
                // Crear autenticación en SecurityContext
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        validation.getUserId(),
                        null,
                        convertRoles(validation.getRoles())
                    );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

### 6. Patrones de Extensibilidad

#### Metadata para Diferentes Dominios

```java
// E-Commerce
TokenGenerationRequest.builder()
    .userId(customer.getId())
    .metadata("cartId", cart.getId())
    .metadata("loyaltyPoints", customer.getPoints())
    .metadata("membershipTier", customer.getTier())
    .build();

// Healthcare
TokenGenerationRequest.builder()
    .userId(doctor.getId())
    .metadata("hospitalId", hospital.getId())
    .metadata("department", doctor.getDepartment())
    .metadata("certificationLevel", doctor.getCertification())
    .build();

// Finance / Banking
TokenGenerationRequest.builder()
    .userId(customer.getId())
    .metadata("accountNumber", account.getNumber())
    .metadata("riskProfile", customer.getRiskProfile())
    .metadata("kycVerified", customer.isKycVerified())
    .build();

// SaaS Multi-Tenant
TokenGenerationRequest.builder()
    .userId(user.getId())
    .metadata("organizationId", org.getId())
    .metadata("subscriptionPlan", org.getPlan())
    .metadata("features", org.getEnabledFeatures())
    .build();
```

### 7. Configuración de Security Profiles

Los profiles se configuran en la base de datos:

```sql
-- Security Profiles
INSERT INTO "joko_security".security_profile
    (id, name, "key", access_token_timeout_seconds, refresh_token_timeout_seconds,
     max_access_token_requests, max_number_of_connections, max_number_devices_user, revocable)
VALUES
    (1, 'Default Profile', 'DEFAULT', 1800, 86400, 50, 5, 3, true),    -- Web: 30min / 24h
    (2, 'Mobile Profile', 'MOBILE', 900, 604800, 30, 3, 2, true),      -- Mobile: 15min / 7 días
    (3, 'Admin Profile', 'ADMIN', 3600, 172800, 100, 10, 5, true),     -- Admin: 1h / 48h
    (4, 'System Profile', 'SYSTEM', 7200, 2592000, 1000, 100, 1, false); -- System: 2h / 30 días
```

### 8. Manejo de Errores

```java
try {
    TokenPairResponse tokens = tokenAdapter.generateTokens(request);
} catch (JokoTokenException e) {
    // Error genérico de tokens
    logger.error("Token error [{}]: {}", e.getErrorCode(), e.getMessage());
    return ResponseEntity.status(500).body(Map.of("error", e.getErrorCode()));
} catch (TokenValidationException e) {
    // Token inválido
    return ResponseEntity.status(401).body(Map.of("error", "INVALID_TOKEN"));
} catch (TokenExpiredException e) {
    // Token expirado
    return ResponseEntity.status(401).body(Map.of("error", "TOKEN_EXPIRED"));
} catch (TokenRevokedException e) {
    // Token revocado
    return ResponseEntity.status(401).body(Map.of("error", "TOKEN_REVOKED"));
}
```

---

## 📚 Referencias

- **Ejemplo Completo:** Ver `development/` para una implementación de referencia
- **Templates de Migraciones:** `database-templates/flyway/`
- **Guía de Empaquetado:** `docs/PACKAGING_GUIDE.md`
- **Documentación del Proyecto:** `README.md`
- **Ejemplo de Integración:** [joko_backend_starter_kit](https://github.com/jokoframework/joko_backend_starter_kit)

---

**Versión:** joko-security v2.0.0 (Spring Boot 3.3.1, Java 17, JJWT 0.12.6)
**Última Actualización:** Diciembre 2024
**Build System:** Maven o Gradle
