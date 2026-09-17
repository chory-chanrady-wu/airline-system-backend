# Updated Passenger API Endpoint

## Summary of Changes
Passengers no longer need to be linked to a user account for login. The system now allows creating independent passengers.

---

## 8.2 Create Passenger (REVISED)

**POST** `/api/v1/passengers`

**Base URL:** `http://localhost:8080`  
**Full URL:** `http://localhost:8080/api/v1/passengers`

### Request Payload (userId is now OPTIONAL)

```json
{
  "userId": null,
  "passportNumber": "JD123456",
  "nationality": "Indonesian",
  "phone": "+62812345678",
  "dateOfBirth": "1990-01-15",
  "emergencyContact": "+62812345679"
}
```

**Field Details:**
- `userId` - **(OPTIONAL)** User ID (if passenger should be linked to a user account). Can be null or omitted.
- `passportNumber` - **(REQUIRED)** Passport number
- `nationality` - Passenger's nationality
- `phone` - Contact phone number  
- `dateOfBirth` - Date of birth (YYYY-MM-DD format)
- `emergencyContact` - Emergency contact number

### Key Changes:
1. **userId is now OPTIONAL** - Passengers can be created without linking to a user account
2. **Independent Passengers** - If userId is null/omitted, passengers don't need system login credentials
3. **Optional User Link** - userId can be provided if you want to link a passenger to an existing user (user must exist)

### Success Response (201 - Created)

```json
{
  "timestamp": "2026-09-15T12:00:00.000Z",
  "status": "CREATED",
  "error": null,
  "message": "Passenger created",
  "data": {
    "passenger": {
      "id": 1,
      "userId": null,
      "userName": null,
      "userEmail": null,
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

### Example Usage:

**Creating an independent passenger (without user):**
```bash
curl -X POST http://localhost:8080/api/v1/passengers \
  -H "Content-Type: application/json" \
  -d '{
    "passportNumber": "AB123456",
    "nationality": "Indonesian",
    "phone": "+62812345678",
    "dateOfBirth": "1990-01-15",
    "emergencyContact": "+62812345679"
  }'
```

**Creating a passenger linked to an existing user:**
```bash
curl -X POST http://localhost:8080/api/v1/passengers \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "1",
    "passportNumber": "AB123456",
    "nationality": "Indonesian",
    "phone": "+62812345678",
    "dateOfBirth": "1990-01-15",
    "emergencyContact": "+62812345679"
  }'
```

---

## Code Changes Made

### 1. PassengerProfile Entity
- Changed `user_id` column from `nullable = false` to `nullable = true`
- Passengers can now exist without an associated user

### 2. PassengerServiceImpl
- Updated `createPassenger()` method to handle optional userId
- If userId is null/empty, passenger is created without user link
- If userId is provided, it validates that the user exists

### 3. Database
- The `passenger_profiles` table `user_id` column now accepts NULL values
- Existing passengers with user links remain unchanged

---

## All Passenger Endpoints

### List Passengers
```
GET /api/v1/passengers?search=john
```

### Create Passenger ✨ (NOW SUPPORTS OPTIONAL USER)
```
POST /api/v1/passengers
```

### Get Passenger
```
GET /api/v1/passengers/{id}
```

### Update Passenger  
```
PATCH /api/v1/passengers/{id}
```

### Delete Passenger
```
DELETE /api/v1/passengers/{id}
```

### Get Passenger Bookings
```
GET /api/v1/passengers/{passengerId}/bookings
```

### Get Passenger Booking History
```
GET /api/v1/passengers/{passengerId}/bookings/history
```

---

## Summary

✅ **Passengers are now independent of the user/login system**  
✅ **userId field is optional when creating passengers**  
✅ **Backward compatible - existing user-linked passengers still work**  
✅ **Response shows null values for user fields when not linked**  


