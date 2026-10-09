# SADEE × DIMA PRO update

## Gaming app
- Existing SADEE × DIMA logo/art preserved.
- Animated splash using the existing logo (pulse/glow) without replacing the artwork.
- Remembered license session: after successful activation, username/key are saved locally and the app revalidates the saved session on next launch instead of asking again.
- Removed the visible logout button from Profile.
- HOME now includes device, Android version, RAM, storage, CPU cores and CPU details.
- Added safe app-cache cleanup and a Storage Cleanup Center that opens Android's storage settings.
- Added richer device information to Monitor.
- License validation registers a device once and updates its metadata; the client no longer performs a second device insert.

## Admin app / panel
- Existing logo preserved with a subtle pulse animation.
- Existing pages retained.
- Added Pro Analytics, Security Center and Product Editor.
- License device counts are calculated from `license_devices` instead of a non-existent `device_count` column.
- Device reset no longer tries to update a non-existent `license_keys.device_count` column.
- Added dashboard metrics and recent activity.

## Supabase
- `admin-panel/license_validation_rpc.sql` and root `LICENSE_FIX_SQL.sql` contain the final device-limit RPC.
- `admin-panel/pro_features.sql` creates the optional `products` table used by Product Editor.
- Added a metadata RPC so the Android app can update registered-device details without inserting duplicate device rows.
