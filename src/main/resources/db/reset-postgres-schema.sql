-- One-time reset after migrating entity IDs from UUID string to Integer.
-- WARNING: this removes all existing data in the current schema.

DROP SCHEMA IF EXISTS public CASCADE;
CREATE SCHEMA public;

