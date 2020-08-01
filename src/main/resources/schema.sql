CREATE SCHEMA IF NOT EXISTS joko_security;

CREATE SCHEMA IF NOT EXISTS profile;
CREATE SCHEMA IF NOT EXISTS basic;

CREATE TABLE IF NOT EXISTS  joko_security.security_profile
(
   id bigint IDENTITY PRIMARY KEY NOT NULL,
   access_token_timeout_seconds int,
   key varchar(255),
   max_access_token_requests int,
   max_number_of_connections int,
   max_number_devices_user int,
   name varchar(255),
   refresh_token_timeout_seconds int,
   revocable bool
)
;

CREATE TABLE IF NOT EXISTS  basic.person
(
   id bigint IDENTITY PRIMARY KEY NOT NULL,
   name varchar(250),
   lastname varchar(255),
   identification_number varchar(100) NOT NULL,
   birthdate timestamp,
   sex varchar(100),
   marital_status varchar(100),
   mobile_phone varchar(100),
   email varchar(255),
   nationality varchar(100)
)
;




CREATE TABLE IF NOT EXISTS  profile.user
(
   id bigint IDENTITY PRIMARY KEY NOT NULL,
   username varchar(255) NOT NULL,
   password varchar(255) NOT NULL,
   profile varchar(50) NOT NULL,
   created timestamp DEFAULT now(),
   last_access_date timestamp,
   person_id bigint
)
;

CREATE TABLE IF NOT EXISTS  basic.country
(
   id varchar(10) PRIMARY KEY NOT NULL,
   description varchar(50)
)
;
