# Flight Booker Backend

Spring Boot backend for a flight booking management system. It exposes a REST API for authentication, users, roles, airports, routes, flights, bookings, passengers, radar status, health checks, and analytics.

## What this backend does

- manages users and roles
- stores airports, routes, airlines, aircraft, and flights
- creates and updates bookings
- supports passenger profiles with or without a linked user account
- exposes simple analytics and health endpoints
- seeds demo data on first startup when the database is empty

> Current public API is REST-first. The project includes GraphQL dependencies/codegen setup in `pom.xml`, but there is no active GraphQL schema or resolver under `src/main/resources/graphql`, so the REST endpoints are the main interface right now.

## Tech stack

- Java 21
- Spring Boot `4.1.1`
- Spring Data JPA
- PostgreSQL for local/dev runtime
- H2 for tests
- Maven Wrapper (`mvnw`, `mvnw.cmd`)
- Docker / Docker Compose support

## Project structure

```text
backend/
├── src/main/java/com/example/backend/
│   ├── controller/      # REST controllers
│   ├── dto/             # request/response DTOs
│   ├── entity/          # JPA entities
│   ├── repository/      # Spring Data repositories
│   ├── service/         # business logic
│   └── util/            # seed data loader and helpers
├── src/main/resources/
│   ├── application.properties
│   ├── schema.sql
│   └── db/              # helper SQL scripts
├── compose.yaml
├── Dockerfile
├── API_PAYLOADS.md      # detailed endpoint payload examples
├── frontend-api.md      # endpoint summary for frontend integration
└── DATABASE_MIGRATION_GUIDE.md
```

## Prerequisites

Choose one of these ways to run the backend:

### Option A: Docker Compose
- Docker Desktop (or Docker Engine + Compose)

### Option B: Local development
- Java 21
- PostgreSQL running locally or in a container

## Default runtime settings

- API base URL: `http://localhost:8080`
- API prefix: `/api/v1`
- Default PostgreSQL port: `5432`
- App port: `8080`

## Quick start with Docker Compose

This is the fastest way to run the backend and database together.

```powershell
docker compose up --build
```

Services defined in `compose.yaml`:

- `postgres` → PostgreSQL 16 on `localhost:5432`
- `app` → Spring Boot backend on `localhost:8080`

Then verify the backend is up:

```powershell
curl.exe http://localhost:8080/api/v1/health
```

## Quick start for local development

### 1) Create a `.env` file in the project root

The backend loads database settings from a root-level `.env` file through `spring.config.import`.

Create `backend/.env` with:

```properties
DB_URL=jdbc:postgresql://localhost:5432/flightdb
DB_USERNAME=postgres
DB_PASSWORD=postgres
DB_DRIVER=org.postgresql.Driver
```

### 2) Start PostgreSQL

If you want to use the included container only for the database:

```powershell
docker compose up -d postgres
```

### 3) Run the backend

```powershell
.\mvnw.cmd spring-boot:run
```

### 4) Check health

```powershell
curl.exe http://localhost:8080/api/v1/health
```

## Build and test

### Run tests

Verified in this workspace:

```powershell
.\mvnw.cmd test
```

### Build a jar

```powershell
.\mvnw.cmd clean package
```

### Run the packaged jar

```powershell
java -jar .\target\backend-0.0.1-SNAPSHOT.jar
```

## Database behavior

- Runtime configuration comes from `src/main/resources/application.properties`
- Schema updates use `spring.jpa.hibernate.ddl-auto=update`
- The app does **not** execute `schema.sql` automatically because `spring.sql.init.mode=never`
- On first startup with an empty database, demo records are inserted by `SeedDataLoader`

Useful SQL helper scripts live in `src/main/resources/db/`, including:

- `reset-postgres-schema.sql`
- `truncate-all-tables.sql`
- `migrate-make-user-optional.sql`

If your local schema is stale, see `DATABASE_MIGRATION_GUIDE.md`.

## Seeded demo data

When the database is empty, the backend seeds:

- roles: Administrator, Operations Agent, Passenger
- airports: `CGK`, `SIN`, `KUL`
- airline: Garuda Indonesia (`GA`)
- aircraft: Boeing 737-800
- routes and sample flights
- one passenger profile and sample bookings

### Seeded login accounts

These accounts are created by `SeedDataLoader`:

| Role | Email | Password |
|---|---|---|
| Administrator | `admin@flightbooker.local` | `Admin@123` |
| Operations Agent | `agent@flightbooker.local` | `Agent@123` |
| Passenger seed user | `raka@flightbooker.local` | `Passenger@123` |

Important:

- the seeded passenger user exists in the database
- passenger login is currently blocked by `AuthServiceImpl`
- use the admin or agent account for login testing

## Authentication notes

Authentication is currently lightweight and intended for development/demo usage.

What is implemented:

- `POST /api/v1/auth/login` returns an access token and refresh token
- `POST /api/v1/auth/refresh` issues a new access token
- `POST /api/v1/auth/logout` returns a success response
- `GET /api/v1/auth/session` returns a derived current-session view

Important limitations to know before integrating a frontend:

- there is no Spring Security filter chain enforcing bearer tokens on requests
- auth tokens are generated by the service layer and refresh tokens are kept in memory
- `register` is currently disabled for passengers
- endpoints such as `/users/me` use service-side “current user” logic, not request-scoped authentication

So: treat the current auth flow as **demo/dev behavior**, not production-ready security.

## How to use the backend

A practical first-use flow is:

1. start PostgreSQL and the backend
2. verify health with `/api/v1/health`
3. login as admin or agent
4. list flights and passengers
5. create a passenger profile if needed
6. create a booking
7. cancel/undo bookings or manage waitlists
8. use analytics endpoints for dashboards

## Common API flows

### 1) Login

```powershell
curl.exe -X POST http://localhost:8080/api/v1/auth/login -H "Content-Type: application/json" -d "{\"email\":\"admin@flightbooker.local\",\"password\":\"Admin@123\"}"
```

### 2) Refresh token

Replace `<refresh-token>` with the value returned by login.

```powershell
curl.exe -X POST http://localhost:8080/api/v1/auth/refresh -H "Content-Type: application/json" -d "{\"refreshToken\":\"<refresh-token>\"}"
```

### 3) List flights

```powershell
curl.exe http://localhost:8080/api/v1/flights
```

### 4) Search flights

```powershell
curl.exe "http://localhost:8080/api/v1/flights/search?from=CGK&to=SIN"
```

### 5) Create a passenger profile

`userId` is optional. `fullName` is required.

```powershell
curl.exe -X POST http://localhost:8080/api/v1/passengers -H "Content-Type: application/json" -d "{\"fullName\":\"John Doe\",\"passportNumber\":\"AB123456\",\"nationality\":\"Cambodian\",\"phone\":\"+85512345678\",\"dateOfBirth\":\"1998-06-15\",\"emergencyContact\":\"Jane Doe (+85598765432)\"}"
```

### 6) Create a booking

Use a real `passengerId` and `flightId` from the API.

```powershell
curl.exe -X POST http://localhost:8080/api/v1/bookings -H "Content-Type: application/json" -d "{\"passengerId\":\"1\",\"flightId\":\"1\",\"seatNumber\":\"12A\",\"amount\":145.00,\"currency\":\"USD\",\"status\":\"Confirmed\"}"
```

### 7) Cancel a booking

```powershell
curl.exe -X POST http://localhost:8080/api/v1/bookings/BK-20260914-001/cancel
```

### 8) Undo booking cancellation

```powershell
curl.exe -X POST http://localhost:8080/api/v1/bookings/BK-20260914-001/undo
```

## Main endpoint groups

### Auth

- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- `POST /api/v1/auth/logout`
- `GET /api/v1/auth/session`

### Users

- `GET /api/v1/users/me`
- `PATCH /api/v1/users/me`
- `PATCH /api/v1/users/me/password`
- `GET /api/v1/users`
- `POST /api/v1/users`
- `GET /api/v1/users/{id}`
- `PATCH /api/v1/users/{id}`
- `DELETE /api/v1/users/{id}`

### Roles

- `GET /api/v1/roles`
- `POST /api/v1/roles`
- `GET /api/v1/roles/{id}`
- `PATCH /api/v1/roles/{id}`
- `DELETE /api/v1/roles/{id}`

### Airports

- `GET /api/v1/airports`
- `POST /api/v1/airports`
- `GET /api/v1/airports/{code}`
- `PATCH /api/v1/airports/{code}`
- `DELETE /api/v1/airports/{code}`

### Routes

- `GET /api/v1/routes`
- `POST /api/v1/routes`
- `GET /api/v1/routes/{from}/{to}`
- `PATCH /api/v1/routes/{from}/{to}`
- `DELETE /api/v1/routes/{from}/{to}`
- `GET /api/v1/routes/{from}/{to}/distance`
- `GET /api/v1/routes/optimize?from=...&to=...&type=cheapest`

### Flights and waitlist

- `GET /api/v1/flights`
- `POST /api/v1/flights`
- `GET /api/v1/flights/{flightId}`
- `PATCH /api/v1/flights/{flightId}`
- `DELETE /api/v1/flights/{flightId}`
- `GET /api/v1/flights/search`
- `GET /api/v1/flights/schedule`
- `GET /api/v1/flights/lookup/{flightId}`
- `GET /api/v1/flights/{flightId}/waitlist`
- `POST /api/v1/flights/{flightId}/waitlist`
- `POST /api/v1/flights/{flightId}/waitlist/promote`
- `DELETE /api/v1/flights/{flightId}/waitlist/{bookingId}`

### Bookings

- `GET /api/v1/bookings`
- `POST /api/v1/bookings`
- `GET /api/v1/bookings/{bookingId}`
- `PATCH /api/v1/bookings/{bookingId}`
- `DELETE /api/v1/bookings/{bookingId}`
- `POST /api/v1/bookings/{bookingId}/cancel`
- `POST /api/v1/bookings/{bookingId}/undo`

### Passengers

- `GET /api/v1/passengers`
- `POST /api/v1/passengers`
- `GET /api/v1/passengers/{id}`
- `PATCH /api/v1/passengers/{id}`
- `DELETE /api/v1/passengers/{id}`
- `GET /api/v1/passengers/{passengerId}/bookings`
- `GET /api/v1/passengers/{passengerId}/bookings/history`

### Health

- `GET /api/v1/health`
- `GET /api/v1/health/database`
- `GET /api/v1/health/providers`

### Analytics

- `GET /api/v1/analytics/dashboard`
- `GET /api/v1/analytics/bookings`
- `GET /api/v1/analytics/load-factors`
- `GET /api/v1/analytics/revenue`
- `GET /api/v1/analytics/flight-status`
- `GET /api/v1/analytics/benchmarks`
- `POST /api/v1/analytics/benchmarks/run`

## Response format

Responses are wrapped by `ApiResponse` and typically look like this:

```json
{
  "timestamp": "2026-09-20T03:24:00Z",
  "status": 200,
  "error": "OK",
  "message": "Flights fetched",
  "data": {
    "count": 2,
    "items": []
  }
}
```

Notes:

- `status` is numeric in the live implementation
- `data` may be omitted when there is no payload
- older markdown docs may show slightly different shapes or messages

## Useful companion docs

- `API_PAYLOADS.md` — full request/response examples
- `frontend-api.md` — frontend-oriented endpoint list
- `DATABASE_MIGRATION_GUIDE.md` — schema migration notes
- `PASSENGER_API_UPDATED.md` — passenger-related API notes
- `FIX_SUMMARY.md` — recent project fixes and behavior changes

## Known implementation notes

- `HELP.md` contains some generated Spring template content that is no longer fully accurate
- `compose.yaml` is valid and defines both the app and PostgreSQL services
- passenger profiles can now be created without a linked user account
- self-registration is intentionally blocked by the current auth service

## Recommended next improvements

If you plan to continue developing this backend, the most useful next steps would be:

1. add a real security layer with request-scoped authentication
2. add a `.env.example` file for easier onboarding
3. add OpenAPI/Swagger for live API documentation
4. add integration tests for the main booking flows
5. clarify which endpoints are admin-only vs public


