--
--
--
--
--



delete from joko_security.security_profile;
INSERT INTO joko_security.security_profile(id,access_token_timeout_seconds,
            key,max_access_token_requests, max_number_of_connections, max_number_devices_user, name,
            refresh_token_timeout_seconds, revocable)
    VALUES ( 1,14440 ,'DEFAULT', 2, null, 1, 'Default configuration(testing purposes)',300, false);
