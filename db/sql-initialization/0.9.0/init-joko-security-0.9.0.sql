--
-- PostgreSQL database dump
--

-- Dumped from database version 9.4.12
-- Dumped by pg_dump version 9.5.5

-- Started on 2018-02-16 17:18:43 -03

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 8 (class 2615 OID 82455)
-- Name: joko_security; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA joko_security;


ALTER SCHEMA joko_security OWNER TO postgres;

SET search_path = joko_security, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 185 (class 1259 OID 82508)
-- Name: audit_session; Type: TABLE; Schema: joko_security; Owner: postgres
--

CREATE TABLE audit_session (
    id bigint NOT NULL,
    creation_date timestamp without time zone,
    remote_ip character varying(255),
    user_agent character varying(255),
    user_date timestamp without time zone,
    id_principal bigint
);


ALTER TABLE audit_session OWNER TO postgres;

--
-- TOC entry 2315 (class 0 OID 0)
-- Dependencies: 185
-- Name: TABLE audit_session; Type: COMMENT; Schema: joko_security; Owner: postgres
--

COMMENT ON TABLE audit_session IS 'Stores the last login of a given user';


--
-- TOC entry 176 (class 1259 OID 82456)
-- Name: audit_session_id_seq; Type: SEQUENCE; Schema: joko_security; Owner: postgres
--

CREATE SEQUENCE audit_session_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE audit_session_id_seq OWNER TO postgres;

--
-- TOC entry 180 (class 1259 OID 82464)
-- Name: consumer_api; Type: TABLE; Schema: joko_security; Owner: postgres
--

CREATE TABLE consumer_api (
    id bigint NOT NULL,
    access_level character varying(255),
    consumer_id character varying(255),
    contact_name character varying(255),
    document_number character varying(255),
    name character varying(255),
    secret character varying(255)
);


ALTER TABLE consumer_api OWNER TO postgres;

--
-- TOC entry 2316 (class 0 OID 0)
-- Dependencies: 180
-- Name: TABLE consumer_api; Type: COMMENT; Schema: joko_security; Owner: postgres
--

COMMENT ON TABLE consumer_api IS 'guarda los consumer para integracion con terceros a nivel de API';


--
-- TOC entry 177 (class 1259 OID 82458)
-- Name: consumer_api_id_seq; Type: SEQUENCE; Schema: joko_security; Owner: postgres
--

CREATE SEQUENCE consumer_api_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE consumer_api_id_seq OWNER TO postgres;

--
-- TOC entry 181 (class 1259 OID 82472)
-- Name: keychain; Type: TABLE; Schema: joko_security; Owner: postgres
--

CREATE TABLE keychain (
    id integer NOT NULL,
    value character varying(500)
);


ALTER TABLE keychain OWNER TO postgres;

--
-- TOC entry 2317 (class 0 OID 0)
-- Dependencies: 181
-- Name: TABLE keychain; Type: COMMENT; Schema: joko_security; Owner: postgres
--

COMMENT ON TABLE keychain IS 'Guarda la clave para firmar los tokens en caso sea modo BD';


--
-- TOC entry 182 (class 1259 OID 82477)
-- Name: principal_session; Type: TABLE; Schema: joko_security; Owner: postgres
--

CREATE TABLE principal_session (
    id bigint NOT NULL,
    app_description character varying(255),
    app_id character varying(255),
    user_description character varying(255),
    user_id character varying(255)
);


ALTER TABLE principal_session OWNER TO postgres;

--
-- TOC entry 178 (class 1259 OID 82460)
-- Name: principal_session_id_seq; Type: SEQUENCE; Schema: joko_security; Owner: postgres
--

CREATE SEQUENCE principal_session_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE principal_session_id_seq OWNER TO postgres;

--
-- TOC entry 183 (class 1259 OID 82487)
-- Name: security_profile; Type: TABLE; Schema: joko_security; Owner: postgres
--

CREATE TABLE security_profile (
    id bigint NOT NULL,
    access_token_timeout_seconds integer,
    key character varying(255),
    max_access_token_requests integer,
    max_number_of_connections integer,
    max_number_devices_user integer,
    name character varying(255),
    refresh_token_timeout_seconds integer,
    revocable boolean
);


ALTER TABLE security_profile OWNER TO postgres;

--
-- TOC entry 2318 (class 0 OID 0)
-- Dependencies: 183
-- Name: TABLE security_profile; Type: COMMENT; Schema: joko_security; Owner: postgres
--

COMMENT ON TABLE security_profile IS 'Establece la configuracion de emision de tokens para los distintos ambientes';


--
-- TOC entry 179 (class 1259 OID 82462)
-- Name: security_profile_id_seq; Type: SEQUENCE; Schema: joko_security; Owner: postgres
--

CREATE SEQUENCE security_profile_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE security_profile_id_seq OWNER TO postgres;

--
-- TOC entry 184 (class 1259 OID 82495)
-- Name: tokens; Type: TABLE; Schema: joko_security; Owner: postgres
--

CREATE TABLE tokens (
    id character varying(255) NOT NULL,
    expiration timestamp without time zone,
    issued_at timestamp without time zone,
    remote_ip character varying(255),
    token_type character varying(255),
    user_agent character varying(255),
    user_id character varying(255),
    security_profile_id bigint
);


ALTER TABLE tokens OWNER TO postgres;

--
-- TOC entry 2319 (class 0 OID 0)
-- Dependencies: 184
-- Name: TABLE tokens; Type: COMMENT; Schema: joko_security; Owner: postgres
--

COMMENT ON TABLE tokens IS 'La lista de tokens de refresh que estan activos';


--
-- TOC entry 2199 (class 2606 OID 82515)
-- Name: audit_session_pkey; Type: CONSTRAINT; Schema: joko_security; Owner: postgres
--

ALTER TABLE ONLY audit_session
    ADD CONSTRAINT audit_session_pkey PRIMARY KEY (id);


--
-- TOC entry 2187 (class 2606 OID 82471)
-- Name: consumer_api_pkey; Type: CONSTRAINT; Schema: joko_security; Owner: postgres
--

ALTER TABLE ONLY consumer_api
    ADD CONSTRAINT consumer_api_pkey PRIMARY KEY (id);


--
-- TOC entry 2189 (class 2606 OID 82476)
-- Name: keychain_pkey; Type: CONSTRAINT; Schema: joko_security; Owner: postgres
--

ALTER TABLE ONLY keychain
    ADD CONSTRAINT keychain_pkey PRIMARY KEY (id);


--
-- TOC entry 2191 (class 2606 OID 82484)
-- Name: principal_session_pkey; Type: CONSTRAINT; Schema: joko_security; Owner: postgres
--

ALTER TABLE ONLY principal_session
    ADD CONSTRAINT principal_session_pkey PRIMARY KEY (id);


--
-- TOC entry 2195 (class 2606 OID 82494)
-- Name: security_profile_pkey; Type: CONSTRAINT; Schema: joko_security; Owner: postgres
--

ALTER TABLE ONLY security_profile
    ADD CONSTRAINT security_profile_pkey PRIMARY KEY (id);


--
-- TOC entry 2197 (class 2606 OID 82502)
-- Name: tokens_pkey; Type: CONSTRAINT; Schema: joko_security; Owner: postgres
--

ALTER TABLE ONLY tokens
    ADD CONSTRAINT tokens_pkey PRIMARY KEY (id);


--
-- TOC entry 2193 (class 2606 OID 82486)
-- Name: uk_muajvqvs1jntexdohty6hexrv; Type: CONSTRAINT; Schema: joko_security; Owner: postgres
--

ALTER TABLE ONLY principal_session
    ADD CONSTRAINT uk_muajvqvs1jntexdohty6hexrv UNIQUE (app_id, user_id);


--
-- TOC entry 2201 (class 2606 OID 82516)
-- Name: fk_audit_principalsession; Type: FK CONSTRAINT; Schema: joko_security; Owner: postgres
--

ALTER TABLE ONLY audit_session
    ADD CONSTRAINT fk_audit_principalsession FOREIGN KEY (id_principal) REFERENCES principal_session(id);


--
-- TOC entry 2200 (class 2606 OID 82503)
-- Name: fk_tokens_securityprofile; Type: FK CONSTRAINT; Schema: joko_security; Owner: postgres
--

ALTER TABLE ONLY tokens
    ADD CONSTRAINT fk_tokens_securityprofile FOREIGN KEY (security_profile_id) REFERENCES security_profile(id);


-- Completed on 2018-02-16 17:18:43 -03

--
-- PostgreSQL database dump complete
--

