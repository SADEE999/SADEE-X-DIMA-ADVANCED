-- SADEE × DIMA GAMING ADMIN PRO additions
create table if not exists public.products (
  id uuid primary key default gen_random_uuid(),
  name text not null,
  category text,
  price_lkr numeric(12,2) default 0,
  price_note text,
  version text,
  download_url text,
  file_url text,
  description text,
  features jsonb default '[]'::jsonb,
  image_url text,
  featured boolean default false,
  visible boolean default true,
  download_enabled boolean default true,
  requires_paid_key boolean default false,
  created_at timestamptz default now(),
  updated_at timestamptz default now()
);

alter table public.products enable row level security;

-- Public read for the admin's publishable Supabase client; writes should be
-- protected by your existing admin access rules if you add authenticated auth.
drop policy if exists products_read on public.products;
create policy products_read on public.products for select using (true);

grant select, insert, update, delete on public.products to anon, authenticated;
