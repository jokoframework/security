-- Create joko_security core tables
-- Copy this file as: V2__create_joko_security_tables.sql

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

COMMENT ON TABLE "joko_security".consumer_api IS 'guarda los consumer para integracion con terceros a nivel de API';

-- Keychain table
CREATE TABLE "joko_security".keychain (
    id INT PRIMARY KEY,
    "value" VARCHAR(500)
);

COMMENT ON TABLE "joko_security".keychain IS 'Guarda la clave para firmar los tokens en caso sea modo BD';

-- Principal session table
CREATE TABLE "joko_security".principal_session (
    id BIGSERIAL PRIMARY KEY,
    app_description VARCHAR(255),
    app_id VARCHAR(255),
    user_description VARCHAR(255),
    user_id VARCHAR(255),
    CONSTRAINT uk_muajvqvs1jntexdohty6hexrv UNIQUE (app_id, user_id)
);

-- Audit session table
CREATE TABLE "joko_security".audit_session (
    id BIGSERIAL PRIMARY KEY,
    creation_date TIMESTAMP,
    remote_ip VARCHAR(255),
    user_agent VARCHAR(255),
    user_date TIMESTAMP,
    id_principal BIGINT,
    FOREIGN KEY (id_principal) REFERENCES "joko_security".principal_session(id)
);

COMMENT ON TABLE "joko_security".audit_session IS 'Stores the last login of a given user';

-- Security profile table
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

COMMENT ON TABLE "joko_security".security_profile IS 'Establece la configuracion de emision de tokens para los distintos ambientes';

-- Seed table
CREATE TABLE "joko_security".seed (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255),
    seed_secret VARCHAR(255)
);

COMMENT ON TABLE "joko_security".seed IS 'Guarda las semillas OTP';

-- Tokens table
CREATE TABLE "joko_security".tokens (
    id VARCHAR(255) PRIMARY KEY,
    expiration TIMESTAMP,
    issued_at TIMESTAMP,
    remote_ip VARCHAR(255),
    token_type VARCHAR(255),
    user_agent VARCHAR(255),
    user_id VARCHAR(255),
    security_profile_id BIGINT,
    FOREIGN KEY (security_profile_id) REFERENCES "joko_security".security_profile(id)
);

COMMENT ON TABLE "joko_security".tokens IS 'La lista de tokens de refresh que estan activos';