#!/bin/bash

##############################################################################
# Script de Publicación - joko-security
#
# Este script facilita la compilación, prueba y publicación de joko-security
# a GitHub Packages, Artifactory o repositorio Maven local usando Maven Wrapper.
#
# Pre-requisitos:
#   - Java 17+
#   - Maven Wrapper (mvnw) en el directorio raíz
#
# Uso:
#   ./publish.sh local                    # Instalar en repositorio local (~/.m2)
#   ./publish.sh test                     # Ejecutar tests
#   ./publish.sh github                   # Publicar en GitHub Packages
#   ./publish.sh artifactory [ARGS]       # Publicar en Artifactory (pasa ARGS)
#   ./publish.sh version X.Y.Z            # Actualizar versión
#
# Ejemplos Artifactory:
#   ./publish.sh artifactory snapshot --no-docs
#   ./publish.sh artifactory release
#
# IMPORTANTE: Ejecutar desde el directorio raíz del proyecto
##############################################################################

set -e  # Exit on error

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

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

check_prerequisites() {
    log_info "Verificando pre-requisitos..."

    # Verificar Maven Wrapper
    if [ ! -f ./mvnw ]; then
        log_error "Maven Wrapper (mvnw) no encontrado en el directorio actual."
        log_warn "Asegúrate de ejecutar este script desde la raíz del proyecto."
        exit 1
    fi

    if ! command -v java &> /dev/null; then
        log_error "Java no está instalado. Instala Java 17 primero."
        exit 1
    fi

    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | awk -F '"' '{print $2}' | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt 17 ]; then
        log_error "Java 17 o superior es requerido. Versión actual: $JAVA_VERSION"
        exit 1
    fi

    # Obtener versión de Maven del wrapper
    MVN_VERSION=$(./mvnw -version 2>/dev/null | head -n1 | awk '{print $3}' || echo "unknown")
    log_info "Pre-requisitos OK (Java $JAVA_VERSION, Maven $MVN_VERSION vía wrapper)"
}

install_local() {
    log_info "Compilando e instalando en repositorio local..."
    # Skip dependency-check en local para evitar logs verbose de NVD
    ./mvnw clean install -Ddependency-check.skip=true
    log_info "✓ Instalado en ~/.m2/repository/io/github/jokoframework/"
    log_warn "Nota: Dependency check fue omitido (solo para instalación local)"
}

run_tests() {
    log_info "Ejecutando tests..."
    ./mvnw clean test
    log_info "✓ Tests completados"
}

publish_github() {
    log_info "Publicando en GitHub Packages..."

    # Check settings.xml
    if [ ! -f ~/.m2/settings.xml ]; then
        log_error "~/.m2/settings.xml no encontrado."
        log_warn "Copia settings.xml.example y configura tus credenciales:"
        log_warn "  cp settings.xml.example ~/.m2/settings.xml"
        exit 1
    fi

    # Check if GitHub token is configured
    if grep -q "TU_GITHUB_TOKEN" ~/.m2/settings.xml; then
        log_error "Configura tu GitHub token en ~/.m2/settings.xml"
        log_warn "Reemplaza TU_GITHUB_TOKEN con tu token personal"
        exit 1
    fi

    ./mvnw clean deploy
    log_info "✓ Publicado en GitHub Packages"
    log_info "Verifica en: https://github.com/jokoframework/security/packages"
}

publish_artifactory() {
    log_info "Publicando en Artifactory interno..."
    log_warn "RECOMENDACIÓN: Usa ./publish-artifactory.sh directamente para todas las opciones."

    # Delegar al script especializado
    if [ ! -f ./publish-artifactory.sh ]; then
        log_error "Script publish-artifactory.sh no encontrado."
        exit 1
    fi

    chmod +x ./publish-artifactory.sh

    # Pasar argumentos adicionales al script
    # Si no hay argumentos, usar snapshot como default
    if [ $# -eq 0 ]; then
        ./publish-artifactory.sh snapshot
    else
        ./publish-artifactory.sh "$@"
    fi
}

update_version() {
    NEW_VERSION=$1

    if [ -z "$NEW_VERSION" ]; then
        log_error "Especifica la nueva versión: ./publish.sh version X.Y.Z"
        exit 1
    fi

    log_info "Actualizando versión a $NEW_VERSION..."

    # Update parent pom
    ./mvnw versions:set -DnewVersion="$NEW_VERSION" -DgenerateBackupPoms=false

    log_info "✓ Versión actualizada a $NEW_VERSION"
    log_warn "Recuerda hacer commit y crear tag:"
    log_warn "  git add pom.xml */pom.xml"
    log_warn "  git commit -m \"chore: Bump version to $NEW_VERSION\""
    log_warn "  git tag -a v$NEW_VERSION -m \"Release $NEW_VERSION\""
    log_warn "  git push origin --tags"
}

show_usage() {
    cat << EOF
Uso: $0 <comando>

Comandos:
  local                      Compilar e instalar en repositorio local (~/.m2)
  test                       Ejecutar tests
  github                     Publicar en GitHub Packages (requiere configuración)
  artifactory [ARGS]         Publicar en Artifactory interno (acepta argumentos)
  version X.Y.Z              Actualizar versión del proyecto
  help                       Mostrar esta ayuda

Ejemplos:
  $0 local                          # Desarrollo local
  $0 test                           # Ejecutar tests antes de publicar
  $0 github                         # Publicar a GitHub
  $0 artifactory                    # Publicar snapshot a Artifactory
  $0 artifactory snapshot --no-docs # Publicar snapshot sin javadoc/sources
  $0 artifactory release            # Publicar release a Artifactory
  $0 version 2.0.1                  # Actualizar a versión 2.0.1

Para publicación avanzada a Artifactory:
  ./publish-artifactory.sh release    # Publicar release
  ./publish-artifactory.sh snapshot   # Publicar snapshot
  ./publish-artifactory.sh --help     # Más opciones y validaciones

Para más información, consulta docs/PACKAGING_GUIDE.md
EOF
}

# Main
case "$1" in
    local)
        check_prerequisites
        install_local
        ;;
    test)
        check_prerequisites
        run_tests
        ;;
    github)
        check_prerequisites
        publish_github
        ;;
    artifactory)
        check_prerequisites
        shift  # Remover "artifactory" de los argumentos
        publish_artifactory "$@"
        ;;
    version)
        check_prerequisites
        update_version "$2"
        ;;
    help|--help|-h|"")
        show_usage
        ;;
    *)
        log_error "Comando desconocido: $1"
        show_usage
        exit 1
        ;;
esac
