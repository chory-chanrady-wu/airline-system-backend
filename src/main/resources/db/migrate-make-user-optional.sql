-- Migration: Make user_id nullable in passenger_profiles table
-- Allows passengers to be created without being linked to a user account

ALTER TABLE passenger_profiles ALTER COLUMN user_id DROP NOT NULL;

