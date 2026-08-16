#!/bin/bash

##############################################################################
# Script de Publicación a Artifactory - joko-security
#
# Publica los artefactos Maven a un Artifactory interno.
# La URL se toma de ARTIFACTORY_BASE_URL (sin default de infraestructura).
#
# Pre-requisitos:
#   - Java 21+
#   - Maven Wrapper (mvnw) en el directorio raíz
#   - ~/.m2/settings.xml configurado con credenciales de Artifactory
#   - Variables de entorno: ARTIFACTORY_USER, ARTIFACTORY_PASSWORD
#
# Uso:
#   ./publish-artifactory.sh                    # Publicar versión actual (SNAPSHOT)
#   ./publish-artifactory.sh release            # Publicar como release (requiere versión sin -SNAPSHOT)
#   ./publish-artifactory.sh snapshot           # Publicar como snapshot (explícito)
#   ./publish-artifactory.sh --help             # Mostrar ayuda
#
# Variables de entorno:
#   ARTIFACTORY_USER      - Usuario de Artifactory (requerido)
#   ARTIFACTORY_PASSWORD  - Password de Artifactory (requerido)
#   ARTIFACTORY_BASE_URL  - URL base de Artifactory (requerido, ej: https://artifactory.example.com/artifactory)
#
# Ejemplos:
#   export ARTIFACTORY_USER="your-username"
#   export ARTIFACTORY_PASSWORD="mi-password-seguro"
#   ./publish-artifactory.sh release
#
##############################################################################

set -e  # Exit on error

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Default values
ARTIFACTORY_BASE_URL="${ARTIFACTORY_BASE_URL:-}"
PUBLISH_TYPE="${1:-snapshot}"
SKIP_DOCS=false

# Check for --no-docs flag
for arg in "$@"; do
    if [ "$arg" == "--no-docs" ] || [ "$arg" == "--skip-docs" ]; then
        SKIP_DOCS=true
    fi
done

# Functions
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_step() {
    echo -e "${BLUE}==>${NC} $1"
}

show_banner() {
    echo ""
    echo "╔════════════════════════════════════════════════════════════╗"
    echo "║   Joko Security - Publicación a Artifactory               ║"
    echo "╚════════════════════════════════════════════════════════════╝"
    echo ""
}

show_usage() {
    cat << EOF
Uso: $0 [release|snapshot] [OPTIONS]

Publica los artefactos Maven de joko-security a Artifactory interno.

Argumentos:
  release     Publicar como release (requiere versión sin -SNAPSHOT en pom.xml)
  snapshot    Publicar como snapshot (default)
  --help      Mostrar esta ayuda

Opciones:
  --no-docs       Omitir generación de javadoc y sources (útil si hay errores)
  --skip-docs     Alias de --no-docs

Variables de entorno requeridas:
  ARTIFACTORY_USER      Usuario de Artifactory
  ARTIFACTORY_PASSWORD  Password de Artifactory
  ARTIFACTORY_BASE_URL  URL base de Artifactory
                        (ej: https://artifactory.example.com/artifactory)

Pre-requisitos:
  1. Configurar ~/.m2/settings.xml con credenciales:
     cp settings.xml.example ~/.m2/settings.xml

  2. Configurar variables de entorno:
     export ARTIFACTORY_USER="your-username"
     export ARTIFACTORY_PASSWORD="tu-password"

  3. Para releases, actualizar versión primero:
     ./publish.sh version 2.0.0
     git add pom.xml */pom.xml
     git commit -m "chore: Bump version to 2.0.0"
     git tag -a v2.0.0 -m "Release 2.0.0"

Ejemplos:
  # Publicar snapshot (desarrollo)
  export ARTIFACTORY_USER="your-username"
  export ARTIFACTORY_PASSWORD="mi-password"
  ./publish-artifactory.sh snapshot

  # Publicar release (producción)
  ./publish.sh version 2.0.0
  ./publish-artifactory.sh release

  # Publicar sin javadoc/sources (si hay problemas)
  ./publish-artifactory.sh snapshot --no-docs

Destinos:
  Releases:  ${ARTIFACTORY_BASE_URL}/libs-release
  Snapshots: ${ARTIFACTORY_BASE_URL}/libs-snapshot

Artefactos publicados:
  - joko-security-parent (POM padre)
  - joko-security-core
  - joko-security-storage-postgres
  - joko-security-web
  - joko-security-autoconfigure
  - joko-security-starter

Documentación: docs/PACKAGING_GUIDE.md
EOF
}

check_prerequisites() {
    log_step "Verificando pre-requisitos..."

    # Verificar Maven Wrapper
    if [ ! -f ./mvnw ]; then
        log_error "Maven Wrapper (mvnw) no encontrado."
        log_warn "Ejecuta este script desde la raíz del proyecto."
        exit 1
    fi

    # Verificar Java
    if ! command -v java &> /dev/null; then
        log_error "Java no está instalado. Instala Java 21+."
        exit 1
    fi

    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | awk -F '"' '{print $2}' | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt 21 ]; then
        log_error "Java 21+ es requerido. Versión actual: $JAVA_VERSION"
        exit 1
    fi

    # Verificar Maven version
    MVN_VERSION=$(./mvnw -version 2>/dev/null | head -n1 | awk '{print $3}' || echo "unknown")
    log_info "Java: $JAVA_VERSION, Maven: $MVN_VERSION (wrapper)"

    # Verificar variables de entorno
    if [ -z "$ARTIFACTORY_USER" ]; then
        log_error "Variable de entorno ARTIFACTORY_USER no configurada."
        log_warn "Configura: export ARTIFACTORY_USER=\"your-username\""
        exit 1
    fi

    if [ -z "$ARTIFACTORY_PASSWORD" ]; then
        log_error "Variable de entorno ARTIFACTORY_PASSWORD no configurada."
        log_warn "Configura: export ARTIFACTORY_PASSWORD=\"tu-password\""
        exit 1
    fi

    if [ -z "$ARTIFACTORY_BASE_URL" ]; then
        log_error "Variable de entorno ARTIFACTORY_BASE_URL no configurada."
        log_warn "Configura: export ARTIFACTORY_BASE_URL=\"https://artifactory.example.com/artifactory\""
        exit 1
    fi

    log_info "Credenciales OK (Usuario: $ARTIFACTORY_USER)"

    # Verificar settings.xml
    if [ ! -f ~/.m2/settings.xml ]; then
        log_error "~/.m2/settings.xml no encontrado."
        log_warn "Copia y configura:"
        log_warn "  cp settings.xml.example ~/.m2/settings.xml"
        exit 1
    fi

    # Verificar que settings.xml tenga los servidores configurados
    # if ! grep -q "central" ~/.m2/settings.xml; then
    #     log_error "~/.m2/settings.xml no tiene configuración de Artifactory."
    #     log_warn "Actualiza settings.xml usando settings.xml.example como referencia."
    #     exit 1
    # fi

    log_info "Pre-requisitos OK"
}

get_current_version() {
    # Extraer versión del pom.xml
    VERSION=$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout 2>/dev/null)
    echo "$VERSION"
}

validate_version_for_release() {
    VERSION=$(get_current_version)

    if [[ "$VERSION" == *"-SNAPSHOT" ]]; then
        log_error "No se puede publicar como release con versión SNAPSHOT: $VERSION"
        log_warn "Actualiza la versión primero:"
        log_warn "  ./publish.sh version ${VERSION%-SNAPSHOT}"
        log_warn "  git add pom.xml */pom.xml"
        log_warn "  git commit -m \"chore: Bump version to ${VERSION%-SNAPSHOT}\""
        exit 1
    fi

    log_info "Versión válida para release: $VERSION"
}

validate_version_for_snapshot() {
    VERSION=$(get_current_version)

    if [[ "$VERSION" != *"-SNAPSHOT" ]]; then
        log_warn "La versión actual no es SNAPSHOT: $VERSION"
        log_warn "¿Estás seguro de querer publicar una versión release como snapshot?"
        log_warn "Recomendación: Usar './publish-artifactory.sh release' en su lugar."
        read -p "Continuar de todos modos? (y/N): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            log_info "Publicación cancelada."
            exit 0
        fi
    fi

    log_info "Versión: $VERSION"
}

show_publish_summary() {
    local VERSION=$1
    local REPO_TYPE=$2

    echo ""
    echo "╔════════════════════════════════════════════════════════════╗"
    echo "║                   Resumen de Publicación                  ║"
    echo "╚════════════════════════════════════════════════════════════╝"
    echo ""
    echo "  Versión:        $VERSION"
    echo "  Tipo:           $REPO_TYPE"
    echo "  Usuario:        $ARTIFACTORY_USER"
    echo "  Base URL:       $ARTIFACTORY_BASE_URL"

    if [ "$REPO_TYPE" == "RELEASE" ]; then
        echo "  Destino:        $ARTIFACTORY_BASE_URL/libs-release"
    else
        echo "  Destino:        $ARTIFACTORY_BASE_URL/libs-snapshot"
    fi

    echo ""
    echo "  Artefactos a publicar:"
    echo "    - joko-security-parent"
    echo "    - joko-security-core"
    echo "    - joko-security-storage-postgres"
    echo "    - joko-security-web"
    echo "    - joko-security-autoconfigure"
    echo "    - joko-security-starter"
    echo ""

    if [ "$SKIP_DOCS" = true ]; then
        echo "  ⚠️  Javadoc y Sources: OMITIDOS"
        echo ""
    fi
}

build_project() {
    log_step "Compilando proyecto..."

    # Build con tests
    ./mvnw clean install -Ddependency-check.skip=true

    log_info "Compilación exitosa"
}

publish_to_artifactory() {
    log_step "Publicando a Artifactory..."

    # Construir comando con opciones
    if [ "$SKIP_DOCS" = true ]; then
        log_warn "Omitiendo generación de javadoc y sources"
        ./mvnw deploy -Partifactory -DskipTests -Dmaven.javadoc.skip=true -Dmaven.source.skip=true
    else
        ./mvnw deploy -Partifactory -DskipTests
    fi

    log_info "Publicación completada"
}

show_success_message() {
    local VERSION=$1
    local REPO_TYPE=$2

    echo ""
    echo "╔════════════════════════════════════════════════════════════╗"
    echo "║            ✓ Publicación Exitosa a Artifactory            ║"
    echo "╚════════════════════════════════════════════════════════════╝"
    echo ""
    log_info "Versión $VERSION publicada como $REPO_TYPE"
    echo ""
    echo "Verificar en Artifactory:"
    echo "  URL: $ARTIFACTORY_BASE_URL/webapp/#/artifacts/browse/tree/General"
    echo ""
    echo "Usar en otro proyecto (Maven):"
    echo ""
    echo "  <repositories>"
    echo "    <repository>"
    echo "      <id>central</id>"
    echo "      <url>$ARTIFACTORY_BASE_URL/libs-release</url>"
    echo "    </repository>"
    echo "  </repositories>"
    echo ""
    echo "  <dependencies>"
    echo "    <dependency>"
    echo "      <groupId>io.github.jokoframework</groupId>"
    echo "      <artifactId>joko-security-starter</artifactId>"
    echo "      <version>$VERSION</version>"
    echo "    </dependency>"
    echo "  </dependencies>"
    echo ""
}

# Main execution
main() {
    show_banner

    # Parse arguments
    case "$PUBLISH_TYPE" in
        --help|-h|help)
            show_usage
            exit 0
            ;;
        release)
            log_info "Modo: RELEASE"
            check_prerequisites
            validate_version_for_release
            VERSION=$(get_current_version)
            show_publish_summary "$VERSION" "RELEASE"
            ;;
        snapshot)
            log_info "Modo: SNAPSHOT"
            check_prerequisites
            validate_version_for_snapshot
            VERSION=$(get_current_version)
            show_publish_summary "$VERSION" "SNAPSHOT"
            ;;
        *)
            log_error "Argumento inválido: $PUBLISH_TYPE"
            show_usage
            exit 1
            ;;
    esac

    # Confirmación
    read -p "Continuar con la publicación? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        log_info "Publicación cancelada."
        exit 0
    fi

    # Build y publish
    build_project
    publish_to_artifactory
    show_success_message "$VERSION" "${PUBLISH_TYPE^^}"
}

# Run main
main
