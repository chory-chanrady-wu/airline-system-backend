-- One-time migration for existing PostgreSQL databases.
-- Adds users.user_type safely when rows already exist.

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS user_type VARCHAR(255);

UPDATE users
SET user_type = 'SYSTEM_USER'
WHERE user_type IS NULL;

ALTER TABLE users
    ALTER COLUMN user_type SET DEFAULT 'SYSTEM_USER';

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'users_user_type_check'
          AND conrelid = 'users'::regclass
    ) THEN
        ALTER TABLE users
            ADD CONSTRAINT users_user_type_check
            CHECK (user_type IN ('SYSTEM_USER', 'PASSENGER'));
    END IF;
END
$$;

ALTER TABLE users
    ALTER COLUMN user_type SET NOT NULL;
