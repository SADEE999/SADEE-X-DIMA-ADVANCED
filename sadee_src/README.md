# SADEE X DIMA — FINAL APP + ADMIN PACKAGE

This package contains three parts:

- `gaming-app/` — SADEE X DIMA Gaming Performance Optimizer Android app
- `admin-app/` — separate Android Admin Panel app (opens the admin control panel directly; no login screen)
- `admin-panel/` — responsive web admin panel
- `.github/workflows/build-all.yml` — one ZIP upload -> build both APKs + deploy web admin to GitHub Pages

## Included changes

- Supabase URL + publishable key configured in both admin web and gaming app.
- Admin web has no email/password login in this build.
- Admin menu switches between Dashboard, License Keys, Devices/Users, Game Profiles, Optimizer Controls, App Updates and Settings.
- License creation, status changes, device reset and search use Supabase.
- Device records can be submitted by the optimizer app after a valid license activation.
- Game profiles, optimizer settings, app updates and remote settings have save controls.
- SADEE X DIMA logo is included in both apps.
- Gaming app has splash/loading animation and updated price list: 7 Days Rs.500, 1 Month Rs.1,000, Lifetime Rs.2,200.

## IMPORTANT — one Supabase SQL step

Because this build intentionally removes the admin login, the Supabase tables must allow the public publishable key to read/write the admin tables. Open Supabase SQL Editor and run:

`admin-panel/supabase_admin.sql`

Do NOT put a service-role/secret key into the app or website.

## GitHub upload

Upload this ZIP only to the repository root. Do not extract it into the repository.

Then run:

Actions -> SADEE X DIMA - Build Gaming App + Admin App + Admin Web -> Run workflow

The workflow builds:

- `SADEE_X_DIMA_GAMING_OPTIMIZER.apk`
- `SADEE_X_DIMA_ADMIN.apk`

and deploys `admin-panel/` to GitHub Pages.

For GitHub Pages, set:

Settings -> Pages -> Source -> GitHub Actions

## Warning

This is a no-login admin build. Anyone who can reach the public admin URL can use the admin controls if the public RLS policies from the SQL file are enabled. Use this only if you intentionally want an open admin panel.

## License key schema fix

The Gaming Optimizer validates licenses using `license_keys.key_code`.
Before using this build with an existing Supabase database, run
`admin-panel/license_key_migration.sql` once in Supabase SQL Editor.
The migration safely handles databases that used the old `license_key` column.
