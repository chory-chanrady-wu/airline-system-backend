CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role_id INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('Active', 'Inactive')),
    user_type VARCHAR(255) DEFAULT 'SYSTEM_USER' NOT NULL CHECK (user_type IN ('SYSTEM_USER', 'PASSENGER')),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS roles (
    id INTEGER PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    permissions TEXT[] NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS permissions (
    id INTEGER PRIMARY KEY,
    code VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS role_permissions (
    role_id INTEGER NOT NULL,
    permission_code VARCHAR(255) NOT NULL,
    PRIMARY KEY (role_id, permission_code)
);

CREATE TABLE IF NOT EXISTS airports (
    code VARCHAR(3) PRIMARY KEY,
    city VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
    latitude DECIMAL(9,6) NOT NULL,
    longitude DECIMAL(9,6) NOT NULL,
    timezone VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS routes (
    id INTEGER PRIMARY KEY,
    from_airport_code VARCHAR(3) NOT NULL,
    to_airport_code VARCHAR(3) NOT NULL,
    distance_km INTEGER NOT NULL,
    duration_minutes INTEGER NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS airlines (
    id INTEGER PRIMARY KEY,
    code VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    logo_url TEXT,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS aircraft (
    id INTEGER PRIMARY KEY,
    registration_number VARCHAR(255) NOT NULL UNIQUE,
    model VARCHAR(255) NOT NULL,
    seat_capacity INTEGER NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS flights (
    id INTEGER PRIMARY KEY,
    flight_number VARCHAR(255) NOT NULL UNIQUE,
    airline_id INTEGER NOT NULL,
    aircraft_id INTEGER NOT NULL,
    route_id INTEGER NOT NULL,
    from_airport_code VARCHAR(3) NOT NULL,
    to_airport_code VARCHAR(3) NOT NULL,
    departure_time TIMESTAMP NOT NULL,
    arrival_time TIMESTAMP NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    seat_capacity INTEGER NOT NULL,
    seats_available INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS flight_statuses (
    id INTEGER PRIMARY KEY,
    flight_id INTEGER NOT NULL,
    status VARCHAR(255) NOT NULL,
    delay_minutes INTEGER,
    gate VARCHAR(255),
    terminal VARCHAR(255),
    remarks TEXT,
    recorded_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS passenger_profiles (
    id INTEGER PRIMARY KEY,
    user_id INTEGER NOT NULL UNIQUE,
    passport_number VARCHAR(255) NOT NULL,
    nationality VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    date_of_birth DATE NOT NULL,
    emergency_contact VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS bookings (
    id INTEGER PRIMARY KEY,
    booking_reference VARCHAR(255) NOT NULL UNIQUE,
    passenger_id INTEGER NOT NULL,
    flight_id INTEGER NOT NULL,
    seat_number VARCHAR(255),
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('Confirmed', 'Waitlisted', 'Cancelled')),
    booked_at TIMESTAMP NOT NULL,
    cancelled_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS waitlist_entries (
    id INTEGER PRIMARY KEY,
    flight_id INTEGER NOT NULL,
    passenger_id INTEGER NOT NULL,
    booking_id INTEGER NOT NULL,
    position INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL,
    joined_at TIMESTAMP NOT NULL,
    offered_at TIMESTAMP,
    expires_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS booking_history (
    id INTEGER PRIMARY KEY,
    booking_id INTEGER NOT NULL,
    passenger_id INTEGER NOT NULL,
    action VARCHAR(20) NOT NULL,
    previous_status VARCHAR(255),
    new_status VARCHAR(255) NOT NULL,
    performed_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS itineraries (
    id INTEGER PRIMARY KEY,
    passenger_id INTEGER NOT NULL,
    booking_id INTEGER,
    origin_airport_code VARCHAR(3) NOT NULL,
    destination_airport_code VARCHAR(3) NOT NULL,
    path TEXT[] NOT NULL,
    layovers TEXT[] NOT NULL,
    stops INTEGER NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    duration_minutes INTEGER NOT NULL,
    algorithm VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS radar_snapshots (
    id INTEGER PRIMARY KEY,
    provider VARCHAR(255) NOT NULL,
    aircraft_count INTEGER NOT NULL,
    fetched_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS analytics_snapshots (
    id INTEGER PRIMARY KEY,
    total_flights BIGINT NOT NULL,
    total_bookings BIGINT NOT NULL,
    total_passengers BIGINT NOT NULL,
    total_airports BIGINT NOT NULL,
    total_routes BIGINT NOT NULL,
    confirmed_bookings BIGINT NOT NULL,
    cancelled_bookings BIGINT NOT NULL,
    waitlisted_bookings BIGINT NOT NULL,
    total_capacity BIGINT NOT NULL,
    occupied_seats BIGINT NOT NULL,
    available_seats BIGINT NOT NULL,
    load_factor DECIMAL(5,2) NOT NULL,
    confirmed_revenue DECIMAL(12,2) NOT NULL,
    generated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS route_load_factors (
    id INTEGER PRIMARY KEY,
    route_id INTEGER NOT NULL,
    total_flights BIGINT NOT NULL,
    total_capacity BIGINT NOT NULL,
    occupied_seats BIGINT NOT NULL,
    available_seats BIGINT NOT NULL,
    load_factor DECIMAL(5,2) NOT NULL,
    calculated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS benchmark_results (
    id INTEGER PRIMARY KEY,
    structure VARCHAR(20) NOT NULL,
    operation VARCHAR(255) NOT NULL,
    input_size BIGINT NOT NULL,
    runtime_milliseconds BIGINT NOT NULL,
    theoretical_complexity VARCHAR(255) NOT NULL,
    executed_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS notifications (
    id INTEGER PRIMARY KEY,
    user_id INTEGER NOT NULL,
    type VARCHAR(20) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    read BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id INTEGER PRIMARY KEY,
    user_id INTEGER NOT NULL,
    action VARCHAR(255) NOT NULL,
    entity_type VARCHAR(255) NOT NULL,
    entity_id VARCHAR(255) NOT NULL,
    metadata JSONB,
    created_at TIMESTAMP NOT NULL
);
