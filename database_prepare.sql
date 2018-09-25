--Indexes
CREATE INDEX aenaflight_source_idx  ON aenaflight_source (flight_code , flight_number );
CREATE INDEX aenaflight_2017_idx   ON aenaflight_2017_01 (flight_icao_code, flight_number);
--Indexes

--Constraints
ALTER TABLE aenaflight_source  ADD CONSTRAINT aenaflight_source_data_unique UNIQUE (flight_code, flight_number, schd_dep_lt);
--Constraints

--aenaflight_source sequence
CREATE SEQUENCE aenaflight_source_id_seq;
-- ALTER SEQUENCE aenaflight_source_id_seq OWNER TO postgres;
--aenaflight_source sequence

--View  groups_to_transforme
--/*
CREATE OR REPLACE VIEW groups_to_transforme AS
SELECT row_number() OVER () AS id,    a_from.flight_icao_code,    a_from.flight_number,    a_from.schd_dep_only_date_lt
FROM aenaflight_2017_01 a_from
WHERE NOT (EXISTS ( SELECT 1
                    FROM aenaflight_source a_to
                    WHERE a_from.flight_icao_code = a_to.flight_code AND a_from.flight_number = a_to.flight_number
                          AND (
                            (a_to.schd_dep_lt is null AND (NULLIF(a_from.schd_dep_only_date_lt,'')=''))
                            or
                            to_date(a_from.schd_dep_only_date_lt, 'dd/MM/yy') = date_trunc('day', a_to.schd_dep_lt))))
GROUP BY a_from.flight_icao_code, a_from.flight_number, a_from.schd_dep_only_date_lt;
--*/

/*
CREATE OR REPLACE VIEW groups_to_transforme AS
select a_from.id, a_from.flight_icao_code, a_from.flight_number, a_from.schd_dep_only_date_lt from
(
SELECT row_number() OVER () AS id, flight_icao_code,    flight_number,    schd_dep_only_date_lt
FROM aenaflight_2017_01
GROUP BY flight_icao_code, flight_number, schd_dep_only_date_lt
) a_from
LEFT JOIN aenaflight_source a_to on a_from.flight_icao_code = a_to.flight_code AND a_from.flight_number = a_to.flight_number
AND (a_to.schd_dep_lt IS NULL AND (NULLIF(a_from.schd_dep_only_date_lt, '') = '') OR to_date(a_from.schd_dep_only_date_lt, 'dd/MM/yy') = date_trunc('day', a_to.schd_dep_lt))
AND a_to.id is null;
-- */

/*
CREATE OR REPLACE VIEW groups_to_transforme AS
SELECT row_number() OVER () AS id, flight_icao_code,    flight_number,    schd_dep_only_date_lt
FROM aenaflight_2017_01
where id not in (select id from aena_transformed)
GROUP BY flight_icao_code, flight_number, schd_dep_only_date_lt
 */

-- ALTER TABLE groups_to_transforme  OWNER TO postgres;
-- GRANT ALL ON TABLE groups_to_transforme TO postgres;
-- GRANT ALL ON TABLE groups_to_transforme TO PUBLIC;
--View  groups_to_transforme



