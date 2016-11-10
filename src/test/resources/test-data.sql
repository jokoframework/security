
INSERT INTO security_profile(id,
            key, name, max_number_of_connections_per_user, max_number_of_connections, 
            refresh_token_timeout_seconds, access_token_timeout_seconds, 
            revocable, max_access_token_requests)
    VALUES ( 1, 'DEFAULT', 'Default configuration(testing purposes)', 1, null, 
            14440, 300, 
            false, 2);