# Complete API Endpoint Payloads

**Base URL:** `http://localhost:8080`  
**API Prefix:** `/api/v1`

---

## 1. AUTHENTICATION ENDPOINTS

### 1.1 Register User
- **POST** `/api/v1/auth/register`
- **Request:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "CREATED",
  "error": null,
  "message": "User registered successfully",
  "data": {
    "user": {
      "id": 1,
      "name": "John Doe",
      "email": "john@example.com",
      "roleId": 1,
      "roleName": "passenger",
      "status": "Active",
      "createdAt": "2026-09-15T12:00:00",
      "updatedAt": "2026-09-15T12:00:00"
    },
    "authenticated": true,
    "token": "sha256_hash_token"
  }
}
```

### 1.2 Login
- **POST** `/api/v1/auth/login`
- **Request:**
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Login successful",
  "data": {
    "user": {
      "id": 1,
      "name": "John Doe",
      "email": "john@example.com",
      "roleId": 1,
      "roleName": "passenger",
      "status": "Active",
      "createdAt": "2026-09-15T12:00:00",
      "updatedAt": "2026-09-15T12:00:00"
    },
    "authenticated": true,
    "token": "sha256_hash_token"
  }
}
```

### 1.3 Logout
- **POST** `/api/v1/auth/logout`
- **Request:** (No body)
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Logout successful",
  "data": {
    "authenticated": false
  }
}
```

### 1.4 Get Session
- **GET** `/api/v1/auth/session`
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Session retrieved",
  "data": {
    "authenticated": true,
    "user": {
      "id": 1,
      "name": "John Doe",
      "email": "john@example.com",
      "roleId": 1,
      "roleName": "passenger",
      "status": "Active",
      "createdAt": "2026-09-15T12:00:00",
      "updatedAt": "2026-09-15T12:00:00"
    },
    "token": "sha256_hash_token"
  }
}
```

---

## 2. USER ENDPOINTS

### 2.1 Get Current User
- **GET** `/api/v1/users/me`
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Current user loaded",
  "data": {
    "user": {
      "id": 1,
      "name": "John Doe",
      "email": "john@example.com",
      "roleId": 1,
      "roleName": "passenger",
      "status": "Active",
      "createdAt": "2026-09-15T12:00:00",
      "updatedAt": "2026-09-15T12:00:00"
    }
  }
}
```

### 2.2 Update Current User
- **PATCH** `/api/v1/users/me`
- **Request:**
```json
{
  "name": "John Doe Updated",
  "email": "john.updated@example.com",
  "roleId": "1",
  "status": "Active"
}
```
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Profile updated",
  "data": {
    "user": {
      "id": 1,
      "name": "John Doe Updated",
      "email": "john.updated@example.com",
      "roleId": 1,
      "roleName": "passenger",
      "status": "Active",
      "createdAt": "2026-09-15T12:00:00",
      "updatedAt": "2026-09-15T12:00:00"
    }
  }
}
```

### 2.3 Change Password
- **PATCH** `/api/v1/users/me/password`
- **Request:**
```json
{
  "currentPassword": "password123",
  "newPassword": "newpassword456"
}
```
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Password updated",
  "data": {
    "changed": true,
    "requiresReauth": true,
    "user": {
      "id": 1,
      "name": "John Doe",
      "email": "john@example.com",
      "roleId": 1,
      "roleName": "passenger",
      "status": "Active",
      "createdAt": "2026-09-15T12:00:00",
      "updatedAt": "2026-09-15T12:00:00"
    }
  }
}
```

### 2.4 List Users
- **GET** `/api/v1/users?search=john`
- **Query Parameters:**
  - `search` (optional)
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Users fetched",
  "data": {
    "search": "john",
    "count": 2,
    "items": [
      {
        "id": 1,
        "name": "John Doe",
        "email": "john@example.com",
        "roleId": 1,
        "roleName": "passenger",
        "status": "Active",
        "createdAt": "2026-09-15T12:00:00",
        "updatedAt": "2026-09-15T12:00:00"
      }
    ]
  }
}
```

### 2.5 Create User
- **POST** `/api/v1/users`
- **Request:**
```json
{
  "name": "Jane Smith",
  "email": "jane@example.com",
  "password": "password123",
  "roleId": "1",
  "status": "Active"
}
```
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "CREATED",
  "error": null,
  "message": "User created",
  "data": {
    "user": {
      "id": 2,
      "name": "Jane Smith",
      "email": "jane@example.com",
      "roleId": 1,
      "roleName": "passenger",
      "status": "Active",
      "createdAt": "2026-09-15T12:00:00",
      "updatedAt": "2026-09-15T12:00:00"
    }
  }
}
```

### 2.6 Get User by ID
- **GET** `/api/v1/users/{id}`
- **Path Parameters:** `id` (integer)
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "User fetched",
  "data": {
    "user": {
      "id": 1,
      "name": "John Doe",
      "email": "john@example.com",
      "roleId": 1,
      "roleName": "passenger",
      "status": "Active",
      "createdAt": "2026-09-15T12:00:00",
      "updatedAt": "2026-09-15T12:00:00"
    }
  }
}
```

### 2.7 Update User by ID
- **PATCH** `/api/v1/users/{id}`
- **Path Parameters:** `id` (integer)
- **Request:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "roleId": "1",
  "status": "Active"
}
```
- **Response:** (Same as Get User by ID)

### 2.8 Delete User by ID
- **DELETE** `/api/v1/users/{id}`
- **Path Parameters:** `id` (integer)
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "User deleted",
  "data": {
    "id": 1
  }
}
```

---

## 3. ROLE ENDPOINTS

### 3.1 List Roles
- **GET** `/api/v1/roles`
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Roles fetched",
  "data": {
    "count": 2,
    "items": [
      {
        "id": 1,
        "name": "passenger",
        "description": "Standard passenger role",
        "permissions": ["book_flight", "view_bookings"],
        "createdAt": "2026-09-15T12:00:00",
        "updatedAt": "2026-09-15T12:00:00"
      },
      {
        "id": 2,
        "name": "admin",
        "description": "Administrator role",
        "permissions": ["manage_users", "manage_flights", "view_analytics"],
        "createdAt": "2026-09-15T12:00:00",
        "updatedAt": "2026-09-15T12:00:00"
      }
    ]
  }
}
```

### 3.2 Create Role
- **POST** `/api/v1/roles`
- **Request:**
```json
{
  "name": "staff",
  "description": "Staff member role",
  "permissions": ["view_flights", "assist_bookings"]
}
```
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "CREATED",
  "error": null,
  "message": "Role created",
  "data": {
    "role": {
      "id": 3,
      "name": "staff",
      "description": "Staff member role",
      "permissions": ["view_flights", "assist_bookings"],
      "createdAt": "2026-09-15T12:00:00",
      "updatedAt": "2026-09-15T12:00:00"
    }
  }
}
```

### 3.3 Get Role by ID
- **GET** `/api/v1/roles/{id}`
- **Path Parameters:** `id` (integer)
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Role fetched",
  "data": {
    "role": {
      "id": 1,
      "name": "passenger",
      "description": "Standard passenger role",
      "permissions": ["book_flight", "view_bookings"],
      "createdAt": "2026-09-15T12:00:00",
      "updatedAt": "2026-09-15T12:00:00"
    }
  }
}
```

### 3.4 Update Role by ID
- **PATCH** `/api/v1/roles/{id}`
- **Path Parameters:** `id` (integer)
- **Request:**
```json
{
  "name": "passenger",
  "description": "Updated passenger role",
  "permissions": ["book_flight", "view_bookings", "cancel_booking"]
}
```
- **Response:** (Same as Get Role by ID)

### 3.5 Delete Role by ID
- **DELETE** `/api/v1/roles/{id}`
- **Path Parameters:** `id` (integer)
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Role deleted",
  "data": {
    "id": 3
  }
}
```

---

## 4. AIRPORT ENDPOINTS

### 4.1 List Airports
- **GET** `/api/v1/airports?search=Jakarta`
- **Query Parameters:**
  - `search` (optional)
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Airports fetched",
  "data": {
    "search": "Jakarta",
    "count": 1,
    "items": [
      {
        "code": "CGK",
        "city": "Jakarta",
        "country": "Indonesia",
        "latitude": -6.1256,
        "longitude": 106.6558,
        "timezone": "WIB",
        "createdAt": "2026-09-15T12:00:00",
        "updatedAt": "2026-09-15T12:00:00"
      }
    ]
  }
}
```

### 4.2 Create Airport
- **POST** `/api/v1/airports`
- **Request:**
```json
{
  "code": "SIN",
  "city": "Singapore",
  "country": "Singapore",
  "latitude": 1.3521,
  "longitude": 103.8198,
  "timezone": "SGT"
}
```
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "CREATED",
  "error": null,
  "message": "Airport created",
  "data": {
    "airport": {
      "code": "SIN",
      "city": "Singapore",
      "country": "Singapore",
      "latitude": 1.3521,
      "longitude": 103.8198,
      "timezone": "SGT",
      "createdAt": "2026-09-15T12:00:00",
      "updatedAt": "2026-09-15T12:00:00"
    }
  }
}
```

### 4.3 Get Airport by Code
- **GET** `/api/v1/airports/{code}`
- **Path Parameters:** `code` (string, e.g., "CGK")
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Airport fetched",
  "data": {
    "airport": {
      "code": "CGK",
      "city": "Jakarta",
      "country": "Indonesia",
      "latitude": -6.1256,
      "longitude": 106.6558,
      "timezone": "WIB",
      "createdAt": "2026-09-15T12:00:00",
      "updatedAt": "2026-09-15T12:00:00"
    }
  }
}
```

### 4.4 Update Airport
- **PATCH** `/api/v1/airports/{code}`
- **Path Parameters:** `code` (string)
- **Request:**
```json
{
  "city": "Jakarta Baru",
  "country": "Indonesia",
  "latitude": -6.1256,
  "longitude": 106.6558,
  "timezone": "WIB"
}
```
- **Response:** (Same as Get Airport by Code)

### 4.5 Delete Airport
- **DELETE** `/api/v1/airports/{code}`
- **Path Parameters:** `code` (string)
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Airport deleted",
  "data": {
    "code": "SIN"
  }
}
```

---

## 5. ROUTE ENDPOINTS

### 5.1 List Routes
- **GET** `/api/v1/routes`
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Routes fetched",
  "data": {
    "count": 1,
    "items": [
      {
        "id": 1,
        "fromAirportCode": "CGK",
        "toAirportCode": "SIN",
        "distanceKm": 900,
        "durationMinutes": 180,
        "active": true,
        "createdAt": "2026-09-15T12:00:00",
        "updatedAt": "2026-09-15T12:00:00"
      }
    ]
  }
}
```

### 5.2 Create Route
- **POST** `/api/v1/routes`
- **Request:**
```json
{
  "fromAirportCode": "CGK",
  "toAirportCode": "SIN",
  "distanceKm": 900,
  "durationMinutes": 180,
  "active": true
}
```
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "CREATED",
  "error": null,
  "message": "Route created",
  "data": {
    "route": {
      "id": 1,
      "fromAirportCode": "CGK",
      "toAirportCode": "SIN",
      "distanceKm": 900,
      "durationMinutes": 180,
      "active": true,
      "createdAt": "2026-09-15T12:00:00",
      "updatedAt": "2026-09-15T12:00:00"
    }
  }
}
```

### 5.3 Get Route
- **GET** `/api/v1/routes/{from}/{to}`
- **Path Parameters:** `from`, `to` (airport codes)
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Route fetched",
  "data": {
    "route": {
      "id": 1,
      "fromAirportCode": "CGK",
      "toAirportCode": "SIN",
      "distanceKm": 900,
      "durationMinutes": 180,
      "active": true,
      "createdAt": "2026-09-15T12:00:00",
      "updatedAt": "2026-09-15T12:00:00"
    }
  }
}
```

### 5.4 Update Route
- **PATCH** `/api/v1/routes/{from}/{to}`
- **Path Parameters:** `from`, `to` (airport codes)
- **Request:**
```json
{
  "fromAirportCode": "CGK",
  "toAirportCode": "SIN",
  "distanceKm": 905,
  "durationMinutes": 185,
  "active": true
}
```
- **Response:** (Same as Get Route)

### 5.5 Delete Route
- **DELETE** `/api/v1/routes/{from}/{to}`
- **Path Parameters:** `from`, `to` (airport codes)
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Route deleted",
  "data": {
    "id": 1
  }
}
```

### 5.6 Get Route Distance
- **GET** `/api/v1/routes/{from}/{to}/distance`
- **Path Parameters:** `from`, `to` (airport codes)
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Route distance calculated",
  "data": {
    "from": "CGK",
    "to": "SIN",
    "distanceKm": 900,
    "durationMinutes": 180
  }
}
```

### 5.7 Route Optimization
- **GET** `/api/v1/routes/optimize?from=CGK&to=SIN&type=cheapest`
- **Query Parameters:**
  - `from` (required)
  - `to` (required)
  - `type` (optional, default: "cheapest")
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Route optimization completed",
  "data": {
    "from": "CGK",
    "to": "SIN",
    "optimizationType": "cheapest",
    "routes": [
      {
        "id": 1,
        "fromAirportCode": "CGK",
        "toAirportCode": "SIN",
        "distanceKm": 900,
        "durationMinutes": 180,
        "score": 100
      }
    ]
  }
}
```

---

## 6. FLIGHT ENDPOINTS

### 6.1 List Flights
- **GET** `/api/v1/flights`
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Flights fetched",
  "data": {
    "count": 0,
    "items": []
  }
}
```

### 6.2 Create Flight
- **POST** `/api/v1/flights`
- **Request:**
```json
{
  "flightNumber": "GA123",
  "airlineId": "1",
  "aircraftId": "1",
  "routeId": "1",
  "fromAirportCode": "CGK",
  "toAirportCode": "SIN",
  "departureTime": "2026-09-20T10:00:00",
  "arrivalTime": "2026-09-20T13:00:00",
  "price": 150.00,
  "seatCapacity": 180,
  "seatsAvailable": 180,
  "status": "Scheduled"
}
```
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "CREATED",
  "error": "Flight create not available yet",
  "message": null,
  "data": null
}
```

### 6.3 Get Flight
- **GET** `/api/v1/flights/{flightId}`
- **Path Parameters:** `flightId` (integer)
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "BAD_REQUEST",
  "error": "Flight not found",
  "message": null,
  "data": null
}
```

### 6.4 Update Flight
- **PATCH** `/api/v1/flights/{flightId}`
- **Path Parameters:** `flightId` (integer)
- **Request:**
```json
{
  "flightNumber": "GA123",
  "airlineId": "1",
  "aircraftId": "1",
  "routeId": "1",
  "fromAirportCode": "CGK",
  "toAirportCode": "SIN",
  "departureTime": "2026-09-20T10:00:00",
  "arrivalTime": "2026-09-20T13:00:00",
  "price": 150.00,
  "seatCapacity": 180,
  "seatsAvailable": 180,
  "status": "Scheduled"
}
```

### 6.5 Delete Flight
- **DELETE** `/api/v1/flights/{flightId}`
- **Path Parameters:** `flightId` (integer)

### 6.6 Search Flights
- **GET** `/api/v1/flights/search?from=CGK&to=SIN&date=2026-09-20`
- **Query Parameters:**
  - `from` (optional)
  - `to` (optional)
  - `date` (optional)
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Flight search results",
  "data": {
    "count": 0,
    "items": []
  }
}
```

### 6.7 Get Flight Schedule
- **GET** `/api/v1/flights/schedule?from=CGK&to=SIN&date=2026-09-20`
- **Query Parameters:**
  - `from` (optional)
  - `to` (optional)
  - `date` (optional)
  - `start` (optional)
  - `end` (optional)
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Flight schedule",
  "data": {
    "count": 0,
    "items": []
  }
}
```

### 6.8 Flight Lookup
- **GET** `/api/v1/flights/lookup/{flightId}`
- **Path Parameters:** `flightId` (integer)

### 6.9 Get Flight Waitlist
- **GET** `/api/v1/flights/{flightId}/waitlist`
- **Path Parameters:** `flightId` (integer)
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Waitlist fetched",
  "data": {
    "count": 0,
    "items": []
  }
}
```

### 6.10 Add to Waitlist
- **POST** `/api/v1/flights/{flightId}/waitlist`
- **Path Parameters:** `flightId` (integer)
- **Request:**
```json
{
  "passengerId": "1",
  "bookingId": "BK-00000001"
}
```

### 6.11 Promote Waitlist
- **POST** `/api/v1/flights/{flightId}/waitlist/promote`
- **Path Parameters:** `flightId` (integer)
- **Request:**
```json
{
  "bookingId": "BK-00000001"
}
```

### 6.12 Remove from Waitlist
- **DELETE** `/api/v1/flights/{flightId}/waitlist/{bookingId}`
- **Path Parameters:** `flightId`, `bookingId` (integers)

---

## 7. BOOKING ENDPOINTS

### 7.1 List Bookings
- **GET** `/api/v1/bookings`
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Bookings fetched",
  "data": {
    "count": 1,
    "items": [
      {
        "id": 1,
        "bookingReference": "BK-00000001",
        "passengerId": 1,
        "passengerName": "John Doe",
        "flightId": 1,
        "flightNumber": "GA123",
        "seatNumber": "12A",
        "amount": 150.00,
        "currency": "USD",
        "status": "Confirmed",
        "bookedAt": "2026-09-15T12:00:00",
        "cancelledAt": null,
        "createdAt": "2026-09-15T12:00:00",
        "updatedAt": "2026-09-15T12:00:00"
      }
    ]
  }
}
```

### 7.2 Create Booking
- **POST** `/api/v1/bookings`
- **Request:**
```json
{
  "passengerId": "1",
  "flightId": "1",
  "seatNumber": "12A",
  "amount": 150.00,
  "currency": "USD",
  "status": "Confirmed"
}
```
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "CREATED",
  "error": null,
  "message": "Booking created",
  "data": {
    "booking": {
      "id": 1,
      "bookingReference": "BK-00000001",
      "passengerId": 1,
      "passengerName": "John Doe",
      "flightId": 1,
      "flightNumber": "GA123",
      "seatNumber": "12A",
      "amount": 150.00,
      "currency": "USD",
      "status": "Confirmed",
      "bookedAt": "2026-09-15T12:00:00",
      "cancelledAt": null,
      "createdAt": "2026-09-15T12:00:00",
      "updatedAt": "2026-09-15T12:00:00"
    },
    "waitlistEntry": null
  }
}
```

### 7.3 Get Booking
- **GET** `/api/v1/bookings/{bookingId}`
- **Path Parameters:** `bookingId` (integer or booking reference)
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Booking fetched",
  "data": {
    "booking": {
      "id": 1,
      "bookingReference": "BK-00000001",
      "passengerId": 1,
      "passengerName": "John Doe",
      "flightId": 1,
      "flightNumber": "GA123",
      "seatNumber": "12A",
      "amount": 150.00,
      "currency": "USD",
      "status": "Confirmed",
      "bookedAt": "2026-09-15T12:00:00",
      "cancelledAt": null,
      "createdAt": "2026-09-15T12:00:00",
      "updatedAt": "2026-09-15T12:00:00"
    }
  }
}
```

### 7.4 Update Booking
- **PATCH** `/api/v1/bookings/{bookingId}`
- **Path Parameters:** `bookingId` (integer)
- **Request:**
```json
{
  "seatNumber": "12B",
  "status": "Confirmed"
}
```
- **Response:** (Same as Get Booking)

### 7.5 Delete Booking
- **DELETE** `/api/v1/bookings/{bookingId}`
- **Path Parameters:** `bookingId` (integer)
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Booking deleted",
  "data": {
    "bookingId": 1
  }
}
```

### 7.6 Cancel Booking
- **POST** `/api/v1/bookings/{bookingId}/cancel`
- **Path Parameters:** `bookingId` (integer)
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Booking cancelled",
  "data": {
    "booking": {
      "id": 1,
      "bookingReference": "BK-00000001",
      "passengerId": 1,
      "passengerName": "John Doe",
      "flightId": 1,
      "flightNumber": "GA123",
      "seatNumber": "12A",
      "amount": 150.00,
      "currency": "USD",
      "status": "Cancelled",
      "bookedAt": "2026-09-15T12:00:00",
      "cancelledAt": "2026-09-15T12:30:00",
      "createdAt": "2026-09-15T12:00:00",
      "updatedAt": "2026-09-15T12:30:00"
    }
  }
}
```

### 7.7 Undo Cancellation
- **POST** `/api/v1/bookings/{bookingId}/undo`
- **Path Parameters:** `bookingId` (integer)
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Cancellation undone",
  "data": {
    "booking": {
      "id": 1,
      "bookingReference": "BK-00000001",
      "passengerId": 1,
      "passengerName": "John Doe",
      "flightId": 1,
      "flightNumber": "GA123",
      "seatNumber": "12A",
      "amount": 150.00,
      "currency": "USD",
      "status": "Confirmed",
      "bookedAt": "2026-09-15T12:00:00",
      "cancelledAt": null,
      "createdAt": "2026-09-15T12:00:00",
      "updatedAt": "2026-09-15T12:30:00"
    }
  }
}
```

---

## 8. PASSENGER ENDPOINTS

### 8.1 List Passengers
- **GET** `/api/v1/passengers?search=john`
- **Query Parameters:**
  - `search` (optional)
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Passengers fetched",
  "data": {
    "search": "john",
    "count": 1,
    "items": [
      {
        "id": 1,
        "userId": 1,
        "userName": "John Doe",
        "userEmail": "john@example.com",
        "passportNumber": "JD123456",
        "nationality": "Indonesian",
        "phone": "+62812345678",
        "dateOfBirth": "1990-01-15",
        "emergencyContact": "+62812345679",
        "createdAt": "2026-09-15T12:00:00",
        "updatedAt": "2026-09-15T12:00:00"
      }
    ]
  }
}
```

### 8.2 Create Passenger
- **POST** `/api/v1/passengers`
- **Request:**
```json
{
  "userId": "1",
  "passportNumber": "JD123456",
  "nationality": "Indonesian",
  "phone": "+62812345678",
  "dateOfBirth": "1990-01-15",
  "emergencyContact": "+62812345679"
}
```
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "CREATED",
  "error": null,
  "message": "Passenger created",
  "data": {
    "passenger": {
      "id": 1,
      "userId": 1,
      "userName": "John Doe",
      "userEmail": "john@example.com",
      "passportNumber": "JD123456",
      "nationality": "Indonesian",
      "phone": "+62812345678",
      "dateOfBirth": "1990-01-15",
      "emergencyContact": "+62812345679",
      "createdAt": "2026-09-15T12:00:00",
      "updatedAt": "2026-09-15T12:00:00"
    }
  }
}
```

### 8.3 Get Passenger
- **GET** `/api/v1/passengers/{id}`
- **Path Parameters:** `id` (integer)
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Passenger fetched",
  "data": {
    "passenger": {
      "id": 1,
      "userId": 1,
      "userName": "John Doe",
      "userEmail": "john@example.com",
      "passportNumber": "JD123456",
      "nationality": "Indonesian",
      "phone": "+62812345678",
      "dateOfBirth": "1990-01-15",
      "emergencyContact": "+62812345679",
      "createdAt": "2026-09-15T12:00:00",
      "updatedAt": "2026-09-15T12:00:00"
    }
  }
}
```

### 8.4 Update Passenger
- **PATCH** `/api/v1/passengers/{id}`
- **Path Parameters:** `id` (integer)
- **Request:**
```json
{
  "passportNumber": "JD123456",
  "nationality": "Indonesian",
  "phone": "+62812345678",
  "dateOfBirth": "1990-01-15",
  "emergencyContact": "+62812345679"
}
```
- **Response:** (Same as Get Passenger)

### 8.5 Delete Passenger
- **DELETE** `/api/v1/passengers/{id}`
- **Path Parameters:** `id` (integer)
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Passenger deleted",
  "data": {
    "id": 1
  }
}
```

### 8.6 Get Passenger Bookings
- **GET** `/api/v1/passengers/{passengerId}/bookings`
- **Path Parameters:** `passengerId` (integer)
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Passenger bookings fetched",
  "data": {
    "passengerId": 1,
    "count": 1,
    "items": [
      {
        "id": 1,
        "bookingReference": "BK-00000001",
        "flightId": 1,
        "flightNumber": "GA123",
        "seatNumber": "12A",
        "amount": 150.00,
        "currency": "USD",
        "status": "Confirmed",
        "bookedAt": "2026-09-15T12:00:00",
        "cancelledAt": null
      }
    ]
  }
}
```

### 8.7 Get Passenger Booking History
- **GET** `/api/v1/passengers/{passengerId}/bookings/history`
- **Path Parameters:** `passengerId` (integer)
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Passenger booking history fetched",
  "data": {
    "passengerId": 1,
    "count": 1,
    "items": [
      {
        "id": 1,
        "bookingId": 1,
        "bookingReference": "BK-00000001",
        "action": "Created",
        "previousStatus": null,
        "newStatus": "Confirmed",
        "performedAt": "2026-09-15T12:00:00"
      }
    ]
  }
}
```

---

## 9. RADAR ENDPOINTS

### 9.1 Get Radar Feed
- **GET** `/api/v1/flights/radar`
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Radar data fetched",
  "data": {
    "count": 0,
    "items": []
  }
}
```

### 9.2 Get Radar Provider Status
- **GET** `/api/v1/flights/radar/status`
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Radar provider status",
  "data": {
    "provider": "database",
    "status": "offline",
    "refreshIntervalSeconds": 30,
    "aircraftCount": 0
  }
}
```

### 9.3 Get Radar for Flight
- **GET** `/api/v1/flights/radar/{flightId}`
- **Path Parameters:** `flightId` (integer)
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Flight radar data fetched",
  "data": {
    "flightId": 1,
    "flightNumber": "GA123",
    "route": {
      "from": "CGK",
      "to": "SIN"
    },
    "status": "Scheduled",
    "gate": null,
    "terminal": null,
    "delayMinutes": null,
    "remarks": null,
    "recordedAt": null
  }
}
```

---

## 10. HEALTH ENDPOINTS

### 10.1 Service Health
- **GET** `/api/v1/health`
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Service is healthy",
  "data": {
    "status": "ok",
    "timestamp": "2026-09-15T12:00:00.000Z",
    "users": 5,
    "flights": 10,
    "bookings": 20
  }
}
```

### 10.2 Database Health
- **GET** `/api/v1/health/database`
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Database health check passed",
  "data": {
    "status": "connected",
    "tables": {
      "users": 5,
      "roles": 2,
      "airports": 3,
      "flights": 10,
      "bookings": 20
    }
  }
}
```

### 10.3 Providers Health
- **GET** `/api/v1/health/providers`
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "External providers health check",
  "data": {
    "radar": "offline",
    "weather": "unknown",
    "radarSnapshot": null
  }
}
```

---

## 11. ANALYTICS ENDPOINTS

### 11.1 Dashboard Analytics
- **GET** `/api/v1/analytics/dashboard`
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Dashboard analytics fetched",
  "data": {
    "activeFlights": 10,
    "bookingsToday": 20,
    "revenue": 3000.00,
    "averageLoadFactor": 0.75,
    "generatedAt": "2026-09-15T12:00:00",
    "source": "analytics_snapshots"
  }
}
```

### 11.2 Booking Analytics
- **GET** `/api/v1/analytics/bookings`
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Booking analytics fetched",
  "data": {
    "bookings": 20,
    "confirmed": 18,
    "cancelled": 2,
    "waitlisted": 0
  }
}
```

### 11.3 Load Factor Analytics
- **GET** `/api/v1/analytics/load-factors`
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Load factor analytics fetched",
  "data": {
    "averageLoadFactor": 0.75,
    "items": []
  }
}
```

### 11.4 Revenue Analytics
- **GET** `/api/v1/analytics/revenue`
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Revenue analytics fetched",
  "data": {
    "totalRevenue": 2700.00,
    "confirmedBookings": 18
  }
}
```

### 11.5 Flight Status Analytics
- **GET** `/api/v1/analytics/flight-status`
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Flight status distribution",
  "data": {
    "count": 0,
    "items": []
  }
}
```

### 11.6 Benchmark Report
- **GET** `/api/v1/analytics/benchmarks`
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Benchmark results fetched",
  "data": {
    "count": 0,
    "items": []
  }
}
```

### 11.7 Run Benchmarks
- **POST** `/api/v1/analytics/benchmarks/run`
- **Request:** (No body required)
- **Response:**
```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "OK",
  "error": null,
  "message": "Benchmark execution started",
  "data": {
    "executionId": "benchmark-12345",
    "status": "running"
  }
}
```

---

## RESPONSE STATUS CODES

| Status Code | Meaning |
|-------------|---------|
| OK | 200 - Success |
| CREATED | 201 - Resource created |
| BAD_REQUEST | 400 - Invalid request |
| NOT_FOUND | 404 - Resource not found |
| CONFLICT | 409 - Resource conflict |
| ERROR | 500 - Server error |

---

## COMMON RESPONSE STRUCTURE

All API responses follow this structure:
```json
{
  "timestamp": "ISO-8601 timestamp",
  "status": "HTTP Status Code",
  "error": "Error message or null",
  "message": "Response message",
  "data": {}
}
```

---

