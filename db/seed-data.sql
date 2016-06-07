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
