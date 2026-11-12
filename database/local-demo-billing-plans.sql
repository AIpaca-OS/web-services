-- Local Rumbo AV2 demonstration catalog. NOT real commercial prices or offers.
-- Run only on the local PostgreSQL database "rumbo" with an owner/admin role.
-- The two plans are intentionally named DEMO so nobody mistakes them for pricing.
-- Idempotent: does not overwrite existing plans created by Billing.
BEGIN;
DO $$
BEGIN
    IF current_database() <> 'rumbo' THEN
        RAISE EXCEPTION 'Expected local rumbo database, found %', current_database();
    END IF;
END $$;

INSERT INTO public.plans (
    name, description, reference_price, currency,
    billing_period_months, vehicle_limit, active, created_at, updated_at
)
SELECT 'DEMO Básico', 'Plan de demostración para un vehículo. Precio referencial, no comercial.',
       29.90, 'PEN', 1, 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM public.plans WHERE name = 'DEMO Básico');

INSERT INTO public.plans (
    name, description, reference_price, currency,
    billing_period_months, vehicle_limit, active, created_at, updated_at
)
SELECT 'DEMO Flota', 'Plan de demostración para hasta tres vehículos. Precio referencial, no comercial.',
       59.90, 'PEN', 1, 3, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM public.plans WHERE name = 'DEMO Flota');
COMMIT;

SELECT id, name, reference_price, currency, active FROM public.plans ORDER BY id;
