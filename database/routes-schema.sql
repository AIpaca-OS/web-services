-- REVIEW FIRST; run once in local PostgreSQL database rumbo. Never execute against production blindly.
BEGIN;
CREATE TABLE IF NOT EXISTS rp_routes (
  id uuid PRIMARY KEY, version bigint NOT NULL DEFAULT 0,
  name varchar(160) NOT NULL, code varchar(40) NOT NULL UNIQUE,
  shift varchar(20) NOT NULL, status varchar(20) NOT NULL,
  vehicle_id varchar(100), departure_time time,
  schedule_confirmed boolean NOT NULL DEFAULT false
);
CREATE TABLE IF NOT EXISTS rp_route_stops (
  route_id uuid NOT NULL REFERENCES rp_routes(id), stop_id uuid NOT NULL,
  address varchar(350) NOT NULL, position integer NOT NULL,
  PRIMARY KEY(route_id, stop_id)
);
CREATE TABLE IF NOT EXISTS rp_route_schedules (
  route_id uuid NOT NULL REFERENCES rp_routes(id), day_of_week varchar(9) NOT NULL,
  departure_time time NOT NULL,
  PRIMARY KEY(route_id, day_of_week)
);
CREATE TABLE IF NOT EXISTS rp_route_assignments (
  route_id uuid NOT NULL REFERENCES rp_routes(id), assignment_id uuid NOT NULL,
  stop_id uuid NOT NULL, student_id varchar(100) NOT NULL,
  PRIMARY KEY(route_id, assignment_id), UNIQUE(route_id, student_id)
);
CREATE TABLE IF NOT EXISTS rp_trips (
  id uuid PRIMARY KEY, version bigint NOT NULL DEFAULT 0,
  route_id uuid NOT NULL REFERENCES rp_routes(id), service_date date NOT NULL,
  status varchar(20) NOT NULL, shift varchar(20) NOT NULL,
  cancel_reason varchar(500), UNIQUE(route_id, service_date)
);
CREATE TABLE IF NOT EXISTS rp_trip_passengers (
  trip_id uuid NOT NULL REFERENCES rp_trips(id), student_id varchar(100) NOT NULL,
  stop_id uuid NOT NULL, stop_position integer NOT NULL,
  stop_address varchar(350) NOT NULL, absent boolean NOT NULL DEFAULT false,
  stop_served boolean NOT NULL DEFAULT false,
  PRIMARY KEY(trip_id,student_id)
);
COMMIT;
