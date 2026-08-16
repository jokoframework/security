# API Testing Files

Este directorio contiene archivos `.http` para probar los endpoints de joko-security usando la extensión REST Client de VS Code.

## 📁 Archivos Disponibles

- **[auth.http](auth.http)** - Flujo completo de autenticación (Login → Access Token → Logout)
- **[sessions.http](sessions.http)** - Gestión de sesiones de usuario
- **[error-tests.http](error-tests.http)** - Testing de casos de error y validaciones

## 📖 Documentación Completa

Para instrucciones detalladas de uso, configuración y troubleshooting, ver el **[README principal del módulo development](../README.md)**.

### Enlaces Directos:

- [Cómo usar los archivos .http](../README.md#-testing-de-api)
- [Usuarios de prueba disponibles](../README.md#-usuarios-de-prueba)
- [URLs de desarrollo](../README.md#-urls-de-desarrollo)
- [Troubleshooting](../README.md#-troubleshooting)

## 🚀 Inicio Rápido

```bash
# 1. Levantar servidor
cd ..
./dev.sh dev

# 2. Instalar extensión "REST Client" en VS Code

# 3. Abrir auth.http y click en "Send Request"
```

---

**Nota:** Este README es un índice rápido. Toda la documentación está centralizada en `../README.md`.
