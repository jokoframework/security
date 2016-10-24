CREATE TABLE sys.audit_session (
    id bigserial NOT NULL,
    user_date timestamp without time zone DEFAULT now() NOT NULL,
    remote_ip character varying(255) NOT NULL,
    user_agent character varying(2048) NOT NULL,
    CONSTRAINT pk_audit_sessions PRIMARY KEY (id)
);