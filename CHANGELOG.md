## Joko Security

Archivo para documentar cambios más importantes de la librería.

## [v1.0.0]

### 1) Upgrade a Spring 1.5.10
Cambia la versión base de 1.3.x a 1.5.10. Se solucionan los
inconvenientes relacioandos a esto:
* Annotations que daban errores de compilación
* Annotations que daban warnings
* Problemas de dependencia en las librerías.

### 2) Se crea un schema llamado "joko_security"
Todas las tablas funcionan en un schema llamado "joko_security". Se
corrigen los scripts de cración y los Entities

### 3) Compatibilidad con scripts de creación de BD basados en
    liquidbase
La mayoría de los proyectos basados en joko tienen un conjunto de
    scripts que nos permiten gestionar el ciclo de la BD con comandos
    como: `./scripts/updater fresh` . 

Los mencioanados scripts no estaban funcioanando en joko-security y se
corrigió para que funcionen.

### 4) Compatibilidad con liquidbase
Se crea el script db-changelog-initial.xml que posee toda la
información para generar una BD para joko-security con liquidbase.

### 5) Se actualiza el README
En base a los cambios anteriores se actualiza el readme, tanto en
contenido como en formato.

### 6) Reseteao de la version
Se decide cambiar a la versión 1.0.0 para corregir algunas
inconsistencias en la manera en como se construyeron las versiones. El
objetivo es que desde este punto podamos ir de manera ordenada.

### 7) Integración con la rama 0.1.4.2.tmp
Existe una rama llamada 0.1.4.2.tmp que poseia mejoras al log del
sistema, requerido por ciertos proyectos. Se realiza una integración
de la misma.

Esta rama tambien posee la funcionalidad de emitir un token con un
subject direferente al original.
El JokoAuthentication posee un metodo nuevo que se llama setSubject. En
base a esto se puede personalizar el subject con el que el token sera
emitido.


## [v0.1.6]
- Auditoria de sesión por usuario y aplicación

## [v0.1.5] 
Para upgrade a esta versión referir al [documento de migración](docs/migration.md)
- Soporte para base de datos Oracle con mejor manejo de esquemas y nombres de columnas

## [v0.1.4]
- Capacidad de eliminar tokens según su fecha de creación

## [v0.1.3]
- Ajuste de convenciones de código para clases que implementan interfaces Java

## [v0.1.2]
- Versión inicial
