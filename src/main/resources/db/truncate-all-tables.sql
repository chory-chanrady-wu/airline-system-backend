-- Truncate all application tables in PostgreSQL.
-- Keeps table structures, removes all rows, and resets identity sequences.

BEGIN;

TRUNCATE TABLE
    role_permissions,
    waitlist_entries,
    booking_history,
    bookings,
    flight_statuses,
    flights,
    aircraft,
    itineraries,
    route_load_factors,
    routes,
    analytics_snapshots,
    radar_snapshots,
    benchmark_results,
    notifications,
    audit_logs,
    passenger_profiles,
    users,
    roles,
    permissions,
    airlines,
    airports
RESTART IDENTITY CASCADE;

COMMIT;

