-- Basic seed data for testing token flow

-- Security Profiles matching DevAuthenticationManager profiles
-- DEFAULT: Standard user profile (30min access, 24h refresh)
INSERT INTO "joko_security".security_profile (id, name, "key", access_token_timeout_seconds, refresh_token_timeout_seconds, max_access_token_requests, max_number_of_connections, max_number_devices_user, revocable)
VALUES (1, 'Default Profile', 'DEFAULT', 1800, 86400, 50, 5, 3, true);

-- ADMIN: Admin profile (1h access, 48h refresh)
INSERT INTO "joko_security".security_profile (id, name, "key", access_token_timeout_seconds, refresh_token_timeout_seconds, max_access_token_requests, max_number_of_connections, max_number_devices_user, revocable)
VALUES (2, 'Admin Profile', 'ADMIN', 3600, 172800, 100, 10, 5, true);

-- MOBILE: Mobile app profile (15min access, 7d refresh)
INSERT INTO "joko_security".security_profile (id, name, "key", access_token_timeout_seconds, refresh_token_timeout_seconds, max_access_token_requests, max_number_of_connections, max_number_devices_user, revocable)
VALUES (3, 'Mobile Profile', 'MOBILE', 900, 604800, 30, 3, 2, true);

-- Keychain (JWT signing secret)
INSERT INTO "joko_security".keychain (id, "value")
VALUES (1, 'ZGV2ZWxvcG1lbnQta2V5LWZvci1qb2tvLXNlY3VyaXR5');

-- Test users
INSERT INTO "joko_security".principal_session (id, app_id, user_id, app_description, user_description)
VALUES
    (1, 'dev-app', 'testuser', 'Development App', 'Test User'),
    (2, 'dev-app', 'admin', 'Development App', 'Admin User'),
    (3, 'dev-app', 'mobileuser', 'Development App', 'Mobile User'),
    (4, 'dev-app', 'readonly', 'Development App', 'Readonly User');
