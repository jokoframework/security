-- Basic seed data for testing token flow

-- Security Profile (30min access token, 24h refresh token)
INSERT INTO "joko_security".security_profile (id, name, "key", access_token_timeout_seconds, refresh_token_timeout_seconds, max_access_token_requests, max_number_of_connections, max_number_devices_user, revocable)
VALUES (1, 'DEFAULT', 'ROLE_USER', 1800, 86400, 50, 5, 3, true);

-- Keychain (JWT signing secret)
INSERT INTO "joko_security".keychain (id, "value")
VALUES (1, 'ZGV2ZWxvcG1lbnQta2V5LWZvci1qb2tvLXNlY3VyaXR5');

-- Test user
INSERT INTO "joko_security".principal_session (id, app_id, user_id, app_description, user_description)
VALUES (1, 'dev-app', 'testuser', 'Development App', 'Test User');
