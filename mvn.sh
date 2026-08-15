#!/bin/bash

# Script para ejecutar comandos Maven con dependency-check deshabilitado por defecto
# Uso: ./mvn.sh [comandos maven]
# Ejemplos:
#   ./mvn.sh install
#   ./mvn.sh clean test
#   ./mvn.sh spring-boot:run

# Verificar que se pasó al menos un comando
if [ $# -eq 0 ]; then
    echo "Uso: ./mvn.sh [comandos maven]"
    echo ""
    echo "Ejemplos:"
    echo "  ./mvn.sh install"
    echo "  ./mvn.sh clean test"
    echo "  ./mvn.sh spring-boot:run"
    echo "  ./mvn.sh -Dtest=TokenServiceTest test"
    echo ""
    echo "Variables de entorno opcionales:"
    echo "  ENABLE_DEPENDENCY_CHECK=true: Habilitar check de dependencias (default: deshabilitado)"
    exit 1
fi

# Configurar dependency-check (deshabilitado por defecto)
DEPENDENCY_CHECK_SKIP="true"
if [ "$ENABLE_DEPENDENCY_CHECK" = "true" ]; then
    DEPENDENCY_CHECK_SKIP="false"
fi

# Ejecutar Maven Wrapper
./mvnw -Ddependency-check.skip="$DEPENDENCY_CHECK_SKIP" "$@"
