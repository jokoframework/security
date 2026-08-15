-- Additional test data for API testing

-- Consumer API (for application authentication)
INSERT INTO "joko_security".consumer_api (id, name, secret, access_level, consumer_id, contact_name, document_number)
VALUES (1, 'dev-app', 'dev-secret-123', 'FULL', 'dev-consumer-001', 'Dev Admin', '12345678');

-- OTP Seed (for two-factor authentication)
-- IMPORTANT: seed_secret must be a valid base-32 string (A-Z, 2-7 only)
-- Use this seed with Google Authenticator or similar apps for testing 2FA
-- COMMENTED OUT: Uncomment to test 2FA flow with testuser
-- INSERT INTO "joko_security".seed (id, user_id, seed_secret)
-- VALUES (1, 'testuser', 'JBSWY3DPEHPK3PXP');
