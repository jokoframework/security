# Guía de Empaquetado y Publicación - joko-security

Esta guía explica cómo empaquetar, publicar y consumir joko-security como dependencia en otros proyectos.

## Arquitectura Multi-Módulo

El proyecto está organizado en módulos:

```
joko-security-parent (2.0.0)
├── joko-security-core              # Servicios JWT, filtros (REQUERIDO)
├── joko-security-storage-postgres  # Integración PostgreSQL para tokens
├── joko-security-web               # Controllers REST (opcionales)
├── joko-security-autoconfigure     # Spring Boot auto-configuration
└── joko-security-starter           # BOM - Agrupa todo en una dependencia
```

## 1. Compilar y Empaquetar Localmente

### Pre-requisitos

- Java 17
- Maven 3.8+
- Git

### Compilar todos los módulos

```bash
# Desde el directorio raíz del proyecto
cd /path/to/joko-security

# Opción 1: Usar el script de ayuda (recomendado)
./publish.sh local

# Opción 2: Usar Maven Wrapper directamente
./mvnw clean install

# Opción 3: Usar Maven instalado globalmente
mvn clean install

# Saltar tests (no recomendado para producción)
./mvnw clean install -DskipTests
```

**Recomendación**: Usar el Maven Wrapper (`./mvnw`) para garantizar que todos usen la misma versión de Maven.

Esto instalará todos los módulos en tu repositorio local Maven:

- `~/.m2/repository/io/github/jokoframework/joko-security-core/2.0.0/`
- `~/.m2/repository/io/github/jokoframework/joko-security-starter/2.0.0/`
- etc.

## 2. Usar desde Repositorio Local (Desarrollo)

Si solo quieres probar en tu máquina local sin publicar:

### Con Maven

En el `pom.xml` de tu proyecto:

```xml
<dependencies>
    <!-- Opción 1: Usar el starter (incluye todo) -->
    <dependency>
        <groupId>io.github.jokoframework</groupId>
        <artifactId>joko-security-starter</artifactId>
        <version>2.0.0</version>
    </dependency>

    <!-- Opción 2: Módulos individuales (solo lo que necesites) -->
    <dependency>
        <groupId>io.github.jokoframework</groupId>
        <artifactId>joko-security-core</artifactId>
        <version>2.0.0</version>
    </dependency>
    <dependency>
        <groupId>io.github.jokoframework</groupId>
        <artifactId>joko-security-storage-postgres</artifactId>
        <version>2.0.0</version>
    </dependency>
</dependencies>
```

### Con Gradle

En el `build.gradle` de tu proyecto:

```gradle
dependencies {
    // Opción 1: Usar el starter (incluye todo)
    implementation 'io.github.jokoframework:joko-security-starter:2.0.0'

    // Opción 2: Módulos individuales (solo lo que necesites)
    implementation 'io.github.jokoframework:joko-security-core:2.0.0'
    implementation 'io.github.jokoframework:joko-security-storage-postgres:2.0.0'
}
```

O con Kotlin DSL (`build.gradle.kts`):

```kotlin
dependencies {
    // Opción 1: Usar el starter (incluye todo)
    implementation("io.github.jokoframework:joko-security-starter:2.0.0")

    // Opción 2: Módulos individuales (solo lo que necesites)
    implementation("io.github.jokoframework:joko-security-core:2.0.0")
    implementation("io.github.jokoframework:joko-security-storage-postgres:2.0.0")
}
```

**Recomendación**: Usar `joko-security-starter` para obtener todos los módulos automáticamente.

**Nota para Gradle**: Maven automáticamente busca en `~/.m2/repository/` (repositorio local). Gradle también busca allí por defecto usando `mavenLocal()` en repositories.

## 3. Publicar en GitHub Packages

### 3.1. Configurar Autenticación

Crear/editar `~/.m2/settings.xml`:

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0">
    <servers>
        <server>
            <id>github</id>
            <username>TU_USUARIO_GITHUB</username>
            <password>TU_GITHUB_TOKEN</password>
        </server>
    </servers>
</settings>
```

**Generar GitHub Token**:

1. Ve a GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic)
2. Generate new token con permisos:
   - `write:packages` (para publicar)
   - `read:packages` (para consumir)
3. Copia el token y úsalo como `password` en settings.xml

### 3.2. Publicar

```bash
# Opción 1: Usar el script de ayuda (recomendado)
./publish.sh github

# Opción 2: Usar Maven Wrapper directamente
./mvnw clean deploy

# Opción 3: Usar Maven instalado globalmente
mvn clean deploy

# Esto publicará todos los módulos en:
# https://maven.pkg.github.com/jokoframework/security
```

### 3.3. Verificar publicación

Visita: `https://github.com/jokoframework/security/packages`

Deberías ver los paquetes publicados:

- `io.github.jokoframework:joko-security-core`
- `io.github.jokoframework:joko-security-starter`
- etc.

## 4. Consumir desde GitHub Packages (Producción)

### 4.1. Configurar proyecto

#### Con Maven

En el `pom.xml` del middleware:

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/jokoframework/security</url>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>io.github.jokoframework</groupId>
        <artifactId>joko-security-starter</artifactId>
        <version>2.0.0</version>
    </dependency>
</dependencies>
```

#### Con Gradle

En `build.gradle`:

```gradle
repositories {
    mavenCentral()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/jokoframework/security")
        credentials {
            username = project.findProperty("gpr.user") ?: System.getenv("GITHUB_USERNAME")
            password = project.findProperty("gpr.token") ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation 'io.github.jokoframework:joko-security-starter:2.0.0'
}
```

O con Kotlin DSL (`build.gradle.kts`):

```kotlin
repositories {
    mavenCentral()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/jokoframework/security")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
            password = project.findProperty("gpr.token") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation("io.github.jokoframework:joko-security-starter:2.0.0")
}
```

### 4.2. Configurar autenticación

#### Para Maven

El equipo que use el middleware necesitará el mismo `~/.m2/settings.xml` con el GitHub token.

#### Para Gradle

Crear `~/.gradle/gradle.properties`:

```properties
gpr.user=TU_USUARIO_GITHUB
gpr.token=TU_GITHUB_TOKEN
```

O usar variables de entorno:

```bash
export GITHUB_USERNAME=tu-usuario-github
export GITHUB_TOKEN=ghp_TuTokenPersonalDeGitHub
```

#### Para CI/CD (Maven y Gradle)

**Maven**: Usar variables de entorno en `settings.xml`

```xml
<!-- settings.xml para CI/CD -->
<settings>
    <servers>
        <server>
            <id>github</id>
            <username>${env.GITHUB_USERNAME}</username>
            <password>${env.GITHUB_TOKEN}</password>
        </server>
    </servers>
</settings>
```

**Gradle**: Las credenciales ya configuradas usan `System.getenv()`, no requiere configuración adicional.

**GitHub Actions / Jenkins**: Configurar variables de entorno

```yaml
# .github/workflows/build.yml
env:
  GITHUB_USERNAME: ${{ github.actor }}
  GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}

steps:
  - name: Build with Maven
    run: mvn clean install

  # o con Gradle
  - name: Build with Gradle
    run: ./gradlew build
```

## 5. Publicar en Artifactory interno

Joko Security puede publicarse en Artifactory interno para uso en proyectos internos.

**URL Base**: https://artifactory.example.com/artifactory/

### 5.1. Configurar Credenciales

#### Opción A: Variables de Entorno (Recomendado)

```bash
# Configurar variables de entorno
export ARTIFACTORY_USER="your-username"
export ARTIFACTORY_PASSWORD="tu-password"

# Opcional: Cambiar URL base si es diferente
export ARTIFACTORY_BASE_URL="https://artifactory.example.com/artifactory"
```

Para hacerlo permanente, agregar a `~/.bashrc` o `~/.zshrc`:

```bash
# ~/.bashrc o ~/.zshrc
export ARTIFACTORY_USER="your-username"
export ARTIFACTORY_PASSWORD="tu-password"
```

#### Opción B: Archivo settings.xml

Copiar y configurar settings.xml:

```bash
cp settings.xml.example ~/.m2/settings.xml
# Las credenciales se tomarán de las variables de entorno
```

El archivo debe contener:

```xml
<settings>
    <servers>
        <server>
            <id>central</id>
            <username>${env.ARTIFACTORY_USER}</username>
            <password>${env.ARTIFACTORY_PASSWORD}</password>
        </server>
        <server>
            <id>snapshots</id>
            <username>${env.ARTIFACTORY_USER}</username>
            <password>${env.ARTIFACTORY_PASSWORD}</password>
        </server>
    </servers>
</settings>
```

### 5.2. Publicar Snapshots (Desarrollo)

Para publicar versiones de desarrollo (-SNAPSHOT):

```bash
# Método 1: Usar script especializado (recomendado)
./publish-artifactory.sh snapshot

# Método 2: Usar script principal
./publish.sh artifactory

# Método 3: Maven directo
./mvnw clean deploy -Partifactory -DskipTests
```

**Destino**: `https://artifactory.example.com/artifactory/libs-snapshot`

### 5.3. Publicar Releases (Producción)

Para publicar versiones estables (sin -SNAPSHOT):

```bash
# 1. Actualizar versión (remover -SNAPSHOT)
./publish.sh version 2.0.0

# 2. Commit y tag
git add pom.xml */pom.xml
git commit -m "chore: Bump version to 2.0.0"
git tag -a v2.0.0 -m "Release 2.0.0"
git push origin develop --tags

# 3. Publicar a Artifactory
./publish-artifactory.sh release
```

**Destino**: `https://artifactory.example.com/artifactory/libs-release`

### 5.4. Verificar Publicación

1. **Web UI de Artifactory**:
   - URL: https://artifactory.example.com/artifactory/webapp/
   - Navegar a: `libs-release` o `libs-snapshot`
   - Buscar: `io/github/jokoframework/joko-security-*`

2. **Maven CLI**:

```bash
# Listar versiones disponibles
curl -u $ARTIFACTORY_USER:$ARTIFACTORY_PASSWORD \
  "https://artifactory.example.com/artifactory/api/search/versions?g=io.github.jokoframework&a=joko-security-starter"
```

### 5.5. Consumir desde Artifactory

#### Configurar proyecto consumidor (Maven)

En `pom.xml`:

```xml
<repositories>
    <repository>
        <id>central</id>
        <name>Artifactory Releases</name>
        <url>https://artifactory.example.com/artifactory/libs-release</url>
    </repository>
    <repository>
        <id>snapshots</id>
        <name>Artifactory Snapshots</name>
        <url>https://artifactory.example.com/artifactory/libs-snapshot</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>io.github.jokoframework</groupId>
        <artifactId>joko-security-starter</artifactId>
        <version>2.0.0</version>
    </dependency>
</dependencies>
```

#### Configurar autenticación (Maven)

El consumidor también necesita `~/.m2/settings.xml`:

```xml
<settings>
    <servers>
        <server>
            <id>central</id>
            <username>${env.ARTIFACTORY_USER}</username>
            <password>${env.ARTIFACTORY_PASSWORD}</password>
        </server>
        <server>
            <id>snapshots</id>
            <username>${env.ARTIFACTORY_USER}</username>
            <password>${env.ARTIFACTORY_PASSWORD}</password>
        </server>
    </servers>
</settings>
```

### 5.6. Publicación Dual (GitHub + Artifactory)

Para publicar en ambos destinos:

```bash
# 1. Actualizar versión para release
./publish.sh version 2.0.0
git add pom.xml */pom.xml
git commit -m "chore: Release 2.0.0"
git tag -a v2.0.0 -m "Release 2.0.0"

# 2. Publicar a GitHub Packages
./publish.sh github

# 3. Publicar a Artifactory
./publish-artifactory.sh release

# 4. Push tag (activa GitHub Actions)
git push origin develop --tags
```

### 5.7. Troubleshooting Artifactory

#### Error: 401 Unauthorized

```
Causa: Credenciales incorrectas o no configuradas
Solución:
  1. Verificar variables de entorno:
     echo $ARTIFACTORY_USER
     echo $ARTIFACTORY_PASSWORD
  2. Verificar ~/.m2/settings.xml tiene las credenciales
  3. Verificar que el ID del servidor coincide: central
```

#### Error: 403 Forbidden

```
Causa: Usuario sin permisos de escritura en Artifactory
Solución:
  1. Contactar al administrador de Artifactory
  2. Solicitar permisos de deploy en libs-release y libs-snapshot
```

#### Error: Connection timeout

```
Causa: Artifactory no accesible (VPN requerida?)
Solución:
  1. Verificar conectividad: ping artifactory.example.com
  2. Conectar a tu VPN si estás remoto
  3. Verificar URL: curl https://artifactory.example.com/artifactory/
```

**Para más información**, ver [docs/ARTIFACTORY.md](./ARTIFACTORY.md)

## 6. Configuración en la aplicación consumidora

### 6.1. Agregar dependencia (pom.xml)

```xml
<dependencies>
    <!-- joko-security starter - Incluye todo -->
    <dependency>
        <groupId>io.github.jokoframework</groupId>
        <artifactId>joko-security-starter</artifactId>
        <version>2.0.0</version>
    </dependency>
</dependencies>
```

### 6.2. Configurar application.yml

```yaml
joko:
  security:
    jwt:
      secret: ${JWT_SECRET} # Variable de entorno
      issuer: my-app
      audience: my-app-users
    storage:
      type: postgres # o redis si lo tienen
    web:
      enabled: false # Deshabilitar controllers de joko, usar los del middleware
```

> **Nota Importante**: Los TTL (Time To Live) de los tokens NO se configuran aquí.
> Se administran desde la base de datos en la tabla `security_profile`, permitiendo
> cambios dinámicos sin redespliegue de la aplicación.

### 6.3. Variables de entorno

```bash
# .env o variables de sistema
export JWT_SECRET="tu-secreto-muy-largo-y-aleatorio-min-256-bits"
export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/joko_security"
export SPRING_DATASOURCE_USERNAME="postgres"
export SPRING_DATASOURCE_PASSWORD="password"
```

## 7. Versionamiento

### Crear nueva versión

```bash
# Opción 1: Usar el script de ayuda (recomendado)
./publish.sh version 2.0.1
git add pom.xml */pom.xml
git commit -m "chore: Bump version to 2.0.1"
git tag -a v2.0.1 -m "Release 2.0.1"
git push origin feature/modular-refactor --tags
./publish.sh github

# Opción 2: Manual con Maven Wrapper
./mvnw versions:set -DnewVersion=2.0.1 -DgenerateBackupPoms=false
git add pom.xml */pom.xml
git commit -m "chore: Bump version to 2.0.1"
git tag -a v2.0.1 -m "Release 2.0.1"
git push origin feature/modular-refactor --tags
./mvnw clean deploy
```

### Semantic Versioning

- **MAJOR** (2.x.x): Cambios incompatibles (breaking changes)
- **MINOR** (x.1.x): Nueva funcionalidad compatible
- **PATCH** (x.x.1): Bug fixes

## 8. CI/CD Automatizado con GitHub Actions

El proyecto incluye workflows de GitHub Actions pre-configurados:

### Workflows Incluidos

1. **Publish to GitHub Packages** (`.github/workflows/publish.yml`)
   - Se ejecuta al crear tags de versión (`v*.*.*`)
   - También se puede ejecutar manualmente
   - Publica todos los módulos a GitHub Packages

### Publicar Nueva Versión con GitHub Actions

```bash
# 1. Actualizar versión (si es necesario)
mvn versions:set -DnewVersion=2.0.1 -DgenerateBackupPoms=false

# 2. Commit cambios
git add pom.xml */pom.xml
git commit -m "chore: Bump version to 2.0.1"

# 3. Crear y push tag
git tag -a v2.0.1 -m "Release 2.0.1"
git push origin develop --tags

# 4. GitHub Actions publicará automáticamente
```

### Configuración Inicial de GitHub Actions

**Importante**: Configurar permisos del repositorio:

1. GitHub → Settings (del repo) → Actions → General
2. En "Workflow permissions", seleccionar **"Read and write permissions"**
3. Guardar

Esto permite que el `GITHUB_TOKEN` tenga permisos para publicar packages.

### Ver detalles completos

Consulta `GITHUB_ACTIONS.md` para:

- Instrucciones detalladas de cada workflow
- Troubleshooting
- Autenticación y permisos
- Verificación de publicaciones

## 9. Troubleshooting

### Error: "Could not find artifact"

- Verifica que `mvn clean install` se ejecutó sin errores
- Confirma que el repositorio está configurado en `<repositories>`
- Revisa autenticación en `~/.m2/settings.xml`

### Error: "401 Unauthorized" al publicar

- Verifica que el GitHub token tenga permisos `write:packages`
- Confirma que el `<id>` en settings.xml coincide con el del pom.xml

### Error: "403 Forbidden"

- El repositorio debe existir en GitHub primero
- Verifica que tienes permisos de escritura en el repo jokoframework/security

### Conflicto de versiones

```bash
# Limpiar repositorio local y recompilar
rm -rf ~/.m2/repository/io/github/jokoframework/joko-security-*
mvn clean install
```

## 10. Checklist de Publicación

- [ ] Tests pasando (`mvn test`)
- [ ] Versión actualizada en `pom.xml`
- [ ] Documentación actualizada (README)
- [ ] Commit y tag creado
- [ ] `mvn clean deploy` exitoso
- [ ] Verificar paquetes en GitHub/Artifactory
- [ ] Probar integración en middleware
- [ ] Actualizar versión en middleware

## Referencias

- **GitHub Packages**: https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry
- **Maven Deploy**: https://maven.apache.org/plugins/maven-deploy-plugin/
- **Repositorio joko-security**: https://github.com/jokoframework/security

---

**Última actualización**: 2024-12-20
**Versión actual**: 2.0.0
**Contacto**: joko-security maintainers
