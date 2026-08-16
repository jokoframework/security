# Joko Security - Development Environment

Este módulo contiene el entorno completo de desarrollo para la biblioteca joko-security. Permite desarrollar, probar y depurar la biblioteca en un entorno Spring Boot real sin necesidad de integrarla en otra aplicación.

## 📦 Cómo Funciona la Arquitectura

### Relación con la Librería Principal

El proyecto `development/` es un **proyecto Maven independiente** que **depende** de la librería principal `joko-security`:

```xml
<!-- development/pom.xml -->
<dependency>
    <groupId>io.github.jokoframework</groupId>
    <artifactId>joko-security</artifactId>
    <version>${project.version}</version>
</dependency>
```

**Flujo de construcción:**

```
┌─────────────────────────────────────────────────────────────┐
│ 1. Proyecto Principal (joko-security)                       │
│    Ubicación: ../src/                                       │
│    Output: joko-security-1.2.17.jar                         │
│    Instalado en: ~/.m2/repository/...                       │
└─────────────────────────────────────────────────────────────┘
                          ↓
                    mvn install
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ 2. Proyecto Development                                     │
│    Ubicación: development/                                  │
│    Depende de: joko-security-1.2.17.jar (de Maven local)    │
│    Output: Aplicación Spring Boot ejecutable               │
└─────────────────────────────────────────────────────────────┘
```

### ¿Por qué esta arquitectura?

1. **Separación de Responsabilidades**:
   - La librería (`../src/`) es independiente y puede ser usada en cualquier proyecto
   - El módulo `development/` es solo para testing y desarrollo, no se distribuye

2. **Testing Realista**:
   - Simula cómo un usuario real usaría la librería
   - No hay dependencias circulares ni hacks de classpath

3. **Ciclo de Desarrollo**:
   - Haces cambios en `../src/`
   - Recompilas con `./dev.sh build` (ejecuta `mvn install` en la librería)
   - El módulo development recoge los cambios automáticamente

### Comandos y su Funcionamiento

```bash
./dev.sh build    # 1. mvn install en ../  (crea JAR)
                  # 2. mvn compile en development/ (usa ese JAR)

./dev.sh dev      # 1. Usa el JAR ya compilado
                  # 2. Ejecuta la app con H2
```

**Importante:** Si modificas código en `../src/`, debes ejecutar `./dev.sh build` para que los cambios se reflejen en el JAR que usa `development/`.

---

## 🚀 Inicio Rápido

```bash
# Primera vez - compila e instala la librería
./dev.sh build

# Inicia con H2 (rápido, en memoria)
./dev.sh dev

# O con PostgreSQL (más realista)
./dev.sh dev-pg
```

**Acceso:**
- API: http://localhost:8080/joko-security-dev
- H2 Console: http://localhost:8080/joko-security-dev/h2-console

---

## 📋 Comandos Disponibles

| Comando | Descripción |
|---------|-------------|
| `./dev.sh build` | Compila librería principal + módulo development |
| `./dev.sh dev` | Inicia aplicación con H2 (desarrollo rápido) |
| `./dev.sh dev-pg` | Inicia aplicación con PostgreSQL |
| `./dev.sh db-up` | Levanta contenedor PostgreSQL + Adminer |
| `./dev.sh db-down` | Detiene contenedor PostgreSQL |
| `./dev.sh db-reset` | Reinicia PostgreSQL con datos limpios |
| `./dev.sh test` | Ejecuta tests de la librería |
| `./dev.sh clean` | Limpia builds (target/) |
| `./dev.sh install` | Instala librería en Maven local (~/.m2) |

---

## 👥 Usuarios de Prueba

El `DevAuthenticationManager` proporciona usuarios hardcoded para testing sin base de datos real:

| Usuario | Password | Roles | Security Profile | Uso |
|---------|----------|-------|------------------|-----|
| `admin` | `admin123` | ROLE_ADMIN, ROLE_USER | ADMIN | Testing de permisos administrativos |
| `testuser` | `test123` | ROLE_USER | DEFAULT | Usuario básico estándar |
| `mobileuser` | `mobile123` | ROLE_USER, ROLE_MOBILE | MOBILE | Testing de apps móviles |
| `readonly` | `readonly123` | ROLE_READONLY | DEFAULT | Testing de permisos solo lectura |

### Security Profiles

Los perfiles definen los timeouts de tokens (ver `V4__seed_development_data.sql`):

| Profile | Access Token | Refresh Token |
|---------|--------------|---------------|
| DEFAULT | 30 min | 24 horas |
| ADMIN | 1 hora | 48 horas |
| MOBILE | 15 min | 7 días |

---

## 🧪 Testing de API

### Archivos .http (VS Code REST Client)

El directorio `api-tests/` contiene archivos `.http` para probar endpoints:

#### 📁 Archivos Disponibles

1. **[auth.http](api-tests/auth.http)** - Flujo completo de autenticación
   - Login → Access Token → Token Info → Refresh → Logout
   - Variables automáticas (sin copiar/pegar tokens)
   - 4 usuarios de prueba
   - Casos de error integrados

2. **[sessions.http](api-tests/sessions.http)** - Gestión de sesiones
   - Listar sesiones activas
   - Paginación y ordenamiento

3. **[error-tests.http](api-tests/error-tests.http)** - Testing de errores
   - Credenciales inválidas
   - Tokens expirados/revocados
   - Requests malformados

### Cómo Usar los Tests

1. **Instalar extensión VS Code:**
   - Nombre: **REST Client**
   - ID: `humao.rest-client`

2. **Levantar servidor:**
   ```bash
   ./dev.sh dev
   ```

3. **Abrir archivo .http** (ejemplo: `api-tests/auth.http`)

4. **Ejecutar requests:**
   - Click en "Send Request" sobre cada línea `###`
   - Los tokens se capturan automáticamente entre requests

### Variables Automáticas

Los archivos usan captura automática de respuestas:

```http
### Login
# @name login
POST {{baseUrl}}/api/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "test123"
}

### Usar el token del login anterior
POST {{baseUrl}}/api/token/user-access
X-JOKO-AUTH: {{login.response.body.$.secret}}
```

**No necesitas copiar/pegar tokens manualmente.**

### Headers Importantes

```http
Content-Type: application/json
X-JOKO-AUTH: {token}    # SIN prefijo "Bearer"
```

⚠️ **Nota:** Joko Security usa `X-JOKO-AUTH`, NO `Authorization: Bearer`.

---

## 🗄️ Base de Datos

### Opción 1: H2 (Desarrollo Rápido)

```bash
./dev.sh dev
```

- **URL JDBC:** `jdbc:h2:mem:app_db`
- **Console:** http://localhost:8080/joko-security-dev/h2-console
- **Usuario:** `sa`
- **Password:** (vacío)
- **Ventajas:** Inicio instantáneo, sin Docker
- **Desventajas:** Se borra al reiniciar, menos realista que PostgreSQL

### Opción 2: PostgreSQL (Más Realista)

```bash
./dev.sh db-up      # Primera vez
./dev.sh dev-pg     # Iniciar app
```

- **URL JDBC:** `jdbc:postgresql://localhost:5433/app_db`
- **Adminer:** http://localhost:8081
- **Usuario:** `app`
- **Password:** `secret`
- **Ventajas:** Persistente, igual a producción
- **Desventajas:** Requiere Docker

### Migraciones Flyway

Las migraciones se ejecutan **automáticamente** al iniciar la aplicación:

```
src/main/resources/db/migration/
├── V1__create_joko_security_schema.sql    # Crea schema
├── V2__create_joko_security_tables.sql    # Crea tablas
├── V3__create_joko_security_indexes.sql   # Crea índices
├── V4__seed_development_data.sql          # Datos base (profiles, keychain)
└── V5__seed_additional_test_data.sql      # Datos de prueba (OTP seeds)
```

**Para PostgreSQL:** Las migraciones también se ejecutan al crear el contenedor Docker (ver `docker-compose.yml`).

---

## 🏗️ Estructura del Módulo

```
development/
├── dev.sh                              # Script principal de desarrollo
├── docker-compose.yml                  # PostgreSQL + Adminer
├── pom.xml                             # Depende de joko-security JAR
│
├── src/main/java/
│   └── io.github.jokoframework.security.development/
│       ├── DevelopmentApplication.java       # Main Spring Boot
│       ├── DevAuthenticationManager.java     # Auth hardcoded
│       ├── DevAuthorizationManager.java      # Security config
│       └── DevJokoAuthentication.java        # Auth wrapper
│
├── src/main/resources/
│   ├── application.properties                # Config H2
│   ├── application-postgres.properties       # Config PostgreSQL
│   └── db/migration/                         # Flyway migrations
│       ├── V1__create_joko_security_schema.sql
│       ├── V2__create_joko_security_tables.sql
│       ├── V3__create_joko_security_indexes.sql
│       ├── V4__seed_development_data.sql
│       └── V5__seed_additional_test_data.sql
│
└── api-tests/                          # Testing con REST Client
    ├── auth.http                       # Flujo de autenticación
    ├── sessions.http                   # Gestión de sesiones
    └── error-tests.http                # Casos de error
```

---

## 💻 Integración con IDEs

### IntelliJ IDEA

1. **Importar proyecto:**
   - File → Open → Seleccionar `development/pom.xml`

2. **Configurar Run Configuration:**
   - Main Class: `io.github.jokoframework.security.development.DevelopmentApplication`
   - Working Directory: `$MODULE_WORKING_DIR$`
   - Active Profiles: (vacío para H2, `postgres` para PostgreSQL)

3. **Ejecutar:**
   - Click en Run/Debug

### VS Code

1. **Abrir carpeta:** `development/`

2. **Instalar extensiones:**
   - Java Extension Pack
   - Spring Boot Tools
   - REST Client (para archivos .http)

3. **Ejecutar:**
   - Command Palette → `Spring Boot Dashboard: Run`
   - O usar `./dev.sh dev` en terminal

### Eclipse

1. **Importar:**
   - File → Import → Existing Maven Projects
   - Seleccionar `development/`

2. **Ejecutar:**
   - Right-click en `DevelopmentApplication.java`
   - Run As → Java Application

---

## 🔧 Desarrollo de la Librería

### Ciclo de Trabajo Recomendado

```bash
# 1. Hacer cambios en la librería principal
cd ../src/main/java/io/github/jokoframework/security/
# ... editar código ...

# 2. Compilar e instalar la librería
cd development/
./dev.sh build

# 3. Probar cambios en ambiente real
./dev.sh dev

# 4. Probar con archivos .http
# Abrir api-tests/auth.http en VS Code
```

### Testing con Diferentes BD

```bash
# Testing rápido con H2 (recomendado durante desarrollo)
./dev.sh dev

# Testing realista con PostgreSQL (antes de commit)
./dev.sh db-up
./dev.sh dev-pg
```

### Ejecutar Tests Unitarios

```bash
# Tests de la librería principal
./dev.sh test

# Solo tests específicos
cd ..
mvn test -Dtest=TokenServiceTest
```

---

## 🌐 URLs de Desarrollo

### Con H2 (`./dev.sh dev`)

| Componente | URL | Credenciales |
|------------|-----|--------------|
| **API REST** | http://localhost:8080/joko-security-dev | N/A |
| **H2 Console** | http://localhost:8080/joko-security-dev/h2-console | `sa` / (vacío) |

**JDBC URL para H2 Console:** `jdbc:h2:mem:app_db`

### Con PostgreSQL (`./dev.sh dev-pg`)

| Componente | URL | Credenciales |
|------------|-----|--------------|
| **API REST** | http://localhost:8080/joko-security-dev | N/A |
| **Adminer** | http://localhost:8081 | `app` / `secret` |

**Conexión Adminer:**
- Sistema: PostgreSQL
- Servidor: `db` (dentro de Docker) o `localhost:5433` (desde host)
- Usuario: `app`
- Contraseña: `secret`
- Base de datos: `app_db`

---

## 🐛 Troubleshooting

### Error: "Cannot resolve joko-security dependency"

**Causa:** La librería principal no está instalada en Maven local.

**Solución:**
```bash
./dev.sh install
# O manualmente:
cd ..
mvn clean install -DskipTests
```

### Error: PostgreSQL connection refused

**Causa:** El contenedor de PostgreSQL no está corriendo.

**Solución:**
```bash
./dev.sh db-up
docker-compose ps  # Verificar que esté UP
```

### Error: Puerto 8080 ya está en uso

**Causa:** Otra aplicación usa el puerto 8080.

**Solución:**
```bash
# Editar application.properties
echo "server.port=8081" >> src/main/resources/application.properties

# O detener la otra aplicación
lsof -ti:8080 | xargs kill
```

### La aplicación no refleja cambios en la librería

**Causa:** No recompilaste la librería después de hacer cambios.

**Solución:**
```bash
./dev.sh build   # Recompila librería + development
```

### Error de OTP al hacer login

**Causa:** El usuario tiene un seed OTP configurado en V5.

**Solución:**
```bash
# Comentar el INSERT en V5__seed_additional_test_data.sql
# O enviar el header SEED_OTP_TOKEN con el código correcto
```

---

## 📚 Referencias

- **Librería Principal:** `../src/` (código fuente de joko-security)
- **Documentación:** `../README.md` (guía del proyecto)
- **Templates Flyway:** `../database-templates/flyway/` (para otros proyectos)
- **Migraciones Liquibase:** `../src/main/resources/db/liquibase/` (alternativa a Flyway)

---

**Última Actualización:** Diciembre 2024
**Versión:** joko-security v1.2.17 (Spring Boot 3.3.1)
