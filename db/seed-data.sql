--
-- Datos de prueba para poder ejecutar operaciones básicas
-- Este script sólo tiene sentido cuando se borra por completo la base de datos
--
-- Primer debe correrse el seed-data-minimum.sql que contiene los parametros necesarios para que funcione la aplicacion
--

delete from sys.security_profile;
INSERT INTO sys.security_profile(
            key, name, max_number_of_connections_per_user, max_number_of_connections, 
            refresh_token_timeout_seconds, access_token_timeout_seconds, 
            revocable, max_access_token_requests)
    VALUES ( 'DEFAULT', 'Default configuration(testing purposes)', 1, null, 
            14440, 300, 
            false, 2);

--1.0.6 DataBase Generated...

delete from security_profile;
INSERT INTO security_profile(id,access_token_timeout_seconds,
            key,max_access_token_requests, max_number_of_connections, max_number_devices_user, name,
            refresh_token_timeout_seconds, revocable)
    VALUES ( 1,14440 ,'DEFAULT', 2, null, 1, 'Default configuration(testing purposes)',300, false);
