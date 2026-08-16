# Joko Security

[![Build Status](https://travis-ci.com/jokoframework/security.svg?branch=develop)](https://travis-ci.com/github/jokoframework/security)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.16-brightgreen.svg)
![Java](https://img.shields.io/badge/Java-17-orange.svg)
![JJWT](https://img.shields.io/badge/JJWT-0.12.6-blue.svg)

Joko Security provee autenticación y autorización mediante Tokens JWT. Puede utilizarse como microservicio independiente o embebido como librería en otra aplicación Spring Boot.

## Características Principales

- ✅ **JWT Tokens**: Access y Refresh tokens con firma segura
- ✅ **Spring Boot 3.5.16**: Spring Security 6.5.x y Tomcat 10.1.57
- ✅ **JJWT 0.12.6**: Biblioteca JWT moderna con protecciones OWASP
- ✅ **Arquitectura Modular**: Módulos independientes y reutilizables
- ✅ **Stateless**: Validación en memoria para escalabilidad
- ✅ **Revocación de tokens**: Almacenamiento en PostgreSQL/Redis
- ✅ **Security Profiles**: Diferentes tiempos de vida para tokens
- ✅ **Two-Factor Auth**: Soporte para TOTP/OTP
- ✅ **Session Auditing**: Registro de sesiones de usuario

## Arquitectura Multi-Módulo

```
joko-security-parent (2.0.0)
├── joko-security-core              # JWT services, filtros (REQUERIDO)
├── joko-security-storage-postgres  # Storage PostgreSQL para tokens
├── joko-security-web               # Controllers REST (opcional)
├── joko-security-autoconfigure     # Spring Boot auto-configuration
└── joko-security-starter           # BOM - Todo en una dependencia
```

## Inicio Rápido

### 1. Como Dependencia en otro Proyecto

El POM del parent **no declara remotes**. Maven resuelve desde **Maven Central** y `~/.m2`. Un remoto privado (GitHub Packages, Artifactory) va en `~/.m2/settings.xml` o `mvn -s settings.xml`, no en el `pom.xml` del proyecto. Plantilla: `settings.xml.example`.

#### Maven

```xml
<dependencies>
    <dependency>
        <groupId>io.github.jokoframework</groupId>
        <artifactId>joko-security-starter</artifactId>
        <version>2.0.0</version>
    </dependency>
</dependencies>
```

Hasta que 2.x esté en Central: `./mvnw install` en este repo e instalar el starter desde el repositorio local.

#### Gradle

```gradle
repositories {
    mavenCentral()
    mavenLocal() // si instalaste el 2.x en local
}

dependencies {
    implementation 'io.github.jokoframework:joko-security-starter:2.0.0'
}
```

Ver [docs/INTEGRATION_GUIDE.md](./docs/INTEGRATION_GUIDE.md).

#### Configurar application.yml

```yaml
joko:
  security:
    jwt:
      secret: ${JWT_SECRET} # Mínimo 256 bits
      issuer: my-app
      audience: my-app-users
    storage:
      type: postgres # postgres | redis | in-memory
    web:
      enabled: false # Deshabilitar controllers de joko (usar los propios)
```

> **Nota**: Los TTL (Time To Live) de los tokens NO se configuran en `application.yml`.
> Se configuran en la base de datos a través de la tabla `security_profile`.

Ver [docs/INTEGRATION_GUIDE.md](./docs/INTEGRATION_GUIDE.md) para guía completa de integración.

### 2. Desarrollo Local (Compilar desde código fuente)

#### Pre-requisitos

- Java 17
- Maven 3.8+
- PostgreSQL 9.4+ (o H2 para testing)

#### Clonar y compilar

```bash
git clone https://github.com/jokoframework/security.git
cd security
git checkout feature/modular-refactor

# Opción 1: Usar script de ayuda (recomendado)
./publish.sh local

# Opción 2: Usar Maven Wrapper directamente
./mvnw clean install

# Opción 3: Usar Maven instalado globalmente
mvn clean install
```

**Recomendación**: Usar `./mvnw` (Maven Wrapper) para garantizar consistencia de versiones.

#### Configurar entorno de desarrollo

1. **Crear directorio de configuración**:

```bash
mkdir -p /opt/joko-security/dev
cp src/main/resources/application.properties.example /opt/joko-security/dev/application.properties
```

2. **Editar application.properties** con tus credenciales de BD

3. **Configurar variables de entorno**:

```bash
export ENV_VARS="/opt/joko-security/development.vars"
```

4. **Inicializar base de datos**:

```bash
# Crear schema y tablas
./scripts/updater fresh

# Cargar datos iniciales
./scripts/updater seed src/main/resources/db/sql/seed-data.sql
```

5. **Ejecutar aplicación**:

```bash
# Con Maven Wrapper (recomendado)
./mvnw spring-boot:run

# O con Maven global
mvn spring-boot:run
```

La aplicación estará disponible en `http://localhost:8080/security`

## Publicar como Dependencia

### Publicar en GitHub Packages

```bash
# Configurar ~/.m2/settings.xml con tu GitHub token
cp settings.xml.example ~/.m2/settings.xml
# Editar y agregar tu token

# Opción 1: Usar script de ayuda (recomendado)
./publish.sh github

# Opción 2: Usar Maven Wrapper
./mvnw clean deploy

# Opción 3: Usar Maven global
mvn clean deploy
```

### Publicar en Artifactory Interno

```bash
# Configurar credenciales
export ARTIFACTORY_USER="your-username"
export ARTIFACTORY_PASSWORD="tu-password"

# Publicar snapshot (desarrollo)
./publish-artifactory.sh snapshot

# Publicar release (producción)
./publish.sh version 2.0.0
./publish-artifactory.sh release
```

Ver [docs/PACKAGING_GUIDE.md](./docs/PACKAGING_GUIDE.md) y [docs/ARTIFACTORY.md](./docs/ARTIFACTORY.md) para instrucciones detalladas.

## Conceptos Clave

### Tokens

**Refresh Token**:

- Tiempo de vida largo (días/semanas)
- Permisos limitados
- Solo para obtener access tokens
- Almacenado de forma segura (cookies HTTP-only, keystore móvil)

**Access Token**:

- Tiempo de vida corto (minutos)
- Permisos completos
- Se renueva antes de expirar
- Almacenado en memoria (no en localStorage)

### Security Profiles

Configuran tiempos de vida de tokens según el canal:

- **Web**: Refresh token de horas
- **Mobile**: Refresh token de semanas
- **Admin**: Tokens más restrictivos

### Flujo de Autenticación

```
1. Login → Refresh Token (24h, permisos limitados)
2. Refresh → Access Token (15min, permisos completos)
3. API Calls → Authorization: Bearer {access_token}
4. Renovar antes de expirar → Repetir paso 2
```

## Personalización

Dos interfaces principales para implementar:

### JokoAuthenticationManager

Valida credenciales y retorna usuarios autenticados:

```java
@Service
public class CustomAuthManager implements JokoAuthenticationManager {
    @Override
    public JwtUserDetails authenticate(String username, String password) {
        // Validar contra tu BD o servicio externo
        User user = userRepository.findByUsername(username);
        if (user != null && passwordMatches(password, user.getPassword())) {
            return new JwtUserDetails(user.getId(), user.getUsername(), user.getRoles());
        }
        throw new BadCredentialsException("Invalid credentials");
    }
}
```

### JokoAuthorizationManager

Configura reglas de seguridad y permisos por URL:

```java
@Service
public class CustomAuthzManager implements JokoAuthorizationManager {
    @Override
    public void configureAuthorization(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/public/**").permitAll()
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        );
    }
}
```

Ver ejemplo completo en [joko_backend_starter_kit](https://github.com/jokoframework/joko_backend_starter_kit)

## Testing

### Ejecutar tests

```bash
# Preparar BD de test
./scripts/updater seed src/main/resources/db/sql/seed-test.sql

# Opción 1: Usar script de ayuda
./publish.sh test

# Opción 2: Usar Maven Wrapper
./mvnw test

# Opción 3: Test específico
./mvnw test -Dtest=TokenServiceTest

# Con Maven global
mvn test -Dtest=TokenServiceTest
```

### Coverage de tests

- Token creation y parsing
- Refresh token flow
- Token revocation
- Security filters
- JWT signature verification

## Configuración

### Variables de entorno requeridas

```bash
# JWT Secret (mínimo 32 caracteres, 256 bits)
JWT_SECRET=tu-secreto-muy-largo-y-aleatorio-importante

# Base de datos
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/joko_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=password
```

### Opciones de configuración avanzadas

Ver `application.properties.example` para todas las opciones disponibles:

- Tiempo de vida de tokens por perfil
- Modo de almacenamiento de secret (BD vs FILE)
- Habilitación de autenticación
- Rutas públicas (sin autenticación)
- Configuración de CORS

## Base de Datos

### Esquema

Todas las tablas en schema `joko_security`:

- `security_profile` - Configuración de tiempos de vida
- `keychain` - Claves secretas para firma JWT
- `token` - Refresh tokens activos/revocados
- `principal_session` - Sesiones de usuario
- `audit_session` - Auditoría de accesos
- `seed` - Semillas para OTP/TOTP
- `consumer_api` - Registro de consumidores API

### Migraciones

El proyecto usa **Liquibase** para migraciones automáticas.

```bash
# Crear BD desde cero
./scripts/updater fresh

# Actualizar schema existente
./scripts/updater update

# Generar diff SQL
mvn liquibase:diff
```

## Seguridad

### Protecciones implementadas

- Verificación de algoritmo JWT (evita el ataque `none`)
- Validación de firma y expiración de tokens
- Rotación de secretos
- HTTPS y CORS configurables
- Consultas parametrizadas (prevención de inyección SQL)

### Seguridad de dependencias

El parent corre [OWASP](https://owasp.org/) (*Open Worldwide Application Security Project*) Dependency-Check **13** en la fase `verify` (`aggregate` de los cinco módulos).

**Default del build: solo warning.** `failBuildOnCVSS` vale `11` (el plugin no corta el build; 11 está fuera de la escala CVSS 0–10). Los hallazgos salen en consola y en el HTML. Un `./mvnw clean install` termina en SUCCESS aunque haya CVE de score 9.

Clave de la [NVD](https://nvd.nist.gov/) (*National Vulnerability Database*):

```bash
# Pedirla en https://nvd.nist.gov/developers/request-an-api-key
export NVD_API_KEY='…'
```

El POM lee `NVD_API_KEY` vía `nvdApiKeyEnvironmentVariable`. Sin esa variable el 13.0.0 no puede actualizar la NVD y el análisis no sirve.

```bash
# Build normal (scan en warning; no falla)
./mvnw clean verify

# Saltar el scan (CI rápido / sin red NVD)
./mvnw clean verify -Ddependency-check.skip=true

# Gate estricto: falla si hay CVE con CVSS >= 8
./mvnw clean verify -Ddependency-check.failBuildOnCVSS=8
```

No uses `mvn dependency-check:check` en un módulo suelto (por ejemplo `joko-security-starter`) si querés el informe del reactor: ese goal **no hereda** la config del parent (`inherited=false`) y no aplica el umbral. El reporte “oficial” es el `aggregate` de la raíz.

Informes:

- `target/dependency-check-report.html`
- `target/dependency-check-report.xml`

La consola *identified with known vulnerabilities* lista CVE **sin score**. El score (CVSS v3/v4) está en el HTML, CVE por CVE. El umbral 8 solo se evalúa con `-Ddependency-check.failBuildOnCVSS=8`.

El JAR `joko-security-storage-postgres-*-SNAPSHOT` puede aparecer como CPE de PostgreSQL servidor: es un falso positivo por el nombre del artefacto, no por el driver.

Suppressions: `dependency-check-suppressions.xml` en la raíz del parent.

## Documentación Adicional

- **[docs/INTEGRATION_GUIDE.md](./docs/INTEGRATION_GUIDE.md)** - Guía completa de integración en otro proyecto
- **[docs/PACKAGING_GUIDE.md](./docs/PACKAGING_GUIDE.md)** - Guía completa de empaquetado y publicación
- **[docs/GITHUB_ACTIONS.md](./docs/GITHUB_ACTIONS.md)** - Guía completa de CI/CD con GitHub Actions
- **[CHANGELOG.md](./CHANGELOG.md)** - Historial de versiones

## Scripts de Ayuda

```bash
./publish.sh local         # Compilar e instalar localmente
./publish.sh test          # Ejecutar tests
./publish.sh github        # Publicar en GitHub Packages
./publish.sh version X.Y.Z # Actualizar versión
```

## Stack Tecnológico

- **Spring Boot**: 3.5.16
- **Spring Security**: 6.5.x (incluido en Spring Boot 3.5.16)
- **Java**: 17
- **JJWT**: 0.12.6
- **PostgreSQL**: 9.4+ (desarrollo y producción)
- **H2**: 2.2.224 (testing)
- **Liquibase**: Migraciones de BD
- **Maven**: 3.8+

## Versionamiento

Seguimos [Semantic Versioning](https://semver.org/):

- **MAJOR** (2.x.x): Cambios incompatibles (breaking changes)
- **MINOR** (x.1.x): Nueva funcionalidad compatible
- **PATCH** (x.x.1): Bug fixes

**Versión actual**: 2.0.0

## Licencia

[Especificar licencia - MIT/Apache/etc]

## Contribuir

1. Fork del proyecto
2. Crear feature branch (`git checkout -b feature/nueva-funcionalidad`)
3. Commit cambios (`git commit -m 'feat: Agregar nueva funcionalidad'`)
4. Push al branch (`git push origin feature/nueva-funcionalidad`)
5. Abrir Pull Request

## Soporte

- **Issues**: https://github.com/jokoframework/security/issues
- **Documentación**: Ver archivos .md en el repositorio
- **Ejemplo de uso**: [joko_backend_starter_kit](https://github.com/jokoframework/joko_backend_starter_kit)

## Roadmap

- [ ] Soporte para Redis como storage alternativo
- [ ] GitHub Actions CI/CD automatizado
- [ ] Docker compose para desarrollo
- [ ] Métricas y monitoring con Micrometer
- [ ] Documentación Swagger/OpenAPI mejorada

---

**Última actualización**: 2024-12-22
**Branch actual**: feature/modular-refactor
**Versión**: 2.0.0-SNAPSHOT
