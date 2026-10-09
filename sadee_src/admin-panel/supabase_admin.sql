create extension if not exists pgcrypto;

create table if not exists public.license_keys(
 id uuid primary key default gen_random_uuid(), key_code text unique not null, username text,
 plan text default '7', status text default 'active', expires_at timestamptz,
 device_limit int default 1, duration_days int not null default 0, device_count int default 0, created_at timestamptz default now(), updated_at timestamptz default now()
);
create table if not exists public.license_devices(
 id uuid primary key default gen_random_uuid(), license_key_id uuid references public.license_keys(id) on delete cascade,
 device_id text not null, device_name text, android_version text, ram text, storage text, cpu text,
 first_activated_at timestamptz default now(), last_active_at timestamptz default now(), unique(license_key_id,device_id)
);
create table if not exists public.game_profiles(id uuid primary key default gen_random_uuid(),game_name text unique not null,enabled boolean default true,performance_mode text default 'high',settings jsonb default '{}'::jsonb);
create table if not exists public.optimizer_settings(id uuid primary key default gen_random_uuid(),key text unique not null,value jsonb default '{}'::jsonb);
create table if not exists public.app_updates(id uuid primary key default gen_random_uuid(),version text not null,apk_url text,message text,force_update boolean default false,active boolean default true,created_at timestamptz default now());
create table if not exists public.app_settings(id uuid primary key default gen_random_uuid(),app_name text default 'SADEE X DIMA',whatsapp text default '+94 76 847 2404',whatsapp_group text,tiktok text,price_7d numeric default 500,price_1m numeric default 1000,price_lifetime numeric default 2200,announcement text,maintenance boolean default false,updated_at timestamptz default now());

-- This build intentionally has NO admin login. These policies allow the public publishable key
-- to read/write the admin tables. Do not use this mode if the panel must be private.
do $$ declare t text; begin
 foreach t in array array['license_keys','license_devices','game_profiles','optimizer_settings','app_updates','app_settings'] loop
   execute format('alter table public.%I enable row level security',t);
   execute format('drop policy if exists public_all_%I on public.%I',t,t);
   execute format('create policy public_all_%I on public.%I for all to anon, authenticated using (true) with check (true)',t,t);
 end loop;
end $$;

insert into public.game_profiles(game_name,enabled,performance_mode) values
('Free Fire',true,'high'),('Free Fire MAX',true,'high'),('PUBG Mobile',true,'high'),('COD Mobile',true,'high'),('Custom Game',true,'high')
on conflict (game_name) do nothing;

insert into public.app_settings(app_name,whatsapp,whatsapp_group,tiktok,price_7d,price_1m,price_lifetime)
select 'SADEE X DIMA','+94 76 847 2404','https://chat.whatsapp.com/I3BsxvZMWM752XxJygaljp?s=cl&p=a&mlu=4&ilr=4','https://www.tiktok.com/@sadeexdima.optimizer?_r=1&_t=ZS-9AI4MbIgK4T',500,1000,2200
where not exists(select 1 from public.app_settings);
