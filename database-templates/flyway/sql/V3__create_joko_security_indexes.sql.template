-- Create indexes and constraints for joko_security tables
-- Copy this file as: V3__create_joko_security_indexes.sql

-- Unique constraints
ALTER TABLE "joko_security".consumer_api ADD CONSTRAINT consumer_api_consumer_id_unique UNIQUE (consumer_id);
ALTER TABLE "joko_security".security_profile ADD CONSTRAINT security_profile_name_unique UNIQUE (name);

-- Indexes for better performance
CREATE INDEX idx_audit_session_id_principal ON "joko_security".audit_session(id_principal);
CREATE INDEX idx_audit_session_user_date ON "joko_security".audit_session(user_date);
CREATE INDEX idx_audit_session_remote_ip ON "joko_security".audit_session(remote_ip);

CREATE INDEX idx_seed_user_id ON "joko_security".seed(user_id);

CREATE INDEX idx_tokens_user_id ON "joko_security".tokens(user_id);
CREATE INDEX idx_tokens_expiration ON "joko_security".tokens(expiration);
CREATE INDEX idx_tokens_token_type ON "joko_security".tokens(token_type);
CREATE INDEX idx_tokens_security_profile_id ON "joko_security".tokens(security_profile_id);

CREATE INDEX idx_principal_session_user_id ON "joko_security".principal_session(user_id);
CREATE INDEX idx_principal_session_app_id ON "joko_security".principal_session(app_id);