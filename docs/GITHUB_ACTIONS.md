# GitHub Actions - Guía Completa

Guía completa para configurar y usar GitHub Actions en joko-security para CI/CD automatizado.

## 📋 Tabla de Contenidos

- [Configuración Inicial](#-configuración-inicial)
- [Workflows Disponibles](#-workflows-disponibles)
- [Inicio Rápido - Publicar Versión](#-inicio-rápido---publicar-versión)
- [Verificar Publicación](#-verificar-publicación)
- [Consumir Packages](#-consumir-packages)
- [Troubleshooting](#-troubleshooting)
- [Avanzado](#-avanzado)

---

## ⚙️ Configuración Inicial

### 1. Habilitar Permisos en GitHub (Una sola vez)

**CRÍTICO**: Sin estos permisos, los workflows no podrán publicar packages.

1. Ve a: `https://github.com/jokoframework/security/settings/actions`
2. En **"Workflow permissions"**:
   - ✅ Selecciona **"Read and write permissions"**
   - ✅ Marca **"Allow GitHub Actions to create and approve pull requests"**
3. Click **"Save"**

### 2. Estructura de Archivos

```
.github/
└── workflows/
    ├── ci.yml.template              # CI - Build y tests automáticos
    ├── publish.yml         # Publicación a GitHub Packages
    └── settings.xml        # Configuración Maven para workflows
```

### 3. Verificar Workflows Activos

1. Ve a la pestaña **"Actions"** en GitHub
2. Deberías ver:
   - ✅ CI - Build and Test
   - ✅ Publish to GitHub Packages

---

## 🔧 Workflows Disponibles

### 1. CI - Build and Test

**Archivo**: `.github/workflows/ci.yml`

**Se ejecuta automáticamente en**:

- Push a `develop`, `main`, o branches `feature/**`
- Pull requests a `develop` o `main`

**Acciones**:

1. Checkout código
2. Setup JDK 17
3. Build con Maven
4. Ejecutar tests
5. OWASP Dependency Check
6. Upload artifacts (test results, reports)

**Duración**: ~3-5 minutos

**Outputs**:

- Test results en artifacts
- OWASP report en artifacts
- Build summary en GitHub

### 2. Publish to GitHub Packages

**Archivo**: `.github/workflows/publish.yml`

**Se ejecuta en**:

- Tags con formato `v*.*.*` (ej: `v2.0.0`, `v2.0.1`)
- Manual desde GitHub UI (workflow_dispatch)

**Acciones**:

1. Checkout código
2. Setup JDK 17
3. Configurar Maven settings
4. Extraer versión del tag
5. Actualizar pom.xml con versión del tag
6. Build con Maven
7. Ejecutar tests
8. Deploy a GitHub Packages

**Duración**: ~5-8 minutos

**Outputs**:

- 5 packages publicados:
  - `joko-security-core`
  - `joko-security-storage-postgres`
  - `joko-security-web`
  - `joko-security-autoconfigure`
  - `joko-security-starter`

**Permisos configurados**:

```yaml
permissions:
  contents: read # Leer código del repositorio
  packages: write # Publicar en GitHub Packages
```

---

## 🚀 Inicio Rápido - Publicar Versión

### Opción A: Automática con Git Tag (Recomendada)

```bash
# 1. Asegúrate de estar en la rama correcta
git checkout develop
git pull origin develop

# 2. (Opcional) Actualizar versión en pom.xml
# Si la versión ya es correcta, saltar este paso
./mvnw versions:set -DnewVersion=2.0.1 -DgenerateBackupPoms=false

# Commit cambios de versión
git add pom.xml */pom.xml
git commit -m "chore: Bump version to 2.0.1"
git push origin develop

# 3. Crear tag y publicar
git tag -a v2.0.1 -m "Release 2.0.1 - Descripción de cambios"
git push origin develop --tags

# 4. ✨ GitHub Actions publicará automáticamente
# Ve a: https://github.com/jokoframework/security/actions
```

**Nota**: El tag DEBE seguir el formato `v*.*.*` (con la 'v' al inicio).

### Opción B: Manual desde GitHub UI

1. Ve a: `https://github.com/jokoframework/security/actions`
2. Click en **"Publish to GitHub Packages"**
3. Click en **"Run workflow"**
4. Selecciona el branch (develop o feature/modular-refactor)
5. Click **"Run workflow"**
6. Espera ~5-8 minutos

**Nota**: La versión publicada será la del `pom.xml` del branch seleccionado.

---

## ✅ Verificar Publicación

### 1. Ver el Workflow

1. Ve a: `https://github.com/jokoframework/security/actions`
2. Click en el último run de **"Publish to GitHub Packages"**
3. Verifica que todos los steps estén en verde ✅
4. En **"Summary"** verás los artefactos publicados

### 2. Ver los Packages

1. Ve a: `https://github.com/jokoframework/packages`
2. Deberías ver 5 packages:
   - joko-security-core
   - joko-security-storage-postgres
   - joko-security-web
   - joko-security-autoconfigure
   - joko-security-starter
3. Click en cada uno para ver las versiones publicadas

### 3. Re-ejecutar Workflow Fallido

Si un workflow falla:

1. GitHub → Actions → Seleccionar el run fallido
2. Click **"Re-run jobs"**
3. Selecciona **"Re-run failed jobs"** o **"Re-run all jobs"**

---

## 📦 Consumir Packages

### 1. Configurar Autenticación

#### Generar GitHub Token

1. GitHub → Settings → Developer settings → Personal access tokens
2. Generate new token (classic)
3. Nombre: "Maven GitHub Packages"
4. Permisos: ✅ `read:packages`
5. Generate token
6. Copiar el token (`ghp_xxxxx`)

#### Para Maven

Crear/editar `~/.m2/settings.xml`:

```xml
<settings>
    <servers>
        <server>
            <id>github</id>
            <username>TU_USUARIO_GITHUB</username>
            <password>ghp_TuTokenPersonalDeGitHub</password>
        </server>
    </servers>
</settings>
```

#### Para Gradle

Crear `~/.gradle/gradle.properties`:

```properties
gpr.user=tu-usuario-github
gpr.token=ghp_TuTokenPersonalDeGitHub
```

O usar variables de entorno:

```bash
export GITHUB_USERNAME=tu-usuario-github
export GITHUB_TOKEN=ghp_TuTokenPersonalDeGitHub
```

### 2. Agregar Dependencia

#### Maven (pom.xml)

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/jokoframework/security</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>io.github.jokoframework</groupId>
        <artifactId>joko-security-starter</artifactId>
        <version>2.0.1</version>
    </dependency>
</dependencies>
```

#### Gradle (build.gradle)

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
    implementation 'io.github.jokoframework:joko-security-starter:2.0.1'
}
```

### 3. Compilar Proyecto

```bash
# Maven
mvn clean install

# Gradle
./gradlew build
```

---

## 🐛 Troubleshooting

### Error: "Resource not accessible by integration"

**Causa**: Falta configurar permisos de workflow.

**Solución**: Ve a [Configuración Inicial](#-configuración-inicial) y configura "Read and write permissions".

### Error: "401 Unauthorized" al consumir packages

**Causa**: Falta configurar GitHub token en settings.xml o gradle.properties.

**Solución**:

1. Genera un GitHub token con permiso `read:packages`
2. Agrégalo a `~/.m2/settings.xml` (Maven) o `~/.gradle/gradle.properties` (Gradle)
3. Verifica que el `<id>` en settings.xml coincida con el del pom.xml (`github`)

### Error: "409 Conflict" al publicar

**Causa**: La versión ya existe en GitHub Packages.

**Solución**: GitHub Packages no permite sobrescribir versiones. Opciones:

- Incrementar versión y publicar nueva
- Eliminar el package existente en GitHub y volver a publicar

### Workflow no se ejecuta al hacer push de tag

**Causa**: El tag no sigue el patrón `v*.*.*`.

**Solución**: El tag debe ser:

- ✅ `v2.0.1`
- ✅ `v2.1.0`
- ❌ `2.0.1` (sin la 'v')
- ❌ `release-2.0.1`

### Tests fallan en CI pero pasan localmente

**Causa**: Diferencias en entorno (BD, configuración, etc.)

**Solución**:

- Verificar que los tests usen H2 in-memory
- Revisar configuración en `application-test.properties`
- Ver logs del workflow para detalles del error

### Error: "Failed to execute goal" en Maven

**Causa**: Dependencias faltantes o problemas de compilación.

**Solución**:

1. Verificar que todas las dependencias estén disponibles
2. Limpiar caché: `mvn clean`
3. Revisar logs detallados en el workflow

---

## 🔍 Avanzado

### Autenticación en CI/CD

Los workflows usan `GITHUB_TOKEN` automático (no requiere configuración):

**settings.xml** (`.github/workflows/settings.xml`):

```xml
<server>
    <id>github</id>
    <username>${env.GITHUB_ACTOR}</username>
    <password>${env.GITHUB_TOKEN}</password>
</server>
```

Variables disponibles automáticamente:

- `GITHUB_ACTOR`: Usuario que ejecuta el workflow
- `GITHUB_TOKEN`: Token con permisos del workflow
- `GITHUB_REF`: Referencia git (branch o tag)
- `GITHUB_SHA`: Commit SHA
- `GITHUB_REPOSITORY`: Nombre del repo (jokoframework/security)
- `GITHUB_WORKSPACE`: Directorio de trabajo

### Monitoreo

#### Ver Estado de Workflows

1. GitHub → Actions
2. Verás lista de todos los workflow runs con:
   - Estado (success, failure, in progress)
   - Duración
   - Branch/tag que lo disparó

#### Badges de Estado

Agregar al README.md:

```markdown
![CI](https://github.com/jokoframework/security/workflows/CI%20-%20Build%20and%20Test/badge.svg)
![Publish](https://github.com/jokoframework/security/workflows/Publish%20to%20GitHub%20Packages/badge.svg)
```

### Debug de Workflows

#### Ver logs detallados

1. GitHub → Actions → Select run
2. Click on job → View logs
3. Expandir steps para ver output detallado

#### Agregar variables de debug

En el workflow, agregar:

```yaml
- name: Debug info
  run: |
    echo "GitHub Actor: $GITHUB_ACTOR"
    echo "GitHub Ref: $GITHUB_REF"
    echo "GitHub SHA: $GITHUB_SHA"
    echo "Workspace: $GITHUB_WORKSPACE"
    mvn --version
    java -version
```

### Mantenimiento

#### Actualizar Versión de Java

En ambos workflows (publish.yml):

```yaml
- name: Set up JDK 17
  uses: actions/setup-java@v4
  with:
    java-version: "17" # Cambiar aquí
```

#### Actualizar Actions

```yaml
# Antes
- uses: actions/checkout@v3

# Después
- uses: actions/checkout@v4
```

#### Agregar Nuevos Workflows

1. Crear archivo en `.github/workflows/nombre.yml`
2. Definir triggers y jobs
3. Commit y push
4. El workflow estará disponible inmediatamente

### Testing Local de Workflows

Antes de publicar:

```bash
# Simular lo que hace el workflow
mvn clean install
mvn test
mvn org.owasp:dependency-check-maven:check
```

---

## 📋 Checklist de Publicación

Antes de publicar, verifica:

- [ ] Tests pasan localmente: `mvn test`
- [ ] Versión actualizada en pom.xml (si corresponde)
- [ ] Cambios commiteados y pusheados
- [ ] Tag creado con formato `vX.Y.Z`
- [ ] Permisos de workflow configurados en GitHub
- [ ] Workflow ejecutado sin errores
- [ ] Packages visibles en GitHub

---

## 🎯 Flujo Completo de Desarrollo

### Desarrollo Diario

```bash
# Crear feature branch
git checkout -b feature/nueva-funcionalidad

# Hacer cambios...
git add .
git commit -m "feat: Nueva funcionalidad"
git push origin feature/nueva-funcionalidad

# CI ejecutará automáticamente tests

# Cuando esté listo, merge a develop
git checkout develop
git merge feature/nueva-funcionalidad
git push origin develop
```

### Publicar Release

```bash
# 1. Actualizar versión
./mvnw versions:set -DnewVersion=2.0.1 -DgenerateBackupPoms=false

# 2. Commit y push
git add pom.xml */pom.xml
git commit -m "chore: Release 2.0.1"
git push origin develop

# 3. Crear y push tag
git tag -a v2.0.1 -m "Release 2.0.1 - Nueva funcionalidad agregada"
git push origin develop --tags

# 4. ✨ GitHub Actions publicará automáticamente
# Verificar en: https://github.com/jokoframework/security/actions
```

---

## 📚 Referencias

- [GitHub Actions Docs](https://docs.github.com/en/actions)
- [GitHub Packages Maven](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry)
- [setup-java Action](https://github.com/actions/setup-java)
- [Maven Deploy Plugin](https://maven.apache.org/plugins/maven-deploy-plugin/)
- **Guía de empaquetado**: `PACKAGING_GUIDE.md`
- **Integración**: `INTEGRATION_GUIDE.md`

---

## 🚀 Próximos Pasos (Mejoras Futuras)

- [ ] Code coverage con JaCoCo
- [ ] SonarCloud integration
- [ ] Dependabot para updates automáticos
- [ ] Release notes automáticos
- [ ] Notificaciones a Slack/Discord
- [ ] Deploy a staging/production

---

**Última actualización**: 2024-12-22
**Repositorio**: jokoframework/security
**Versión actual**: 2.0.0
