--Indexes
CREATE INDEX aenaflight_join_idx  ON aenaflight_source (flight_code , flight_number );
CREATE INDEX aenaflight_2017_group_idx  ON aenaflight_2017_01 (flight_icao_code, flight_number );
--Indexes


--View  groups_to_transforme
SELECT row_number() OVER () AS id,    a_from.flight_icao_code,    a_from.flight_number,    a_from.schd_dep_only_date_lt
FROM aenaflight_2017_01 a_from
WHERE NOT (EXISTS ( SELECT 1
                    FROM aenaflight_source a_to
                    WHERE a_from.flight_icao_code = a_to.flight_code AND a_from.flight_number = a_to.flight_number
                          AND (
                            (a_to.schd_dep_lt is null AND (a_from.schd_dep_only_date_lt is null or a_from.schd_dep_only_date_lt = ''))
                            or
                            to_date(a_from.schd_dep_only_date_lt, 'dd/MM/yy') = date_trunc('day', a_to.schd_dep_lt))))
GROUP BY a_from.flight_icao_code, a_from.flight_number, a_from.schd_dep_only_date_lt;

ALTER TABLE groups_to_transforme  OWNER TO postgres;
GRANT ALL ON TABLE groups_to_transforme TO postgres;
GRANT ALL ON TABLE groups_to_transforme TO PUBLIC;
--View  groups_to_transforme



--aenaflight_source sequence
CREATE SEQUENCE public.aenaflight_source_id_seq;
ALTER SEQUENCE public.aenaflight_source_id_seq OWNER TO postgres;
--aenaflight_source sequence