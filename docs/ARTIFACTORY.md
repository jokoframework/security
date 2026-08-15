# Guía de Artifactory - Joko Security

Esta guía documenta la integración con Artifactory interno para publicación y consumo de joko-security.

## Información de Artifactory

- **URL Base**: https://artifactory.example.com/artifactory/
- **Web UI**: https://artifactory.example.com/artifactory/webapp/
- **Repositorio Releases**: `libs-release`
- **Repositorio Snapshots**: `libs-snapshot`

## Acceso

### Requisitos

1. **Cuenta de usuario** en tu Artifactory
2. **Permisos de deploy** en los repositorios libs-release y libs-snapshot
3. **Conectividad** a tu red interna (VPN si estás remoto)

### Solicitar Acceso

Contactar a tu equipo de DevOps:

- Solicitar usuario y password de Artifactory
- Solicitar permisos de deploy (write) en:
  - libs-release
  - libs-snapshot

## Configuración Local

### 1. Variables de Entorno

```bash
# Agregar a ~/.bashrc o ~/.zshrc
export ARTIFACTORY_USER="your-username"
export ARTIFACTORY_PASSWORD="tu-password"
export ARTIFACTORY_BASE_URL="https://artifactory.example.com/artifactory"
```

Recargar configuración:

```bash
source ~/.bashrc  # o source ~/.zshrc
```

### 2. Maven Settings

Copiar settings.xml:

```bash
cp settings.xml.example ~/.m2/settings.xml
```

El archivo debe tener:

```xml
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
```

### 3. Verificar Configuración

```bash
# Test de conectividad
curl -u $ARTIFACTORY_USER:$ARTIFACTORY_PASSWORD \
  "https://artifactory.example.com/artifactory/api/system/ping"

# Debería retornar: OK
```

## Publicación

### Snapshots (Desarrollo)

```bash
# Verificar versión actual es SNAPSHOT
./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout
# Debe terminar en -SNAPSHOT (ej: 2.0.0-SNAPSHOT)

# Publicar
./publish-artifactory.sh snapshot
```

### Releases (Producción)

```bash
# 1. Actualizar versión (remover -SNAPSHOT)
./publish.sh version 2.0.0

# 2. Verificar cambios
git diff pom.xml

# 3. Commit y tag
git add pom.xml */pom.xml
git commit -m "chore: Release 2.0.0"
git tag -a v2.0.0 -m "Release 2.0.0"

# 4. Publicar
./publish-artifactory.sh release

# 5. Push (opcional, después de verificar)
git push origin develop --tags
```

## Consumo

### Desde Maven

En el proyecto consumidor:

**pom.xml**:

```xml
<repositories>
    <repository>
        <id>central</id>
        <url>https://artifactory.example.com/artifactory/libs-release</url>
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

**~/.m2/settings.xml** (mismo que para publicar):

```xml
<servers>
    <server>
        <id>central</id>
        <username>${env.ARTIFACTORY_USER}</username>
        <password>${env.ARTIFACTORY_PASSWORD}</password>
    </server>
</servers>
```

### Desde Gradle

**build.gradle**:

```gradle
repositories {
    maven {
        url "https://artifactory.example.com/artifactory/libs-release"
        credentials {
            username = System.getenv("ARTIFACTORY_USER")
            password = System.getenv("ARTIFACTORY_PASSWORD")
        }
    }
}

dependencies {
    implementation 'io.github.jokoframework:joko-security-starter:2.0.0'
}
```

## Verificación

### Web UI

1. Abrir: https://artifactory.example.com/artifactory/webapp/
2. Login con credenciales
3. Ir a: Artifacts → libs-release → io → github → jokoframework
4. Verificar presencia de joko-security-*

### Maven CLI

```bash
# Buscar artefacto específico
curl -u $ARTIFACTORY_USER:$ARTIFACTORY_PASSWORD \
  "https://artifactory.example.com/artifactory/api/storage/libs-release/io/github/jokoframework/joko-security-starter/2.0.0"

# Listar todas las versiones
curl -u $ARTIFACTORY_USER:$ARTIFACTORY_PASSWORD \
  "https://artifactory.example.com/artifactory/api/search/versions?g=io.github.jokoframework&a=joko-security-starter"
```

## Comparación GitHub vs Artifactory

| Aspecto | GitHub Packages | Artifactory interno |
|---------|----------------|-------------------|
| Acceso | Público (con token) | Internal (VPN) |
| Autenticación | GitHub token | User/password |
| Uso | Proyectos open source | Internal projects |
| Disponibilidad | Siempre (internet) | Internal network + VPN |
| Costo | Gratis (GitHub Free) | Infraestructura interna |
| Permisos | Por repositorio GitHub | Por usuario Artifactory |

## Mejores Prácticas

### Para Desarrollo

1. **Usar snapshots** para trabajo en progreso
2. **Publicar frecuentemente** para compartir cambios con el equipo
3. **Verificar antes de publicar**: `./mvnw clean test`

### Para Producción

1. **Crear releases** solo de versiones estables
2. **Siempre crear tag Git** con la versión
3. **Probar localmente** antes de publicar
4. **Documentar cambios** en CHANGELOG.md
5. **Publicar dual** (GitHub + Artifactory) para redundancia

### Seguridad

1. **NO hardcodear** credenciales en archivos
2. **Usar variables de entorno** siempre
3. **NO commitear** ~/.m2/settings.xml al repositorio
4. **Rotar passwords** periódicamente
5. **Usar VPN** cuando trabajes remoto

## Troubleshooting

### Problema: Cannot connect to Artifactory

**Síntomas**:
```
Connection timed out
```

**Soluciones**:
1. Verificar VPN conectada (si remoto)
2. Ping al servidor: `ping artifactory.example.com`
3. Verificar URL correcta

### Problema: 401 Unauthorized

**Síntomas**:
```
status code: 401, reason phrase: Unauthorized
```

**Soluciones**:
1. Verificar credenciales:
   ```bash
   echo $ARTIFACTORY_USER
   echo $ARTIFACTORY_PASSWORD
   ```
2. Probar login manual en web UI
3. Verificar settings.xml configurado

### Problema: 403 Forbidden

**Síntomas**:
```
status code: 403, reason phrase: Forbidden
```

**Soluciones**:
1. Verificar permisos de deploy en Artifactory
2. Contactar admin para solicitar permisos
3. Verificar que publicas al repositorio correcto

### Problema: Artifact already exists

**Síntomas**:
```
Failed to transfer file... Return code is: 409
```

**Soluciones**:
1. No se puede sobrescribir releases (es correcto!)
2. Para snapshots: Verificar versión en pom.xml termina en -SNAPSHOT
3. Para releases: Incrementar versión

## CI/CD con Artifactory

### Jenkins

```groovy
pipeline {
    environment {
        ARTIFACTORY_USER = credentials('artifactory-user')
        ARTIFACTORY_PASSWORD = credentials('artifactory-password')
    }

    stages {
        stage('Publish') {
            steps {
                sh './publish-artifactory.sh release'
            }
        }
    }
}
```

### GitLab CI

```yaml
publish:
  script:
    - export ARTIFACTORY_USER=$CI_ARTIFACTORY_USER
    - export ARTIFACTORY_PASSWORD=$CI_ARTIFACTORY_PASSWORD
    - ./publish-artifactory.sh release
  only:
    - tags
```

### GitHub Actions

```yaml
- name: Publish to Artifactory
  env:
    ARTIFACTORY_USER: ${{ secrets.ARTIFACTORY_USER }}
    ARTIFACTORY_PASSWORD: ${{ secrets.ARTIFACTORY_PASSWORD }}
  run: ./publish-artifactory.sh release
```

## Flujo Completo de Trabajo

### Desarrollo Diario

```bash
# 1. Trabajar en feature
git checkout -b feature/nueva-funcionalidad

# 2. Hacer cambios y commits
git add .
git commit -m "feat: nueva funcionalidad"

# 3. Publicar snapshot para compartir
./publish-artifactory.sh snapshot

# 4. Otros desarrolladores pueden usar
# Versión: 2.0.0-SNAPSHOT (actualiza automáticamente)
```

### Release de Versión

```bash
# 1. Preparar release
git checkout develop
git pull

# 2. Actualizar versión
./publish.sh version 2.1.0

# 3. Commit y tag
git add pom.xml */pom.xml
git commit -m "chore: Release 2.1.0"
git tag -a v2.1.0 -m "Release 2.1.0"

# 4. Publicar a ambos destinos
./publish.sh github              # GitHub Packages
./publish-artifactory.sh release  # Artifactory

# 5. Push
git push origin develop --tags

# 6. Preparar siguiente desarrollo
./publish.sh version 2.2.0-SNAPSHOT
git add pom.xml */pom.xml
git commit -m "chore: Prepare next development iteration"
git push
```

## Contactos

- **Administración Artifactory**: tu equipo de DevOps
- **Soporte técnico**: joko-security maintainers
- **Documentación**: docs/PACKAGING_GUIDE.md

---

**Última actualización**: 2024-12-29
**Versión**: 1.0
**Mantenedor**: Equipo Joko Security
