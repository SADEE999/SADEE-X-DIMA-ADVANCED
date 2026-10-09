-- SADEE × DIMA final license validation + device-limit enforcement
-- 1 license key can activate on the number of devices selected by Admin.

ALTER TABLE public.license_keys
DROP CONSTRAINT IF EXISTS license_keys_device_limit_check;

ALTER TABLE public.license_keys
ADD CONSTRAINT license_keys_device_limit_check
CHECK (device_limit >= 1);

DROP FUNCTION IF EXISTS public.validate_license(text,text,text);

CREATE OR REPLACE FUNCTION public.validate_license(
    p_username text,
    p_key_code text,
    p_device_id text
)
RETURNS jsonb
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_license public.license_keys%ROWTYPE;
    v_device_count integer := 0;
    v_existing boolean := false;
    v_limit integer;
BEGIN
    SELECT * INTO v_license
    FROM public.license_keys
    WHERE lower(trim(username)) = lower(trim(p_username))
      AND trim(key_code) = trim(p_key_code)
    ORDER BY created_at DESC NULLS LAST
    LIMIT 1;

    IF NOT FOUND THEN
        RETURN jsonb_build_object('valid',false,'reason','invalid_license');
    END IF;

    IF lower(coalesce(v_license.status,'')) <> 'active' THEN
        RETURN jsonb_build_object('valid',false,'reason','inactive','status',v_license.status);
    END IF;

    IF v_license.expires_at IS NOT NULL AND v_license.expires_at <= now() THEN
        RETURN jsonb_build_object('valid',false,'reason','expired','status','expired','expires_at',v_license.expires_at);
    END IF;

    v_limit := greatest(coalesce(v_license.device_limit,1),1);
    PERFORM pg_advisory_xact_lock(hashtext(v_license.id::text));

    SELECT EXISTS(
        SELECT 1 FROM public.license_devices
        WHERE license_key_id=v_license.id AND device_id=p_device_id
    ) INTO v_existing;

    SELECT count(*)::integer INTO v_device_count
    FROM public.license_devices
    WHERE license_key_id=v_license.id;

    IF NOT v_existing AND v_limit < 999 AND v_device_count >= v_limit THEN
        RETURN jsonb_build_object(
            'valid',false,'reason','device_limit','device_limit',v_limit,
            'used_devices',v_device_count,'id',v_license.id
        );
    END IF;

    IF NOT v_existing THEN
        INSERT INTO public.license_devices(
            id,license_key_id,device_id,device_name,android_version,storage,cpu,first_activated_at,last_active_at
        ) VALUES (
            gen_random_uuid(),v_license.id,p_device_id,NULL,NULL,NULL,NULL,now(),now()
        );
        v_device_count := v_device_count + 1;
    ELSE
        UPDATE public.license_devices
        SET last_active_at=now()
        WHERE license_key_id=v_license.id AND device_id=p_device_id;
    END IF;

    UPDATE public.license_keys
    SET last_active_at=now(), activated_at=coalesce(activated_at,now()), updated_at=now()
    WHERE id=v_license.id;

    RETURN jsonb_build_object(
        'valid',true,'reason','ok','id',v_license.id,'username',v_license.username,
        'key_code',v_license.key_code,'status',v_license.status,'expires_at',v_license.expires_at,
        'device_limit',v_limit,'used_devices',v_device_count,'duration_days',v_license.duration_days
    );
END;
$$;

GRANT EXECUTE ON FUNCTION public.validate_license(text,text,text) TO anon,authenticated;
NOTIFY pgrst,'reload schema';


CREATE OR REPLACE FUNCTION public.update_device_metadata(
    p_license_id uuid,
    p_device_id text,
    p_device_name text,
    p_android_version text,
    p_ram text,
    p_storage text,
    p_cpu text
)
RETURNS boolean
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
BEGIN
    UPDATE public.license_devices
    SET device_name=p_device_name,
        android_version=p_android_version,
        ram=p_ram,
        storage=p_storage,
        cpu=p_cpu,
        last_active_at=now()
    WHERE license_key_id=p_license_id
      AND device_id=p_device_id;
    RETURN FOUND;
END;
$$;

GRANT EXECUTE ON FUNCTION public.update_device_metadata(uuid,text,text,text,text,text,text) TO anon,authenticated;
NOTIFY pgrst,'reload schema';
