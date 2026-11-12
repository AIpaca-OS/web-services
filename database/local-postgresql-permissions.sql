-- Rumbo / Open Source - local development database permissions
-- Execute in pgAdmin Query Tool as PostgreSQL table owner or administrator.
-- Database: rumbo. This script does not delete data or alter table structures.
-- Only for the dedicated LOCAL development database, not a production database.
--
-- Important: 'rumbo' below is the Spring datasource username configured in
-- application.properties by default. Change it if you use a different role.
BEGIN;

DO $$
BEGIN
    IF current_database() <> 'rumbo' THEN
        RAISE EXCEPTION 'Wrong database: %. Expected local database rumbo.',
            current_database();
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'rumbo') THEN
        RAISE EXCEPTION 'Database role rumbo does not exist. Check SPRING_DATASOURCE_USERNAME.';
    END IF;
END $$;

GRANT CONNECT ON DATABASE rumbo TO rumbo;
GRANT USAGE ON SCHEMA public TO rumbo;

-- Fixes SQLSTATE 42501 on notifications, plans, subscriptions and other local
-- domain tables created by an administrator rather than by the app role.
GRANT SELECT, INSERT, UPDATE, DELETE
ON ALL TABLES IN SCHEMA public TO rumbo;

-- JPA IDENTITY / serial columns can use PostgreSQL sequences when inserting.
GRANT USAGE, SELECT, UPDATE
ON ALL SEQUENCES IN SCHEMA public TO rumbo;

COMMIT;

-- Checks: true for each table indicates the required SELECT privilege.
SELECT current_database() AS database_name, current_user AS script_executor;
SELECT t.table_name,
       has_table_privilege('rumbo', format('%I.%I', t.table_schema, t.table_name), 'SELECT') AS can_select,
       has_table_privilege('rumbo', format('%I.%I', t.table_schema, t.table_name), 'INSERT') AS can_insert,
       has_table_privilege('rumbo', format('%I.%I', t.table_schema, t.table_name), 'UPDATE') AS can_update,
       has_table_privilege('rumbo', format('%I.%I', t.table_schema, t.table_name), 'DELETE') AS can_delete
FROM information_schema.tables AS t
WHERE t.table_schema = 'public'
  AND t.table_type = 'BASE TABLE'
  AND t.table_name IN ('notifications', 'plans', 'subscriptions', 'vc_vehicles')
ORDER BY t.table_name;
