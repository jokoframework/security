--
-- PostgreSQL database dump
--

-- Dumped from database version 9.4.4
-- Dumped by pg_dump version 9.4.4
-- Started on 2016-10-24 15:48:06 PYST

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 182 (class 3079 OID 11895)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2092 (class 0 OID 0)
-- Dependencies: 182
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 172 (class 1259 OID 16590)
-- Name: audit_session; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--


ALTER TABLE audit_session OWNER TO postgres;

--
-- TOC entry 178 (class 1259 OID 16647)
-- Name: audit_session_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--


ALTER TABLE audit_session_id_seq OWNER TO postgres;

--
-- TOC entry 173 (class 1259 OID 16598)
-- Name: consumer_api; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
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
-- TOC entry 179 (class 1259 OID 16649)
-- Name: consumer_api_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE consumer_api_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE consumer_api_id_seq OWNER TO postgres;

--
-- TOC entry 174 (class 1259 OID 16606)
-- Name: keychain; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE keychain (
    id integer NOT NULL,
    value character varying(500)
);


ALTER TABLE keychain OWNER TO postgres;

--
-- TOC entry 175 (class 1259 OID 16611)
-- Name: principal_session; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
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
-- TOC entry 180 (class 1259 OID 16651)
-- Name: principal_session_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE principal_session_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE principal_session_id_seq OWNER TO postgres;

--
-- TOC entry 176 (class 1259 OID 16619)
-- Name: security_profile; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
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
-- TOC entry 181 (class 1259 OID 16653)
-- Name: security_profile_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE security_profile_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE security_profile_id_seq OWNER TO postgres;

--
-- TOC entry 177 (class 1259 OID 16627)
-- Name: tokens; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
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
-- TOC entry 2075 (class 0 OID 16590)
-- Dependencies: 172
-- Data for Name: audit_session; Type: TABLE DATA; Schema: public; Owner: postgres
--

--
-- TOC entry 2093 (class 0 OID 0)
-- Dependencies: 178
-- Name: audit_session_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('audit_session_id_seq', 1, false);


--
-- TOC entry 2076 (class 0 OID 16598)
-- Dependencies: 173
-- Data for Name: consumer_api; Type: TABLE DATA; Schema: public; Owner: postgres
--

--
-- TOC entry 2094 (class 0 OID 0)
-- Dependencies: 179
-- Name: consumer_api_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('consumer_api_id_seq', 1, false);


--
-- TOC entry 2077 (class 0 OID 16606)
-- Dependencies: 174
-- Data for Name: keychain; Type: TABLE DATA; Schema: public; Owner: postgres
--

--
-- TOC entry 2078 (class 0 OID 16611)
-- Dependencies: 175
-- Data for Name: principal_session; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2095 (class 0 OID 0)
-- Dependencies: 180
-- Name: principal_session_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('principal_session_id_seq', 1, false);


--
-- TOC entry 2079 (class 0 OID 16619)
-- Dependencies: 176
-- Data for Name: security_profile; Type: TABLE DATA; Schema: public; Owner: postgres
--


--
-- TOC entry 2096 (class 0 OID 0)
-- Dependencies: 181
-- Name: security_profile_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('security_profile_id_seq', 1, false);


--
-- TOC entry 2080 (class 0 OID 16627)
-- Dependencies: 177
-- Data for Name: tokens; Type: TABLE DATA; Schema: public; Owner: postgres
--


--
-- TOC entry 1951 (class 2606 OID 16597)
-- Name: audit_session_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY audit_session
    ADD CONSTRAINT audit_session_pkey PRIMARY KEY (id);


--
-- TOC entry 1953 (class 2606 OID 16605)
-- Name: consumer_api_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY consumer_api
    ADD CONSTRAINT consumer_api_pkey PRIMARY KEY (id);


--
-- TOC entry 1955 (class 2606 OID 16610)
-- Name: keychain_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY keychain
    ADD CONSTRAINT keychain_pkey PRIMARY KEY (id);


--
-- TOC entry 1957 (class 2606 OID 16618)
-- Name: principal_session_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY principal_session
    ADD CONSTRAINT principal_session_pkey PRIMARY KEY (id);


--
-- TOC entry 1961 (class 2606 OID 16626)
-- Name: security_profile_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY security_profile
    ADD CONSTRAINT security_profile_pkey PRIMARY KEY (id);


--
-- TOC entry 1963 (class 2606 OID 16634)
-- Name: tokens_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY tokens
    ADD CONSTRAINT tokens_pkey PRIMARY KEY (id);


--
-- TOC entry 1959 (class 2606 OID 16636)
-- Name: uk_muajvqvs1jntexdohty6hexrv; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY principal_session
    ADD CONSTRAINT uk_muajvqvs1jntexdohty6hexrv UNIQUE (app_id, user_id);


--
-- TOC entry 1964 (class 2606 OID 16637)
-- Name: fk_733l8gb99j2smqbt37gx1sems; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY audit_session
    ADD CONSTRAINT fk_733l8gb99j2smqbt37gx1sems FOREIGN KEY (id_principal) REFERENCES principal_session(id);


--
-- TOC entry 1965 (class 2606 OID 16642)
-- Name: fk_jbqrobh7wdleenm46iag1sx3d; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tokens
    ADD CONSTRAINT fk_jbqrobh7wdleenm46iag1sx3d FOREIGN KEY (security_profile_id) REFERENCES security_profile(id);


--
-- TOC entry 2091 (class 0 OID 0)
-- Dependencies: 5
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2016-10-24 15:48:06 PYST

--
-- PostgreSQL database dump complete
--
