## Migración 0.1.4 a 0.1.5 - PostgreSQL

A partir de la versión 0.1.5 se usa un sólo nombre de esquema.

En Postgres 9.X los pasos para usar un sólo esquema son:

1. Cambiar el nombre de las tablas a un schema, por ejemplo "joko_sec"

    ```
    ALTER TABLE  sys.security_profile SET SCHEMA joko_sec.security_profile
    ```

2. Se debe configurar un esquema por defecto en el application.properties
 
    ``` 
    spring.jpa.properties.hibernate.default_schema=joko_sec
    ```

3. Se debe modificar el nombre de la columna:

    ```
    ALTER TABLE joko_sec.security_profile RENAME max_number_of_connections_per_user TO max_number_devices_user
    ```
   
