# Database Migration Guide - Make user_id Nullable in passenger_profiles

## Problem
The `passenger_profiles` table has a `NOT NULL` constraint on the `user_id` column, which prevents creating passengers without a user account. This needs to be fixed.

## Solution Options

### Option 1: FASTEST - Reset Database (Recommended for Development)

If you're in development and can afford to lose data:

1. **Connect to PostgreSQL:**
   - Host: `localhost`
   - Database: `flightDB`
   - Username: `postgres`
   - Password: `Admin@2026`

2. **Drop and recreate the database:**

   Using pgAdmin (GUI):
   - Open pgAdmin
   - Right-click on database `flightDB`
   - Select "Delete/Drop"
   - The application will recreate the schema automatically on restart

   OR using SQL command:
   ```sql
   DROP DATABASE IF EXISTS flightDB;
   CREATE DATABASE flightDB;
   ```

3. **Restart the Spring Boot application**
   - The application will recreate all tables with the updated schema

---

### Option 2: Manual Migration

If you need to preserve data, run this SQL command in your PostgreSQL client:

```sql
ALTER TABLE passenger_profiles ALTER COLUMN user_id DROP NOT NULL;
```

**Steps:**
1. Open pgAdmin or your PostgreSQL client
2. Connect to `flightDB` database
3. Open a SQL Query window
4. Copy and paste the above command
5. Execute it

---

### Option 3: Using a Database Client (DBeaver, DataGrip, etc.)

1. Open your database client
2. Connect to PostgreSQL with provided credentials
3. Execute this SQL:
   ```sql
   ALTER TABLE passenger_profiles ALTER COLUMN user_id DROP NOT NULL;
   ```

---

## Verification

After applying the fix, verify the change:

```sql
-- Check the column definition
\d passenger_profiles

-- Or query the information schema
SELECT column_name, is_nullable, data_type 
FROM information_schema.columns 
WHERE table_name = 'passenger_profiles' AND column_name = 'user_id';
```

The `is_nullable` column should show `YES`.

---

## After Migration

Once the database is fixed:

1. Rebuild and restart the application:
   ```bash
   mvn clean compile
   ```

2. Start the application

3. Test creating a passenger WITHOUT userId:
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

---

## What Changed

### Files Modified:
1. **src/main/resources/schema.sql**
   - Changed line 108 from:
     ```sql
     user_id INTEGER NOT NULL UNIQUE,
     ```
   - To:
     ```sql
     user_id INTEGER UNIQUE,
     ```

2. **Entity: PassengerProfile.java**
   - Changed column constraint from:
     ```java
     @JoinColumn(name = "user_id", nullable = false, unique = true)
     ```
   - To:
     ```java
     @JoinColumn(name = "user_id", nullable = true, unique = true)
     ```

3. **Service: PassengerServiceImpl.java**
   - Updated `createPassenger()` to handle optional userId

### New Migration Script:
- **src/main/resources/db/migrate-make-user-optional.sql**
  - Contains the ALTER TABLE statement for existing databases

---

## Database Connection Details (for reference)

```
Host: localhost
Port: 5432
Database: flightDB
Username: postgres
Password: Admin@2026
Driver: org.postgresql.Driver
```

---

## Summary

✅ Code changes completed  
✅ Database schema file updated  
✅ Migration script created  
⏳ Database needs to be updated (choose one option above)  

Once you apply the database migration, passengers can be created without requiring a user account!

