-- Table: sys.principal_session

-- DROP TABLE sys.principal_session;

CREATE TABLE sys.principal_session
(
  id bigint NOT NULL,
  app_description character varying(255),
  app_id character varying(255),
  user_description character varying(255),
  user_id character varying(255),
  CONSTRAINT principal_session_pkey PRIMARY KEY (id),
  CONSTRAINT uk_muajvqvs1jntexdohty6hexrv UNIQUE (app_id, user_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE sys.principal_session
  OWNER TO postgres;

-- Column: user_date

-- ALTER TABLE sys.audit_session DROP COLUMN user_date;

ALTER TABLE sys.audit_session ADD COLUMN user_date timestamp without time zone;

-- Column: id_principal

-- ALTER TABLE sys.audit_session DROP COLUMN id_principal;

ALTER TABLE sys.audit_session ADD COLUMN id_principal bigint;

-- Foreign Key: sys.fk_733l8gb99j2smqbt37gx1sems

-- ALTER TABLE sys.audit_session DROP CONSTRAINT fk_733l8gb99j2smqbt37gx1sems;

ALTER TABLE sys.audit_session
  ADD CONSTRAINT fk_733l8gb99j2smqbt37gx1sems FOREIGN KEY (id_principal)
      REFERENCES sys.principal_session (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;