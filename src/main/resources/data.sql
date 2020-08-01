
MERGE INTO joko_security.security_profile(id,access_token_timeout_seconds,
            key,max_access_token_requests, max_number_of_connections, max_number_devices_user, name,
            refresh_token_timeout_seconds, revocable)
    KEY(id)
    VALUES (1,14440 ,'DEFAULT', 2, null, 1, 'Default configuration(testing purposes)',300, false);

MERGE INTO joko_security.security_profile(id,access_token_timeout_seconds,
            key,max_access_token_requests, max_number_of_connections, max_number_devices_user, name,
            refresh_token_timeout_seconds, revocable)
    VALUES (2,0 ,'EXPIRATION_SECURITY_PROFILE', 2, null, 1, 'Default ' ||
     'configuration(testing purposes)',0, false);


MERGE INTO basic.country (id, description)
KEY(ID)
VALUES ('PY','Paraguay'),
 ('AR', 'Argentina'),
 ('BR', 'Brasil'),
 ('UY', 'Uruguay');

MERGE INTO profile.user (id,username, password, created, profile) KEY (ID)
VALUES (1, 'admin', '$2a$06$MRQTEuDm5qsu4Rz952Ck5Oc4rsL9busImPxAzql.QY43qnSp4bWgG', now(), 'ADMIN') ;
