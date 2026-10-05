# SADEE X DIMA — Gaming Performance Optimizer

Separate new Android project. It does not modify the existing/old optimizer.

## Included
- 7-page flow: Login/License, Home, Game Center, Optimizer, Monitor, Game Launch, Profile.
- License purchase selector: 7 Days Rs.500, 1 Month Rs.1,000, Lifetime Rs.2,200.
- WhatsApp order to +94 76 847 2404.
- WhatsApp group + TikTok buttons.
- Username + license key activation flow.
- Device information and game launching.
- GitHub Actions APK build workflow.

## Supabase
The app currently has empty `SUPABASE_URL` and `SUPABASE_ANON_KEY` constants in `MainActivity.java`.
Set them to the new project's Supabase URL and public anon key. Never put a service-role key in the APK.

Expected license table fields for the online verification path:
`username`, `license_key`, `status`.

The local setup mode is enabled until Supabase credentials are configured; this is only for UI testing and should be disabled before a production release.

Android cannot safely perform arbitrary system-level RAM cleaning or thermal/FPS control without supported APIs/root. The app therefore does not falsely claim unsupported system modifications.
