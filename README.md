# Joko Security
Joko Security provee la capacidad de realizar autenticacion y autorizacion por medio de Tokens JWT.
Se puede utilizar de dos maneras, como un componente separado que emite tokens o embebido como una librería dentro de otra aplicación Web.
Joko Security es una extensión de spring-security que permite trabajar con token de refresh, y acceso utilizando como formato de tokens JWT.

## Configuración Inicial

### Configuracion de la Base de Datos
Joko-security necesita un repositorio de datos en el cual se almacenan datos para que permiten realizar el proceso de autorización. 
El sistema utiliza JPA de una manera bastante agnóstica a la BD. Sin embargo, actualmente solamente está probado con PostgreSQL 9.3

#### Inicio desde .sql
El inicio mas sencillo es correr el script .sql correspondiente a la BD que utiliza. Estos scripts se encuentran en /db/sql-initialization
 
## Conceptos de token
Un token es un permiso particular que garantiza al poseedor acceso a ciertos recursos. Los *tokens* son firmados por joko-security con una clave secreta,por lo tanto no pueden ser alterados. Esto permite a Joko-security realizar la validación en memoria de los *tokens*. Por ejemplo: un *token* extemporáneo se rechaza sin mayor chequeo. 

Realizar las validaciones en memoria sin tener que tocar la base de datos permite a joko-security escalar con mayor rapidez al ser en gran medida *stateless*. 

Los *tokens* en Joko siguen el standard :abbr:`JWT (JSON Web Tokens)` [#]_ . Existen dos tipos de token:

### Refresh Token 
Cuando un usuario se autentica al sistema recibe un `refresh token`. Este *token* permite al usuario acceder al sistema por un tiempo prolongado pero con pocos permisos de acceso.
#### Un `refresh token` tiene información necesaria para obtener un nuevo `access token`.
#### Un `access token` sirve para realizar operaciones.
 
Dependiendo del `security profile`  el sistema devolverá un *refresh token* con mayor o menor tiempo de vida. Por ejemplo si el usuario accede desde una aplicación web se podría dar un token de una semana, y si accede desde la web en términos de horas. Si el usuario no utiliza la aplicación por 1 (una) semana, entonces necesitará realizar un nuevo login (esto es aceptable desde el punto de vista UX). Los refresh token son especialmente útiles para las aplicaciones móviles en las cuales es molesto pedir el usuario en cada momento la autenticación.

##Guardar el token de refresh de manera segura**

En el caso de una aplicación móvil se tendría que guardar en el *key store*, y en el caso de una aplicación Web en los :term:`cookies` (NO guardarlos en *WEB storage*)

### Access Token
Un :term:`access token` permite al usuario realizar todas las operaciones que su perfil permita.

Un token de acceso tiene un tiempo de vida corto, y la aplicación tendrá que renovar el token de acceso antes de que este fenezca.Esto crea la sensación al usuario de estar siempre conectado, mientras que también brinda un mayor nivel de seguridad

Para mayor seguridad el token de acceso se debería de sostener solo en memoria.
     
## Personalización
Joko-security no posee utilidad por si solo, sino que presenta un conjunto de genérico de funcionalidades que deben de ser especializadas y de esta manera permite ahorrar tiempo a un programador. 
Son dos las clases que se deben implementar para configurar joko-security, estas son: JokoAuthenticationManager, JokoAuthorizationManager y , para configurar la autenticación y la autorización respectivamente.

### JokoAuthenticationManager
Para determinar si ciertas credenciales son o no correctas el sistema que utilice Joko-security debe extender JokoAuthenticationManager o la correspondiente clase de Spring org.springframework.security.authentication.AuthenticationManager.
En el caso que se realice una especialización nueva la recomendación es utilizar JokoAuthenticationManager. La compatibilidad con spring debería de utilizarse para soportar AuthenticationManager que ya fueron anteriormente implementados.

### JokoAuthorizationManager
Se debe implementar esta interfaz para:
- Determinar las autorizaciones para un request en particular.
	- Esto debe hacrse examinando el token y tratando de no tocar la BD en lo posible. Recordemos que este método será invocada con cada request.
- Determinar los URLs a los que se tiene acceso en base a las autorizaciones
	- Se configura utilizando spring-security con la ventaja de que joko ya realiza las configuraciones básicas requeridas en proyectos de este tipo.
 
## Ejemplos
Dentro del directorio "samples" se puede observar ejemplos que muestran

## Obtener el jar
El proyecto no está publicado actualmente en ningún maven repository. Por lo tanto, se requiere bajar el código fuente y realizar la instalación del jar.

	mvn -Dspring.config.location=file:///opt/joko/development/application.properties install

Un archivo de ejemplo de application.properties puede obtenerse en src/main/resources/application.properties.example	
## Funcionalidades proveídas por Joko
Se listan a continuación las configuraciones básicas y funcionalidades proveídas por Joko:

- Error básico de forbidden devuelve código de error 403 Forbidden.
- Error básico al no estar autenticado devuelve código de error http 401 Unauthorized
- Se pueden lanzar las excepciones JokoUnauthorizedException y JokoUnauthenticatedException desde cualquier lugar, el sistema devolverá 403 y 401 respectivamente.
- La configuración del tiempo de vida de los tokens es en base al security profile
- Los tokens de refresh se pueden revocar
- Configuracion de spring-security especializada para aplicaciones stateless 

# Unit Tests
joko-security cuenta con una clase que contiene tests unitarios, para las funcionalidades principales de módulo: 

- Creación de tokens
- Parseo
- Refresh

Se puede correr los tests mediante maven

	mvn -Dspring.config.location=file:///opt/joko/development/application.properties test
