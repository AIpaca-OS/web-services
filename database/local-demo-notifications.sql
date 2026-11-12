-- Test-only notification examples to verify the shared UI and API.
-- These are generic notices, NOT real messages to authorized guardians.
-- Run in pgAdmin on the local database rumbo only, after notifications-schema.sql.
BEGIN;
DO $$
BEGIN
    IF current_database() <> 'rumbo' THEN
        RAISE EXCEPTION 'Expected local rumbo database, found %', current_database();
    END IF;
END $$;

INSERT INTO public.notifications (type, priority, title, message, created_at, read_at, route_id, student_id)
SELECT 'info', 'regular',
       'DEMO: Notificaciones disponibles',
       'Aviso general de prueba. Los avisos privados necesitan IAM y Profiles.',
       CURRENT_TIMESTAMP, NULL, NULL, NULL
WHERE NOT EXISTS (
    SELECT 1 FROM public.notifications
    WHERE title = 'DEMO: Notificaciones disponibles'
);
COMMIT;

SELECT id, title, created_at FROM public.notifications ORDER BY created_at DESC;
