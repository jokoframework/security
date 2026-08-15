-- ============================================================
-- Joko Security - Development Database Initialization
-- ============================================================
-- Execute this script manually in PostgreSQL to set up the development environment
-- Database: app_db (localhost:5433)
-- User: app / Password: secret

-- Create schema
CREATE SCHEMA IF NOT EXISTS "joko_security";

-- Create sequence
CREATE SEQUENCE "joko_security".id_seq;

-- Consumer API table
CREATE TABLE "joko_security".consumer_api (
    id BIGSERIAL PRIMARY KEY,
    access_level VARCHAR(255),
    consumer_id VARCHAR(255),
    contact_name VARCHAR(255),
    document_number VARCHAR(255),
    name VARCHAR(255),
    secret VARCHAR(255)
);

-- Keychain table (stores JWT signing secret)
CREATE TABLE "joko_security".keychain (
    id INT PRIMARY KEY,
    "value" VARCHAR(500)
);

-- Principal session table (user sessions)
CREATE TABLE "joko_security".principal_session (
    id BIGSERIAL PRIMARY KEY,
    app_description VARCHAR(255),
    app_id VARCHAR(255),
    user_description VARCHAR(255),
    user_id VARCHAR(255)
);

-- Security profile table (token configurations)
CREATE TABLE "joko_security".security_profile (
    id BIGSERIAL PRIMARY KEY,
    access_token_timeout_seconds INT,
    "key" VARCHAR(255),
    max_access_token_requests INT,
    max_number_of_connections INT,
    max_number_devices_user INT,
    name VARCHAR(255),
    refresh_token_timeout_seconds INT,
    revocable BOOLEAN
);

-- Seed table (for OTP/2FA)
CREATE TABLE "joko_security".seed (
    id BIGSERIAL PRIMARY KEY,
    seed_secret VARCHAR(255),
    user_id VARCHAR(255)
);

-- Token table (active tokens)
CREATE TABLE "joko_security".token (
    id BIGSERIAL PRIMARY KEY,
    consumer_api_id BIGINT,
    expire_refresh_token TIMESTAMP,
    principal_session_id BIGINT,
    refresh_token VARCHAR(500),
    revoked BOOLEAN,
    security_profile_id BIGINT
);

-- Audit session table (session logs)
CREATE TABLE "joko_security".audit_session (
    id BIGSERIAL PRIMARY KEY,
    access_date TIMESTAMP,
    access_token VARCHAR(500),
    app_description VARCHAR(255),
    app_id VARCHAR(255),
    ip VARCHAR(255),
    user_agent VARCHAR(255),
    user_description VARCHAR(255),
    user_id VARCHAR(255)
);

-- Create indexes
CREATE INDEX idx_consumer_api_name ON "joko_security".consumer_api(name);
CREATE INDEX idx_principal_session_user_id ON "joko_security".principal_session(user_id);
CREATE INDEX idx_security_profile_key ON "joko_security".security_profile("key");
CREATE INDEX idx_seed_user_id ON "joko_security".seed(user_id);
CREATE INDEX idx_token_refresh_token ON "joko_security".token(refresh_token);

-- Insert seed data
-- Security Profile (30min access token, 24h refresh token)
INSERT INTO "joko_security".security_profile (id, name, "key", access_token_timeout_seconds, refresh_token_timeout_seconds, max_access_token_requests, max_number_of_connections, max_number_devices_user, revocable)
VALUES (1, 'DEFAULT', 'ROLE_USER', 1800, 86400, 50, 5, 3, true);

-- Keychain (JWT signing secret)
INSERT INTO "joko_security".keychain (id, "value")
VALUES (1, 'ZGV2ZWxvcG1lbnQta2V5LWZvci1qb2tvLXNlY3VyaXR5');

-- Test user
INSERT INTO "joko_security".principal_session (id, app_id, user_id, app_description, user_description)
VALUES (1, 'dev-app', 'testuser', 'Development App', 'Test User');

-- Consumer API (for application authentication)
INSERT INTO "joko_security".consumer_api (id, name, secret, access_level, consumer_id, contact_name, document_number)
VALUES (1, 'dev-app', 'dev-secret-123', 'FULL', 'dev-consumer-001', 'Dev Admin', '12345678');

-- OTP Seed (for two-factor authentication)
INSERT INTO "joko_security".seed (id, user_id, seed_secret)
VALUES (1, 'testuser', 'development-seed');

-- Verify data
SELECT 'Security Profiles' as table_name, COUNT(*) as count FROM "joko_security".security_profile
UNION ALL
SELECT 'Keychain', COUNT(*) FROM "joko_security".keychain
UNION ALL
SELECT 'Principal Sessions', COUNT(*) FROM "joko_security".principal_session
UNION ALL
SELECT 'Consumer APIs', COUNT(*) FROM "joko_security".consumer_api
UNION ALL
SELECT 'Seeds', COUNT(*) FROM "joko_security".seed;
