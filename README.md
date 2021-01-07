# Joko Security

Joko Security provee la capacidad de realizar autenticación y autorización por
.medio de Tokens JWT.  Se puede utilizar de dos maneras, como un componente
separado que emite tokens o embebido como una librería dentro de otra aplicación
Web.  Joko Security es una extensión de spring-security que permite trabajar con
token de refresh, y acceso utilizando como formato de tokens JWT.

## Configuración embebido en otra App

### Configuracion de la Base de Datos
Joko-security necesita un repositorio de datos en el cual se almacenan datos que
permiten realizar el proceso de autorización. El sistema utiliza JPA de una manera bastante agnóstica a la
BD. Sin embargo, actualmente solamente está probado con PostgreSQL 9.4

#### Escenario embebido en otra aplicacion
Una opción es utilizar Joko-security embebido dentro de otra aplicacion. En 
este caso el repositorio de datos debe tener la estructura de tablas que Joko
 esta esperando.
 Si el repositorio de datos se inicializa con liquibase, entonces todo el 
 contenido para la creacion de la estructura necesaria se encuentra en:
 ./db/liquibase/db-changelog-evolucion.xml
 
 Este archivo puede ser referenciado dentro del ciclo de actualizacion de la 
 BD en el proyecto que incluya a joko-security como librería.
 
 
#### Inicio desde .sql 
El inicio mas sencillo es correr el script .sql
correspondiente a la BD que utiliza. Estos scripts se encuentran en:
```shell
/db/sql-initialization
```

## Configuracion de su propia BD

En Joko poseemos un conjunto de scripts que nos permiten automatizar el ciclo
 de vida de una aplicación. Con esto se puede crear facilmente toda la BD 
 desde la linea de comandos. Para actualizar hay que seguir los siguientes 
 pasos:

### Step 1) Crear el directorio PROFILE_DIR
El directorio de profile contiene el archivo application.properties con la 
configuracion necesaria para lanzar la aplicacion spring-boot.

La convencion utilizada es tener un directorio, dentro del cual existan 
varios PROFILE_DIR segun se requiera. Por ejemplo:
```shell
/opt/joko-demo/dev
/opt/joko-demo/qa
```

En el anterior ejemplo existen dos PROFILE_DIR dentro del joko-demo, el 
primero para development y el segundo con datos de quality assurance.

Obs.: Un archivo de ejemplo para el application.properties se encuentra en 
`src/main/resources/application.properties`

### Step 2) Configuración del archivo "development.vars"

Se debe configurar el archivo "development.vars", que servirá para la 
ejecucion de liquibase. Este es un archivo bash que debe tener dos variables: 

- MVN_SETTINGS: Archivo de configuracion de perfil Maven. En caso de utilizar
 el Artifactory interno, sería el recien descargado. Ej. $HOME/.m2/settings.xml
- PROFILE_DIR: Directorio de perfil creado en el punto inicial. Ej. /opt/joko

Un ejemplo de este archivo se encuenta en `src/main/resources/development.vars`.

Se recomienda que este archivo esté fuera del workspsace en el directorio 
padre de los PROFILE_DIR. Ejemplo: ``/opt/joko-security/``.
Este directorio es lo que se llama "ext.prop.dir" en las siguientes secciones.

### Step 3) Configuración de variables de entorno
Exportar variable, desde la terminal:
```shell
  $ export ENV_VARS="/opt/joko-security/development.vars"
```
Obs.: El truco es tener varios archivos profile.vars y cada uno apuntando a
 un PROFILE_DIR diferente. 
 
### Step 4) Ejecutar Liquibase.

1. Crea la schema de cero.
```shell
  $ ./scripts/updater fresh
```
2. (Re)Inicializa datos básicos
```shell
  $ ./scripts/updater seed src/main/resources/db/sql/seed-data.sql
```
**OJO**:
  * El parámetro "fresh" elimina la base de datos que está configurada en el application.properties
    y la vuelve a crear desde cero con la última versión del schema

  * El parámetro "seed <file>" carga datos indicados en el archivo <file>, para los casos en que se
    ejecute "fresh" siempre debe ir seguido de un "seed" con el archivo que (re)inicializa los datos
    básicos del sistema 

  * Los datos básicos del sistema estan en dos archivos:
    ** seed-data.sql: Todos la configuracion base que es independiente al 
    ambiente
    ** [ambiente]-config. Por ejemplo: dev-config.sql . Posee los parametros 
    de configuracion adecuados  para el ambiente de
  desarrollo. Tambien existe qa-config y prod-config

3. Para correr el liquibase en modo de actualización ejecute:
```shell
  $ ./scripts/updater update
```

 
## Conceptos de token 
Un token es un permiso particular que garantiza al
poseedor acceso a ciertos recursos. Los *tokens* son firmados por joko-security
con una clave secreta, por lo tanto no pueden ser alterados. Esto permite a
Joko-security realizar la validación en memoria de los *tokens*. Por ejemplo: un
*token* extemporáneo se rechaza sin mayor chequeo.

Realizar las validaciones en memoria sin tener que tocar la base de datos
permite a los sistemas que utilizan joko-security escalar con mayor rapidez al
ser en gran medida *stateless*.

Los *tokens* en Joko siguen el standard :abbr:`JWT (JSON Web Tokens)` [#]_
. Existen dos tipos de token:

### Refresh Token 
Cuando un usuario se autentica al sistema recibe un `refresh
token`.  Este *token* permite al usuario acceder al sistema por un tiempo
prolongado pero con pocos permisos de acceso.  #### Un `refresh token` tiene
información necesaria para obtener un nuevo `access token`.  #### Un `access
token` sirve para realizar operaciones.
 
Dependiendo del `security profile` el sistema devolverá un *refresh token* con
mayor o menor tiempo de vida. Por ejemplo si el usuario accede desde una
aplicación web se podría dar un token de una semana, y si accede desde la web en
términos de horas. Si el usuario no utiliza la aplicación por 1 (una) semana,
entonces necesitará realizar un nuevo login (esto es aceptable desde el punto de
vista UX). Los refresh token son especialmente útiles para las aplicaciones
móviles en las cuales es molesto pedir el usuario en cada momento la
autenticación.

##Guardar el token de refresh de manera segura

En el caso de una aplicación móvil se tendría que guardar en el *key store*, y
en el caso de una aplicación Web en los :term:`cookies` (NO guardarlos en *WEB
storage*)

### Access Token 
Un :term:`access token` permite al usuario realizar todas las
operaciones que su perfil permita.

Un token de acceso tiene un tiempo de vida corto, y la aplicación tendrá que
renovar el token de acceso antes de que este fenezca.Esto crea la sensación al
usuario de estar siempre conectado, mientras que también brinda un mayor nivel
de seguridad.

Para mayor seguridad el token de acceso se debería de sostener solo en memoria.
     
## Personalización
Joko-security no posee utilidad por si solo, sino que
presenta un conjunto de genérico de funcionalidades que deben de ser
especializadas y de esta manera permite ahorrar tiempo a un programador.  Son
dos las clases que se deben implementar para configurar joko-security, estas
son: JokoAuthenticationManager, JokoAuthorizationManager, para configurar la
autenticación y la autorización respectivamente.

### JokoAuthenticationManager 
Para determinar si ciertas credenciales son o no
correctas el sistema que utilice Joko-security debe extender
JokoAuthenticationManager o la correspondiente clase de Spring
org.springframework.security.authentication.AuthenticationManager.  En el caso
que se realice una especialización nueva la recomendación es utilizar
JokoAuthenticationManager. La compatibilidad con spring debería de utilizarse
solo para soportar AuthenticationManager que ya fueron anteriormente implementados.

### JokoAuthorizationManager
Se debe implementar esta interfaz para:
- Determinar las autorizaciones para un request en particular.
	- Esto debe hacerse examinando el token y tratando de no tocar la BD en
	lo posible. Recordemos que este método será invocada con cada request.
- Determinar los URLs a los que se tiene acceso en base a las autorizaciones
	- Se configura utilizando spring-security con la ventaja de que joko ya
	realiza las configuraciones básicas requeridas en proyectos de este tipo.
 
## Ejemplos
Se recomienda tomar como modelo el proyecto [joko_backend_starter_kit](https://github.com/jokoframework/joko_backend_starter_kit)

## Obtener el jar
El proyecto no está publicado actualmente en ningún maven repository. Por lo tanto, se requiere bajar el código fuente y realizar la instalación del jar. En la instalación del jar se correran los Unit Tests por defecto, se debe prepara la BD como se define en la sección "Unit Tests"


	mvn -Dext.prop.dir=/opt/joko-security/test -Dspring.config.location=file:///opt/joko-security/dev/application.properties install

Un archivo de ejemplo de application.properties puede obtenerse en src/main/resources/application.properties.example	
## Funcionalidades proveídas por Joko
Se listan a continuación las configuraciones básicas y funcionalidades proveídas por Joko:

- Error básico de forbidden devuelve código de error 403 Forbidden.
- Error básico al no estar autenticado devuelve código de error http 401 Unauthorized
- Se pueden lanzar las excepciones JokoUnauthorizedException y
- JokoUnauthenticatedException desde cualquier lugar, el sistema devolverá 403 y
- 401 respectivamente. 
- La configuración del tiempo de vida de los tokens es en base al security profile
- Los tokens de refresh se pueden revocar
- Configuracion de spring-security especializada para aplicaciones stateless 

# Unit Tests
joko-security cuenta con una clase que contiene tests unitarios, para las funcionalidades principales de módulo: 

- Creación de tokens
- Parseo
- Refresh

Se puede correr los tests mediante maven

   1) Actualizar los datos de una BD fresca con: 
  $ ./scripts/updater seed src/main/resources/db/sql/seed-test.sql
     
   2) Correr MVN	
	mvn -Dext.prop.dir=/opt/joko-security/dev -Dspring.config.location=file:///opt/joko-security/dev/application.properties test

# Configuraciones
En esta sección describimos la configuracion que se debería de tener en 
cuenta para que funcione correctamente joko-security

Toda la configuración se realiza en el archivo application.properties y el 
archivo `src/main/resources/application.properties` contiene un ejemplo 
comentado con las opciones

## Configuraciones Basicas 
El sistema necesita un secreto para firmar los tokens. Este secreto puede ser
 guradado en dos lugares:
 * BD: Si se guarda en la Base de datos va a la tabla joko_security.keychain
 * FILE: Si va al filesystem se debe configurar la propiedad joko.secret.file

ATENCIÓN: Es MUY importante que este secreto no sea accedido por terceras 
personas. La recomendacion para esto es:
* BD: En este caso asigne permisos a la tabla con solo lectura y solamente 
para el usuario que se utiliza en la aplicacion
* FILE: Asigne permisos de lecutra y solo para el usuario que se utiliza al 
momento de levantar la aplicacion.

Obs.:En modo BD puede dejarse sin crear un archivo y el sistema va a crear 
un secreto la primera vez que se levanta.

## Uso del OTP

Primeramente hay que ver si quiere registrar en el usuario la semilla que seria utilizada para generar el OTP que sera comparado
con el OTP que ingresa:
 * Ingresar Semilla: si quiere ingresar una semilla, debe ir a la pagina https://freeotp.github.io/qrcode.html. En esa pagina debe completar
   los datos opcionales como el nombre de la cuenta relacionada a la semilla, y poner la opcion "TIMEOUT" para que funcione como Timed-OTP.
   Esta aplicacion genera un QR que debe ser escaneado por su telefono, utilizando el programa FreeOTP que se puede descargar para Android.
   La semilla solo se ingresa una vez por lo que en nuevos logins, el usuario solo debe completar el campo de "user" y "password", y eliminar
   el campo de semilla.
 * No ingresar una semilla: si no desea en el momento ingresar una semilla, puede simplemente eliminar el campo de "seed" y el token sera
   generado.

Luego de tener una semilla en la DB, se procede al siguiente paso, el cual tendran 2 opciones:
 * Sin semilla guardada: solo debe ingresar un "0" en la linea de OTP, si realmente no tiene una semilla guardada, entonces se le consedera
   el token, de lo contrario se le dira que el OTP assignado no concuerda con el OTP generado en el programa.
 * Con semilla guardada: en la aplicacion FreeOTP en el celular podra ver el codigo de 6 digitos que debe ingresar para el parametro de OTP
   en el servidor.

## Configuraciones del POM file Asegurese que las versiones de las dependencias
en los archivos pom.xml tengan la misma version, esto le generara problemas a la
hora de querer levantar el servicio.


# Changelog
Para una descripcion detallada de las versiones ver el archivo de [Changelog](CHANGELOG.md)


