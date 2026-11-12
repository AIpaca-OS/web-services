-- Run against PostgreSQL before deploying with SPRING_JPA_DDL_AUTO=validate.
-- Student-linked notices must never be exposed by unauthenticated public endpoints.
CREATE TABLE IF NOT EXISTS rp_student_journey_notices (
    id BIGSERIAL PRIMARY KEY,
    student_id VARCHAR(100) NOT NULL,
    route_id UUID NOT NULL,
    trip_id UUID,
    event_key VARCHAR(220) NOT NULL UNIQUE,
    kind VARCHAR(40) NOT NULL,
    title VARCHAR(160) NOT NULL,
    message VARCHAR(800) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    read_at TIMESTAMPTZ
);
CREATE INDEX IF NOT EXISTS ix_student_journey_notices_student
    ON rp_student_journey_notices(student_id);
