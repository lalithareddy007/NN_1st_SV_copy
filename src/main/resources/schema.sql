--
-- Name: LMS; Type: DATABASE; Schema: -; Owner: postgres
--

--CREATE DATABASE IF NOT EXISTS "LMS" WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE = 'English_United States.1252';


--ALTER DATABASE "LMS" OWNER TO postgres;

--
-- Name: tbl_lms_assignments; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE IF NOT EXISTS  public.tbl_lms_assignments (
    a_id bigint NOT NULL,
    a_name character varying NOT NULL,
    a_description character varying NOT NULL,
    a_comments character varying,
    a_due_date date DEFAULT (CURRENT_DATE + '7 days'::interval) NOT NULL,
    a_path_attach1 character varying,
    a_path_attach2 character varying,
    a_path_attach3 character varying,
    a_path_attach4 character varying,
    a_path_attach5 character varying,
    a_created_by character varying NOT NULL,
    a_batch_id integer NOT NULL,
    a_grader_id character varying NOT NULL,
    creation_time timestamp without time zone DEFAULT now() NOT NULL,
    last_mod_time timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.tbl_lms_assignments OWNER TO postgres;

--
-- Name: tbl_lms_assignments_a_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE  IF NOT EXISTS  public.tbl_lms_assignments_a_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tbl_lms_assignments_a_id_seq OWNER TO postgres;

--
-- Name: tbl_lms_assignments_a_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tbl_lms_assignments_a_id_seq OWNED BY public.tbl_lms_assignments.a_id;


--
-- Name: tbl_lms_attendance; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE IF NOT EXISTS  public.tbl_lms_attendance (
    att_id bigint NOT NULL,
    cs_id integer NOT NULL,
    student_id character varying NOT NULL,
    attendance character varying DEFAULT ROW('Not Marked', 'Present', 'Absent', 'Excused') NOT NULL,
    creation_time timestamp without time zone DEFAULT now() NOT NULL,
    last_mod_time timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.tbl_lms_attendance OWNER TO postgres;

--
-- Name: tbl_lms_attendance_att_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE  IF NOT EXISTS  public.tbl_lms_attendance_att_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tbl_lms_attendance_att_id_seq OWNER TO postgres;

--
-- Name: tbl_lms_attendance_att_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tbl_lms_attendance_att_id_seq OWNED BY public.tbl_lms_attendance.att_id;


--
-- Name: tbl_lms_batch; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE IF NOT EXISTS  public.tbl_lms_batch (
    batch_id integer NOT NULL,
    batch_name character varying NOT NULL,
    batch_description character varying,
    batch_status character varying DEFAULT 'ACTIVE'::character varying NOT NULL,
    batch_program_id integer NOT NULL,
    batch_no_of_classes integer DEFAULT 1 NOT NULL,
    creation_time timestamp without time zone DEFAULT now() NOT NULL,
    last_mod_time timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.tbl_lms_batch OWNER TO postgres;

--
-- Name: tbl_lms_batch_batch_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE  IF NOT EXISTS  public.tbl_lms_batch_batch_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tbl_lms_batch_batch_id_seq OWNER TO postgres;

--
-- Name: tbl_lms_batch_batch_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tbl_lms_batch_batch_id_seq OWNED BY public.tbl_lms_batch.batch_id;


--
-- Name: tbl_lms_class_sch; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE IF NOT EXISTS  public.tbl_lms_class_sch (
    cs_id bigint NOT NULL,
    batch_id integer NOT NULL,
    class_no integer NOT NULL,
    class_date date,
    class_topic character varying,
    class_staff_id character varying,
    class_description character varying,
    class_comments character varying,
    class_notes character varying,
    class_recording_path character varying,
    creation_time timestamp without time zone DEFAULT now() NOT NULL,
    last_mod_time timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.tbl_lms_class_sch OWNER TO postgres;

--
-- Name: tbl_lms_class_sch_cs_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE  IF NOT EXISTS  public.tbl_lms_class_sch_cs_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tbl_lms_class_sch_cs_id_seq OWNER TO postgres;

--
-- Name: tbl_lms_class_sch_cs_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tbl_lms_class_sch_cs_id_seq OWNED BY public.tbl_lms_class_sch.cs_id;


--
-- Name: tbl_lms_program; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE IF NOT EXISTS  public.tbl_lms_program (
    program_id integer NOT NULL,
    program_name character varying NOT NULL,
    program_description character varying,
    program_status character varying DEFAULT 'ACTIVE'::character varying NOT NULL,
    creation_time timestamp without time zone DEFAULT now() NOT NULL,
    last_mod_time timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.tbl_lms_program OWNER TO postgres;

--
-- Name: tbl_lms_program_program_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE  IF NOT EXISTS  public.tbl_lms_program_program_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tbl_lms_program_program_id_seq OWNER TO postgres;

--
-- Name: tbl_lms_program_program_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tbl_lms_program_program_id_seq OWNED BY public.tbl_lms_program.program_id;


--
-- Name: tbl_lms_role; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE IF NOT EXISTS  public.tbl_lms_role (
    role_id character varying NOT NULL,
    role_name character varying NOT NULL,
    role_desc character varying,
    creation_time timestamp without time zone DEFAULT now() NOT NULL,
    last_mod_time timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.tbl_lms_role OWNER TO postgres;

--
-- Name: tbl_lms_skill_master; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE IF NOT EXISTS  public.tbl_lms_skill_master (
    skill_id bigint NOT NULL,
    skill_name character varying NOT NULL,
    creation_time timestamp without time zone DEFAULT now() NOT NULL,
    last_mod_time timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.tbl_lms_skill_master OWNER TO postgres;

--
-- Name: tbl_lms_skill_master_skill_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE  IF NOT EXISTS  public.tbl_lms_skill_master_skill_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tbl_lms_skill_master_skill_id_seq OWNER TO postgres;

--
-- Name: tbl_lms_skill_master_skill_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tbl_lms_skill_master_skill_id_seq OWNED BY public.tbl_lms_skill_master.skill_id;


--
-- Name: tbl_lms_submissions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE IF NOT EXISTS  public.tbl_lms_submissions (
    sub_id bigint NOT NULL,
    sub_a_id integer NOT NULL,
    sub_student_id character varying NOT NULL,
    sub_description character varying NOT NULL,
    sub_comments character varying,
    sub_path_attach1 character varying,
    sub_path_attach2 character varying,
    sub_path_attach3 character varying,
    sub_path_attach4 character varying,
    sub_path_attach5 character varying,
    sub_datetime timestamp without time zone,
    graded_by character varying,
    graded_datetime timestamp without time zone,
    grade numeric NOT NULL,
    creation_time timestamp without time zone DEFAULT now() NOT NULL,
    last_mod_time timestamp without time zone DEFAULT now() NOT NULL,
    CONSTRAINT tbl_lms_submissions_grade_check CHECK ((grade < (300)::numeric))
);


ALTER TABLE public.tbl_lms_submissions OWNER TO postgres;

--
-- Name: tbl_lms_submissions_sub_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE  IF NOT EXISTS  public.tbl_lms_submissions_sub_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tbl_lms_submissions_sub_id_seq OWNER TO postgres;

--
-- Name: tbl_lms_submissions_sub_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tbl_lms_submissions_sub_id_seq OWNED BY public.tbl_lms_submissions.sub_id;


--
-- Name: tbl_lms_user; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE IF NOT EXISTS  public.tbl_lms_user (
    user_id character varying NOT NULL,
    user_first_name character varying NOT NULL,
    user_last_name character varying NOT NULL,
    user_phone_number numeric NOT NULL,
    user_location character varying NOT NULL,
    user_time_zone character varying DEFAULT 'EST'::character varying NOT NULL,
    user_linkedin_url character varying,
    user_edu_ug character varying,
    user_edu_pg character varying,
    user_comments character varying,
    user_visa_status character varying DEFAULT 'Not-Specified'::character varying NOT NULL,
    creation_time timestamp without time zone DEFAULT now() NOT NULL,
    last_mod_time timestamp without time zone DEFAULT now() NOT NULL,
    CONSTRAINT time_zone_chk CHECK (((user_time_zone)::text = ANY ((ARRAY['PST'::character varying, 'MST'::character varying, 'CST'::character varying, 'EST'::character varying, 'IST'::character varying])::text[]))),
    CONSTRAINT visa_status_chk CHECK (((user_visa_status)::text = ANY ((ARRAY['Not-Specified'::character varying, 'NA'::character varying, 'GC-EAD'::character varying, 'H4-EAD'::character varying, 'H4'::character varying, 'H1B'::character varying, 'Canada-EAD'::character varying, 'Indian-Citizen'::character varying, 'US-Citizen'::character varying, 'Canada-Citizen'::character varying])::text[])))
);


ALTER TABLE public.tbl_lms_user OWNER TO postgres;

--
-- Name: tbl_lms_user_files; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE IF NOT EXISTS  public.tbl_lms_user_files (
    user_file_id bigint NOT NULL,
    user_id character varying NOT NULL,
    user_file_type character varying NOT NULL,
    user_file_path character varying NOT NULL,
    CONSTRAINT file_desc_check CHECK (((user_file_type)::text = ANY ((ARRAY['Resume'::character varying, 'ProfilePic'::character varying])::text[])))
);


ALTER TABLE public.tbl_lms_user_files OWNER TO postgres;

--
-- Name: tbl_lms_user_files_user_file_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE  IF NOT EXISTS  public.tbl_lms_user_files_user_file_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tbl_lms_user_files_user_file_id_seq OWNER TO postgres;

--
-- Name: tbl_lms_user_files_user_file_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tbl_lms_user_files_user_file_id_seq OWNED BY public.tbl_lms_user_files.user_file_id;


--
-- Name: tbl_lms_user_login; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE IF NOT EXISTS  public.tbl_lms_user_login (
    user_id character varying NOT NULL,
    user_login_name character varying NOT NULL,
    user_password character varying DEFAULT 'WELCOME@123'::character varying NOT NULL,
    user_login_status character varying DEFAULT 'ACTIVE'::character varying NOT NULL,
    user_security_q character varying NOT NULL,
    user_security_a character varying NOT NULL,
    creation_time timestamp without time zone DEFAULT now() NOT NULL,
    last_mod_time timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.tbl_lms_user_login OWNER TO postgres;

--
-- Name: tbl_lms_userbatch_map; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE IF NOT EXISTS  public.tbl_lms_userbatch_map (
    ub_map_id character varying NOT NULL,
    user_role_id integer NOT NULL,
    batch_id integer NOT NULL
);


ALTER TABLE public.tbl_lms_userbatch_map OWNER TO postgres;

--
-- Name: tbl_lms_userrole_map; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE IF NOT EXISTS  public.tbl_lms_userrole_map (
    user_role_id bigint NOT NULL,
    user_id character varying NOT NULL,
    role_id character varying NOT NULL,
    user_role_status character varying DEFAULT 'ACTIVE'::character varying NOT NULL,
    creation_time timestamp without time zone DEFAULT now() NOT NULL,
    last_mod_time timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.tbl_lms_userrole_map OWNER TO postgres;

--
-- Name: tbl_lms_userrole_map_user_role_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE  IF NOT EXISTS  public.tbl_lms_userrole_map_user_role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tbl_lms_userrole_map_user_role_id_seq OWNER TO postgres;

--
-- Name: tbl_lms_userrole_map_user_role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tbl_lms_userrole_map_user_role_id_seq OWNED BY public.tbl_lms_userrole_map.user_role_id;


--
-- Name: tbl_lms_userskill_map; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE IF NOT EXISTS  public.tbl_lms_userskill_map (
    user_skill_id character varying NOT NULL,
    user_id character varying NOT NULL,
    skill_id integer NOT NULL,
    months_of_exp numeric NOT NULL,
    creation_time timestamp without time zone DEFAULT now() NOT NULL,
    last_mod_time timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.tbl_lms_userskill_map OWNER TO postgres;

--
-- Name: tbl_lms_assignments a_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_assignments ALTER COLUMN a_id SET DEFAULT nextval('public.tbl_lms_assignments_a_id_seq'::regclass);


--
-- Name: tbl_lms_attendance att_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_attendance ALTER COLUMN att_id SET DEFAULT nextval('public.tbl_lms_attendance_att_id_seq'::regclass);


--
-- Name: tbl_lms_batch batch_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_batch ALTER COLUMN batch_id SET DEFAULT nextval('public.tbl_lms_batch_batch_id_seq'::regclass);


--
-- Name: tbl_lms_class_sch cs_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_class_sch ALTER COLUMN cs_id SET DEFAULT nextval('public.tbl_lms_class_sch_cs_id_seq'::regclass);


--
-- Name: tbl_lms_program program_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_program ALTER COLUMN program_id SET DEFAULT nextval('public.tbl_lms_program_program_id_seq'::regclass);


--
-- Name: tbl_lms_skill_master skill_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_skill_master ALTER COLUMN skill_id SET DEFAULT nextval('public.tbl_lms_skill_master_skill_id_seq'::regclass);


--
-- Name: tbl_lms_submissions sub_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_submissions ALTER COLUMN sub_id SET DEFAULT nextval('public.tbl_lms_submissions_sub_id_seq'::regclass);


--
-- Name: tbl_lms_user_files user_file_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_user_files ALTER COLUMN user_file_id SET DEFAULT nextval('public.tbl_lms_user_files_user_file_id_seq'::regclass);


--
-- Name: tbl_lms_userrole_map user_role_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_userrole_map ALTER COLUMN user_role_id SET DEFAULT nextval('public.tbl_lms_userrole_map_user_role_id_seq'::regclass);

--
-- Name: tbl_lms_assignments assignment_id_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_assignments
    ADD CONSTRAINT assignment_id_pk PRIMARY KEY (a_id);


--
-- Name: tbl_lms_batch batch_id_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_batch
    ADD CONSTRAINT batch_id_pk PRIMARY KEY (batch_id);


--
-- Name: tbl_lms_class_sch cs_id_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_class_sch
    ADD CONSTRAINT cs_id_pk PRIMARY KEY (cs_id);


--
-- Name: tbl_lms_program program_id_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_program
    ADD CONSTRAINT program_id_pk PRIMARY KEY (program_id);


--
-- Name: tbl_lms_role role_id_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_role
    ADD CONSTRAINT role_id_pk PRIMARY KEY (role_id);


--
-- Name: tbl_lms_skill_master skill_id_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_skill_master
    ADD CONSTRAINT skill_id_pk PRIMARY KEY (skill_id);


--
-- Name: tbl_lms_batch tbl_lms_batch_batch_name_batch_program_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_batch
    ADD CONSTRAINT tbl_lms_batch_batch_name_batch_program_id_key UNIQUE (batch_name, batch_program_id);


--
-- Name: tbl_lms_program tbl_lms_program_program_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_program
    ADD CONSTRAINT tbl_lms_program_program_name_key UNIQUE (program_name);


--
-- Name: tbl_lms_user_files tbl_lms_user_files_user_id_user_file_type_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_user_files
    ADD CONSTRAINT tbl_lms_user_files_user_id_user_file_type_key UNIQUE (user_id, user_file_type);


--
-- Name: tbl_lms_userrole_map tbl_lms_userrole_map_user_id_role_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_userrole_map
    ADD CONSTRAINT tbl_lms_userrole_map_user_id_role_id_key UNIQUE (user_id, role_id);


--
-- Name: tbl_lms_userskill_map tbl_lms_userskill_map_user_id_skill_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_userskill_map
    ADD CONSTRAINT tbl_lms_userskill_map_user_id_skill_id_key UNIQUE (user_id, skill_id);


--
-- Name: tbl_lms_userbatch_map ub_map_id_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_userbatch_map
    ADD CONSTRAINT ub_map_id_pk PRIMARY KEY (ub_map_id);


--
-- Name: tbl_lms_user user_id_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_user
    ADD CONSTRAINT user_id_pk PRIMARY KEY (user_id);


--
-- Name: tbl_lms_user_login user_login_id_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_user_login
    ADD CONSTRAINT user_login_id_pk PRIMARY KEY (user_id);


--
-- Name: tbl_lms_userrole_map user_role_id_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_userrole_map
    ADD CONSTRAINT user_role_id_pk PRIMARY KEY (user_role_id);


--
-- Name: tbl_lms_userskill_map userskill_id_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_userskill_map
    ADD CONSTRAINT userskill_id_pk PRIMARY KEY (user_skill_id);


--
-- Name: tbl_lms_assignments a_batch_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_assignments
    ADD CONSTRAINT a_batch_id_fk FOREIGN KEY (a_batch_id) REFERENCES public.tbl_lms_batch(batch_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: tbl_lms_assignments a_grader_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_assignments
    ADD CONSTRAINT a_grader_id_fk FOREIGN KEY (a_grader_id) REFERENCES public.tbl_lms_user(user_id) ON UPDATE CASCADE;


--
-- Name: tbl_lms_batch batch_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_batch
    ADD CONSTRAINT batch_fk FOREIGN KEY (batch_program_id) REFERENCES public.tbl_lms_program(program_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: tbl_lms_userbatch_map batch_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_userbatch_map
    ADD CONSTRAINT batch_id_fk FOREIGN KEY (batch_id) REFERENCES public.tbl_lms_batch(batch_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: tbl_lms_class_sch cs_batch_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_class_sch
    ADD CONSTRAINT cs_batch_id_fk FOREIGN KEY (batch_id) REFERENCES public.tbl_lms_batch(batch_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: tbl_lms_attendance cs_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_attendance
    ADD CONSTRAINT cs_id_fk FOREIGN KEY (cs_id) REFERENCES public.tbl_lms_class_sch(cs_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: tbl_lms_class_sch staff_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_class_sch
    ADD CONSTRAINT staff_id_fk FOREIGN KEY (class_staff_id) REFERENCES public.tbl_lms_user(user_id) ON UPDATE CASCADE;


--
-- Name: tbl_lms_attendance staff_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_attendance
    ADD CONSTRAINT staff_id_fk FOREIGN KEY (student_id) REFERENCES public.tbl_lms_user(user_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: tbl_lms_submissions sub_a_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_submissions
    ADD CONSTRAINT sub_a_id_fk FOREIGN KEY (sub_a_id) REFERENCES public.tbl_lms_assignments(a_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: tbl_lms_submissions sub_student_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_submissions
    ADD CONSTRAINT sub_student_id_fk FOREIGN KEY (sub_student_id) REFERENCES public.tbl_lms_user(user_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: tbl_lms_userskill_map us_skill_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_userskill_map
    ADD CONSTRAINT us_skill_id_fk FOREIGN KEY (skill_id) REFERENCES public.tbl_lms_skill_master(skill_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: tbl_lms_userskill_map us_user_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_userskill_map
    ADD CONSTRAINT us_user_id_fk FOREIGN KEY (user_id) REFERENCES public.tbl_lms_user(user_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: tbl_lms_user_files user_file_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_user_files
    ADD CONSTRAINT user_file_fk FOREIGN KEY (user_id) REFERENCES public.tbl_lms_user(user_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: tbl_lms_user_login user_login_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_user_login
    ADD CONSTRAINT user_login_fk FOREIGN KEY (user_id) REFERENCES public.tbl_lms_user(user_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: tbl_lms_userrole_map user_role_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_userrole_map
    ADD CONSTRAINT user_role_fk FOREIGN KEY (user_id) REFERENCES public.tbl_lms_user(user_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: tbl_lms_userbatch_map user_role_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_userbatch_map
    ADD CONSTRAINT user_role_fk FOREIGN KEY (user_role_id) REFERENCES public.tbl_lms_userrole_map(user_role_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: tbl_lms_userrole_map user_role_fk1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tbl_lms_userrole_map
    ADD CONSTRAINT user_role_fk1 FOREIGN KEY (role_id) REFERENCES public.tbl_lms_role(role_id) ON UPDATE CASCADE ON DELETE CASCADE;


