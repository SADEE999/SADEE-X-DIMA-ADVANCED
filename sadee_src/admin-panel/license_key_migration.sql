-- SADEE X DIMA license schema migration
-- Run this ONCE in Supabase SQL Editor before using the fixed build.

DO $$
BEGIN
  -- Old builds used license_key; the Gaming App uses key_code.
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema='public' AND table_name='license_keys' AND column_name='license_key'
  ) AND NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema='public' AND table_name='license_keys' AND column_name='key_code'
  ) THEN
    ALTER TABLE public.license_keys RENAME COLUMN license_key TO key_code;
  END IF;

  -- If both columns exist, copy the old value into key_code where needed.
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema='public' AND table_name='license_keys' AND column_name='license_key'
  ) AND EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema='public' AND table_name='license_keys' AND column_name='key_code'
  ) THEN
    UPDATE public.license_keys
    SET key_code = COALESCE(NULLIF(key_code,''), license_key)
    WHERE key_code IS NULL OR key_code='';
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema='public' AND table_name='license_keys' AND column_name='duration_days'
  ) THEN
    ALTER TABLE public.license_keys ADD COLUMN duration_days integer;
  END IF;

  UPDATE public.license_keys
  SET duration_days = CASE
    WHEN plan IN ('7','7 DAYS') THEN 7
    WHEN plan IN ('30','1 MONTH','30 DAYS') THEN 30
    ELSE COALESCE(duration_days,0)
  END
  WHERE duration_days IS NULL;

  ALTER TABLE public.license_keys
  ALTER COLUMN duration_days SET DEFAULT 0;

  ALTER TABLE public.license_keys
  ALTER COLUMN duration_days SET NOT NULL;
END $$;

NOTIFY pgrst, 'reload schema';

-- Verify:
SELECT key_code, username, plan, duration_days, device_limit, status, expires_at
FROM public.license_keys
ORDER BY created_at DESC;
