-- One-time migration for existing PostgreSQL databases.
-- Removes aircraft.airline_id and its FK constraints.

DO $$
DECLARE
    constraint_name text;
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'aircraft' AND column_name = 'airline_id'
    ) THEN
        FOR constraint_name IN
            SELECT conname
            FROM pg_constraint
            WHERE conrelid = 'aircraft'::regclass
              AND contype = 'f'
              AND conkey @> ARRAY[
                  (SELECT attnum FROM pg_attribute
                   WHERE attrelid = 'aircraft'::regclass
                     AND attname = 'airline_id'
                     AND NOT attisdropped)
              ]
        LOOP
            EXECUTE format('ALTER TABLE aircraft DROP CONSTRAINT IF EXISTS %I', constraint_name);
        END LOOP;

        ALTER TABLE aircraft DROP COLUMN IF EXISTS airline_id;
    END IF;
END
$$;

