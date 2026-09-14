# Frontend API Reference

This document summarizes all currently available REST endpoints for frontend integration.

## Base URL

- `http://localhost:8080`

## API Prefix

- All endpoints are under `/api/v1`

## Common Response Pattern

Controllers use `ApiResponse` helpers, so responses are generally wrapped as:

```json
{
  "success": true,
  "message": "...",
  "data": {}
}
```

Note: exact keys can vary slightly by endpoint.

## Auth

### Register
- **POST** `/api/v1/auth/register`
- **Description:** Register a new user.
- **Body:** JSON user registration fields.

### Login
- **POST** `/api/v1/auth/login`
- **Description:** Login user.
- **Body:** JSON credentials.

### Logout
- **POST** `/api/v1/auth/logout`
- **Description:** Logout current session.

### Session
- **GET** `/api/v1/auth/session`
- **Description:** Get current session info.

## Users

### Current User
- **GET** `/api/v1/users/me`
- **Description:** Get current user profile.

### Update Current User
- **PATCH** `/api/v1/users/me`
- **Description:** Update current user profile.
- **Body:** JSON profile fields.

### Change Password
- **PATCH** `/api/v1/users/me/password`
- **Description:** Change current user password.
- **Body:** JSON password fields.

### List Users
- **GET** `/api/v1/users`
- **Description:** List users.
- **Query:** `search` (optional)

### Create User
- **POST** `/api/v1/users`
- **Description:** Create user.
- **Body:** JSON user fields.

### Get User by ID
- **GET** `/api/v1/users/{id}`
- **Description:** Get a specific user.
- **Path params:** `id` (integer)

### Update User by ID
- **PATCH** `/api/v1/users/{id}`
- **Description:** Update a specific user.
- **Path params:** `id` (integer)
- **Body:** JSON fields.

### Delete User by ID
- **DELETE** `/api/v1/users/{id}`
- **Description:** Delete a specific user.
- **Path params:** `id` (integer)

## Roles

### List Roles
- **GET** `/api/v1/roles`
- **Description:** List roles.

### Create Role
- **POST** `/api/v1/roles`
- **Description:** Create role.
- **Body:** JSON role fields.

### Get Role by ID
- **GET** `/api/v1/roles/{id}`
- **Description:** Get role by ID.
- **Path params:** `id` (integer)

### Update Role by ID
- **PATCH** `/api/v1/roles/{id}`
- **Description:** Update role.
- **Path params:** `id` (integer)
- **Body:** JSON fields.

### Delete Role by ID
- **DELETE** `/api/v1/roles/{id}`
- **Description:** Delete role.
- **Path params:** `id` (integer)

## Airports

### List Airports
- **GET** `/api/v1/airports`
- **Description:** List airports.

### Create Airport
- **POST** `/api/v1/airports`
- **Description:** Create airport.
- **Body:** JSON airport fields.

### Get Airport by Code
- **GET** `/api/v1/airports/{code}`
- **Description:** Get airport by IATA code.
- **Path params:** `code` (string, e.g. `CGK`)

### Update Airport
- **PATCH** `/api/v1/airports/{code}`
- **Description:** Update airport.
- **Path params:** `code`
- **Body:** JSON fields.

### Delete Airport
- **DELETE** `/api/v1/airports/{code}`
- **Description:** Delete airport.
- **Path params:** `code`

## Routes

### List Routes
- **GET** `/api/v1/routes`
- **Description:** List routes.

### Create Route
- **POST** `/api/v1/routes`
- **Description:** Create route.
- **Body:** JSON route fields.

### Get Route
- **GET** `/api/v1/routes/{from}/{to}`
- **Description:** Get route by origin and destination.
- **Path params:** `from`, `to` (airport codes)

### Update Route
- **PATCH** `/api/v1/routes/{from}/{to}`
- **Description:** Update route.
- **Path params:** `from`, `to`
- **Body:** JSON fields.

### Delete Route
- **DELETE** `/api/v1/routes/{from}/{to}`
- **Description:** Delete route.
- **Path params:** `from`, `to`

### Route Distance
- **GET** `/api/v1/routes/{from}/{to}/distance`
- **Description:** Get route distance.
- **Path params:** `from`, `to`

### Route Optimization
- **GET** `/api/v1/routes/optimize`
- **Description:** Compute optimized route.
- **Query:**
  - `from` (required)
  - `to` (required)
  - `type` (optional, default `cheapest`)

## Flights

### List Flights
- **GET** `/api/v1/flights`
- **Description:** List flights.

### Create Flight
- **POST** `/api/v1/flights`
- **Description:** Create flight.
- **Body:** JSON flight fields.

### Get Flight
- **GET** `/api/v1/flights/{flightId}`
- **Description:** Get flight by ID.
- **Path params:** `flightId` (integer)

### Update Flight
- **PATCH** `/api/v1/flights/{flightId}`
- **Description:** Update flight.
- **Path params:** `flightId` (integer)
- **Body:** JSON fields.

### Delete Flight
- **DELETE** `/api/v1/flights/{flightId}`
- **Description:** Delete flight.
- **Path params:** `flightId` (integer)

### Search Flights
- **GET** `/api/v1/flights/search`
- **Description:** Search flights.
- **Query:** `from`, `to`, `date` (all optional)

### Flight Schedule
- **GET** `/api/v1/flights/schedule`
- **Description:** Get flight schedule.
- **Query:** `from`, `to`, `date`, `start`, `end` (optional)

### Flight Lookup
- **GET** `/api/v1/flights/lookup/{flightId}`
- **Description:** Lookup a flight.
- **Path params:** `flightId` (integer)

### Waitlist - List
- **GET** `/api/v1/flights/{flightId}/waitlist`
- **Description:** Get flight waitlist.
- **Path params:** `flightId` (integer)

### Waitlist - Add
- **POST** `/api/v1/flights/{flightId}/waitlist`
- **Description:** Add booking to waitlist.
- **Path params:** `flightId` (integer)
- **Body:** JSON waitlist fields.

### Waitlist - Promote
- **POST** `/api/v1/flights/{flightId}/waitlist/promote`
- **Description:** Promote waitlisted booking.
- **Path params:** `flightId` (integer)

### Waitlist - Remove
- **DELETE** `/api/v1/flights/{flightId}/waitlist/{bookingId}`
- **Description:** Remove booking from waitlist.
- **Path params:** `flightId`, `bookingId` (integer)

## Radar

### Radar Feed
- **GET** `/api/v1/flights/radar`
- **Description:** Get radar snapshot feed.

### Radar Provider Status
- **GET** `/api/v1/flights/radar/status`
- **Description:** Get radar provider status.

### Radar for Flight
- **GET** `/api/v1/flights/radar/{flightId}`
- **Description:** Get radar data for a specific flight.
- **Path params:** `flightId` (integer)

## Bookings

### List Bookings
- **GET** `/api/v1/bookings`
- **Description:** List bookings.

### Create Booking
- **POST** `/api/v1/bookings`
- **Description:** Create booking.
- **Body:** JSON booking fields.

### Get Booking
- **GET** `/api/v1/bookings/{bookingId}`
- **Description:** Get booking by ID.
- **Path params:** `bookingId` (integer)

### Update Booking
- **PATCH** `/api/v1/bookings/{bookingId}`
- **Description:** Update booking.
- **Path params:** `bookingId` (integer)
- **Body:** JSON fields.

### Delete Booking
- **DELETE** `/api/v1/bookings/{bookingId}`
- **Description:** Delete booking.
- **Path params:** `bookingId` (integer)

### Cancel Booking
- **POST** `/api/v1/bookings/{bookingId}/cancel`
- **Description:** Cancel booking.
- **Path params:** `bookingId` (integer)

### Undo Cancel
- **POST** `/api/v1/bookings/{bookingId}/undo`
- **Description:** Undo booking cancellation.
- **Path params:** `bookingId` (integer)

## Passengers

### List Passengers
- **GET** `/api/v1/passengers`
- **Description:** List passengers.
- **Query:** `search` (optional)

### Create Passenger
- **POST** `/api/v1/passengers`
- **Description:** Create passenger.
- **Body:** JSON passenger fields.

### Get Passenger
- **GET** `/api/v1/passengers/{id}`
- **Description:** Get passenger by ID.
- **Path params:** `id` (integer)

### Update Passenger
- **PATCH** `/api/v1/passengers/{id}`
- **Description:** Update passenger.
- **Path params:** `id` (integer)
- **Body:** JSON fields.

### Delete Passenger
- **DELETE** `/api/v1/passengers/{id}`
- **Description:** Delete passenger.
- **Path params:** `id` (integer)

### Passenger Bookings
- **GET** `/api/v1/passengers/{passengerId}/bookings`
- **Description:** Get bookings for passenger.
- **Path params:** `passengerId` (integer)

### Passenger Booking History
- **GET** `/api/v1/passengers/{passengerId}/bookings/history`
- **Description:** Get booking history for passenger.
- **Path params:** `passengerId` (integer)

## Health

### Service Health
- **GET** `/api/v1/health`
- **Description:** Check service health.

### Database Health
- **GET** `/api/v1/health/database`
- **Description:** Check database connectivity/health.

### Providers Health
- **GET** `/api/v1/health/providers`
- **Description:** Check external provider health.

## Analytics

### Dashboard Analytics
- **GET** `/api/v1/analytics/dashboard`
- **Description:** Dashboard summary metrics.

### Booking Analytics
- **GET** `/api/v1/analytics/bookings`
- **Description:** Booking statistics.

### Load Factor Analytics
- **GET** `/api/v1/analytics/load-factors`
- **Description:** Route/load factor metrics.

### Revenue Analytics
- **GET** `/api/v1/analytics/revenue`
- **Description:** Revenue metrics.

### Flight Status Analytics
- **GET** `/api/v1/analytics/flight-status`
- **Description:** Flight status distribution.

### Benchmark Report
- **GET** `/api/v1/analytics/benchmarks`
- **Description:** Benchmark results.

### Run Benchmarks
- **POST** `/api/v1/analytics/benchmarks/run`
- **Description:** Trigger benchmark execution.

