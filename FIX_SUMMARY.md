# Complete Fix Summary: Passengers Without User/Login System

## Issue Encountered
```
ERROR: null value in column "user_id" of relation "passenger_profiles" 
violates not-null constraint
```

**Root Cause**: The database schema had `user_id` with `NOT NULL` constraint, preventing passengers from being created without a user account.

---

## ✅ All Changes Applied

### 1. **PassengerProfile Entity** - UPDATED ✓
**File**: `src/main/java/com/example/backend/entity/PassengerProfile.java`

```java
// BEFORE:
@JoinColumn(name = "user_id", nullable = false, unique = true)
private User user;

// AFTER:
@JoinColumn(name = "user_id", nullable = true, unique = true)
private User user;
```

### 2. **PassengerServiceImpl** - UPDATED ✓
**File**: `src/main/java/com/example/backend/service/impl/PassengerServiceImpl.java`

```java
// BEFORE:
User user = resolveUser(request.userId());
if (user == null) {
    return ApiResponse.badRequest("User not found");
}

// AFTER:
User user = null;
if (request.userId() != null && !request.userId().isBlank()) {
    user = resolveUser(request.userId());
    if (user == null) {
        return ApiResponse.badRequest("User not found");
    }
}
```

### 3. **Database Schema** - UPDATED ✓
**File**: `src/main/resources/schema.sql` (Line 108)

```sql
-- BEFORE:
user_id INTEGER NOT NULL UNIQUE,

-- AFTER:
user_id INTEGER UNIQUE,
```

### 4. **Database Migration Script** - CREATED ✓
**File**: `src/main/resources/db/migrate-make-user-optional.sql`

```sql
-- Migration: Make user_id nullable in passenger_profiles table
-- Allows passengers to be created without being linked to a user account

ALTER TABLE passenger_profiles ALTER COLUMN user_id DROP NOT NULL;
```

### 5. **API Documentation** - UPDATED ✓
**File**: `API_PAYLOADS.md`

Updated section 8.2 to show:
- userId is now optional (can be null)
- Response shows null user fields when not linked

### 6. **Documentation Files** - CREATED ✓
- `PASSENGER_API_UPDATED.md` - Complete API documentation
- `DATABASE_MIGRATION_GUIDE.md` - Step-by-step migration instructions

---

## 🔧 Next Steps to Complete Fix

### Choose ONE option to update the database:

#### **Option 1: Reset Database (Fastest for Development)**
```sql
DROP DATABASE IF EXISTS flightDB;
CREATE DATABASE flightDB;
-- Then restart the Spring Boot application
```

#### **Option 2: Apply Migration (Preserve Data)**
Connect to PostgreSQL and run:
```sql
ALTER TABLE passenger_profiles ALTER COLUMN user_id DROP NOT NULL;
```

#### **Option 3: Use Database Client**
- Open pgAdmin, DataGrip, or DBeaver
- Connect to `flightDB`
- Execute: `ALTER TABLE passenger_profiles ALTER COLUMN user_id DROP NOT NULL;`

---

## ✓ Build Status
```
[INFO] BUILD SUCCESS
[INFO] Total time: 7.828 s
```
Project compiles without errors ✅

---

## 📝 Testing

After applying the database migration, test with:

### Create passenger WITHOUT user:
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

Expected Response (201):
```json
{
  "timestamp": "2026-09-17T22:35:00.000Z",
  "status": "CREATED",
  "error": null,
  "message": "Passenger created",
  "data": {
    "passenger": {
      "id": 10000001,
      "userId": null,
      "userName": null,
      "userEmail": null,
      "passportNumber": "AB123456",
      "nationality": "Indonesian",
      "phone": "+62812345678",
      "dateOfBirth": "1990-01-15",
      "emergencyContact": "+62812345679",
      "createdAt": "2026-09-17T22:35:00",
      "updatedAt": "2026-09-17T22:35:00"
    }
  }
}
```

### Create passenger WITH user (still supported):
```bash
curl -X POST http://localhost:8080/api/v1/passengers \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "1",
    "passportNumber": "CD789012",
    "nationality": "Indonesian",
    "phone": "+62812345679",
    "dateOfBirth": "1995-05-20",
    "emergencyContact": "+62812345680"
  }'
```

---

## 📊 Summary of Changes

| Component | Status | Type |
|-----------|--------|------|
| Java Entity | ✅ DONE | Code |
| Service Implementation | ✅ DONE | Code |
| Database Schema | ✅ DONE | Code |
| Migration Script | ✅ DONE | SQL |
| API Documentation | ✅ DONE | Doc |
| Project Build | ✅ DONE | Verify |
| Database Migration | ⏳ PENDING | Action Needed |

---

## Database Details
- **Host**: localhost
- **Port**: 5432
- **Database**: flightDB
- **User**: postgres
- **Password**: Admin@2026

---

## Files to Review

1. `PASSENGER_API_UPDATED.md` - Complete API reference
2. `DATABASE_MIGRATION_GUIDE.md` - Detailed migration steps
3. `src/main/resources/db/migrate-make-user-optional.sql` - Migration script

---

**Status**: 🔄 Awaiting database migration completion

Once you apply one of the migration options above, passengers can be created independently without requiring a user account!

