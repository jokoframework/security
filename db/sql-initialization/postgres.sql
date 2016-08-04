--
-- PostgreSQL database dump
--

-- Dumped from database version 9.3.5
-- Dumped by pg_dump version 9.3.1
-- Started on 2016-05-14 11:00:37 PYT

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 8 (class 2615 OID 141683)
-- Name: conf; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA conf;


ALTER SCHEMA conf OWNER TO postgres;

--
-- TOC entry 7 (class 2615 OID 141682)
-- Name: sys; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA sys;


ALTER SCHEMA sys OWNER TO postgres;

--
-- TOC entry 184 (class 3079 OID 12018)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2293 (class 0 OID 0)
-- Dependencies: 184
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = conf, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 182 (class 1259 OID 141737)
-- Name: labels; Type: TABLE; Schema: conf; Owner: postgres; Tablespace: 
--

CREATE TABLE labels (
    language_id bigint NOT NULL,
    value character varying(1024),
    key character varying(512) NOT NULL
);


ALTER TABLE conf.labels OWNER TO postgres;

--
-- TOC entry 183 (class 1259 OID 141743)
-- Name: languages; Type: TABLE; Schema: conf; Owner: postgres; Tablespace: 
--

CREATE TABLE languages (
    id bigint NOT NULL,
    active boolean DEFAULT true NOT NULL,
    created timestamp with time zone DEFAULT now() NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    modified timestamp with time zone DEFAULT now(),
    iso_language character varying(255) NOT NULL,
    name character varying(255)
);


ALTER TABLE conf.languages OWNER TO postgres;

--
-- TOC entry 180 (class 1259 OID 141726)
-- Name: parameters; Type: TABLE; Schema: conf; Owner: postgres; Tablespace: 
--

CREATE TABLE parameters (
    id bigint NOT NULL,
    active boolean,
    description character varying(255),
    label character varying(255),
    type character varying(255),
    value character varying(255)
);


ALTER TABLE conf.parameters OWNER TO postgres;

--
-- TOC entry 181 (class 1259 OID 141734)
-- Name: parameters_id_seq; Type: SEQUENCE; Schema: conf; Owner: postgres
--

CREATE SEQUENCE parameters_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE conf.parameters_id_seq OWNER TO postgres;

--
-- TOC entry 2294 (class 0 OID 0)
-- Dependencies: 181
-- Name: parameters_id_seq; Type: SEQUENCE OWNED BY; Schema: conf; Owner: postgres
--

ALTER SEQUENCE parameters_id_seq OWNED BY parameters.id;


SET search_path = public, pg_catalog;

--
-- TOC entry 173 (class 1259 OID 141676)
-- Name: databasechangelog; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE databasechangelog (
    id character varying(255) NOT NULL,
    author character varying(255) NOT NULL,
    filename character varying(255) NOT NULL,
    dateexecuted timestamp without time zone NOT NULL,
    orderexecuted integer NOT NULL,
    exectype character varying(10) NOT NULL,
    md5sum character varying(35),
    description character varying(255),
    comments character varying(255),
    tag character varying(255),
    liquibase character varying(20)
);


ALTER TABLE public.databasechangelog OWNER TO postgres;

--
-- TOC entry 172 (class 1259 OID 141671)
-- Name: databasechangeloglock; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE databasechangeloglock (
    id integer NOT NULL,
    locked boolean NOT NULL,
    lockgranted timestamp without time zone,
    lockedby character varying(255)
);


ALTER TABLE public.databasechangeloglock OWNER TO postgres;

SET search_path = sys, pg_catalog;

--
-- TOC entry 179 (class 1259 OID 141715)
-- Name: consumer_api; Type: TABLE; Schema: sys; Owner: postgres; Tablespace: 
--

CREATE TABLE consumer_api (
    id bigint NOT NULL,
    document_number character varying(255),
    name character varying(255),
    contact_name character varying(255),
    consumer_id character varying(255) NOT NULL,
    secret character varying(255) NOT NULL,
    access_level character varying(50) NOT NULL
);


ALTER TABLE sys.consumer_api OWNER TO postgres;

--
-- TOC entry 2295 (class 0 OID 0)
-- Dependencies: 179
-- Name: COLUMN consumer_api.document_number; Type: COMMENT; Schema: sys; Owner: postgres
--

COMMENT ON COLUMN consumer_api.document_number IS 'Cédula de Identidad';


--
-- TOC entry 2296 (class 0 OID 0)
-- Dependencies: 179
-- Name: COLUMN consumer_api.name; Type: COMMENT; Schema: sys; Owner: postgres
--

COMMENT ON COLUMN consumer_api.name IS 'El nombre con el que se le identifica a este acceso';


--
-- TOC entry 2297 (class 0 OID 0)
-- Dependencies: 179
-- Name: COLUMN consumer_api.contact_name; Type: COMMENT; Schema: sys; Owner: postgres
--

COMMENT ON COLUMN consumer_api.contact_name IS 'Nombre de la persona responsable';


--
-- TOC entry 2298 (class 0 OID 0)
-- Dependencies: 179
-- Name: COLUMN consumer_api.consumer_id; Type: COMMENT; Schema: sys; Owner: postgres
--

COMMENT ON COLUMN consumer_api.consumer_id IS 'El id generado por el sistema para el consumer';


--
-- TOC entry 2299 (class 0 OID 0)
-- Dependencies: 179
-- Name: COLUMN consumer_api.secret; Type: COMMENT; Schema: sys; Owner: postgres
--

COMMENT ON COLUMN consumer_api.secret IS 'Clave de la conexion';


--
-- TOC entry 2300 (class 0 OID 0)
-- Dependencies: 179
-- Name: COLUMN consumer_api.access_level; Type: COMMENT; Schema: sys; Owner: postgres
--

COMMENT ON COLUMN consumer_api.access_level IS 'El tipo de acceso que se concede';


--
-- TOC entry 178 (class 1259 OID 141713)
-- Name: consumer_api_id_seq; Type: SEQUENCE; Schema: sys; Owner: postgres
--

CREATE SEQUENCE consumer_api_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE sys.consumer_api_id_seq OWNER TO postgres;

--
-- TOC entry 2301 (class 0 OID 0)
-- Dependencies: 178
-- Name: consumer_api_id_seq; Type: SEQUENCE OWNED BY; Schema: sys; Owner: postgres
--

ALTER SEQUENCE consumer_api_id_seq OWNED BY consumer_api.id;


--
-- TOC entry 177 (class 1259 OID 141705)
-- Name: keychain; Type: TABLE; Schema: sys; Owner: postgres; Tablespace: 
--

CREATE TABLE keychain (
    id integer NOT NULL,
    value character varying(2000)
);


ALTER TABLE sys.keychain OWNER TO postgres;

--
-- TOC entry 176 (class 1259 OID 141694)
-- Name: security_profile; Type: TABLE; Schema: sys; Owner: postgres; Tablespace: 
--

CREATE TABLE security_profile (
    id bigint NOT NULL,
    key character varying(50) NOT NULL,
    name character varying(50) NOT NULL,
    max_number_of_connections_per_user integer,
    max_number_of_connections integer,
    refresh_token_timeout_seconds integer,
    access_token_timeout_seconds integer,
    revocable boolean DEFAULT false NOT NULL,
    max_access_token_requests integer
);


ALTER TABLE sys.security_profile OWNER TO postgres;

--
-- TOC entry 2302 (class 0 OID 0)
-- Dependencies: 176
-- Name: COLUMN security_profile.key; Type: COMMENT; Schema: sys; Owner: postgres
--

COMMENT ON COLUMN security_profile.key IS 'Un key para identificar de manera unica a un security profile';


--
-- TOC entry 2303 (class 0 OID 0)
-- Dependencies: 176
-- Name: COLUMN security_profile.name; Type: COMMENT; Schema: sys; Owner: postgres
--

COMMENT ON COLUMN security_profile.name IS 'Nombre de la aplicacion. Tiene semantica solo para la persona que creo. Es un nombre descriptivo';


--
-- TOC entry 2304 (class 0 OID 0)
-- Dependencies: 176
-- Name: COLUMN security_profile.max_number_of_connections_per_user; Type: COMMENT; Schema: sys; Owner: postgres
--

COMMENT ON COLUMN security_profile.max_number_of_connections_per_user IS 'El numero maximo de conexiones que pueden existir en base a un mismo usuario. Si es null indica que es infinito';


--
-- TOC entry 2305 (class 0 OID 0)
-- Dependencies: 176
-- Name: COLUMN security_profile.max_number_of_connections; Type: COMMENT; Schema: sys; Owner: postgres
--

COMMENT ON COLUMN security_profile.max_number_of_connections IS 'Numero maximo de conexiones totales que pueden existir de este security profile. Si el usuario A y B estan conectados, entonces el numero de conexiones sería 2.';


--
-- TOC entry 2306 (class 0 OID 0)
-- Dependencies: 176
-- Name: COLUMN security_profile.refresh_token_timeout_seconds; Type: COMMENT; Schema: sys; Owner: postgres
--

COMMENT ON COLUMN security_profile.refresh_token_timeout_seconds IS 'Tiempo de validez de un refresh token para esta aplicación. Medido en segundos.';


--
-- TOC entry 2307 (class 0 OID 0)
-- Dependencies: 176
-- Name: COLUMN security_profile.access_token_timeout_seconds; Type: COMMENT; Schema: sys; Owner: postgres
--

COMMENT ON COLUMN security_profile.access_token_timeout_seconds IS 'Tiempo de vida de un access token para esta aplicación, medido en segundos.';


--
-- TOC entry 2308 (class 0 OID 0)
-- Dependencies: 176
-- Name: COLUMN security_profile.revocable; Type: COMMENT; Schema: sys; Owner: postgres
--

COMMENT ON COLUMN security_profile.revocable IS 'Indica si producira access token revocables. Los access token revocables degradan el performance porque se controlan contra la BD central.';


--
-- TOC entry 2309 (class 0 OID 0)
-- Dependencies: 176
-- Name: COLUMN security_profile.max_access_token_requests; Type: COMMENT; Schema: sys; Owner: postgres
--

COMMENT ON COLUMN security_profile.max_access_token_requests IS 'El numero maximo de veces que un access token puede usarse. Los que tengan un valor diferente de null se convierten automaticamente en revocables. NULL indica infinito (en la practica sera hasta que expire el token)';


--
-- TOC entry 175 (class 1259 OID 141692)
-- Name: security_profile_id_seq; Type: SEQUENCE; Schema: sys; Owner: postgres
--

CREATE SEQUENCE security_profile_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE sys.security_profile_id_seq OWNER TO postgres;

--
-- TOC entry 2310 (class 0 OID 0)
-- Dependencies: 175
-- Name: security_profile_id_seq; Type: SEQUENCE OWNED BY; Schema: sys; Owner: postgres
--

ALTER SEQUENCE security_profile_id_seq OWNED BY security_profile.id;


--
-- TOC entry 174 (class 1259 OID 141684)
-- Name: tokens; Type: TABLE; Schema: sys; Owner: postgres; Tablespace: 
--

CREATE TABLE tokens (
    id character varying(25) NOT NULL,
    user_id character varying(25),
    security_profile_id integer,
    remote_ip character varying(15),
    user_agent character varying(150),
    issued_at timestamp with time zone,
    expiration timestamp with time zone,
    token_type character varying(20) NOT NULL,
    token character varying(1000)
);


ALTER TABLE sys.tokens OWNER TO postgres;

--
-- TOC entry 2311 (class 0 OID 0)
-- Dependencies: 174
-- Name: COLUMN tokens.id; Type: COMMENT; Schema: sys; Owner: postgres
--

COMMENT ON COLUMN tokens.id IS 'El jti del token emitido';


--
-- TOC entry 2312 (class 0 OID 0)
-- Dependencies: 174
-- Name: COLUMN tokens.user_id; Type: COMMENT; Schema: sys; Owner: postgres
--

COMMENT ON COLUMN tokens.user_id IS 'El id del user que es dueño del token.
JWT (sub claim)';


--
-- TOC entry 2313 (class 0 OID 0)
-- Dependencies: 174
-- Name: COLUMN tokens.security_profile_id; Type: COMMENT; Schema: sys; Owner: postgres
--

COMMENT ON COLUMN tokens.security_profile_id IS 'El id del security profile que se utilizo para crear el token.';


--
-- TOC entry 2314 (class 0 OID 0)
-- Dependencies: 174
-- Name: COLUMN tokens.remote_ip; Type: COMMENT; Schema: sys; Owner: postgres
--

COMMENT ON COLUMN tokens.remote_ip IS 'El remote IP (si se puede determinar) desde el cual se genero el token';


--
-- TOC entry 2315 (class 0 OID 0)
-- Dependencies: 174
-- Name: COLUMN tokens.user_agent; Type: COMMENT; Schema: sys; Owner: postgres
--

COMMENT ON COLUMN tokens.user_agent IS 'El atributo "User-Agent" de la cabecera http que se utilizo al crear el token';


--
-- TOC entry 2316 (class 0 OID 0)
-- Dependencies: 174
-- Name: COLUMN tokens.issued_at; Type: COMMENT; Schema: sys; Owner: postgres
--

COMMENT ON COLUMN tokens.issued_at IS 'La fecha en la que se creo el token.
JWT claim iat';


--
-- TOC entry 2317 (class 0 OID 0)
-- Dependencies: 174
-- Name: COLUMN tokens.expiration; Type: COMMENT; Schema: sys; Owner: postgres
--

COMMENT ON COLUMN tokens.expiration IS 'La fecha en la que expira el token.
JWT claim exp';


--
-- TOC entry 2318 (class 0 OID 0)
-- Dependencies: 174
-- Name: COLUMN tokens.token_type; Type: COMMENT; Schema: sys; Owner: postgres
--

COMMENT ON COLUMN tokens.token_type IS 'El tipo de token que se mapea a un enum';


--
-- TOC entry 2319 (class 0 OID 0)
-- Dependencies: 174
-- Name: COLUMN tokens.token; Type: COMMENT; Schema: sys; Owner: postgres
--

COMMENT ON COLUMN tokens.token IS 'El token entero, solo se guarda en modo debug. En produccion esto será siempre null para evitar un vector de seguridad.';


SET search_path = conf, pg_catalog;

--
-- TOC entry 2137 (class 2604 OID 141736)
-- Name: id; Type: DEFAULT; Schema: conf; Owner: postgres
--

ALTER TABLE ONLY parameters ALTER COLUMN id SET DEFAULT nextval('parameters_id_seq'::regclass);


SET search_path = sys, pg_catalog;

--
-- TOC entry 2136 (class 2604 OID 141718)
-- Name: id; Type: DEFAULT; Schema: sys; Owner: postgres
--

ALTER TABLE ONLY consumer_api ALTER COLUMN id SET DEFAULT nextval('consumer_api_id_seq'::regclass);


--
-- TOC entry 2134 (class 2604 OID 141697)
-- Name: id; Type: DEFAULT; Schema: sys; Owner: postgres
--

ALTER TABLE ONLY security_profile ALTER COLUMN id SET DEFAULT nextval('security_profile_id_seq'::regclass);


SET search_path = conf, pg_catalog;

--
-- TOC entry 2161 (class 2606 OID 141754)
-- Name: labels_pkey; Type: CONSTRAINT; Schema: conf; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY labels
    ADD CONSTRAINT labels_pkey PRIMARY KEY (language_id, key);


--
-- TOC entry 2163 (class 2606 OID 141758)
-- Name: languages_iso_language_key; Type: CONSTRAINT; Schema: conf; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY languages
    ADD CONSTRAINT languages_iso_language_key UNIQUE (iso_language);


--
-- TOC entry 2165 (class 2606 OID 141756)
-- Name: languages_pkey; Type: CONSTRAINT; Schema: conf; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY languages
    ADD CONSTRAINT languages_pkey PRIMARY KEY (id);


--
-- TOC entry 2159 (class 2606 OID 141733)
-- Name: pk_parameters; Type: CONSTRAINT; Schema: conf; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY parameters
    ADD CONSTRAINT pk_parameters PRIMARY KEY (id);


SET search_path = public, pg_catalog;

--
-- TOC entry 2143 (class 2606 OID 141675)
-- Name: pk_databasechangeloglock; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY databasechangeloglock
    ADD CONSTRAINT pk_databasechangeloglock PRIMARY KEY (id);


SET search_path = sys, pg_catalog;

--
-- TOC entry 2155 (class 2606 OID 141725)
-- Name: consumer_api_consumer_id_key; Type: CONSTRAINT; Schema: sys; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY consumer_api
    ADD CONSTRAINT consumer_api_consumer_id_key UNIQUE (consumer_id);


--
-- TOC entry 2157 (class 2606 OID 141723)
-- Name: pk_consumer_api; Type: CONSTRAINT; Schema: sys; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY consumer_api
    ADD CONSTRAINT pk_consumer_api PRIMARY KEY (id);


--
-- TOC entry 2153 (class 2606 OID 141712)
-- Name: pk_keychain; Type: CONSTRAINT; Schema: sys; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY keychain
    ADD CONSTRAINT pk_keychain PRIMARY KEY (id);


--
-- TOC entry 2147 (class 2606 OID 141700)
-- Name: pk_security_profile; Type: CONSTRAINT; Schema: sys; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY security_profile
    ADD CONSTRAINT pk_security_profile PRIMARY KEY (id);


--
-- TOC entry 2145 (class 2606 OID 141691)
-- Name: pk_tokens; Type: CONSTRAINT; Schema: sys; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY tokens
    ADD CONSTRAINT pk_tokens PRIMARY KEY (id);


--
-- TOC entry 2149 (class 2606 OID 141702)
-- Name: security_profile_key_key; Type: CONSTRAINT; Schema: sys; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY security_profile
    ADD CONSTRAINT security_profile_key_key UNIQUE (key);


--
-- TOC entry 2151 (class 2606 OID 141704)
-- Name: security_profile_name_key; Type: CONSTRAINT; Schema: sys; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY security_profile
    ADD CONSTRAINT security_profile_name_key UNIQUE (name);


SET search_path = conf, pg_catalog;

--
-- TOC entry 2166 (class 2606 OID 141759)
-- Name: fkbdd05fffa100b5bd; Type: FK CONSTRAINT; Schema: conf; Owner: postgres
--

ALTER TABLE ONLY labels
    ADD CONSTRAINT fkbdd05fffa100b5bd FOREIGN KEY (language_id) REFERENCES languages(id);


--
-- TOC entry 2292 (class 0 OID 0)
-- Dependencies: 5
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2016-05-14 11:00:38 PYT

--
-- PostgreSQL database dump complete
--

delete from sys.security_profile;
INSERT INTO sys.security_profile(
            key, name, max_number_of_connections_per_user, max_number_of_connections, 
            refresh_token_timeout_seconds, access_token_timeout_seconds, 
            revocable, max_access_token_requests)
    VALUES ( 'DEFAULT', 'Default configuration(testing purposes)', 1, null, 
            14440, 300, 
            false, 2);

