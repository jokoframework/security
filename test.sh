#!/bin/bash

# Script simplificado para ejecutar tests de integración
# Usa TestContainers para gestionar PostgreSQL automáticamente
#
# Uso: ./test.sh [opciones]
# Ejemplos:
#   ./test.sh                              # Ejecutar todos los tests
#   ./test.sh TokenServiceTest             # Ejecutar un test específico
#   ./test.sh TokenFlowIntegrationTest     # Ejecutar test de integración con TestContainers
#   ./test.sh integration                  # Ejecutar solo tests de integración
#
# Variables de entorno opcionales:
#   ENABLE_DEPENDENCY_CHECK=true           # Habilitar OWASP dependency check
#   SKIP_TESTCONTAINERS=true              # Usar H2 en lugar de TestContainers

set -e  # Salir si algún comando falla

echo "🧪 Ejecutando tests de Joko Security"
echo ""

# Verificar Docker (requerido por TestContainers)
if ! command -v docker &> /dev/null; then
    echo "⚠️  Advertencia: Docker no encontrado"
    echo "   TestContainers requiere Docker para ejecutar PostgreSQL"
    echo "   Los tests usarán H2 en memoria como fallback"
    echo ""
fi

# Verificar si Docker está corriendo
if command -v docker &> /dev/null; then
    if ! docker info &> /dev/null; then
        echo "⚠️  Advertencia: Docker no está corriendo"
        echo "   Por favor inicia Docker Desktop para usar TestContainers"
        echo "   Los tests usarán H2 en memoria como fallback"
        echo ""
    else
        echo "✅ Docker está corriendo - TestContainers habilitado"
        echo ""
    fi
fi

# Determinar qué tests ejecutar
if [ $# -eq 0 ]; then
    echo "🚀 Ejecutando todos los tests..."
    echo ""
    ./mvn.sh clean test
elif [ "$1" = "integration" ]; then
    echo "🚀 Ejecutando solo tests de integración..."
    echo ""
    ./mvn.sh clean test -Dtest="*IntegrationTest"
else
    TEST_NAME="$1"
    echo "🚀 Ejecutando test: $TEST_NAME..."
    echo ""
    ./mvn.sh clean test -Dtest="$TEST_NAME"
fi

# Verificar resultado
EXIT_CODE=$?
echo ""
if [ $EXIT_CODE -eq 0 ]; then
    echo "✅ Tests completados exitosamente"
    # echo ""
    # echo "💡 Tips:"
    # echo "   - Los tests de integración usan PostgreSQL real vía TestContainers"
    # echo "   - No necesitas preparar la base de datos manualmente"
    # echo "   - Liquibase se ejecuta automáticamente en el contenedor"
else
    echo "❌ Tests fallaron (código de salida: $EXIT_CODE)"
    echo ""
    echo "💡 Troubleshooting:"
    echo "   - Verifica que Docker está corriendo: docker info"
    echo "   - Revisa los logs arriba para detalles del error"
    echo "   - Para tests individuales: ./test.sh NombreDelTest"
    exit $EXIT_CODE
fi
