ALTER TABLE passenger_profiles
ADD COLUMN IF NOT EXISTS full_name VARCHAR(255);

UPDATE passenger_profiles p
SET full_name = COALESCE(NULLIF(u.name, ''), p.passport_number)
FROM users u
WHERE p.user_id = u.id
  AND (p.full_name IS NULL OR btrim(p.full_name) = '');

UPDATE passenger_profiles
SET full_name = passport_number
WHERE full_name IS NULL OR btrim(full_name) = '';

ALTER TABLE passenger_profiles
ALTER COLUMN full_name SET NOT NULL;

