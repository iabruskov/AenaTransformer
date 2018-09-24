--View  groups_to_transforme
CREATE OR REPLACE VIEW groups_to_transforme AS
 SELECT row_number() OVER () AS id,
    a_from.flight_icao_code,
    a_from.flight_number,
    a_from.schd_dep_only_date_lt
   FROM aenaflight_2017_01 a_from
  WHERE NOT (EXISTS ( SELECT 1
           FROM aenaflight_source a_to
          WHERE a_from.flight_icao_code::text = a_to.flight_code::text AND a_from.flight_number::text = a_to.flight_number::text AND a_from.schd_dep_only_date_lt::text = to_char(a_to.schd_dep_lt, 'MM/dd/yy'::text)))
  GROUP BY a_from.flight_number, a_from.flight_icao_code, a_from.schd_dep_only_date_lt;

ALTER TABLE groups_to_transforme  OWNER TO postgres;
GRANT ALL ON TABLE groups_to_transforme TO postgres;
GRANT ALL ON TABLE groups_to_transforme TO PUBLIC;
--View  groups_to_transforme

--aenaflight_source sequence
CREATE SEQUENCE public.aenaflight_source_id_seq;
ALTER SEQUENCE public.aenaflight_source_id_seq OWNER TO postgres;
--aenaflight_source sequence