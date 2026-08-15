-- Additional test data for API testing

-- Consumer API (for application authentication)
INSERT INTO "joko_security".consumer_api (id, name, secret, access_level, consumer_id, contact_name, document_number)
VALUES (1, 'dev-app', 'dev-secret-123', 'FULL', 'dev-consumer-001', 'Dev Admin', '12345678');

-- OTP Seed (for two-factor authentication)
INSERT INTO "joko_security".seed (id, user_id, seed_secret)
VALUES (1, 'testuser', 'development-seed');
