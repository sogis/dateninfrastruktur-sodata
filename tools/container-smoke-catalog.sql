-- Synthetic data for the subset of the public view contract exercised by the smoke test.
CREATE ROLE di_public LOGIN PASSWORD :'catalog_password';
CREATE SCHEMA api_public;

CREATE VIEW api_public.offices AS
SELECT '00000000-0000-0000-0000-000000000001'::uuid AS id,
       'smoke-office'::text AS identifier, 'Smoke Office'::text AS name,
       NULL::text AS abbreviation, NULL::text AS email, NULL::text AS website;

CREATE VIEW api_public.datasets AS
SELECT '00000000-0000-0000-0000-000000000002'::uuid AS id,
       'container-smoke'::text AS identifier, 'standalone'::text AS kind,
       'Sodata Container Smoke Dataset'::text AS title,
       'Read from the PostgreSQL smoke fixture.'::text AS description,
       'open'::text AS access_level, CURRENT_DATE AS modified,
       offices.id AS owner_office, offices.id AS publisher_office
FROM api_public.offices;

CREATE VIEW api_public.issues AS
SELECT id, id AS dataset_id, NULL::text AS issue_key, modified
FROM api_public.datasets WHERE false;

CREATE VIEW api_public.themes AS
SELECT id AS dataset_id, NULL::text AS theme FROM api_public.datasets WHERE false;

CREATE VIEW api_public.keywords AS
SELECT id AS dataset_id, NULL::text AS keyword FROM api_public.datasets WHERE false;

GRANT USAGE ON SCHEMA api_public TO di_public;
GRANT SELECT ON ALL TABLES IN SCHEMA api_public TO di_public;
